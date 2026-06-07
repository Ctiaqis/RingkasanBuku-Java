package util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Filter untuk menghapus stop words dari teks.
 * Mendukung stop words Bahasa Indonesia dan Bahasa Inggris.
 */
public class StopWordFilter {

    private final Set<String> stopWords;

    public StopWordFilter() {
        stopWords = new HashSet<>();
        loadStopWords();
    }

    private void loadStopWords() {
        // Stop words Bahasa Indonesia
        String[] indonesian = {
            "yang", "dan", "di", "ke", "dari", "ini", "itu", "dengan",
            "untuk", "pada", "adalah", "dalam", "tidak", "akan", "ada",
            "juga", "karena", "sehingga", "oleh", "atau", "tetapi",
            "sedang", "telah", "sudah", "jika", "bila", "maka", "saja",
            "lagi", "masih", "seperti", "namun", "bahwa", "saat", "ketika",
            "agar", "supaya", "dapat", "harus", "perlu", "lebih", "sangat",
            "bisa", "pun", "pun", "hal", "cara", "serta", "maupun",
            "mereka", "kita", "kami", "anda", "dia", "ia", "nya"
        };

        // Stop words Bahasa Inggris
        String[] english = {
            "the", "be", "to", "of", "and", "a", "in", "that", "have",
            "it", "for", "not", "on", "with", "he", "as", "you", "do",
            "at", "this", "but", "his", "by", "from", "they", "we",
            "say", "her", "she", "or", "an", "will", "my", "one",
            "all", "would", "there", "their", "what", "so", "up",
            "out", "if", "about", "who", "get", "which", "go", "me",
            "when", "make", "can", "like", "time", "no", "just",
            "him", "know", "take", "people", "into", "year", "your",
            "good", "some", "could", "them", "see", "other", "than",
            "then", "now", "look", "only", "come", "its", "over",
            "think", "also", "back", "after", "use", "two", "how",
            "our", "work", "first", "well", "way", "even", "new",
            "want", "because", "any", "these", "give", "day", "most"
        };

        stopWords.addAll(Arrays.asList(indonesian));
        stopWords.addAll(Arrays.asList(english));
    }

    /**
     * Memeriksa apakah kata termasuk stop word.
     *
     * @param word kata yang akan diperiksa (case-insensitive)
     * @return true jika kata adalah stop word
     */
    public boolean isStopWord(String word) {
        return stopWords.contains(word.toLowerCase().trim());
    }

    /**
     * Menghapus stop words dari array kata.
     */
    public String[] filterWords(String[] words) {
        return Arrays.stream(words)
                     .filter(w -> !isStopWord(w))
                     .toArray(String[]::new);
    }

    /**
     * Menambahkan custom stop word.
     */
    public void addStopWord(String word) {
        stopWords.add(word.toLowerCase());
    }

    public Set<String> getStopWords() {
        return new HashSet<>(stopWords); // defensive copy
    }
}