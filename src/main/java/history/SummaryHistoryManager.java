package history;

import model.SummaryRecord;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Class untuk menyimpan, membaca, dan menghapus riwayat ringkasan.
 *
 * Method yang dipakai oleh MainFrame dan HistoryDialog:
 * - save(SummaryRecord record)
 * - loadAll()
 * - delete(String id)
 * - clearAll()
 */
public class SummaryHistoryManager {

    private static final String DATA_FOLDER = "data";
    private static final String HISTORY_FILE = "history.txt";

    private final File historyFile;

    public SummaryHistoryManager() {
        File folder = new File(DATA_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        this.historyFile = new File(folder, HISTORY_FILE);

        try {
            if (!historyFile.exists()) {
                historyFile.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("Gagal membuat file history: " + e.getMessage());
        }
    }

    /**
     * Menyimpan satu record riwayat ke file.
     */
    public void save(SummaryRecord record) throws IOException {
        if (record == null) {
            return;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                historyFile.toPath(),
                StandardCharsets.UTF_8,
                java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.APPEND)) {
            writer.write(toLine(record));
            writer.newLine();
        }
    }

    /**
     * Membaca semua riwayat dari file.
     */
    public List<SummaryRecord> loadAll() {
        List<SummaryRecord> records = new ArrayList<>();

        if (!historyFile.exists()) {
            return records;
        }

        try (BufferedReader reader = Files.newBufferedReader(
                historyFile.toPath(),
                StandardCharsets.UTF_8)) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                SummaryRecord record = fromLine(line);
                if (record != null) {
                    records.add(record);
                }
            }
        } catch (Exception e) {
            System.err.println("Gagal membaca history: " + e.getMessage());
        }

        return records;
    }

    /**
     * Menghapus satu riwayat berdasarkan id.
     */
    public void delete(String id) throws IOException {
        if (id == null || id.trim().isEmpty()) {
            return;
        }

        List<SummaryRecord> records = loadAll();
        records.removeIf(record -> id.equals(record.getId()));

        rewriteFile(records);
    }

    /**
     * Menghapus semua riwayat.
     */
    public void clearAll() throws IOException {
        rewriteFile(new ArrayList<>());
    }

    /**
     * Alias tambahan supaya aman kalau class lain memakai nama method berbeda.
     */
    public void addHistory(SummaryRecord record) throws IOException {
        save(record);
    }

    public List<SummaryRecord> getAllHistory() {
        return loadAll();
    }

    /**
     * Menulis ulang seluruh isi file history.
     */
    private void rewriteFile(List<SummaryRecord> records) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(
                historyFile.toPath(),
                StandardCharsets.UTF_8,
                java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.TRUNCATE_EXISTING)) {
            for (SummaryRecord record : records) {
                writer.write(toLine(record));
                writer.newLine();
            }
        }
    }

    /**
     * Mengubah SummaryRecord menjadi satu baris teks.
     * Format:
     * id|createdAt|bookTitleBase64|qualityBase64|methodBase64|summaryBase64
     */
    private String toLine(SummaryRecord record) {
        return safe(record.getId()) + "|" +
                safe(record.getCreatedAt().toString()) + "|" +
                encode(record.getBookTitle()) + "|" +
                encode(record.getQuality()) + "|" +
                encode(record.getMethodUsed()) + "|" +
                encode(record.getSummaryText());
    }

    /**
     * Mengubah satu baris teks menjadi SummaryRecord.
     */
    private SummaryRecord fromLine(String line) {
        try {
            String[] parts = line.split("\\|", -1);

            if (parts.length != 6) {
                return null;
            }

            String id = parts[0];
            LocalDateTime createdAt = LocalDateTime.parse(parts[1]);
            String bookTitle = decode(parts[2]);
            String quality = decode(parts[3]);
            String methodUsed = decode(parts[4]);
            String summaryText = decode(parts[5]);

            return new SummaryRecord(
                    id,
                    bookTitle,
                    createdAt,
                    quality,
                    methodUsed,
                    summaryText);
        } catch (Exception e) {
            System.err.println("Data history rusak / tidak valid: " + e.getMessage());
            return null;
        }
    }

    private String encode(String value) {
        if (value == null) {
            value = "";
        }

        return Base64.getEncoder().encodeToString(
                value.getBytes(StandardCharsets.UTF_8));
    }

    private String decode(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        return new String(
                Base64.getDecoder().decode(value),
                StandardCharsets.UTF_8);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}