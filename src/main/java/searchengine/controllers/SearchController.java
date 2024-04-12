package searchengine.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import searchengine.dto.ErrorResponse;
import searchengine.dto.Response;
import searchengine.service.search.SearchRequestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
public class SearchController {
    @Autowired
    SearchRequestService searchRequestService;
    @GetMapping("/search")
    public Response search(@RequestParam(required = false) String query,
                           @RequestParam(required = false) String site,
                           @RequestParam(required = false) Integer offset,
                           @RequestParam(required = false) Integer limit) {
        log.info("Поисковый запрос: " + query);
        SearchRequestService request = new SearchRequestService().buildRequest(query, site, offset, limit);
        if (request == null) {
            return new ErrorResponse("Задан пустой поисковый запрос");
        }
        log.info("Леммы: " + request.getQueryWords());
        return searchRequestService.receiveResponse(request);
    }


}
