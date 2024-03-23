package searchengine.dto.statistics;

import lombok.Data;
import searchengine.model.Site;
import searchengine.repository.Repos;
import searchengine.service.indexing.SiteBuilder;

import java.util.List;

@Data
public class TotalStatistics {
    private int howManySites;
    private int howManyPages;
    private int howManyLemmas;
    private boolean indexing;

    public TotalStatistics() {
        int siteCount = Repos.siteRepo.countByType(Site.INDEXED) +
                Repos.siteRepo.countByType(Site.FAILED);
        setHowManySites(siteCount);

        List<Site> indexedSites = Repos.siteRepo.findAllByType(Site.INDEXED);
        setHowManyPages(Repos.pageRepo.countBySites(indexedSites));
        setHowManyLemmas(Repos.lemmaRepo.countBySites(indexedSites));

        setIndexing(!SiteBuilder.getIndexingSites().isEmpty());
    }
}
