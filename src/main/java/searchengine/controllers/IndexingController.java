package searchengine.controllers;

import org.springframework.web.bind.annotation.*;
import searchengine.dto.ErrorResponse;
import searchengine.dto.Response;
import searchengine.service.indexing.IndexingService;
import searchengine.service.indexing.PageBuilder;

@RestController
@RequestMapping("/api")
public class IndexingController {
    private IndexingService indexingService;
    public IndexingController(IndexingService indexingService) {
        this.indexingService = indexingService;
    }

    @GetMapping("/startIndexing")
    public Response startIndexing() {
        boolean isIndexing = IndexingService.startIndexing();
        if (isIndexing) {
            return new ErrorResponse("Индексация уже запущена");
        }
        return new Response();
    }

    @PostMapping("/indexPage")
    public Response indexPage(@RequestParam(required = false) String url) {
        String result = PageBuilder.indexPage(url);
        if (result.equals(PageBuilder.OK)) {
            return new Response();
        }
        return new ErrorResponse(result);
    }

    @GetMapping("/stopIndexing")
    public Response stopIndexing() {
        boolean isIndexing = IndexingService.stopIndexing();
        if (isIndexing) {
            return new Response();
        }
        return new ErrorResponse("Индексация не была запущена");
    }
}
