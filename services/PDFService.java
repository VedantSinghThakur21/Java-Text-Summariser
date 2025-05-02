package services;

import exceptions.EmptyPDFException;
import exceptions.InvalidPDFException;
import models.PDFDocument;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class PDFService {
    // Extracts text content from a given PDF document
    public void extractText(PDFDocument pdfDoc) throws InvalidPDFException, EmptyPDFException {
        try (PDDocument document = PDDocument.load(new File(pdfDoc.getFilePath()))) {

            // Reject encrypted PDFs
            if (document.isEncrypted()) {
                throw new InvalidPDFException("Encrypted PDFs are not supported.");
            }

            // Use PDFTextStripper to extract text
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            // Check if the PDF contains any readable content
            if (text.trim().isEmpty()) {
                throw new EmptyPDFException("PDF has no readable content.");
            }

            // Set extracted content to the PDFDocument object
            pdfDoc.setContent(text);
            // Handle invalid or unreadable PDF files
        } catch (IOException e) {
            throw new InvalidPDFException("Unable to process PDF: " + e.getMessage());
        }
    }

    // Summarizes text by extracting the top scoring sentences based on word frequency
    public List<String> summarizeText(String text, int maxPoints) {

        // Break text into valid sentences
        List<String> sentences = extractSentences(text);

        // Compute word frequency across all sentences
        Map<String, Integer> wordFrequencies = calculateWordFrequencies(sentences);

        // Score each sentence based on word frequencies
        Map<String, Integer> sentenceScores = scoreSentences(sentences, wordFrequencies);

        // Return the top N scoring sentences as the summary
        return sentenceScores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(maxPoints)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // Breaks the input text into meaningful, distinct sentences
    private List<String> extractSentences(String text) {
        return Arrays.stream(text.split("(?<=[.!?])\\s+")) // Split on sentence endings
                .map(String::trim)
                .filter(s -> s.length() > 30 && s.split("\\s+").length >= 5) // Basic filter to skip trivial sentences
                .distinct() // Remove duplicate sentences
                .collect(Collectors.toList());
    }

    // Calculates frequency of each word across all sentences
    private Map<String, Integer> calculateWordFrequencies(List<String> sentences) {
        Map<String, Integer> freq = new HashMap<>();
        for (String sentence : sentences) {
            for (String word : sentence.toLowerCase().split("\\W+")) { // Normalize and split on non-word chars
                if (word.length() > 2) { // Ignore very short words (e.g., 'an', 'to')
                    freq.put(word, freq.getOrDefault(word, 0) + 1);
                }
            }
        }
        return freq;
    }

    // Scores each sentence based on the sum of word frequencies
    private Map<String, Integer> scoreSentences(List<String> sentences, Map<String, Integer> wordFreq) {
        Map<String, Integer> scores = new HashMap<>();
        for (String sentence : sentences) {
            int score = Arrays.stream(sentence.toLowerCase().split("\\W+"))
                    .filter(w -> w.length() > 2)
                    .mapToInt(w -> wordFreq.getOrDefault(w, 0)) // Sum up word frequency values
                    .sum();
            scores.put(sentence, score);
        }
        return scores;
    }



}
