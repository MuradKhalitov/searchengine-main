package searchengine.service.indexing;

import lombok.extern.slf4j.Slf4j;
import searchengine.config.Configs;
import searchengine.model.Indecs;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.repository.Repos;
import org.jsoup.nodes.Document;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class PageBuilder implements Runnable {
    public static final String OK = "OK";
    public static final String NOT_FOUND = "\"Данная страница находится за пределами сайтов, " +
            "указанных в конфигурационном файле";
    public static final String SITE_NOT_INDEXED = "Нельзя индексировать страницу " +
            "сайта, если сайт ещё не индексирован";
    public static final String RUNNING = "Индексация уже запущена";

    private final Site site;
    private List<Page> oldPages;
    private Page page = null;

    public PageBuilder(Site site, String pagePath) {
        this.site = site;
        oldPages = Repos.pageRepo.findAllBySiteAndPathAndCode(site, pagePath, Node.OK);

        Node node = new Node(site, pagePath);
        node.setFromPageBuilder(true);
        Document doc = node.processAndReturnPageDoc();
        if (doc == null) {
            return;
        }
        int id = node.getAddedPageId();
        page = Repos.pageRepo.findById(id).orElse(null);
        if (page == null) {
            doc = null;
            return;
        }
        page.setContent(doc.outerHtml());
        page.setPath(pagePath);
    }

    @Override
    public void run() {
        log.info("Проиндексирована страница " + site.getUrl() + page.getPath());
        List<Lemma> lemmaList = Repos.lemmaRepo.findAllBySite(site);
        Map<String, Lemma> lemmas = new HashMap<>();
        for (Lemma lemma : lemmaList) {
            lemmas.put(lemma.getLemma(), lemma);
        }

        List<Indecs> indecsList = Repos.indexRepo.findAllBySite(site);
        Map<Integer, Indecs> indices = new HashMap<>();
        for (Indecs indecs : indecsList) {
            indices.put(indecs.hashCode(), indecs);
        }

        IndexBuilder indexBuilder = new IndexBuilder(site, page, lemmas, indices);
        indexBuilder.fillLemmasAndIndices();

        List<Lemma> lemmasToDelete = new ArrayList<>();
        if (oldPages != null && oldPages.size() > 0) {
            List<Integer> oldPageIds = oldPages.stream().map(p -> p.getId()).toList();
            for (Indecs indecs : indices.values().stream()
                    .filter(index -> oldPageIds.contains(index.getPage().getId()))
                    .toList()) {
                Lemma lemma = indecs.getLemma();
                lemma.setFrequency(lemma.getFrequency() - 1);
                if (lemma.getFrequency() == 0) {
                    lemmas.remove(lemma.getLemma());
                    lemmasToDelete.add(lemma);
                }
            }
        }

        Repos.lemmaRepo.deleteAllInBatch(lemmasToDelete);

        List<Indecs> pageIndices = new ArrayList<>();
        pageIndices.addAll(indices.values().stream()
                .filter(index -> index.getPage().getId() == page.getId())
                .toList());

        synchronized (Page.class) {
            Repos.pageRepo.saveAndFlush(page);
        }
        Repos.lemmaRepo.deleteAllInBatch(lemmasToDelete);
        Repos.lemmaRepo.saveAllAndFlush(lemmaList);
        Repos.indexRepo.saveAllAndFlush(pageIndices);
        synchronized (Page.class) {
            if (oldPages != null) {
                for (Page p : oldPages) {
                    Repos.pageRepo.deleteById(p.getId());
                }
            }
        }

        IndexingService.getIndexingSites().remove(site.getUrl());
    }

    public static String indexPage(String stringUrl) {
        URL url;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            return NOT_FOUND;
        }
        String home = url.getProtocol() + "://" + url.getHost();
        String path = url.getFile();

        if (IndexingService.getIndexingSites().containsKey(home)) {
            return RUNNING;
        }

        if (!Configs.getAllSiteUrls().contains(home)) {
            return NOT_FOUND;
        }
        Site site = Repos.siteRepo.findByUrlAndType(home, Site.INDEXED).orElse(null);

        if (path.isEmpty()) {
            IndexingService.buildSingleSite(home);
        } else {
            if (site == null) {
                return SITE_NOT_INDEXED;
            }
            PageBuilder pageBuilder = new PageBuilder(site, path);
            if (pageBuilder.page == null) {
                return NOT_FOUND;
            }
            IndexingService.getIndexingSites().put(site.getUrl(), site);
            pageBuilder.run();
        }

        return OK;
    }
}
