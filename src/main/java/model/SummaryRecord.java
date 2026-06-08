package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Model class untuk menyimpan data satu sesi ringkasan.
 *
 * Konsep OOP: Encapsulation
 * - Semua field dideklarasikan private
 * - Akses hanya melalui getter dan setter
 * - Validasi dapat ditambahkan di dalam setter
 */
public class SummaryRecord {

    // Encapsulation: semua field private
    private String id;
    private String bookTitle;
    private LocalDateTime createdAt;
    private String quality;
    private String methodUsed;
    private String summaryText;

    /**
     * Constructor utama — digunakan saat membuat record baru.
     */
    public SummaryRecord(String bookTitle, String quality,
                         String methodUsed, String summaryText) {
        this.id = UUID.randomUUID().toString();
        this.bookTitle = bookTitle;
        this.createdAt = LocalDateTime.now();
        this.quality = quality;
        this.methodUsed = methodUsed;
        this.summaryText = summaryText;
    }

    /**
     * Constructor dengan semua parameter — digunakan saat load dari file.
     */
    public SummaryRecord(String id, String bookTitle, LocalDateTime createdAt,
                         String quality, String methodUsed, String summaryText) {
        this.id = id;
        this.bookTitle = bookTitle;
        this.createdAt = createdAt;
        this.quality = quality;
        this.methodUsed = methodUsed;
        this.summaryText = summaryText;
    }

    /**
     * Mengembalikan tanggal dalam format yang mudah dibaca.
     */
    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return createdAt.format(formatter);
    }

    // === Getter (Encapsulation) ===

    public String getId() {
        return id;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getQuality() {
        return quality;
    }

    public String getMethodUsed() {
        return methodUsed;
    }

    public String getSummaryText() {
        return summaryText;
    }

    // === Setter (Encapsulation) ===

    public void setBookTitle(String bookTitle) {
        if (bookTitle != null && !bookTitle.trim().isEmpty()) {
            this.bookTitle = bookTitle;
        }
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public void setMethodUsed(String methodUsed) {
        this.methodUsed = methodUsed;
    }

    public void setSummaryText(String summaryText) {
        this.summaryText = summaryText;
    }

    @Override
    public String toString() {
        return "SummaryRecord{" +
               "id='" + id + '\'' +
               ", bookTitle='" + bookTitle + '\'' +
               ", createdAt=" + getFormattedDate() +
               ", quality='" + quality + '\'' +
               ", methodUsed='" + methodUsed + '\'' +
               '}';
    }

    /**
     * Mengembalikan representasi JSON dari record ini.
     */
    public String toJson() {
        return "{\n" +
               "  \"id\": \"" + escapeJson(id) + "\",\n" +
               "  \"bookTitle\": \"" + escapeJson(bookTitle) + "\",\n" +
               "  \"createdAt\": \"" + createdAt.toString() + "\",\n" +
               "  \"quality\": \"" + escapeJson(quality) + "\",\n" +
               "  \"methodUsed\": \"" + escapeJson(methodUsed) + "\",\n" +
               "  \"summaryText\": \"" + escapeJson(summaryText) + "\"\n" +
               "}";
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}