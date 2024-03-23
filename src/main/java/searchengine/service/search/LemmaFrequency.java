package searchengine.service.search;

import lombok.Data;

@Data
public class LemmaFrequency {
    private String lemma;
    private float frequency;
}
