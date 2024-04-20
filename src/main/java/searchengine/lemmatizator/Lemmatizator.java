package searchengine.lemmatizator;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Lemmatizator {
    public static final String WORD_SEPARATORS =
            "\\s*(\\s|,|;|\\?|-|–|—|\\[|]|\\{|}|«|»|'|'|`|\"|!|\\.|\\(|\\))\\s*";
    private final static LuceneMorphology russianMorphology;
    private final static LuceneMorphology englishMorphology;

    static {
        try {
            russianMorphology = new RussianLuceneMorphology();
            englishMorphology = new EnglishLuceneMorphology();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> decomposeTextToLemmas(String text) {
        List<String> result = new ArrayList<>();
        String[] words = text.split(WORD_SEPARATORS);

        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            List<String> lemmas = processOneWord(word);
            result.addAll(lemmas);
        }
        return result;
    }

    public static List<String> processOneWord(String word) {
        List<String> result = new ArrayList<>();
        List<String> infos = new ArrayList<>();
        word = word.toLowerCase(Locale.ROOT);
        if (word.matches("[а-яё]+")) {
            word = word.replaceAll("ё", "е");
            try {
                infos = russianMorphology.getMorphInfo(word);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (word.matches("[a-z]+")) {
            try {
                infos = englishMorphology.getMorphInfo(word);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (String info : infos) {
            String lemma = morphInfoToLemma(info);
            if (!lemma.isEmpty())
                result.add(lemma);
        }
        return result;
    }

    private static String morphInfoToLemma(String morphInfo) {
        int pos = morphInfo.indexOf('|');
        if (pos < 0) {
            return "";
        }
        String wordType = String.valueOf(morphInfo.charAt(pos + 1));
        if (wordType.matches("[nfoklp]")) {
            return "";
        }
        return morphInfo.substring(0, pos);
    }
}
