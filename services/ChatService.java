package services;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import exceptions.AnswerNotFoundException;


public class ChatService {
    private List<String> tokenize(String text) {
        if (text == null) throw new NullPointerException("Text cannot be null.");
        return Arrays.stream(text.toLowerCase()
                        .replaceAll("[^a-zA-Z0-9 ]", " ")
                        .split("\\s+"))
                .filter(w -> w.length() > 2)
                .collect(Collectors.toList());
    }

    private Map<String, Integer> getTermFrequency(List<String> words) {
        Map<String, Integer> freqMap = new HashMap<>();
        for (String word : words) {
            freqMap.put(word, freqMap.getOrDefault(word, 0) + 1);
        }
        return freqMap;
    }

    private Set<String> buildVocabulary(List<Map<String, Integer>> docs) {
        Set<String> vocab = new HashSet<>();
        for (Map<String, Integer> doc : docs) {
            vocab.addAll(doc.keySet());
        }
        return vocab;
    }

    private double[] buildTFVector(Map<String, Integer> tf, Set<String> vocab) {
        double[] vector = new double[vocab.size()];
        int i = 0;
        for (String word : vocab) {
            vector[i++] = tf.getOrDefault(word, 0);
        }
        return vector;
    }

    private double cosineSimilarity(double[] vec1, double[] vec2) {
        if (vec1.length != vec2.length) {
            throw new IllegalArgumentException("Vector lengths must match.");
        }
        double dot = 0.0, normA = 0.0, normB = 0.0;
        for (int i = 0; i < vec1.length; i++) {
            dot += vec1[i] * vec2[i];
            normA += vec1[i] * vec1[i];
            normB += vec2[i] * vec2[i];
        }
        return (normA == 0 || normB == 0) ? 0.0 : dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
