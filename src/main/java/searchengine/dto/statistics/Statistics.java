package searchengine.dto.statistics;

import lombok.Data;
import searchengine.model.Site;
import searchengine.repository.Repos;

import java.util.ArrayList;
import java.util.List;

@Data
public class Statistics {
    private TotalStatistics total;
    private List<DetailedStatistics> detailed;

    public Statistics() {
        total = new TotalStatistics();
        detailed = new ArrayList<>();

        List<Site> sites = Repos.siteRepo.findAll().stream()
                .filter(site -> site.getType().equals(Site.INDEXED) ||
                        site.getType().equals(Site.FAILED) ||
                        site.getType().equals(Site.INDEXING))
                .toList();
        for (Site site : sites) {
            DetailedStatistics detailedStatistics = new DetailedStatistics(site);
            detailed.add(detailedStatistics);
        }
    }
}
