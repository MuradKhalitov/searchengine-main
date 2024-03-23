package searchengine.dto.statistics;

import lombok.Data;
import searchengine.model.Site;
import searchengine.repository.Repos;

import java.time.ZoneOffset;

@Data
public class DetailedStatistics {
    private String url;
    private String name;
    private String status;
    private long statusTime;
    private String error;
    private int pages;
    private int lemmas;

    public DetailedStatistics(Site site) {
        url = site.getUrl();
        name = site.getName();
        status = site.getType();
        statusTime = (site.getStatusTime().toEpochSecond(ZoneOffset.UTC) - 3 * 3600) * 1000;
        error = site.getLastError();
        pages = Repos.pageRepo.countBySite(site);
        lemmas = Repos.lemmaRepo.countBySite(site);
    }
}
