package summarizer;

/**
 * Summarizer dengan mekanisme fallback:
 * Mencoba menggunakan API (Hugging Face) terlebih dahulu.
 * Jika API gagal (token kosong, error jaringan, dll.),
 * secara otomatis beralih ke RuleBasedSummarizer (offline).
 *
 * Konsep OOP:
 * - Implements interface Summarizer (Interface + Polymorphism)
 * - Composition: mengandung ApiBasedSummarizer dan RuleBasedSummarizer
 * - Encapsulation: field private, akses via method
 *
 * Digunakan saat kualitas = "Seimbang".
 */
public class FallbackSummarizer implements Summarizer {

    // Encapsulation: field private
    private final String apiToken;
    private final boolean forceOffline; // true jika token memang tidak ada (dari BEST mode)

    private boolean usedFallback = false;
    private String  fallbackReason = "";

    // Komponen internal (Composition)
    private final RuleBasedSummarizer ruleBasedSummarizer;

    public FallbackSummarizer(String apiToken, boolean forceOffline) {
        this.apiToken      = (apiToken == null) ? "" : apiToken.trim();
        this.forceOffline  = forceOffline;
        this.ruleBasedSummarizer = new RuleBasedSummarizer();
    }

    /**
     * Merangkum teks:
     * 1. Jika token tersedia dan forceOffline=false, coba pakai API.
     * 2. Jika API gagal atau token kosong, gunakan RuleBasedSummarizer.
     */
    @Override
    public String summarize(String text, model.SummaryLength length) throws SummarizerException {
        if (text == null || text.trim().isEmpty()) {
            throw new SummarizerException("Teks tidak boleh kosong.");
        }

        // Langsung pakai rule-based jika token kosong atau dipaksa offline
        if (apiToken.isEmpty() || forceOffline) {
            usedFallback   = true;
            fallbackReason = forceOffline
                ? "Mode Terbaik dipilih tetapi token API tidak tersedia."
                : "Token API kosong — menggunakan mode offline.";
            return ruleBasedSummarizer.summarize(text, length);
        }

        // Coba pakai API
        try {
            ApiBasedSummarizer apiSummarizer = new ApiBasedSummarizer(apiToken);
            String result = apiSummarizer.summarize(text, length);
            usedFallback = false;
            return result;
        } catch (Exception e) {
            // API gagal — fallback ke rule-based
            usedFallback   = true;
            fallbackReason = "API tidak merespons: " + e.getMessage()
                           + "\nBeralih ke mode offline (Rule-Based).";
            return ruleBasedSummarizer.summarize(text, length);
        }
    }

    @Override
    public String getMethodName() {
        if (usedFallback) {
            return "Rule-Based Summarizer (Fallback)";
        }
        return "Hugging Face API (Seimbang)";
    }

    // === Getter untuk informasi fallback ===

    /**
     * Mengembalikan true jika summarizer beralih ke mode offline (fallback).
     */
    public boolean isUsedFallback() {
        return usedFallback;
    }

    /**
     * Mengembalikan pesan peringatan yang siap ditampilkan di GUI.
     */
    public String getFallbackWarningMessage() {
        return "⚠ Ringkasan dibuat dengan mode offline (Rule-Based).\n\n"
             + "Alasan: " + fallbackReason + "\n\n"
             + "Untuk kualitas terbaik, pastikan token API valid\n"
             + "dan koneksi internet tersedia.";
    }

    /**
     * Mengembalikan alasan fallback (untuk keperluan logging/debug).
     */
    public String getFallbackReason() {
        return fallbackReason;
    }
}