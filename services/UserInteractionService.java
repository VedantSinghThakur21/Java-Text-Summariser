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
}