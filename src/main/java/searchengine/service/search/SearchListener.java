package searchengine.service.search;

import searchengine.dto.SearchResponse;
import org.springframework.stereotype.Component;

import java.util.concurrent.SynchronousQueue;

@Component
public class SearchListener implements Runnable {
    private static final SynchronousQueue<SearchRequestService> requestQueue = new SynchronousQueue();
    private static final SynchronousQueue<SearchResponse> responseQueue = new SynchronousQueue();

    public static SynchronousQueue<SearchRequestService> getRequestQueue() {
        return requestQueue;
    }

    public static SynchronousQueue<SearchResponse> getResponseQueue() {
        return responseQueue;
    }

    public SearchListener() {
        new Thread(this, "SearchListener").start();
    }

    @Override
    public void run() {
        for (; ; ) {
            SearchRequestService request;
            try {
                request = requestQueue.take();
                SearchResponse response = SearchResponseBuilder.receiveResponse(request);
                responseQueue.put(response);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
