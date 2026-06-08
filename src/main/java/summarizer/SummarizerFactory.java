package summarizer;

import model.SummaryQuality;

/**
 * Factory class untuk membuat instance Summarizer yang sesuai
 * berdasarkan kualitas yang dipilih pengguna.
 *
 * Konsep OOP:
 * - Factory Pattern: memusatkan logika pembuatan objek
 * - Singleton: satu instance factory untuk seluruh aplikasi
 * - Polymorphism: create() mengembalikan tipe Summarizer (interface)
 */
public class SummarizerFactory {

    // === Singleton ===
    private static SummarizerFactory instance;

    // Encapsulation: field private
    private String apiToken = "";

    // Konstruktor private (Singleton)
    private SummarizerFactory() {}

    /**
     * Mendapatkan satu-satunya instance factory (Singleton Pattern).
     */
    public static synchronized SummarizerFactory getInstance() {
        if (instance == null) {
            instance = new SummarizerFactory();
        }
        return instance;
    }

    /**
     * Menyimpan API token Hugging Face untuk digunakan saat membuat summarizer.
     */
    public void setApiToken(String apiToken) {
        this.apiToken = (apiToken == null) ? "" : apiToken.trim();
    }

    public String getApiToken() {
        return apiToken;
    }

    /**
     * Membuat dan mengembalikan Summarizer yang sesuai berdasarkan kualitas.
     *
     * CEPAT    -> RuleBasedSummarizer (offline, cepat)
     * OTOMATIS -> FallbackSummarizer  (coba API, fallback ke rule-based)
     *
     * Polymorphism: semua kembalikan tipe Summarizer (interface).
     */
    public Summarizer create(SummaryQuality quality) {
        if (quality == SummaryQuality.CEPAT) {
            return new RuleBasedSummarizer();
        } else {
            // OTOMATIS
            // Coba API dulu; jika gagal atau token kosong, gunakan rule-based
            return new FallbackSummarizer(apiToken, false);
        }
    }
}