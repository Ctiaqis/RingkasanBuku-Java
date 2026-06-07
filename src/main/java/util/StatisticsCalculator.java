package util;

import model.SummaryStatistics;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility class untuk menghitung statistik ringkasan dan mengekstrak keyword.
 */
public class StatisticsCalculator {

    private final StopWordFilter stopWordFilter;
    private static final Pattern NON_ALPHA = Pattern.compile("[^a-zA-Z0-9\\s]");
    private static final int TOP_KEYWORDS = 5;

    public StatisticsCalculator() {
        this.stopWordFilter = new StopWordFilter();
    }

    public StatisticsCalculator(StopWordFilter stopWordFilter) {
        this.stopWordFilter = stopWordFilter;
    }

    /**
     * Menghitung statistik lengkap dari teks asli dan hasil ringkasan.
     *
     * @param originalText  teks asli sebelum diringkas
     * @param summaryText   teks hasil ringkasan
     * @return objek SummaryStatistics berisi semua statistik
     */
    public SummaryStatistics calculate(String originalText, String summaryText) {
        int originalCount = countWords(originalText);
        int summaryCount  = countWords(summaryText);
        List<String> keywords = extractKeywords(originalText);

        return new SummaryStatistics(originalCount, summaryCount, keywords);
    }

    /**
     * Menghitung jumlah kata dalam teks.
     */
    public int countWords(String text) {
        if (text == null || text.trim().isEmpty()) return 0;
        return text.trim().split("\\s+").length;
    }

    /**
     * Mengekstrak top N keyword berdasarkan frekuensi kata.
     * Stop words dan kata pendek diabaikan.
     */
    public List<String> extractKeywords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }

        // Preprocessing
        String cleaned = NON_ALPHA.matcher(text.toLowerCase()).replaceAll(" ");
        String[] words = cleaned.split("\\s+");

        // Hitung frekuensi, abaikan stop words dan kata < 3 karakter
        Map<String, Integer> frequency = new HashMap<>();
        for (String word : words) {
            if (word.length() >= 3 && !stopWordFilter.isStopWord(word)) {
                frequency.put(word, frequency.getOrDefault(word, 0) + 1);
            }
        }

        // Urutkan berdasarkan frekuensi (tertinggi dulu) dan ambil top N
        return frequency.entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .limit(TOP_KEYWORDS)
            .map(e -> capitalize(e.getKey()))
            .collect(Collectors.toList());
    }

    /**
     * Kapitalisasi huruf pertama kata.
     */
    private String capitalize(String word) {
        if (word == null || word.isEmpty()) return word;
        return Character.toUpperCase(word.charAt(0)) + word.substring(1);
    }
}