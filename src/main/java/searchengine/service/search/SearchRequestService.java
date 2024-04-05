package searchengine.service.search;

import lombok.Data;
import org.springframework.stereotype.Service;
import searchengine.dto.Response;
import searchengine.dto.SearchResponse;
import searchengine.lemmatizator.Lemmatizator;
import searchengine.model.Site;
import searchengine.repository.Repos;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Service
public class SearchRequestService {
    private List<String> queryWords = new ArrayList<>();
    private List<String> siteUrls = new ArrayList<>();
    private int offset;
    private int limit;

    private boolean ready;
    private long lastTime;

    @Override
    public int hashCode() {
        return Objects.hash(queryWords, siteUrls);
    }

    @Override
     public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != SearchRequestService.class) {
            return false;
        }
        SearchRequestService sr = (SearchRequestService) obj;
        return queryWords.size() == sr.queryWords.size() &&
                siteUrls.size() == sr.siteUrls.size();
    }

    public SearchRequestService buildRequest(String query, String siteUrl, Integer offset, Integer limit) {
        queryWords = Lemmatizator.decomposeTextToLemmas(query);
        if (queryWords.isEmpty()) {
            return null;
        }

        if (siteUrl == null) {
            Repos.siteRepo.findAllByType(Site.INDEXED)
                    .forEach(site -> siteUrls.add(site.getUrl()));
        } else {
            siteUrls.add(siteUrl);
        }
        this.offset = offset == null ? 0 : offset;
        this.limit = limit == null ? 20 : limit;
        return this;
    }
    public Response receiveResponse(SearchRequestService request) {
        SearchResponse response;
        try {
            SearchListener.getRequestQueue().put(request);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            response = SearchListener.getResponseQueue().take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return response;
    }
}
