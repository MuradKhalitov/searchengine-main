package searchengine.controllers;

import searchengine.dto.Response;
import searchengine.service.indexing.SiteBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticsController {
    @GetMapping("/statistics")
    public Response statistics() {
        return SiteBuilder.getStatistics();
    }
}
