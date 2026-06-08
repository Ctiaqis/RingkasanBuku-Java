package gui;

import history.SummaryHistoryManager;
import input.TextInputHandler;
import model.*;
import summarizer.*;
import util.FileExporter;
import util.StatisticsCalculator;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;

/**
 * Jendela utama aplikasi Ringkasan Buku Otomatis.
 * Ditulis manual dengan Java Swing, tanpa GUI Builder.
 */
public class MainFrame extends JFrame {

    // Komponen Input
    private JTextField txtJudul;
    private JTextArea txtInput;
    private JButton btnUploadTxt;
    private JButton btnUploadPdf;
    private JComboBox<String> cmbKualitas;
    private JComboBox<String> cmbPanjang;
    private JButton btnRingkas;

    // Komponen Output
    private JTextArea txtOutput;
    private JLabel lblMetode;
    private JLabel lblStatistik;
    private JLabel lblKeyword;
    private JButton btnRiwayat;
    private JButton btnSimpanTxt;
    private JButton btnSimpanPdf;

    // Status & Menu
    private JLabel lblStatus;
    private JProgressBar progressBar;

    private JMenuItem menuItemBaru, menuItemUploadTxt, menuItemUploadPdf;
    private JMenuItem menuItemSimpanTxt, menuItemSimpanPdf, menuItemKeluar;
    private JMenuItem menuItemBersihkanInput, menuItemBersihkanOutput, menuItemResetForm;
    private JMenuItem menuItemPetunjuk, menuItemTentang;

    // Dependencies
    private final SummaryHistoryManager historyManager;
    private final TextInputHandler inputHandler;
    private final StatisticsCalculator statsCalculator;
    private final FileExporter fileExporter;

    private BookSummary currentSummary;
    private String apiToken = "hf_DummyTokenKalianDisini";

    private static final String APP_TITLE = "Aplikasi Ringkasan Buku Otomatis";
    private static final String INPUT_PLACEHOLDER = "Tempel teks buku/artikel di sini atau gunakan tombol Upload TXT/PDF...";
    private static final String OUTPUT_PLACEHOLDER = "Hasil ringkasan akan muncul di sini setelah tombol RINGKAS ditekan.";

    public MainFrame() {
        this.historyManager = new SummaryHistoryManager();
        this.inputHandler = new TextInputHandler();
        this.statsCalculator = new StatisticsCalculator();
        this.fileExporter = new FileExporter();

        initFrame();
        buildUI();
        setupTextAreaStyle();

        SummarizerFactory.getInstance().setApiToken(apiToken);
    }

