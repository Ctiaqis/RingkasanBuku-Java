package service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

/**
 * Service untuk mengekstrak teks dari file PDF menggunakan Apache PDFBox.
 */
public class PdfExtractionService {

    /**
     * Mengekstrak seluruh teks dari file PDF.
     *
     * @param pdfFile file PDF yang akan diekstrak
     * @return teks hasil ekstraksi
     * @throws IOException jika file tidak dapat dibaca
     */
    public String extractText(File pdfFile) throws IOException {
        if (pdfFile == null || !pdfFile.exists()) {
            throw new IOException("File PDF tidak ditemukan.");
        }
        if (!pdfFile.getName().toLowerCase().endsWith(".pdf")) {
            throw new IOException("File bukan berformat PDF.");
        }

        try (PDDocument document = PDDocument.load(pdfFile)) {
            if (document.isEncrypted()) {
                throw new IOException("File PDF dienkripsi dan tidak dapat dibaca.");
            }

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            if (text == null || text.trim().isEmpty()) {
                throw new IOException("Tidak ada teks yang dapat diekstrak dari PDF ini.\n" +
                                      "(PDF mungkin berisi gambar/scan saja)");
            }
            return text.trim();
        }
    }

    /**
     * Mengekstrak teks dari rentang halaman tertentu.
     *
     * @param pdfFile file PDF
     * @param startPage halaman awal (1-indexed)
     * @param endPage   halaman akhir (inklusif)
     * @return teks dari rentang halaman yang ditentukan
     */
    public String extractTextFromPages(File pdfFile, int startPage, int endPage)
            throws IOException {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(startPage);
            stripper.setEndPage(endPage);
            return stripper.getText(document).trim();
        }
    }

    /**
     * Menghitung jumlah halaman dalam file PDF.
     */
    public int getPageCount(File pdfFile) throws IOException {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            return document.getNumberOfPages();
        }
    }
}