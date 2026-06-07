package summarizer;

/**
 * Interface utama untuk semua implementasi summarizer.
 *
 * Konsep OOP: Interface
 * - Mendefinisikan kontrak yang harus dipenuhi oleh semua implementasi.
 * - ApiBasedSummarizer dan RuleBasedSummarizer sama-sama implement interface ini.
 * - Mendukung polymorphism: variabel bertipe Summarizer bisa menampung
 *   objek dari class mana pun yang mengimplementasikan interface ini.
 */
public interface Summarizer {

    /**
     * Merangkum teks yang diberikan.
     *
     * @param text teks yang akan diringkas
     * @return hasil ringkasan
     * @throws Exception jika terjadi error saat proses summarization
     */
    String summarize(String text) throws Exception;

    /**
     * Mendapatkan nama/label metode yang digunakan.
     * Digunakan untuk ditampilkan di GUI.
     *
     * @return nama metode (contoh: "Hugging Face API" atau "Rule-Based Summarizer")
     */
    String getMethodName();
}