package searchengine.service.indexing;

import searchengine.config.Configs;
import searchengine.model.Site;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public class PagesOfSiteBuilder extends RecursiveAction {
    private final Node node;
    public PagesOfSiteBuilder(Node node) {
        this.node = node;
    }

    @Override
    protected void compute() {
        List<PagesOfSiteBuilder> builders = new ArrayList<>();

        if (node.getSite().getPages().size() >=
                Configs.getConfigs().getMaxPagesInSite()) {
            node.getSite().setType(Site.INDEXED);
            return;
        }

        int pause = Configs.ConfigSite.getPauseBySiteName(node.getSite().getName());
        takeABreak(pause);
        Document doc = node.processAndReturnPageDoc();

        if (IndexingService.isStopping()) {
            return;
        }

        List<Node> children = node.getChildren(doc);

        if (children == null || children.size() == 0) {
            return;
        }

        for (Node child : children) {
            PagesOfSiteBuilder builder = new PagesOfSiteBuilder(child);
            builder.fork();
            builders.add(builder);
        }
        builders.forEach(ForkJoinTask::join);
    }

    private void takeABreak(int pauseInMsec) {
        Site site = node.getSite();
      //  synchronized (site) {
            long now = System.currentTimeMillis();
            if (now - site.getLastPageReadingTime() >= pauseInMsec) {
                site.setLastPageReadingTime(now);
                return;
            }
            try {
                long toSleep = pauseInMsec - (now - site.getLastPageReadingTime());
                Thread.sleep(toSleep);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            site.setLastPageReadingTime(System.currentTimeMillis());
     //  }
    }

    public static void build(Site site) {
        Node node = new Node(site, "/");
        PagesOfSiteBuilder builder = new PagesOfSiteBuilder(node);
        new ForkJoinPool(Runtime.getRuntime().availableProcessors() / 2).invoke(builder);
    }
}
