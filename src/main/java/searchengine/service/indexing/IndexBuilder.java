package searchengine.service.indexing;

import lombok.extern.slf4j.Slf4j;
import searchengine.lemmatizator.Lemmatizator;
import searchengine.model.*;
import searchengine.repository.Repos;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

@Slf4j
public class IndexBuilder {
    private final Site site;
    private final Page page;
    private Map<String, Lemma> lemmas;
    private Map<Integer, Indecs> indices;
    private final Set<String> lemmasInPage;

    public IndexBuilder(Site site, Page page, Map<String, Lemma> lemmas, Map<Integer, Indecs> indices) {
        this.site = site;
        this.page = page;
        this.lemmas = lemmas;
        this.indices = indices;
        lemmasInPage = new HashSet<>();
    }

    public static void build(Site site) {
        Repos.lemmaRepo.deleteAllInBatchBySite(site);
        log.info(TABS + "Сайт \"" + site.getName() + "\": строим леммы и индексы");
        IndexBuilder indexBuilder = new IndexBuilder(site, null, null, null);
        indexBuilder.buildIndex();
        indexBuilder.saveLemmasAndIndices();
    }

    private void buildIndex() {
        lemmas = new HashMap<>();
        indices = new HashMap<>();

        List<Page> pages = site.getPages().stream()
                .filter(p1 -> p1.getCode() == Node.OK)
                .sorted(Comparator.comparingInt(Page::getId)).toList();
        for (Page page : pages) {
            if (IndexingService.isStopping()) {
                return;
            }
            Page pag;
            pag = Repos.pageRepo.findById(page.getId()).orElse(null);
            if (pag == null) {
                continue;
            }
            IndexBuilder indexBuilder = new IndexBuilder(
                    site, pag, lemmas, indices);
            indexBuilder.fillLemmasAndIndices();
            pag.setContent(null);
            pag.setPath(null);
        }
    }

    public void fillLemmasAndIndices() {
        Document doc = Jsoup.parse(page.getContent());
        Map<String, Float> fields = new HashMap<>();
        fields.put("title", 1.0f);
        fields.put("body", 0.8f);
        fields.put("h1", 0.1f);

        for (Map.Entry<String, Float> field : fields.entrySet()) {
            Elements elements = doc.getElementsByTag(field.getKey());
            for (Element element : elements) {
                String text = element.text();
                List<String> lemmaNames = Lemmatizator.decomposeTextToLemmas(text);
                for (String lemmaName : lemmaNames) {
                    insertIntoLemmasAndIndices(lemmaName, field.getValue());
                }
            }
        }
    }

    private void insertIntoLemmasAndIndices(String lemmaName, float weight) {
        Lemma lemma = lemmas.get(lemmaName);
        if (lemma == null) {
            lemma = new Lemma();
            lemma.setLemma(lemmaName);
            lemma.setFrequency(1);
            lemma.setSite(site);
            lemma.setWeight(weight);
            lemmas.put(lemmaName, lemma);

            Indecs indecs = new Indecs(page, lemma, weight);
            indices.put(indecs.hashCode(), indecs);

            lemmasInPage.add(lemmaName);
            return;
        }
        if (lemmasInPage.contains(lemmaName)) {
            Indecs auxIndecs = new Indecs(page, lemma, 0);
            Indecs indecs = indices.get(auxIndecs.hashCode());
            indecs.setRank(indecs.getRank() + weight);
        } else {
            lemmasInPage.add(lemmaName);
            lemma.setFrequency(lemma.getFrequency() + 1);
            Indecs indecs = new Indecs(page, lemma, weight);
            indices.put(indecs.hashCode(), indecs);
        }
    }

    public static final String TABS = "\t\t";

    public void saveLemmasAndIndices() {
        log.info(TABS + "Сайт \"" + site.getName() + "\": cохраняем леммы");
        if (IndexingService.isStopping()) {
            return;
        }
        var lemmaCollection = lemmas.values();
        synchronized (Lemma.class) {
            Repos.lemmaRepo.saveAllAndFlush(lemmaCollection);
        }

        log.info(TABS + "Сайт \"" + site.getName() + "\": cохраняем индексы");
        saveIndicesByMultipleInsert();

        log.info(TABS + "Сайт \"" + site.getName() + "\": " +
                "всего сохранено страниц - " + site.getPages().size());
    }

    private void saveIndicesByMultipleInsert() {
        List<Indecs> siteIndices = indices.values().stream()
                .filter(index -> index.getPage().getSite().getId() == site.getId()
                        && index.getPage().getCode() == Node.OK)
                .toList();
        if (siteIndices.size() == 0) {
            return;
        }
        String siteName = site.getName();
        synchronized (Indecs.class) {
            Repos.indexImplRepo.insertIndexList(siteName, siteIndices);
        }
    }
}
