package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class untuk formatting dan parsing tanggal/waktu.
 */
public class DateUtil {

    private static final DateTimeFormatter DISPLAY_FORMAT =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final DateTimeFormatter FILE_FORMAT =
        DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private static final DateTimeFormatter STORAGE_FORMAT =
        DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private DateUtil() {} // prevent instantiation

    /**
     * Format tanggal untuk tampilan GUI (misal: "07/06/2025 14:30")
     */
    public static String formatForDisplay(LocalDateTime dateTime) {
        return dateTime.format(DISPLAY_FORMAT);
    }

    /**
     * Format tanggal untuk nama file (misal: "20250607_143022")
     */
    public static String formatForFileName(LocalDateTime dateTime) {
        return dateTime.format(FILE_FORMAT);
    }

    /**
     * Format tanggal untuk penyimpanan JSON (ISO format)
     */
    public static String formatForStorage(LocalDateTime dateTime) {
        return dateTime.format(STORAGE_FORMAT);
    }

    /**
     * Parse tanggal dari format storage
     */
    public static LocalDateTime parseFromStorage(String dateString) {
        return LocalDateTime.parse(dateString, STORAGE_FORMAT);
    }

    /**
     * Mendapatkan waktu sekarang
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
}