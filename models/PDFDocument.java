package models;

public class PDFDocument {
    private String fileName;  // Name of the file (also used as the file path)
    private String content;   // Content of the PDF document

    public PDFDocument() {} // Default constructor
    public PDFDocument(String fileName, String content) {  // Constructor with parameters
        this.fileName = fileName;
        this.content = content;
    }
    public String getFileName() {  // Returns the file name
        return fileName;
    }
    public String getFilePath() {  // Returns the file path (same as file name in this case)
        return fileName;
    }

    public String getContent() {  // Returns the content of the PDF document
        return content;
    }

    public void setFileName(String fileName) {  // Sets the file name
        this.fileName = fileName;
    }

    public void setFilePath(String filePath) {  // Sets the file path (same as file name in this case)
        this.fileName = filePath;
    }
    public void setContent(String content) {  // Sets the content of the PDF document
        this.content = content;
    }
}