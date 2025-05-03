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
    public String getFilePath() {
        return fileName; 
    }

    public String getContent() {
        return content;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public void setFilePath(String filePath) {
        this.fileName = filePath; 
    }