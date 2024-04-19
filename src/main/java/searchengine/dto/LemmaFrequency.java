package searchengine.dto;

import lombok.Data;

@Data
public class LemmaFrequency {
    private String lemma;
    private float frequency;
}
