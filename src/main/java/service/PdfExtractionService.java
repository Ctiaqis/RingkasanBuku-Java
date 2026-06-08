package service;

import org.apache.pdfbox.Loader;
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
        validatePdfFile(pdfFile);

        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            if (document.isEncrypted()) {
                throw new IOException("File PDF dienkripsi dan tidak dapat dibaca.");
            }

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            if (text == null || text.trim().isEmpty()) {
                throw new IOException(
                        "Tidak ada teks yang dapat diekstrak dari PDF ini.\n" +
                                "(PDF mungkin berisi gambar/scan saja)");
            }

            return text.trim();
        }
    }

    /**
     * Mengekstrak teks dari rentang halaman tertentu.
     *
     * @param pdfFile   file PDF
     * @param startPage halaman awal (1-indexed)
     * @param endPage   halaman akhir (inklusif)
     * @return teks dari rentang halaman yang ditentukan
     */
    public String extractTextFromPages(File pdfFile, int startPage, int endPage)
            throws IOException {
        validatePdfFile(pdfFile);

        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            int pageCount = document.getNumberOfPages();

            if (startPage < 1 || endPage < startPage || endPage > pageCount) {
                throw new IOException(
                        "Rentang halaman tidak valid. PDF memiliki " + pageCount + " halaman.");
            }

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(startPage);
            stripper.setEndPage(endPage);

            String text = stripper.getText(document);

            if (text == null || text.trim().isEmpty()) {
                throw new IOException("Tidak ada teks yang dapat diekstrak dari halaman tersebut.");
            }

            return text.trim();
        }
    }

    /**
     * Menghitung jumlah halaman dalam file PDF.
     */
    public int getPageCount(File pdfFile) throws IOException {
        validatePdfFile(pdfFile);

        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            return document.getNumberOfPages();
        }
    }

    /**
     * Validasi dasar file PDF.
     */
    private void validatePdfFile(File pdfFile) throws IOException {
        if (pdfFile == null || !pdfFile.exists()) {
            throw new IOException("File PDF tidak ditemukan.");
        }

        if (!pdfFile.isFile()) {
            throw new IOException("Path yang dipilih bukan file.");
        }

        if (!pdfFile.getName().toLowerCase().endsWith(".pdf")) {
            throw new IOException("File bukan berformat PDF.");
        }
    }
}