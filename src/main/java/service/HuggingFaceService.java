package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Service untuk berkomunikasi dengan Hugging Face Inference API.
 *
 * Menggunakan Java HttpClient (built-in sejak Java 11) untuk HTTP request.
 * Jackson digunakan untuk serialisasi/deserialisasi JSON.
 */
public class HuggingFaceService {

    private final String apiToken;
    private final String modelUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    private static final int TIMEOUT_SECONDS = 60;
    // Batas karakter per request (BART-large-CNN optimal di ~1024 token / ~4000 karakter)
    private static final int MAX_INPUT_CHARS = 4000;

    public HuggingFaceService(String apiToken, String modelUrl) {
        this.apiToken = apiToken;
        this.modelUrl = modelUrl;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))
            .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Memanggil Hugging Face Inference API untuk summarization.
     *
     * @param inputText teks yang akan diringkas
     * @return hasil ringkasan dari API
     * @throws Exception jika API error, timeout, atau koneksi gagal
     */
    public String callSummarizationApi(String inputText) throws Exception {
        // Potong teks jika terlalu panjang
        String text = inputText.length() > MAX_INPUT_CHARS
            ? inputText.substring(0, MAX_INPUT_CHARS)
            : inputText;

        // Buat JSON request body
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("inputs", text);

        // Tambahkan parameter opsional
        ObjectNode parameters = objectMapper.createObjectNode();
        parameters.put("max_length", 150);
        parameters.put("min_length", 30);
        parameters.put("do_sample", false);
        requestBody.set("parameters", parameters);

        String jsonBody = objectMapper.writeValueAsString(requestBody);

        // Buat HTTP Request
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(modelUrl))
            .header("Authorization", "Bearer " + apiToken)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
            .build();

        // Kirim request dan terima response
        HttpResponse<String> response = httpClient.send(
            request, HttpResponse.BodyHandlers.ofString()
        );

        return parseResponse(response);
    }

    /**
     * Mem-parse response JSON dari Hugging Face API.
     * Format: [{"summary_text": "..."}]
     */
    private String parseResponse(HttpResponse<String> response) throws Exception {
        int statusCode = response.statusCode();
        String body = response.body();

        if (statusCode == 200) {
            JsonNode root = objectMapper.readTree(body);
            if (root.isArray() && root.size() > 0) {
                JsonNode first = root.get(0);
                if (first.has("summary_text")) {
                    return first.get("summary_text").asText();
                }
            }
            throw new Exception("Format response tidak dikenali: " + body);
        } else if (statusCode == 401) {
            throw new Exception("Token API tidak valid. Periksa kembali Hugging Face API Token Anda.");
        } else if (statusCode == 429) {
            throw new Exception("Rate limit API tercapai. Coba lagi beberapa saat kemudian.");
        } else if (statusCode == 503) {
            throw new Exception("Model sedang dimuat. Coba lagi dalam 20 detik.");
        } else {
            throw new Exception("API Error " + statusCode + ": " + body);
        }
    }
}