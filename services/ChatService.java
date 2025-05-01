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

}
