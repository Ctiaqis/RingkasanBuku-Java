package model;

/**
 * Aggregate model class yang menggabungkan semua data hasil ringkasan.
 *
 * Konsep OOP: Encapsulation + Composition
 * - Berisi SummaryRecord dan SummaryStatistics sebagai komponen
 * - Menjadi objek utama yang diteruskan ke FileExporter dan GUI
 */
public class BookSummary {

    // Encapsulation: field private
    private SummaryRecord record;
    private SummaryStatistics statistics;
    private String inputText;
    private String outputText;

    public BookSummary(SummaryRecord record, SummaryStatistics statistics,
                       String inputText, String outputText) {
        this.record = record;
        this.statistics = statistics;
        this.inputText = inputText;
        this.outputText = outputText;
    }

    /**
     * Membuat konten lengkap untuk export TXT.
     */
    public String toExportText() {
        return "=== RINGKASAN BUKU ===\n\n" +
               "Judul   : " + record.getBookTitle() + "\n" +
               "Tanggal : " + record.getFormattedDate() + "\n" +
               "Kualitas: " + record.getQuality() + "\n" +
               "Metode  : " + record.getMethodUsed() + "\n\n" +
               "--- RINGKASAN ---\n\n" +
               outputText + "\n\n" +
               "--- STATISTIK ---\n\n" +
               statistics.getFormattedStats() + "\n\n" +
               "--- KEYWORD UTAMA ---\n\n" +
               statistics.getFormattedKeywords();
    }

    // === Getter (Encapsulation) ===

    public SummaryRecord getRecord() {
        return record;
    }

    public SummaryStatistics getStatistics() {
        return statistics;
    }

    public String getInputText() {
        return inputText;
    }

    public String getOutputText() {
        return outputText;
    }

    // === Setter (Encapsulation) ===

    public void setRecord(SummaryRecord record) {
        this.record = record;
    }

    public void setStatistics(SummaryStatistics statistics) {
        this.statistics = statistics;
    }

    public void setOutputText(String outputText) {
        this.outputText = outputText;
    }
}