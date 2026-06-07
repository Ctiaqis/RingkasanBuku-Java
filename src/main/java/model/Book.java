package main.java.model;

/**
 * Model class yang merepresentasikan sebuah Buku.
 * Menerapkan konsep ENCAPSULATION dengan private fields dan public getter/setter.
 *
 * @author Mahasiswa PBO
 * @version 1.0.0
 */
public class Book {

    // ==================== ENCAPSULATION: Private Fields ====================
    private String title;
    private String author;
    private String content;
    private String sourceType; // "MANUAL", "TXT", "PDF"
    private String filePath;
    private int characterCount;
    private int sentenceCount;

    // ==================== Constructor ====================

    /**
     * Constructor default.
     */
    public Book() {
        this.title = "Tanpa Judul";
        this.author = "Tidak Diketahui";
        this.content = "";
        this.sourceType = "MANUAL";
        this.filePath = "";
    }

    /**
     * Constructor dengan parameter lengkap.
     * OVERLOADING: Constructor dengan parameter berbeda.
     *
     * @param title    Judul buku
     * @param author   Penulis buku
     * @param content  Isi konten buku
     * @param sourceType Tipe sumber input
     */
    public Book(String title, String author, String content, String sourceType) {
        this.title = title;
        this.author = author;
        setContent(content); // Gunakan setter untuk update hitungan
        this.sourceType = sourceType;
        this.filePath = "";
    }

    /**
     * Constructor dari file.
     * OVERLOADING: Constructor dengan parameter berbeda.
     *
     * @param title    Judul buku
     * @param content  Isi konten buku
     * @param sourceType Tipe sumber input
     * @param filePath Path file sumber
     */
    public Book(String title, String content, String sourceType, String filePath) {
        this.title = title;
        this.author = "Tidak Diketahui";
        setContent(content);
        this.sourceType = sourceType;
        this.filePath = filePath;
    }

    // ==================== ENCAPSULATION: Getter Methods ====================

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getSourceType() {
        return sourceType;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getCharacterCount() {
        return characterCount;
    }

    public int getSentenceCount() {
        return sentenceCount;
    }

    // ==================== ENCAPSULATION: Setter Methods ====================

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            this.title = "Tanpa Judul";
        } else {
            this.title = title.trim();
        }
    }

    public void setAuthor(String author) {
        this.author = (author != null && !author.trim().isEmpty()) ? author.trim() : "Tidak Diketahui";
    }

    /**
     * Setter konten yang otomatis menghitung jumlah karakter dan kalimat.
     * Menerapkan ENCAPSULATION dengan logika validasi di dalam setter.
     *
     * @param content Isi teks buku
     */
    public void setContent(String content) {
        this.content = (content != null) ? content : "";
        this.characterCount = this.content.length();
        this.sentenceCount = countSentences(this.content);
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public void setFilePath(String filePath) {
        this.filePath = (filePath != null) ? filePath : "";
    }

    // ==================== Private Helper Methods ====================

    /**
     * Menghitung jumlah kalimat dalam teks.
     *
     * @param text Teks yang akan dihitung
     * @return Jumlah kalimat
     */
    private int countSentences(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        String[] sentences = text.split("[.!?]+\\s*");
        int count = 0;
        for (String sentence : sentences) {
            if (!sentence.trim().isEmpty()) {
                count++;
            }
        }
        return count;
    }

    // ==================== Override toString ====================

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", sourceType='" + sourceType + '\'' +
                ", characterCount=" + characterCount +
                ", sentenceCount=" + sentenceCount +
                '}';
    }
}