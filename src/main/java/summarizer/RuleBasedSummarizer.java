package summarizer;

import util.StopWordFilter;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Implementasi Summarizer berbasis aturan (Word Frequency Based).
 *
 * Konsep OOP:
 * - Implements interface Summarizer (Interface + Polymorphism)
 * - Encapsulation: field private, akses via method
 *
 * Algoritma:
 * 1. Pisahkan teks menjadi kalimat-kalimat
 * 2. Preprocessing teks (lowercase, hapus karakter khusus)
 * 3. Hapus stop words
 * 4. Hitung frekuensi kata dengan HashMap
 * 5. Beri skor pada setiap kalimat
 * 6. Pilih kalimat dengan skor tertinggi
 */
public class RuleBasedSummarizer implements Summarizer {

    // Encapsulation: field private
    private final StopWordFilter stopWordFilter;

    private static final Pattern SENTENCE_SPLIT = Pattern.compile("(?<=[.!?])\\s+");
    private static final Pattern NON_ALPHA = Pattern.compile("[^a-zA-Z0-9\\s]");

    public RuleBasedSummarizer() {
        this.stopWordFilter = new StopWordFilter();
    }

    /**
     * Merangkum teks menggunakan metode frekuensi kata.
     */
    @Override
    public String summarize(String text, model.SummaryLength length) throws SummarizerException {
        try {
            if (text == null || text.trim().isEmpty()) {
                throw new IllegalArgumentException("Teks tidak boleh kosong.");
            }
            
            int maxSentences = (length == model.SummaryLength.PENDEK) ? 2 : 4;

        // Langkah 1: Pisahkan menjadi kalimat
        String[] sentences = splitIntoSentences(text);
        if (sentences.length <= maxSentences) {
            return text; // teks sudah singkat, kembalikan apa adanya
        }

        // Langkah 2 & 3: Preprocessing + hapus stop words
        // Langkah 4: Hitung frekuensi kata dengan HashMap
        Map<String, Integer> wordFrequency = calculateWordFrequency(text);

        // Langkah 5: Beri skor setiap kalimat
        Map<Integer, Double> sentenceScores = scoreSentences(sentences, wordFrequency);

        // Langkah 6: Pilih kalimat dengan skor tertinggi
        return buildSummary(sentences, sentenceScores, maxSentences);
        } catch (Exception e) {
            throw new SummarizerException("Gagal merangkum dengan Rule-Based", e);
        }
    }

    /**
     * Memisahkan teks menjadi array kalimat.
     */
    private String[] splitIntoSentences(String text) {
        String[] raw = SENTENCE_SPLIT.split(text.trim());
        List<String> sentences = new ArrayList<>();
        for (String s : raw) {
            String trimmed = s.trim();
            if (!trimmed.isEmpty()) {
                sentences.add(trimmed);
            }
        }
        return sentences.toArray(new String[0]);
    }

    /**
     * Menghitung frekuensi setiap kata menggunakan HashMap.
     * Stop words dihapus terlebih dahulu.
     */
    private Map<String, Integer> calculateWordFrequency(String text) {
        Map<String, Integer> frequency = new HashMap<>();

        // Preprocessing: lowercase, hapus karakter non-alfanumerik
        String cleaned = NON_ALPHA.matcher(text.toLowerCase()).replaceAll(" ");

        // Hapus stop words dan hitung frekuensi
        String[] words = cleaned.split("\\s+");
        for (String word : words) {
            if (word.length() > 2 && !stopWordFilter.isStopWord(word)) {
                frequency.put(word, frequency.getOrDefault(word, 0) + 1);
            }
        }
        return frequency;
    }

    /**
     * Memberikan skor pada setiap kalimat berdasarkan frekuensi kata.
     */
    private Map<Integer, Double> scoreSentences(String[] sentences,
                                                 Map<String, Integer> wordFrequency) {
        Map<Integer, Double> scores = new HashMap<>();

        for (int i = 0; i < sentences.length; i++) {
            String cleaned = NON_ALPHA.matcher(sentences[i].toLowerCase()).replaceAll(" ");
            String[] words = cleaned.split("\\s+");

            double score = 0.0;
            int wordCount = 0;

            for (String word : words) {
                if (wordFrequency.containsKey(word)) {
                    score += wordFrequency.get(word);
                    wordCount++;
                }
            }

            // Normalisasi skor berdasarkan panjang kalimat (hindari bias kalimat panjang)
            if (wordCount > 0) {
                scores.put(i, score / wordCount);
            } else {
                scores.put(i, 0.0);
            }
        }
        return scores;
    }

    /**
     * Membangun teks ringkasan dari kalimat-kalimat dengan skor tertinggi.
     * Urutan kalimat dipertahankan sesuai teks asli.
     */
    private String buildSummary(String[] sentences, Map<Integer, Double> sentenceScores, int maxSentences) {
        // Urutkan indeks kalimat berdasarkan skor (tertinggi dulu)
        List<Integer> sortedIndices = new ArrayList<>(sentenceScores.keySet());
        sortedIndices.sort((a, b) -> Double.compare(sentenceScores.get(b), sentenceScores.get(a)));

        // Ambil top N kalimat
        int count = Math.min(maxSentences, sortedIndices.size());
        List<Integer> selectedIndices = sortedIndices.subList(0, count);

        // Kembalikan ke urutan asli teks
        Collections.sort(selectedIndices);

        StringBuilder summary = new StringBuilder();
        for (int idx : selectedIndices) {
            if (summary.length() > 0) {
                summary.append(" ");
            }
            summary.append(sentences[idx]);
        }
        return summary.toString();
    }

    @Override
    public String getMethodName() {
        return "Rule-Based Summarizer";
    }

    // dihapus: get/setMaxSentences
}