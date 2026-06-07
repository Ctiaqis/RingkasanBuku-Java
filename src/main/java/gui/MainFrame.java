package gui;

import history.SummaryHistoryManager;
import input.TextInputHandler;
import model.BookSummary;
import model.SummaryRecord;
import model.SummaryStatistics;
import summarizer.*;
import util.FileExporter;
import util.StatisticsCalculator;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;

/**
 * Jendela utama (main frame) aplikasi Ringkasan Buku Otomatis.
 * Dibangun manual menggunakan Java Swing di VS Code (tanpa GUI Builder).
 *
 * Menerapkan seluruh konsep OOP:
 * - Encapsulation (semua field private)
 * - Interface (menggunakan Summarizer interface)
 * - Polymorphism (variabel bertipe Summarizer)
 * - Inheritance (menggunakan BaseDialog via HistoryDialog & AboutDialog)
 */
public class MainFrame extends JFrame {

    // ─── Field (Encapsulation) ───
    private JTextField titleField;
    private JTextArea inputArea;
    private JComboBox<String> qualityCombo;
    private JTextArea outputArea;
    private JLabel methodLabel;
    private JLabel statsLabel;
    private JPanel keywordPanel;
    private JProgressBar progressBar;
    private JButton ringkasBtn;

    // Dependencies
    private final SummaryHistoryManager historyManager;
    private final TextInputHandler inputHandler;
    private final StatisticsCalculator statsCalculator;
    private final FileExporter fileExporter;

    // Current result
    private BookSummary currentSummary;

    // Konfigurasi
    private String apiToken = "";

    private static final String APP_TITLE = "Aplikasi Ringkasan Buku Otomatis";
    private static final String[] QUALITY_OPTIONS = {
        "⚡ Cepat (Offline)",
        "⚖ Seimbang — Direkomendasikan",
        "✨ Terbaik (Butuh Internet)"
    };

    public MainFrame() {
        this.historyManager  = new SummaryHistoryManager();
        this.inputHandler    = new TextInputHandler();
        this.statsCalculator = new StatisticsCalculator();
        this.fileExporter    = new FileExporter();

        initFrame();
        buildUI();
        promptApiToken();
    }

    // ─────────────────────────────────────────────────────────
    // Frame setup
    // ─────────────────────────────────────────────────────────

