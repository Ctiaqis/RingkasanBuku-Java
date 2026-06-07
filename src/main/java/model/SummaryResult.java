package main.java.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Model class yang menyimpan hasil ringkasan buku.
 * Menerapkan konsep ENCAPSULATION dengan private fields dan public accessor.
 *
 * @author Mahasiswa PBO
 * @version 1.0.0
 */
public class SummaryResult {

    // ==================== ENCAPSULATION: Private Fields ====================
    private String originalText;
    private String summaryText;
    private String method;        // "Rule-based" atau "API-based"
    private String summaryType;   // "Ringkasan Pendek", "Ringkasan Detail"
    private int originalCharCount;
    private int summaryCharCount;
    private int originalSentenceCount;
    private int summarySentenceCount;
    private LocalDateTime generatedAt;
    private String bookTitle;
    private String bookSource;
    private double compressionRatio; // Rasio kompresi (%)

    // ==================== Constructor ====================

    /**
     * Constructor default.
     */
    public SummaryResult() {
        this.generatedAt = LocalDateTime.now();
        this.compressionRatio = 0.0;
    }

    /**
     * Constructor lengkap.
     *
     * @param originalText Teks asli
     * @param summaryText  Teks ringkasan
     * @param method       Metode ringkasan
     * @param summaryType  Tipe ringkasan
     * @param bookTitle    Judul buku
     * @param bookSource   Sumber file buku
     */
    public SummaryResult(String originalText, String summaryText,
                         String method, String summaryType,
                         String bookTitle, String bookSource) {
        this.originalText = originalText;
        this.summaryText = summaryText;
        this.method = method;
        this.summaryType = summaryType;
        this.bookTitle = bookTitle;
        this.bookSource = bookSource;
        this.generatedAt = LocalDateTime.now();
        calculateStats();
    }

    // ==================== Private Helper Methods ====================

    /**
     * Menghitung statistik ringkasan secara otomatis.
     */
    private void calculateStats() {
        this.originalCharCount = (originalText != null) ? originalText.length() : 0;
        this.summaryCharCount = (summaryText != null) ? summaryText.length() : 0;
        this.originalSentenceCount = countSentences(originalText);
        this.summarySentenceCount = countSentences(summaryText);

        if (originalCharCount > 0) {
            this.compressionRatio = ((double) summaryCharCount / originalCharCount) * 100.0;
        }
    }

    private int countSentences(String text) {
        if (text == null || text.trim().isEmpty()) return 0;
        String[] sentences = text.split("[.!?]+\\s*");
        int count = 0;
        for (String s : sentences) {
            if (!s.trim().isEmpty()) count++;
        }
        return count;
    }

    // ==================== Getter Methods ====================

    public String getOriginalText() { return originalText; }
    public String getSummaryText() { return summaryText; }
    public String getMethod() { return method; }
    public String getSummaryType() { return summaryType; }
    public int getOriginalCharCount() { return originalCharCount; }
    public int getSummaryCharCount() { return summaryCharCount; }
    public int getOriginalSentenceCount() { return originalSentenceCount; }
    public int getSummarySentenceCount() { return summarySentenceCount; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public String getBookTitle() { return bookTitle; }
    public String getBookSource() { return bookSource; }
    public double getCompressionRatio() { return compressionRatio; }

    /**
     * Mengembalikan tanggal dan waktu dalam format yang mudah dibaca.
     *
     * @return String tanggal dan waktu
     */
    public String getFormattedDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return generatedAt.format(formatter);
    }

    // ==================== Setter Methods ====================

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
        calculateStats();
    }

    public void setSummaryText(String summaryText) {
        this.summaryText = summaryText;
        calculateStats();
    }

    public void setMethod(String method) { this.method = method; }
    public void setSummaryType(String summaryType) { this.summaryType = summaryType; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    public void setBookSource(String bookSource) { this.bookSource = bookSource; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    @Override
    public String toString() {
        return "SummaryResult{" +
                "method='" + method + '\'' +
                ", summaryType='" + summaryType + '\'' +
                ", originalChars=" + originalCharCount +
                ", summaryChars=" + summaryCharCount +
                ", compressionRatio=" + String.format("%.1f", compressionRatio) + "%" +
                ", generatedAt=" + getFormattedDateTime() +
                '}';
    }
}