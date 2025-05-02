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
}