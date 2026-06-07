package util;

import model.BookSummary;
import model.SummaryStatistics;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Utility class untuk export hasil ringkasan ke file TXT dan PDF.
 * Menggunakan Apache PDFBox untuk pembuatan file PDF.
 */
public class FileExporter {

    // === Export ke TXT ===

    /**
     * Menyimpan hasil ringkasan ke file TXT.
     *
     * @param summary   objek BookSummary berisi semua data ringkasan
     * @param outputFile file tujuan
     * @throws IOException jika gagal menulis file
     */
    public void exportTxt(BookSummary summary, File outputFile) throws IOException {
        String content = summary.toExportText();
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {
            writer.write(content);
        }
    }

    // === Export ke PDF ===

    /**
     * Menyimpan hasil ringkasan ke file PDF menggunakan Apache PDFBox.
     *
     * @param summary   objek BookSummary berisi semua data ringkasan
     * @param outputFile file PDF tujuan
     * @throws IOException jika gagal membuat file PDF
     */
    public void exportPdf(BookSummary summary, File outputFile) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            float margin = 50;
            float yStart = PDRectangle.A4.getHeight() - margin;

            // Inisialisasi font
            PDType1Font fontBold    = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                float y = yStart;
                float width = PDRectangle.A4.getWidth() - 2 * margin;

                // Judul aplikasi
                y = writeText(cs, "APLIKASI RINGKASAN BUKU OTOMATIS", margin, y,
                              fontBold, 14);
                y -= 4;

                // Garis horizontal
                cs.setLineWidth(0.5f);
                cs.moveTo(margin, y);
                cs.lineTo(PDRectangle.A4.getWidth() - margin, y);
                cs.stroke();
                y -= 16;

                // Metadata buku
                y = writeText(cs, "Judul   : " + summary.getRecord().getBookTitle(),
                              margin, y, fontBold, 11);
                y = writeText(cs, "Tanggal : " + summary.getRecord().getFormattedDate(),
                              margin, y, fontRegular, 11);
                y = writeText(cs, "Kualitas: " + summary.getRecord().getQuality(),
                              margin, y, fontRegular, 11);
                y = writeText(cs, "Metode  : " + summary.getRecord().getMethodUsed(),
                              margin, y, fontRegular, 11);
                y -= 12;

                // Ringkasan
                y = writeText(cs, "RINGKASAN", margin, y, fontBold, 12);
                y -= 4;
                y = writeWrappedText(cs, summary.getOutputText(),
                                     margin, y, width, fontRegular, 10, doc);
                y -= 12;

                // Statistik
                y = writeText(cs, "STATISTIK", margin, y, fontBold, 12);
                y -= 4;
                SummaryStatistics stats = summary.getStatistics();
                y = writeText(cs,
                    "Kata Awal: " + stats.getOriginalWordCount() +
                    "  |  Kata Ringkasan: " + stats.getSummaryWordCount() +
                    "  |  Kompresi: " + String.format("%.0f%%", stats.getCompressionRate()),
                    margin, y, fontRegular, 10);
                y -= 12;

                // Keyword
                y = writeText(cs, "KEYWORD UTAMA", margin, y, fontBold, 12);
                y -= 4;
                List<String> keywords = stats.getKeywords();
                if (keywords != null) {
                    for (String kw : keywords) {
                        y = writeText(cs, "• " + kw, margin + 10, y, fontRegular, 10);
                    }
                }
            }

            doc.save(outputFile);
        }
    }

    // === Helper Methods ===

    private float writeText(PDPageContentStream cs, String text,
                             float x, float y, PDType1Font font, float fontSize)
            throws IOException {
        cs.beginText();
        cs.setFont(font, fontSize);
        cs.newLineAtOffset(x, y);
        cs.showText(sanitize(text));
        cs.endText();
        return y - (fontSize + 4);
    }

    /**
     * Menulis teks dengan word-wrap otomatis.
     * Jika teks melebihi halaman, tambahkan halaman baru.
     */
    private float writeWrappedText(PDPageContentStream cs, String text,
                                    float x, float y, float width,
                                    PDType1Font font, float fontSize,
                                    PDDocument doc) throws IOException {
        String[] words = text.split("\\s+");
        StringBuilder line = new StringBuilder();
        float lineHeight = fontSize + 4;
        float margin = 50;

        for (String word : words) {
            String testLine = line.isEmpty() ? word : line + " " + word;
            float lineWidth = font.getStringWidth(sanitize(testLine)) / 1000 * fontSize;

            if (lineWidth > width && line.length() > 0) {
                y = writeText(cs, line.toString(), x, y, font, fontSize);
                line = new StringBuilder(word);

                if (y < margin + lineHeight) {
                    // Halaman baru jika konten overflow
                    y = PDRectangle.A4.getHeight() - margin;
                }
            } else {
                line = new StringBuilder(testLine);
            }
        }
        if (line.length() > 0) {
            y = writeText(cs, line.toString(), x, y, font, fontSize);
        }
        return y;
    }

    /**
     * Menghapus karakter non-printable yang bisa merusak PDF.
     */
    private String sanitize(String text) {
        if (text == null) return "";
        return text.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]", "");
    }
}