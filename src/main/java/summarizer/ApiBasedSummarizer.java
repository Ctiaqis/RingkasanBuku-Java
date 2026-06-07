package summarizer;

import service.HuggingFaceService;

/**
 * Implementasi Summarizer menggunakan Hugging Face Inference API.
 *
 * Konsep OOP:
 * - Implements interface Summarizer (Interface + Polymorphism)
 * - Encapsulation: semua field private, diakses via getter/setter
 *
 * Digunakan saat kualitas ringkasan = "Seimbang" atau "Terbaik"
 */
public class ApiBasedSummarizer implements Summarizer {

    // Encapsulation: field private
    private String apiToken;
    private String modelUrl;
    private final HuggingFaceService huggingFaceService;

    private static final String DEFAULT_MODEL_URL =
        "https://api-inference.huggingface.co/models/facebook/bart-large-cnn";

    public ApiBasedSummarizer(String apiToken) {
        this.apiToken = apiToken;
        this.modelUrl = DEFAULT_MODEL_URL;
        this.huggingFaceService = new HuggingFaceService(apiToken, modelUrl);
    }

    public ApiBasedSummarizer(String apiToken, String modelUrl) {
        this.apiToken = apiToken;
        this.modelUrl = modelUrl;
        this.huggingFaceService = new HuggingFaceService(apiToken, modelUrl);
    }

    /**
     * Merangkum teks menggunakan Hugging Face API.
     * Implements method dari interface Summarizer.
     */
    @Override
    public String summarize(String text) throws Exception {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Teks tidak boleh kosong.");
        }
        return huggingFaceService.callSummarizationApi(text);
    }

    @Override
    public String getMethodName() {
        return "Hugging Face API";
    }

    /**
     * Memeriksa apakah token API telah diset.
     */
    public boolean isAvailable() {
        return apiToken != null && !apiToken.trim().isEmpty();
    }

    // === Getter / Setter (Encapsulation) ===

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public String getModelUrl() {
        return modelUrl;
    }

    public void setModelUrl(String modelUrl) {
        this.modelUrl = modelUrl;
    }
}