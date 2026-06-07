package input;

import service.PdfExtractionService;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Handler untuk membaca input teks dari berbagai sumber.
 * Mendukung file TXT dan PDF.
 */
public class TextInputHandler {

    private final PdfExtractionService pdfExtractionService;

    public TextInputHandler() {
        this.pdfExtractionService = new PdfExtractionService();
    }

    /**
     * Membaca teks dari file (TXT atau PDF).
     *
     * @param file file yang akan dibaca
     * @return teks hasil pembacaan
     * @throws IOException jika file tidak dapat dibaca
     */
    public String readFromFile(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("File tidak boleh null.");
        }
        if (!file.exists()) {
            throw new IOException("File tidak ditemukan: " + file.getAbsolutePath());
        }

        String fileName = file.getName().toLowerCase();

        if (fileName.endsWith(".txt")) {
            return readTextFile(file);
        } else if (fileName.endsWith(".pdf")) {
            return pdfExtractionService.extractText(file);
        } else {
            throw new IOException("Format file tidak didukung. Gunakan .txt atau .pdf");
        }
    }

    /**
     * Membaca isi file teks.
     */
    private String readTextFile(File file) throws IOException {
        byte[] bytes = Files.readAllBytes(file.toPath());
        String content = new String(bytes, StandardCharsets.UTF_8).trim();
        if (content.isEmpty()) {
            throw new IOException("File TXT kosong.");
        }
        return content;
    }

    /**
     * Validasi bahwa teks tidak kosong dan memiliki panjang minimum.
     *
     * @param text teks yang akan divalidasi
     * @throws IllegalArgumentException jika teks tidak valid
     */
    public void validateInput(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Teks tidak boleh kosong.\nMasukkan teks atau upload file terlebih dahulu."
            );
        }
        if (text.trim().split("\\s+").length < 20) {
            throw new IllegalArgumentException(
                "Teks terlalu pendek untuk diringkas.\nMinimum 20 kata diperlukan."
            );
        }
    }
}