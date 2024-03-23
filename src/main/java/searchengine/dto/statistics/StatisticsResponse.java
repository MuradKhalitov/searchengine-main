package searchengine.dto.statistics;

import lombok.Data;
import lombok.EqualsAndHashCode;
import searchengine.dto.Response;

@Data
@EqualsAndHashCode(callSuper = false)
public class StatisticsResponse extends Response {
    private Statistics statistics = new Statistics();
}