    private void initFrame() {
        setTitle(APP_TITLE);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(950, 620));
        setPreferredSize(new Dimension(1050, 680));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int opt = JOptionPane.showConfirmDialog(MainFrame.this,
                        "Keluar dari aplikasi?", "Konfirmasi",
                        JOptionPane.YES_NO_OPTION);
                if (opt == JOptionPane.YES_OPTION)
                    dispose();
            }
        });
    }

    private void buildUI() {
        setJMenuBar(buildMenuBar());

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildInputPanel(), buildOutputPanel());
        splitPane.setResizeWeight(0.48);
        splitPane.setDividerSize(5);

        root.add(splitPane, BorderLayout.CENTER);
        root.add(buildStatusBar(), BorderLayout.SOUTH);

        setContentPane(root);
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel buildInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Input",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 12)));

        // Judul Buku
        JPanel titleRow = new JPanel(new BorderLayout(6, 0));
        titleRow.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        titleRow.add(new JLabel("Judul Buku:"), BorderLayout.WEST);
        txtJudul = new JTextField();
        txtJudul.setFont(new Font("SansSerif", Font.PLAIN, 13));
        titleRow.add(txtJudul, BorderLayout.CENTER);
        panel.add(titleRow, BorderLayout.NORTH);

        // Input Teks
        txtInput = new JTextArea();
        txtInput.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtInput.setLineWrap(true);
        txtInput.setWrapStyleWord(true);
        txtInput.setMargin(new Insets(8, 8, 8, 8));
        txtInput.setTabSize(4);
        txtInput.setText(INPUT_PLACEHOLDER);
        txtInput.setForeground(Color.GRAY);
        txtInput.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtInput.getText().equals(INPUT_PLACEHOLDER)) {
                    txtInput.setText("");
                    txtInput.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtInput.getText().trim().isEmpty()) {
                    txtInput.setText(INPUT_PLACEHOLDER);
                    txtInput.setForeground(Color.GRAY);
                }
            }
        });

        JScrollPane inputScroll = new JScrollPane(txtInput);
        inputScroll.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
        panel.add(inputScroll, BorderLayout.CENTER);

        // Control Panel
        panel.add(buildInputControlPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildInputControlPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 4));
        panel.setBorder(BorderFactory.createEmptyBorder(4, 6, 6, 6));

        JPanel uploadRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btnUploadTxt = new JButton("⬆ Upload TXT");
        btnUploadPdf = new JButton("📄 Upload PDF");
        btnUploadTxt.addActionListener(e -> uploadFile("txt"));
        btnUploadPdf.addActionListener(e -> uploadFile("pdf"));
        uploadRow.add(btnUploadTxt);
        uploadRow.add(btnUploadPdf);
        panel.add(uploadRow, BorderLayout.NORTH);

        JPanel settingsRow = new JPanel(new GridLayout(2, 1, 0, 4));

        JPanel qualityRow = new JPanel(new BorderLayout(8, 0));
        qualityRow.add(new JLabel("Kualitas Ringkasan:"), BorderLayout.WEST);
        cmbKualitas = new JComboBox<>(new String[] {
                "Cepat - Offline",
                "Otomatis - Disarankan"
        });
        cmbKualitas.setSelectedIndex(0);
        qualityRow.add(cmbKualitas, BorderLayout.CENTER);

        JPanel lengthRow = new JPanel(new BorderLayout(8, 0));
        lengthRow.add(new JLabel("Panjang Ringkasan:"), BorderLayout.WEST);
        cmbPanjang = new JComboBox<>(new String[] {
                "Ringkasan Pendek",
                "Ringkasan Detail"
        });
        cmbPanjang.setSelectedIndex(0);
        lengthRow.add(cmbPanjang, BorderLayout.CENTER);

        settingsRow.add(qualityRow);
        settingsRow.add(lengthRow);

        JPanel bottomRow = new JPanel(new BorderLayout(8, 0));
        bottomRow.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        bottomRow.add(settingsRow, BorderLayout.CENTER);

        btnRingkas = new JButton("▶ RINGKAS");
        btnRingkas.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnRingkas.setBackground(new Color(24, 95, 165));
        btnRingkas.setForeground(Color.WHITE);
        btnRingkas.setFocusPainted(false);
        btnRingkas.setOpaque(true);
        btnRingkas.setBorderPainted(false);
        btnRingkas.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRingkas.setPreferredSize(new Dimension(130, 40));
        btnRingkas.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                if (btnRingkas.isEnabled())
                    btnRingkas.setBackground(new Color(40, 120, 200));
            }

            public void mouseExited(MouseEvent evt) {
                if (btnRingkas.isEnabled())
                    btnRingkas.setBackground(new Color(24, 95, 165));
            }
        });
        btnRingkas.addActionListener(e -> btnRingkasActionPerformed());
        bottomRow.add(btnRingkas, BorderLayout.EAST);

        panel.add(bottomRow, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Hasil Ringkasan",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 12)));

        txtOutput = new JTextArea();
        txtOutput.setMargin(new Insets(10, 10, 10, 10));
        txtOutput.setBackground(new Color(250, 250, 252));
        txtOutput.setText(OUTPUT_PLACEHOLDER);

        JScrollPane outputScroll = new JScrollPane(txtOutput);
        outputScroll.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
        panel.add(outputScroll, BorderLayout.CENTER);

        panel.add(buildOutputInfoPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildOutputInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 4));
        panel.setBorder(BorderFactory.createEmptyBorder(4, 6, 6, 6));

        lblMetode = new JLabel("Metode: -");
        lblMetode.setFont(new Font("SansSerif", Font.ITALIC, 12));
        lblMetode.setForeground(new Color(59, 109, 17));
        panel.add(lblMetode, BorderLayout.NORTH);

        lblStatistik = new JLabel("Kata awal: - | Kata ringkasan: - | Kompresi: -");
        lblStatistik.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lblStatistik.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Statistik Ringkasan"),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)));

        lblKeyword = new JLabel("Keyword: -");
        lblKeyword.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblKeyword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Keyword Utama"),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)));

        JPanel infoGroup = new JPanel(new GridLayout(2, 1, 0, 4));
        infoGroup.add(lblStatistik);
        infoGroup.add(lblKeyword);
        panel.add(infoGroup, BorderLayout.CENTER);

        JPanel exportRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        btnRiwayat = new JButton("📋 Riwayat");
        btnSimpanTxt = new JButton("💾 Simpan TXT");
        btnSimpanPdf = new JButton("📑 Simpan PDF");

        btnSimpanTxt.setEnabled(false);
        btnSimpanPdf.setEnabled(false);

        btnRiwayat.addActionListener(e -> showHistory());
        btnSimpanTxt.addActionListener(e -> exportResult("txt"));
        btnSimpanPdf.addActionListener(e -> exportResult("pdf"));

        exportRow.add(btnRiwayat);
        exportRow.add(btnSimpanTxt);
        exportRow.add(btnSimpanPdf);
        panel.add(exportRow, BorderLayout.SOUTH);
        return panel;
    }

    private JMenuBar buildMenuBar() {
        JMenuBar mb = new JMenuBar();

        JMenu menuFile = new JMenu("File");
        menuItemBaru = new JMenuItem("Baru");
        menuItemUploadTxt = new JMenuItem("Upload TXT");
        menuItemUploadPdf = new JMenuItem("Upload PDF");
        menuItemSimpanTxt = new JMenuItem("Simpan TXT");
        menuItemSimpanPdf = new JMenuItem("Simpan PDF");
        menuItemKeluar = new JMenuItem("Keluar");

        menuItemSimpanTxt.setEnabled(false);
        menuItemSimpanPdf.setEnabled(false);

        menuItemBaru.addActionListener(e -> resetForm());
        menuItemUploadTxt.addActionListener(e -> uploadFile("txt"));
        menuItemUploadPdf.addActionListener(e -> uploadFile("pdf"));
        menuItemSimpanTxt.addActionListener(e -> exportResult("txt"));
        menuItemSimpanPdf.addActionListener(e -> exportResult("pdf"));
        menuItemKeluar.addActionListener(e -> System.exit(0));

        menuFile.add(menuItemBaru);
        menuFile.addSeparator();
        menuFile.add(menuItemUploadTxt);
        menuFile.add(menuItemUploadPdf);
        menuFile.addSeparator();
        menuFile.add(menuItemSimpanTxt);
        menuFile.add(menuItemSimpanPdf);
        menuFile.addSeparator();
        menuFile.add(menuItemKeluar);

        JMenu menuTampilan = new JMenu("Tampilan");
        menuItemBersihkanInput = new JMenuItem("Bersihkan Input");
        menuItemBersihkanOutput = new JMenuItem("Bersihkan Output");
        menuItemResetForm = new JMenuItem("Reset Form");

        menuItemBersihkanInput.addActionListener(e -> {
            txtInput.setText(INPUT_PLACEHOLDER);
            txtInput.setForeground(Color.GRAY);
        });
        menuItemBersihkanOutput.addActionListener(e -> {
            txtOutput.setText(OUTPUT_PLACEHOLDER);
            txtOutput.setForeground(Color.GRAY);
        });
        menuItemResetForm.addActionListener(e -> resetForm());

        menuTampilan.add(menuItemBersihkanInput);
        menuTampilan.add(menuItemBersihkanOutput);
        menuTampilan.addSeparator();
        menuTampilan.add(menuItemResetForm);

        JMenu menuBantuan = new JMenu("Bantuan");
        menuItemPetunjuk = new JMenuItem("Petunjuk Penggunaan");
        menuItemTentang = new JMenuItem("Tentang Aplikasi");

        menuItemPetunjuk.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "1. Masukkan teks buku/artikel ke bagian Input.\n" +
                        "2. Pilih Kualitas Ringkasan dan Panjang Ringkasan.\n" +
                        "3. Klik tombol RINGKAS.\n" +
                        "4. Simpan hasilnya ke TXT atau PDF.",
                "Petunjuk", JOptionPane.INFORMATION_MESSAGE));
        menuItemTentang.addActionListener(e -> new AboutDialog(this).showDialog());

        menuBantuan.add(menuItemPetunjuk);
        menuBantuan.add(menuItemTentang);

        mb.add(menuFile);
        mb.add(menuTampilan);
        mb.add(menuBantuan);
        return mb;
    }

    private JPanel buildStatusBar() {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        panel.setPreferredSize(new Dimension(0, 24));

        progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(150, 16));
        progressBar.setVisible(false);

        lblStatus = new JLabel("  Siap");
        lblStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblStatus.setForeground(Color.GRAY);

        panel.add(lblStatus, BorderLayout.WEST);
        panel.add(progressBar, BorderLayout.EAST);
        return panel;
    }

    private SummaryQuality getSelectedQuality() {
        String selected = cmbKualitas.getSelectedItem().toString();

        if (selected.equals("Cepat - Offline")) {
            return SummaryQuality.CEPAT;
        }

        return SummaryQuality.OTOMATIS;
    }

    private void btnRingkasActionPerformed() {
        String inputText = txtInput.getText().trim();
        String bookTitle = txtJudul.getText().trim();

        if (inputText.isEmpty() || inputText.equals(INPUT_PLACEHOLDER)) {
            JOptionPane.showMessageDialog(this, "Teks tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (bookTitle.isEmpty())
            bookTitle = "Tanpa Judul";

        SummaryQuality quality = getSelectedQuality();

        int lengthIndex = cmbPanjang.getSelectedIndex();
        SummaryLength length = SummaryLength.PENDEK;
        if (lengthIndex == 1)
            length = SummaryLength.DETAIL;

        final String finalBookTitle = bookTitle;
        final SummaryQuality finalQuality = quality;
        final SummaryLength finalLength = length;

        btnRingkas.setEnabled(false);
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);
        lblStatus.setText("Meringkas teks...");

        txtOutput.setForeground(Color.BLACK);
        txtOutput.setText("⏳ Sedang memproses...");

        SwingWorker<BookSummary, Void> worker = new SwingWorker<>() {
            @Override
            protected BookSummary doInBackground() throws Exception {
                Summarizer summarizer = SummarizerFactory.getInstance().create(finalQuality);
                String summary = "";
                String methodName = summarizer.getMethodName();

                if (summarizer instanceof FallbackSummarizer fs) {
                    try {
                        summary = summarizer.summarize(inputText, finalLength);
                        if (fs.isUsedFallback()) {
                            methodName = "Rule-Based Summarizer (Fallback)";
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(MainFrame.this,
                                        "AI Summarizer tidak tersedia.\nSistem akan menggunakan metode Rule-Based Summarizer.",
                                        "Fallback Warning", JOptionPane.WARNING_MESSAGE);
                            });
                        } else {
                            methodName = "AI Summarizer";
                        }
                    } catch (Exception e) {
                        throw e;
                    }
                } else {
                    summary = summarizer.summarize(inputText, finalLength);
                    methodName = "Rule-Based Summarizer";
                }

                SummaryStatistics stats = statsCalculator.calculate(inputText, summary);
                SummaryRecord record = new SummaryRecord(finalBookTitle, cmbKualitas.getSelectedItem().toString(),
                        methodName, summary);
                return new BookSummary(record, stats, inputText, summary);
            }

            @Override
            protected void done() {
                btnRingkas.setEnabled(true);
                btnRingkas.setBackground(new Color(24, 95, 165));
                progressBar.setVisible(false);
                progressBar.setIndeterminate(false);

                try {
                    currentSummary = get();
                    displayResult(currentSummary);
                    historyManager.save(currentSummary.getRecord());
                    lblStatus.setText("Ringkasan berhasil dibuat.");

                    btnSimpanTxt.setEnabled(true);
                    btnSimpanPdf.setEnabled(true);
                    menuItemSimpanTxt.setEnabled(true);
                    menuItemSimpanPdf.setEnabled(true);
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    txtOutput.setText("");
                    txtOutput.setForeground(Color.GRAY);
                    txtOutput.setText(OUTPUT_PLACEHOLDER);

                    JOptionPane.showMessageDialog(MainFrame.this, "Gagal memproses ringkasan:\n" + cause.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    lblStatus.setText("Gagal membuat ringkasan.");
                }
            }
        };
        worker.execute();
    }

    private void displayResult(BookSummary summary) {
        txtOutput.setText(summary.getOutputText());
        txtOutput.setCaretPosition(0);

        lblMetode.setText("Metode: " + summary.getRecord().getMethodUsed());

        SummaryStatistics stats = summary.getStatistics();
        lblStatistik.setText(String.format("Kata awal: %,d | Kata ringkasan: %,d | Kompresi: %.0f%%",
                stats.getOriginalWordCount(), stats.getSummaryWordCount(), stats.getCompressionRate()));

        List<String> keywords = stats.getKeywords();
        String kwString = keywords != null && !keywords.isEmpty() ? String.join(", ", keywords) : "-";
        lblKeyword.setText("Keyword: " + kwString);
    }

    private void resetForm() {
        txtJudul.setText("");
        txtInput.setText(INPUT_PLACEHOLDER);
        txtInput.setForeground(Color.GRAY);
        txtOutput.setText(OUTPUT_PLACEHOLDER);
        txtOutput.setForeground(Color.GRAY);

        cmbKualitas.setSelectedIndex(0);
        cmbPanjang.setSelectedIndex(0);

        lblMetode.setText("Metode: -");
        lblStatistik.setText("Kata awal: - | Kata ringkasan: - | Kompresi: -");
        lblKeyword.setText("Keyword: -");
        lblStatus.setText("Form berhasil di-reset.");

        currentSummary = null;

        btnSimpanTxt.setEnabled(false);
        btnSimpanPdf.setEnabled(false);
        menuItemSimpanTxt.setEnabled(false);
        menuItemSimpanPdf.setEnabled(false);
    }

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
            lblStatus.setText("Memuat file " + type.toUpperCase() + "...");
            try {
                String text = inputHandler.readFromFile(file);
                txtInput.setText(text);
                txtInput.setForeground(Color.BLACK);
                if (txtJudul.getText().trim().isEmpty()) {
                    String name = file.getName();
                    txtJudul.setText(name.substring(0, name.lastIndexOf('.')));
                }
                lblStatus.setText("Siap");
            } catch (Exception ex) {
                lblStatus.setText("Gagal membaca file.");
                JOptionPane.showMessageDialog(this, "Gagal membaca file:\n" + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportResult(String format) {
        if (currentSummary == null)
            return;
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Simpan sebagai " + format.toUpperCase());
        String defaultName = currentSummary.getRecord().getBookTitle().replaceAll("[^a-zA-Z0-9_]", "_") + "." + format;
        chooser.setSelectedFile(new File(defaultName));

        if ("txt".equals(format))
            chooser.setFileFilter(new FileNameExtensionFilter("Text File (*.txt)", "txt"));
        else
            chooser.setFileFilter(new FileNameExtensionFilter("PDF File (*.pdf)", "pdf"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                if ("txt".equals(format))
                    fileExporter.exportTxt(currentSummary, file);
                else
                    fileExporter.exportPdf(currentSummary, file);
                lblStatus.setText("Berhasil menyimpan " + format.toUpperCase() + ".");
                JOptionPane.showMessageDialog(this, "File berhasil disimpan:\n" + file.getAbsolutePath(), "Berhasil",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                lblStatus.setText("Gagal menyimpan file.");
                JOptionPane.showMessageDialog(this, "Gagal menyimpan file:\n" + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showHistory() {
        new HistoryDialog(this, historyManager).showDialog();
    }

    private void setupTextAreaStyle() {
        Font textAreaFont = new Font("Consolas", Font.PLAIN, 14);
        // Fallback jika Consolas tidak ditemukan
        if (textAreaFont.getFamily().equals(Font.DIALOG)) {
            textAreaFont = new Font(Font.MONOSPACED, Font.PLAIN, 16);
        }

        Color placeholderColor = Color.GRAY;

        // Styling txtInput
        txtInput.setFont(textAreaFont);
        txtInput.setLineWrap(true);
        txtInput.setWrapStyleWord(true);
        if (txtInput.getText().equals(INPUT_PLACEHOLDER)) {
            txtInput.setForeground(placeholderColor);
        }

        // Styling txtOutput
        txtOutput.setFont(textAreaFont);
        txtOutput.setLineWrap(true);
        txtOutput.setWrapStyleWord(true);
        txtOutput.setEditable(false);
        if (txtOutput.getText().equals(OUTPUT_PLACEHOLDER)) {
            txtOutput.setForeground(placeholderColor);
        }
    }
}