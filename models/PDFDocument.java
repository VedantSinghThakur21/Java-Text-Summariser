package models;

public class PDFDocument {
    private String fileName;  
    private String content;

    public PDFDocument() {}
    public PDFDocument(String fileName, String content) {
        this.fileName = fileName;
        this.content = content;
    }
    public String getFileName() {
        return fileName;
    }