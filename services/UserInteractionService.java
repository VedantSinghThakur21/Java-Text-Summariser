package services;

import exceptions.AnswerNotFoundException;
import exceptions.EmptyPDFException;
import exceptions.InvalidPDFException;
import models.PDFDocument;

import java.io.File;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;

public class UserInteractionService {
    private final Scanner scanner = new Scanner(System.in);
    private final PDFProcessor processor = new PDFProcessor();
    private final ChatService chatService = new ChatService();
    private final DBService dbService = new DBService();
    private final PDFService pdfService = new PDFService();
    private PDFDocument currentDoc = new PDFDocument();
    private String cachedPDFContent = "";

    public void start() {
        boolean running = true;

        System.out.println("📘 Welcome to SmartPDFChatBot!");

        while (running) {
            System.out.println("\n==== Main Menu ====");
            System.out.println("1. Upload PDF file");
            System.out.println("2. Ask a question");
            System.out.println("3. Show summary");
            System.out.println("4. View saved history");
            System.out.println("5. Delete history");
            System.out.println("6. Exit");
            System.out.print("Choose an option (1-6): ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1": handlePDFUpload(); break;
                case "2": handleQuestion(); break;
                case "3": handleSummary(); break;
                case "4": dbService.viewHistory(); break;
                case "5": dbService.deleteHistory(); break;
                case "6":
                    running = false;
                    System.out.println("👋 Thank you for using SmartPDFChatBot. Goodbye!");
                    break;
                default:
                    System.out.println("❗ Invalid choice. Please enter a number between 1 and 6.");
            }
        }

        scanner.close();
    }

    private void handlePDFUpload() {
        System.out.print("Enter PDF file path: ");
        String path = scanner.nextLine();
        currentDoc.setFilePath(path);

        try {
            processor.readPDF(path);
            cachedPDFContent = processor.getPdfText();
            currentDoc.setContent(cachedPDFContent);
            dbService.savePDFContent(new File(path).getName(), cachedPDFContent);
            System.out.println("✅ PDF loaded successfully.");
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private void handleQuestion() {
        if (cachedPDFContent == null || cachedPDFContent.isEmpty()) {
            System.out.println("⚠️ Please upload a PDF first.");
            return;
        }

        System.out.print("Ask a question: ");
        String question = scanner.nextLine();

        if (question.trim().isEmpty()) {
            System.out.println("⚠️ Please ask something meaningful.");
            return;
        }

        if (isSummaryRequest(question)) {
            handleSummaryRequest(question);
            return;
        }

        try {
            String answer = dbService.getPreviousAnswer(question);
            if (answer == null || answer.isEmpty()) {
                answer = chatService.getAnswer(question, cachedPDFContent);
                dbService.saveQuestionAnswer(question, answer);
            }
            System.out.println("\nAnswer:\n" + answer);
        } catch (AnswerNotFoundException e) {
            System.out.println("🤖 Sorry, I couldn’t find a relevant answer.");
        } catch (Exception e) {
            System.out.println("❌ Error generating answer: " + e.getMessage());
        }
    }

    private void handleSummary() {
        if (cachedPDFContent == null || cachedPDFContent.isEmpty()) {
            System.out.println("⚠️ Please upload a PDF first.");
            return;
        }

        System.out.print("How many summary points? (default 5): ");
        String input = scanner.nextLine().trim();
        int points = 5;
        try {
            if (!input.isEmpty()) {
                points = Integer.parseInt(input);
            }
        } catch (NumberFormatException e) {
            System.out.println("⚠️ Invalid number. Using default 5.");
        }

        List<String> summary = pdfService.summarizeText(cachedPDFContent, points);
        System.out.println("\n📄 Summary:");
        summary.forEach(s -> System.out.println("• " + s));
    }

    private boolean isSummaryRequest(String question) {
        String q = question.toLowerCase();
        return q.contains("summarize") || q.contains("summary");
    }
}