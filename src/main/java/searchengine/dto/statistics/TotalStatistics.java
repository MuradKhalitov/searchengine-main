package searchengine.dto.statistics;

import lombok.Data;
import searchengine.model.Site;
import searchengine.repository.Repos;
import searchengine.service.indexing.IndexingService;

import java.util.List;

@Data
public class TotalStatistics {
    private int sites;
    private int pages;
    private int lemmas;
    private boolean indexing;

    public TotalStatistics() {
        int siteCount = Repos.siteRepo.countByType(Site.INDEXED) +
                Repos.siteRepo.countByType(Site.FAILED);
        setSites(siteCount);

        List<Site> indexedSites = Repos.siteRepo.findAllByType(Site.INDEXED);
        setPages(Repos.pageRepo.countBySites(indexedSites));
        setLemmas(Repos.lemmaRepo.countBySites(indexedSites));

        setIndexing(!IndexingService.getIndexingSites().isEmpty());
    }
}
