package services;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import exceptions.AnswerNotFoundException;

/**
 * ChatService is responsible for analyzing PDF content and answering
 * user questions using cosine similarity and pattern-based extraction.
 * Supports both regular and definition-style queries.
 */
public class ChatService {
    // Tokenizes text into lowercase, filtered words
    private List<String> tokenize(String text) {
        if (text == null) throw new NullPointerException("Text cannot be null.");
        return Arrays.stream(text.toLowerCase()
                        .replaceAll("[^a-zA-Z0-9 ]", " ")
                        .split("\\s+"))
                .filter(w -> w.length() > 2)
                .collect(Collectors.toList());
    }

    // Computes term frequency of words in a document
    private Map<String, Integer> getTermFrequency(List<String> words) {
        Map<String, Integer> freqMap = new HashMap<>();
        for (String word : words) {
            freqMap.put(word, freqMap.getOrDefault(word, 0) + 1);
        }
        return freqMap;
    }

    // Builds a combined vocabulary from multiple documents
    private Set<String> buildVocabulary(List<Map<String, Integer>> docs) {
        Set<String> vocab = new HashSet<>();
        for (Map<String, Integer> doc : docs) {
            vocab.addAll(doc.keySet());
        }
        return vocab;
    }

    // Converts term frequency map to vector
    private double[] buildTFVector(Map<String, Integer> tf, Set<String> vocab) {
        double[] vector = new double[vocab.size()];
        int i = 0;
        for (String word : vocab) {
            vector[i++] = tf.getOrDefault(word, 0);
        }
        return vector;
    }

    // Calculates cosine similarity between two vectors
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

    // Checks if the question is a definition-style question
    private boolean isDefinitionQuestion(String question) {
        question = question.toLowerCase();
        return question.startsWith("what is") || question.startsWith("define")
                || question.startsWith("explain") || question.contains("definition of")
                || question.startsWith("tell me about") || question.contains("meaning of");
    }

    // Extracts the key term from a question
    private String extractMainKeyword(String question) {
        String[] stopWords = {"what", "is", "define", "the", "a", "an", "of", "about", "explain", "tell", "me", "meaning"};
        List<String> words = Arrays.asList(question.toLowerCase().split("\\s+"));
        return words.stream()
                .filter(word -> !Arrays.asList(stopWords).contains(word))
                .findFirst()
                .orElse(words.get(words.size() - 1));
    }

    /**
     * Answers a question using the content extracted from a PDF.
     * Supports definition-style and similarity-based answering.
     */
    public String getAnswer(String question, String pdfText) throws AnswerNotFoundException {

        if (question == null || pdfText == null) {
            throw new IllegalArgumentException("Question and PDF content must not be null.");
        }

        // Clean and normalize PDF text
        pdfText = pdfText.replaceAll("[\\r\\n]+", "\n").replaceAll("\u2022\\s*", "").trim();

        List<String> sentences = Arrays.stream(pdfText.split("(?<=[.!?])\\s+"))
                .map(String::trim)
                .filter(s -> s.length() > 40 && s.split("\\s+").length >= 6)
                .filter(s -> !s.matches("^[A-Z][a-zA-Z0-9\\s]{0,25}$"))
                .distinct()
                .collect(Collectors.toList());

        if (sentences.isEmpty()) {
            throw new AnswerNotFoundException("No informative content found in the document.");
        }


        // Handle definition-style questions
        if (isDefinitionQuestion(question)) {
            String keyword = extractMainKeyword(question);
            Pattern defPattern = Pattern.compile(
                    "(?i)(?:(?:the|a|an)\\s+)?\\b" + Pattern.quote(keyword) +
                            "\\b\\s+(is|refers to|means|can be defined as)[^.]{10,}?\\.",
                    Pattern.CASE_INSENSITIVE);

            for (String sentence : sentences) {
                Matcher matcher = defPattern.matcher(sentence);
                if (matcher.find()) {
                    return "Here’s what I found:\n\n" + matcher.group().trim();
                }
            }

            for (String sentence : sentences) {
                if (sentence.toLowerCase().contains(keyword.toLowerCase())) {
                    return "Here’s what I found:\n\n" + sentence;
                }
            }
        }

        // Cosine similarity-based matching
        List<Map<String, Integer>> sentenceTFs = sentences.stream()
                .map(this::tokenize)
                .map(this::getTermFrequency)
                .collect(Collectors.toList());

        Map<String, Integer> questionTF = getTermFrequency(tokenize(question));
        Set<String> vocabulary = buildVocabulary(sentenceTFs);
        vocabulary.addAll(questionTF.keySet());

        double[] questionVector = buildTFVector(questionTF, vocabulary);

        Map<String, Double> scoredAnswers = new LinkedHashMap<>();
        for (int i = 0; i < sentences.size(); i++) {
            double[] sentenceVector = buildTFVector(sentenceTFs.get(i), vocabulary);
            double score = cosineSimilarity(questionVector, sentenceVector);
            if (score > 0.15) {
                scoredAnswers.put(sentences.get(i), score);
            }
        }

        if (scoredAnswers.isEmpty()) {
            throw new AnswerNotFoundException("No relevant answer found for your question.");
        }

        List<String> topSentences = scoredAnswers.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(4)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        StringBuilder formatted = new StringBuilder("Here’s what I found:\n\n");
        topSentences.forEach(s -> formatted.append(s).append(" "));
        return formatted.toString().trim();
    }

}