    private void initFrame() {
        setTitle(APP_TITLE);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(900, 620));
        setPreferredSize(new Dimension(1050, 680));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int opt = JOptionPane.showConfirmDialog(MainFrame.this,
                    "Keluar dari aplikasi?", "Konfirmasi",
                    JOptionPane.YES_NO_OPTION);
                if (opt == JOptionPane.YES_OPTION) dispose();
            }
        });
    }

    // ─────────────────────────────────────────────────────────
    // Bangun seluruh UI
    // ─────────────────────────────────────────────────────────

    private void buildUI() {
        setJMenuBar(buildMenuBar());

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel tengah: kiri (input) + kanan (output)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            buildInputPanel(), buildOutputPanel());
        splitPane.setResizeWeight(0.48);
        splitPane.setDividerSize(5);

        root.add(buildTopBar(), BorderLayout.NORTH);
        root.add(splitPane, BorderLayout.CENTER);
        root.add(buildStatusBar(), BorderLayout.SOUTH);

        setContentPane(root);
        pack();
        setLocationRelativeTo(null);
    }

    // ─────────────────────────────────────────────────────────
    // Top bar: judul + tombol token
    // ─────────────────────────────────────────────────────────

    private JPanel buildTopBar() {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        JLabel appTitle = new JLabel("📚 " + APP_TITLE);
        appTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        panel.add(appTitle, BorderLayout.WEST);

        JButton tokenBtn = new JButton("🔑 Set API Token");
        tokenBtn.addActionListener(e -> promptApiToken());

        JButton aboutBtn = new JButton("ℹ Tentang");
        aboutBtn.addActionListener(e -> new AboutDialog(this).showDialog());

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        btnGroup.add(tokenBtn);
        btnGroup.add(aboutBtn);
        panel.add(btnGroup, BorderLayout.EAST);
        return panel;
    }

    // ─────────────────────────────────────────────────────────
    // Input Panel (kiri)
    // ─────────────────────────────────────────────────────────

    private JPanel buildInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Input",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 12)
        ));

        // Judul buku
        JPanel titleRow = new JPanel(new BorderLayout(6, 0));
        titleRow.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        titleRow.add(new JLabel("Judul Buku:"), BorderLayout.WEST);
        titleField = new JTextField();
        titleField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        titleRow.add(titleField, BorderLayout.CENTER);
        panel.add(titleRow, BorderLayout.NORTH);

        // Area input teks
        inputArea = new JTextArea();
        inputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setMargin(new Insets(6, 6, 6, 6));
        inputArea.setTabSize(4);
        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
        panel.add(inputScroll, BorderLayout.CENTER);

        panel.add(buildInputControlPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildInputControlPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 4));
        panel.setBorder(BorderFactory.createEmptyBorder(4, 6, 6, 6));

        // Upload buttons
        JPanel uploadRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JButton uploadTxtBtn = new JButton("⬆ Upload TXT");
        JButton uploadPdfBtn = new JButton("📄 Upload PDF");
        uploadTxtBtn.addActionListener(e -> uploadFile("txt"));
        uploadPdfBtn.addActionListener(e -> uploadFile("pdf"));
        uploadRow.add(uploadTxtBtn);
        uploadRow.add(uploadPdfBtn);
        panel.add(uploadRow, BorderLayout.NORTH);

        // Kualitas + Ringkas button
        JPanel qualityRow = new JPanel(new BorderLayout(8, 0));
        qualityRow.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        JPanel comboGroup = new JPanel(new BorderLayout(4, 0));
        comboGroup.add(new JLabel("Kualitas:"), BorderLayout.WEST);
        qualityCombo = new JComboBox<>(QUALITY_OPTIONS);
        qualityCombo.setSelectedIndex(1); // Default: Seimbang
        comboGroup.add(qualityCombo, BorderLayout.CENTER);

        ringkasBtn = new JButton("▶  RINGKAS");
        ringkasBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        ringkasBtn.setBackground(new Color(24, 95, 165));
        ringkasBtn.setForeground(Color.WHITE);
        ringkasBtn.setPreferredSize(new Dimension(120, 32));
        ringkasBtn.addActionListener(e -> startSummarization());

        qualityRow.add(comboGroup, BorderLayout.CENTER);
        qualityRow.add(ringkasBtn, BorderLayout.EAST);
        panel.add(qualityRow, BorderLayout.SOUTH);
        return panel;
    }

    // ─────────────────────────────────────────────────────────
    // Output Panel (kanan)
    // ─────────────────────────────────────────────────────────

    private JPanel buildOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Hasil Ringkasan",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 12)
        ));

        // Hasil ringkasan
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setMargin(new Insets(8, 8, 8, 8));
        outputArea.setBackground(new Color(250, 250, 252));
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
        panel.add(outputScroll, BorderLayout.CENTER);

        panel.add(buildOutputInfoPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildOutputInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 4));
        panel.setBorder(BorderFactory.createEmptyBorder(4, 6, 6, 6));

        // Metode yang digunakan
        methodLabel = new JLabel(" Metode: —");
        methodLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        methodLabel.setForeground(new Color(59, 109, 17));
        panel.add(methodLabel, BorderLayout.NORTH);

        // Statistik
        statsLabel = new JLabel(" Statistik: —");
        statsLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        statsLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Statistik"),
            BorderFactory.createEmptyBorder(2, 4, 2, 4)
        ));

        // Keyword panel
        keywordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        keywordPanel.setBorder(BorderFactory.createTitledBorder("Keyword Utama"));

        JPanel infoGroup = new JPanel(new GridLayout(2, 1, 0, 4));
        infoGroup.add(statsLabel);
        infoGroup.add(keywordPanel);
        panel.add(infoGroup, BorderLayout.CENTER);

        // Export buttons
        JPanel exportRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));

        JButton historyBtn = new JButton("📋 Riwayat");
        historyBtn.addActionListener(e -> showHistory());

        JButton saveTxtBtn = new JButton("💾 Simpan TXT");
        JButton savePdfBtn = new JButton("📑 Simpan PDF");
        saveTxtBtn.setForeground(new Color(39, 80, 10));
        savePdfBtn.setForeground(new Color(39, 80, 10));
        saveTxtBtn.addActionListener(e -> exportResult("txt"));
        savePdfBtn.addActionListener(e -> exportResult("pdf"));

        exportRow.add(historyBtn);
        exportRow.add(saveTxtBtn);
        exportRow.add(savePdfBtn);
        panel.add(exportRow, BorderLayout.SOUTH);
        return panel;
    }

    // ─────────────────────────────────────────────────────────
    // Menu bar
    // ─────────────────────────────────────────────────────────

    private JMenuBar buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem newItem    = new JMenuItem("Baru");
        JMenuItem exportTxt  = new JMenuItem("Ekspor ke TXT");
        JMenuItem exportPdf  = new JMenuItem("Ekspor ke PDF");
        JMenuItem exitItem   = new JMenuItem("Keluar");
        newItem.addActionListener(e -> clearAll());
        exportTxt.addActionListener(e -> exportResult("txt"));
        exportPdf.addActionListener(e -> exportResult("pdf"));
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(newItem);
        fileMenu.addSeparator();
        fileMenu.add(exportTxt);
        fileMenu.add(exportPdf);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu viewMenu = new JMenu("Tampilan");
        JMenuItem historyItem = new JMenuItem("Riwayat Ringkasan");
        historyItem.addActionListener(e -> showHistory());
        viewMenu.add(historyItem);

        JMenu helpMenu = new JMenu("Bantuan");
        JMenuItem aboutItem = new JMenuItem("Tentang Aplikasi");
        JMenuItem tokenItem = new JMenuItem("Atur API Token");
        aboutItem.addActionListener(e -> new AboutDialog(this).showDialog());
        tokenItem.addActionListener(e -> promptApiToken());
        helpMenu.add(tokenItem);
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }

    // ─────────────────────────────────────────────────────────
    // Status bar
    // ─────────────────────────────────────────────────────────

    private JPanel buildStatusBar() {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        panel.setPreferredSize(new Dimension(0, 24));

        progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(150, 16));
        progressBar.setVisible(false);

        JLabel statusText = new JLabel("  Siap");
        statusText.setFont(new Font("SansSerif", Font.PLAIN, 11));
        statusText.setForeground(Color.GRAY);

        panel.add(statusText, BorderLayout.WEST);
        panel.add(progressBar, BorderLayout.EAST);
        return panel;
    }

    // ─────────────────────────────────────────────────────────
    // Core logic: Summarization
    // ─────────────────────────────────────────────────────────

    private void startSummarization() {
        String inputText  = inputArea.getText().trim();
        String bookTitle  = titleField.getText().trim();
        String qualityRaw = (String) qualityCombo.getSelectedItem();

        // Validasi input
        try {
            inputHandler.validateInput(inputText);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (bookTitle.isEmpty()) {
            int opt = JOptionPane.showConfirmDialog(this,
                "Judul buku kosong. Lanjutkan tanpa judul?",
                "Peringatan", JOptionPane.YES_NO_OPTION);
            if (opt != JOptionPane.YES_OPTION) return;
            bookTitle = "Tanpa Judul";
        }

        // Tentukan kualitas dari pilihan combo
        String quality = parseQuality(qualityRaw);

        // Buat Summarizer via Factory (Polymorphism)
        SummarizerFactory factory = SummarizerFactory.getInstance();
        factory.setApiToken(apiToken);
        Summarizer summarizer = factory.create(quality); // Polymorphism di sini!

        // Jalankan di background thread agar GUI tidak freeze
        final String finalBookTitle = bookTitle;
        final String finalQuality   = quality;

        ringkasBtn.setEnabled(false);
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);
        outputArea.setText("⏳ Sedang memproses...");

        SwingWorker<BookSummary, Void> worker = new SwingWorker<>() {
            @Override
            protected BookSummary doInBackground() throws Exception {
                String summary = summarizer.summarize(inputText);  // Polymorphism!
                SummaryStatistics stats = statsCalculator.calculate(inputText, summary);

                String methodName = summarizer.getMethodName();

                // Tampilkan warning fallback jika ada
                if (summarizer instanceof FallbackSummarizer fs && fs.isUsedFallback()) {
                    SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(MainFrame.this,
                            fs.getFallbackWarningMessage(),
                            "AI Summarizer Tidak Tersedia",
                            JOptionPane.WARNING_MESSAGE)
                    );
                }

                SummaryRecord record = new SummaryRecord(
                    finalBookTitle, finalQuality, methodName, summary
                );
                return new BookSummary(record, stats, inputText, summary);
            }

            @Override
            protected void done() {
                ringkasBtn.setEnabled(true);
                progressBar.setVisible(false);
                progressBar.setIndeterminate(false);

                try {
                    currentSummary = get();
                    displayResult(currentSummary);

                    // Simpan ke riwayat
                    historyManager.save(currentSummary.getRecord());

                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    outputArea.setText("");
                    JOptionPane.showMessageDialog(MainFrame.this,
                        "Gagal memproses ringkasan:\n\n" + cause.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void displayResult(BookSummary summary) {
        outputArea.setText(summary.getOutputText());
        outputArea.setCaretPosition(0);

        // Metode
        methodLabel.setText("  ✓ Metode: " + summary.getRecord().getMethodUsed());

        // Statistik
        SummaryStatistics stats = summary.getStatistics();
        statsLabel.setText(String.format(
            "<html><pre>Kata awal: %,d  |  Ringkasan: %,d  |  Kompresi: %.0f%%</pre></html>",
            stats.getOriginalWordCount(),
            stats.getSummaryWordCount(),
            stats.getCompressionRate()
        ));

        // Keyword chips
        keywordPanel.removeAll();
        List<String> keywords = stats.getKeywords();
        if (keywords != null) {
            for (String kw : keywords) {
                JLabel chip = new JLabel(kw);
                chip.setFont(new Font("SansSerif", Font.BOLD, 11));
                chip.setForeground(new Color(12, 68, 124));
                chip.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(24, 95, 165), 1, true),
                    BorderFactory.createEmptyBorder(1, 7, 1, 7)
                ));
                chip.setBackground(new Color(230, 241, 251));
                chip.setOpaque(true);
                keywordPanel.add(chip);
            }
        }
        keywordPanel.revalidate();
        keywordPanel.repaint();
    }

    // ─────────────────────────────────────────────────────────
    // File operations
    // ─────────────────────────────────────────────────────────

    private void uploadFile(String type) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Pilih file " + type.toUpperCase());

        if ("txt".equals(type)) {
            chooser.setFileFilter(new FileNameExtensionFilter("File Teks (*.txt)", "txt"));
        } else {
            chooser.setFileFilter(new FileNameExtensionFilter("File PDF (*.pdf)", "pdf"));
        }

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                String text = inputHandler.readFromFile(file);
                inputArea.setText(text);
                if (titleField.getText().trim().isEmpty()) {
                    String name = file.getName();
                    titleField.setText(name.substring(0, name.lastIndexOf('.')));
                }
                JOptionPane.showMessageDialog(this,
                    "File berhasil dimuat.\n" + file.getName(),
                    "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Gagal membaca file:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportResult(String format) {
        if (currentSummary == null) {
            JOptionPane.showMessageDialog(this,
                "Belum ada hasil ringkasan.\nRingkas teks terlebih dahulu.",
                "Perhatian", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Simpan sebagai " + format.toUpperCase());

        String defaultName = currentSummary.getRecord().getBookTitle()
                             .replaceAll("[^a-zA-Z0-9_]", "_") + "." + format;
        chooser.setSelectedFile(new File(defaultName));

        if ("txt".equals(format)) {
            chooser.setFileFilter(new FileNameExtensionFilter("Text File (*.txt)", "txt"));
        } else {
            chooser.setFileFilter(new FileNameExtensionFilter("PDF File (*.pdf)", "pdf"));
        }

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                if ("txt".equals(format)) {
                    fileExporter.exportTxt(currentSummary, file);
                } else {
                    fileExporter.exportPdf(currentSummary, file);
                }
                JOptionPane.showMessageDialog(this,
                    "File berhasil disimpan:\n" + file.getAbsolutePath(),
                    "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Gagal menyimpan file:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ─────────────────────────────────────────────────────────
    // Helper methods
    // ─────────────────────────────────────────────────────────

    private void showHistory() {
        new HistoryDialog(this, historyManager).showDialog();
    }

    private void clearAll() {
        titleField.setText("");
        inputArea.setText("");
        outputArea.setText("");
        methodLabel.setText(" Metode: —");
        statsLabel.setText(" Statistik: —");
        keywordPanel.removeAll();
        keywordPanel.repaint();
        currentSummary = null;
    }

    private void promptApiToken() {
        String token = JOptionPane.showInputDialog(this,
            "Masukkan Hugging Face API Token Anda:\n" +
            "(Dapatkan di: https://huggingface.co/settings/tokens)\n\n" +
            "Kosongkan untuk menggunakan mode offline (Rule-Based).",
            "API Token",
            JOptionPane.QUESTION_MESSAGE);

        if (token != null) {
            apiToken = token.trim();
            SummarizerFactory.getInstance().setApiToken(apiToken);
            String status = apiToken.isEmpty() ? "Mode Offline aktif." : "Token API berhasil disimpan.";
            JOptionPane.showMessageDialog(this, status, "Konfigurasi", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private String parseQuality(String displayValue) {
        if (displayValue == null) return SummarizerFactory.QUALITY_BALANCED;
        if (displayValue.contains("Cepat"))     return SummarizerFactory.QUALITY_FAST;
        if (displayValue.contains("Terbaik"))   return SummarizerFactory.QUALITY_BEST;
        return SummarizerFactory.QUALITY_BALANCED;
    }
}