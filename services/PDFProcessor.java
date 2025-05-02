package services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class PDFProcessor {
    private String pdfText;
    private String fileName;


    public void readPDF(String filePath) throws IOException {
        File file = new File(filePath);
        this.fileName = file.getName();

        try (PDDocument document = PDDocument.load(file)) {
            if (document.isEncrypted()) {
                throw new IOException("Encrypted PDFs are not supported.");
            }
            PDFTextStripper stripper = new PDFTextStripper();
            this.pdfText = stripper.getText(document).trim();
        }
    }

    public String getSummary() {
        return getSummary(5);
    }

    // Generate smart summary of the PDF
    public String getSummary(int numSentences) {
        if (pdfText == null || pdfText.isEmpty()) {
            return "No PDF content available to summarize.";
        }

        List<String> sentences = Arrays.stream(pdfText.split("(?<=[.!?])\\s+"))
                .map(String::trim)
                .filter(s -> s.length() > 30 && s.split(" ").length >= 5)
                .distinct()
                .collect(Collectors.toList());

        if (sentences.size() < numSentences) {
            return String.join(" ", sentences);
        }

        // Word frequency map
        Map<String, Integer> wordFreq = new HashMap<>();
        for (String word : pdfText.toLowerCase().split("\\W+")) {
            if (word.length() > 2) {
                wordFreq.put(word, wordFreq.getOrDefault(word, 0) + 1);
            }
        }

        // Score each sentence
        Map<String, Integer> sentenceScores = new HashMap<>();
        for (String sentence : sentences) {
            int score = 0;
            for (String word : sentence.toLowerCase().split("\\W+")) {
                score += wordFreq.getOrDefault(word, 0);
            }
            sentenceScores.put(sentence, score);
        }

        return sentenceScores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(numSentences)
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(" "));

    }

}
