package model;

import java.util.List;

/**
 * Model class untuk menyimpan statistik hasil ringkasan.
 *
 * Konsep OOP: Encapsulation
 * - Semua field private
 * - Akses melalui getter
 * - Field kompresi dihitung otomatis saat objek dibuat
 */
public class SummaryStatistics {

    // Encapsulation: semua field private
    private int originalWordCount;
    private int summaryWordCount;
    private double compressionRate;
    private List<String> keywords;

    public SummaryStatistics(int originalWordCount, int summaryWordCount,
                              List<String> keywords) {
        this.originalWordCount = originalWordCount;
        this.summaryWordCount = summaryWordCount;
        this.keywords = keywords;

        // Hitung persentase kompresi secara otomatis
        if (originalWordCount > 0) {
            this.compressionRate = ((double)(originalWordCount - summaryWordCount)
                                    / originalWordCount) * 100.0;
        } else {
            this.compressionRate = 0.0;
        }
    }

    /**
     * Mengembalikan statistik dalam format teks siap tampil.
     */
    public String getFormattedStats() {
        return String.format(
            "Jumlah kata awal    : %,d\n" +
            "Jumlah kata ringkasan : %,d\n" +
            "Persentase kompresi : %.0f%%",
            originalWordCount, summaryWordCount, compressionRate
        );
    }

    /**
     * Mengembalikan keyword dalam format bullet list.
     */
    public String getFormattedKeywords() {
        if (keywords == null || keywords.isEmpty()) {
            return "Tidak ada keyword ditemukan.";
        }
        StringBuilder sb = new StringBuilder();
        for (String kw : keywords) {
            sb.append("• ").append(kw).append("\n");
        }
        return sb.toString().trim();
    }

    // === Getter (Encapsulation) ===

    public int getOriginalWordCount() {
        return originalWordCount;
    }

    public int getSummaryWordCount() {
        return summaryWordCount;
    }

    public double getCompressionRate() {
        return compressionRate;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    // === Setter (Encapsulation) ===

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
}