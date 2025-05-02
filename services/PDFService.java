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
    public void extractText(PDFDocument pdfDoc) throws InvalidPDFException, EmptyPDFException {
        try (PDDocument document = PDDocument.load(new File(pdfDoc.getFilePath()))) {

            if (document.isEncrypted()) {
                throw new InvalidPDFException("Encrypted PDFs are not supported.");
            }

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            if (text.trim().isEmpty()) {
                throw new EmptyPDFException("PDF has no readable content.");
            }

            pdfDoc.setContent(text);
        } catch (IOException e) {
            throw new InvalidPDFException("Unable to process PDF: " + e.getMessage());
        }
    }

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



}
