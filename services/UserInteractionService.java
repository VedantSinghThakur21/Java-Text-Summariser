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
    // Core controller class for SmartPDFChatBot
    private final Scanner scanner = new Scanner(System.in);     // Scanner to capture user input
    private final PDFProcessor processor = new PDFProcessor();     // Responsible for reading PDF content
    private final ChatService chatService = new ChatService();     // Handles AI question answering
    private final DBService dbService = new DBService();        // Handles history persistence
    private final PDFService pdfService = new PDFService();     // Summarization engine
    private PDFDocument currentDoc = new PDFDocument();     // Current PDF document instance
    private String cachedPDFContent = "";       // Stores the content of the loaded PDF

    // Starts the chatbot's user interface loop
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

    // Handles the PDF upload operation
    private void handlePDFUpload() {
        System.out.print("Enter PDF file path: ");
        String path = scanner.nextLine();
        currentDoc.setFilePath(path);

        try {
            processor.readPDF(path);
            cachedPDFContent = processor.getPdfText();      // Read the content of the PDF
            currentDoc.setContent(cachedPDFContent);        // Cache the extracted text
            dbService.savePDFContent(new File(path).getName(), cachedPDFContent);       // Save to DB
            System.out.println("✅ PDF loaded successfully.");
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // Handles user question input and response generation
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

        // If question is a summary request, route accordingly
        if (isSummaryRequest(question)) {
            handleSummaryRequest(question);
            return;
        }

        try {
            // Check if answer exists in saved history
            String answer = dbService.getPreviousAnswer(question);
            if (answer == null || answer.isEmpty()) {
                // Generate new answer using AI
                answer = chatService.getAnswer(question, cachedPDFContent);
                dbService.saveQuestionAnswer(question, answer);     // Save Q&A for future reference
            }
            System.out.println("\nAnswer:\n" + answer);
        } catch (AnswerNotFoundException e) {
            System.out.println("🤖 Sorry, I couldn’t find a relevant answer.");
        } catch (Exception e) {
            System.out.println("❌ Error generating answer: " + e.getMessage());
        }
    }

    // Handles summarization when selected from the main menu
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
                points = Integer.parseInt(input);       // Parse number of summary points
            }
        } catch (NumberFormatException e) {
            System.out.println("⚠️ Invalid number. Using default 5.");
        }

        // Display the summary
        List<String> summary = pdfService.summarizeText(cachedPDFContent, points);
        System.out.println("\n📄 Summary:");
        summary.forEach(s -> System.out.println("• " + s));
    }

    // Determines whether the user question is a summary-related request
    private boolean isSummaryRequest(String question) {
        String q = question.toLowerCase();
        return q.contains("summarize") || q.contains("summary");
    }

    // Handles automatic summary requests from user questions like "Summarize in 3 points"
    private void handleSummaryRequest(String question) {
        int points = 5;
        Matcher matcher = Pattern.compile("(\\d+)\\s*(points|bullets|lines)?").matcher(question);
        if (matcher.find()) {
            try {
                points = Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException ignored) {}
        }

        // Display the generated summary
        List<String> summary = pdfService.summarizeText(cachedPDFContent, points);
        System.out.println("\n📄 Summary in " + points + " points:");
        summary.forEach(s -> System.out.println("• " + s));

        // Save Q&A as a summary response
        dbService.saveQuestionAnswer(question, String.join(" ", summary));
    }
}