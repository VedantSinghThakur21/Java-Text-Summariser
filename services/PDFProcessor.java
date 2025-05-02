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

}
