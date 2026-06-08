package history;

import model.SummaryRecord;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Manager untuk menyimpan dan memuat riwayat ringkasan dari file JSON lokal.
 *
 * File disimpan di: [user home]/AplikasiRingkasan/history.json
 *
 * Konsep OOP:
 * - Encapsulation : filePath dan records disimpan private
 * - Single Responsibility : class ini hanya mengurus penyimpanan riwayat
 */
public class SummaryHistoryManager {

    // === Encapsulation: field private ===
    private final String filePath;
    private List<SummaryRecord> records;

    private static final String APP_DIR = System.getProperty("user.home") +
            File.separator + "AplikasiRingkasan";
    private static final String FILENAME = "history.json";

    // === Constructor ===
    public SummaryHistoryManager() {
        this.filePath = APP_DIR + File.separator + FILENAME;
        this.records = new ArrayList<>();
        ensureDirectoryExists();
        loadFromFile();
    }

    // Constructor untuk testing (custom path)
    public SummaryHistoryManager(String customPath) {
        this.filePath = customPath;
        this.records = new ArrayList<>();
        loadFromFile();
    }

    // === Public methods ===

    /**
     * Menyimpan satu record baru ke riwayat.
     * Record langsung di-persist ke file.
     */
    public void save(SummaryRecord record) {
        records.add(0, record); // tambah di awal (terbaru di atas)
        persistToFile();
    }

    /**
     * Mengambil semua riwayat (urutan terbaru dulu).
     * Return defensive copy agar list internal tidak bisa diubah dari luar.
     */
    public List<SummaryRecord> loadAll() {
        return Collections.unmodifiableList(records);
    }

    /**
     * Menghapus semua riwayat.
     */
    public void clearAll() {
        records.clear();
        persistToFile();
    }

    /**
     * Menghapus satu record berdasarkan id.
     */
    public void delete(String id) {
        records.removeIf(record -> record.getId().equals(id));
        persistToFile();
    }

    /**
     * Menghapus satu record berdasarkan index.
     */
    public void deleteAt(int index) {
        if (index >= 0 && index < records.size()) {
            records.remove(index);
            persistToFile();
        }
    }

    public int size() {
        return records.size();
    }

    // === Private helper methods ===

    private void ensureDirectoryExists() {
        File dir = new File(APP_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Menyimpan semua records ke file JSON.
     * Format JSON ditulis manual agar tidak perlu library tambahan.
     */
    private void persistToFile() {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(filePath), StandardCharsets.UTF_8))) {

            writer.write("[\n");
            for (int i = 0; i < records.size(); i++) {
                writer.write(records.get(i).toJson());
                if (i < records.size() - 1)
                    writer.write(",");
                writer.newLine();
            }
            writer.write("]");

        } catch (IOException e) {
            System.err.println("Gagal menyimpan riwayat: " + e.getMessage());
        }
    }

    /**
     * Memuat records dari file JSON saat startup.
     * Parser JSON sederhana — menggunakan regex dasar.
     */
    private void loadFromFile() {
        File file = new File(filePath);
        if (!file.exists())
            return;

        try {
            String content = new String(
                    Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
            records = parseJsonArray(content);
        } catch (IOException e) {
            System.err.println("Gagal memuat riwayat: " + e.getMessage());
            records = new ArrayList<>();
        }
    }

    /**
     * Parser JSON array sederhana.
     * Hanya menangani format yang dihasilkan oleh SummaryRecord.toJson().
     */
    private List<SummaryRecord> parseJsonArray(String json) {
        List<SummaryRecord> result = new ArrayList<>();
        if (json == null || json.trim().isEmpty())
            return result;

        // Pisahkan berdasarkan object JSON { ... }
        int depth = 0;
        int start = -1;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') {
                if (depth == 0)
                    start = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && start >= 0) {
                    String obj = json.substring(start, i + 1);
                    SummaryRecord record = parseJsonObject(obj);
                    if (record != null)
                        result.add(record);
                    start = -1;
                }
            }
        }
        return result;
    }

    /** Parse satu JSON object menjadi SummaryRecord */
    private SummaryRecord parseJsonObject(String obj) {
        try {
            String id = extractJsonString(obj, "id");
            String bookTitle = extractJsonString(obj, "bookTitle");
            String dateStr = extractJsonString(obj, "createdAt");
            String quality = extractJsonString(obj, "quality");
            String methodUsed = extractJsonString(obj, "methodUsed");
            String summaryText = extractJsonString(obj, "summaryText");

            LocalDateTime createdAt = dateStr != null
                    ? LocalDateTime.parse(dateStr)
                    : LocalDateTime.now();

            if (id == null) id = java.util.UUID.randomUUID().toString();

            return new SummaryRecord(id, bookTitle, createdAt, quality, methodUsed, summaryText);
        } catch (Exception e) {
            return null;
        }
    }

    /** Ekstrak nilai string dari JSON dengan regex sederhana */
    private String extractJsonString(String json, String key) {
        String pattern = "\"" + key + "\":\\s*\"";
        int idx = json.indexOf(pattern);
        if (idx < 0)
            return null;

        int start = idx + pattern.length();
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '\\' && i + 1 < json.length()) {
                char next = json.charAt(i + 1);
                switch (next) {
                    case '"':
                        sb.append('"');
                        i++;
                        break;
                    case '\\':
                        sb.append('\\');
                        i++;
                        break;
                    case 'n':
                        sb.append('\n');
                        i++;
                        break;
                    case 'r':
                        sb.append('\r');
                        i++;
                        break;
                    case 't':
                        sb.append('\t');
                        i++;
                        break;
                    default:
                        sb.append(c);
                }
            } else if (c == '"') {
                break;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    // === Getter ===
    public String getFilePath() {
        return filePath;
    }
}