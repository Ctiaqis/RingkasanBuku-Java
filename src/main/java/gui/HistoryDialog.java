package gui;

import history.SummaryHistoryManager;
import model.SummaryRecord;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Dialog untuk menampilkan riwayat ringkasan.
 *
 * Konsep OOP: Inheritance
 * - Extends BaseDialog (mewarisi konfigurasi dasar dialog)
 * - Override abstract method initComponents()
 */
public class HistoryDialog extends BaseDialog {

    // Field tambahan yang spesifik untuk HistoryDialog
    private SummaryHistoryManager historyManager;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private JTextArea detailArea;
    private List<SummaryRecord> records;

    private static final String[] COLUMNS = {
        "Judul Buku", "Tanggal", "Kualitas", "Metode"
    };

    public HistoryDialog(JFrame parent, SummaryHistoryManager historyManager) {
        super(parent, "Riwayat Ringkasan", true);
        this.historyManager = historyManager;
    }

    /**
     * Implementasi abstract method dari BaseDialog (Inheritance + Override).
     * Membangun seluruh komponen GUI dialog ini.
     */
    @Override
    protected void initComponents() {
        setSize(750, 520);
        setLayout(new BorderLayout(8, 8));

        // ─── Header ───
        JPanel headerPanel = new JPanel(new BorderLayout(8, 0));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 14, 4, 14));
        headerPanel.add(createHeaderLabel("📋  Riwayat Ringkasan"), BorderLayout.WEST);

        JButton refreshBtn = new JButton("↻ Muat Ulang");
        refreshBtn.addActionListener(e -> loadHistory());
        headerPanel.add(refreshBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // ─── Split pane: tabel (kiri/atas) + detail (kanan/bawah) ───
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.55);
        splitPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        // Tabel riwayat
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        historyTable = new JTable(tableModel);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyTable.setRowHeight(24);
        historyTable.getTableHeader().setReorderingAllowed(false);

        // Set lebar kolom
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(220);
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(130);
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(90);
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(200);

        // Event: klik baris → tampilkan detail ringkasan
        historyTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelectedDetail();
            }
        });

        splitPane.setTopComponent(new JScrollPane(historyTable));

        // Area detail ringkasan
        detailArea = new JTextArea();
        detailArea.setEditable(false);
        detailArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        detailArea.setLineWrap(true);
        detailArea.setWrapStyleWord(true);
        detailArea.setMargin(new Insets(8, 8, 8, 8));

        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.add(new JLabel("  Detail Ringkasan:"), BorderLayout.NORTH);
        detailPanel.add(new JScrollPane(detailArea), BorderLayout.CENTER);
        splitPane.setBottomComponent(detailPanel);

        add(splitPane, BorderLayout.CENTER);

        // ─── Footer tombol ───
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));

        JButton deleteBtn = new JButton("🗑 Hapus");
        deleteBtn.addActionListener(e -> deleteSelected());

        JButton clearBtn = new JButton("⚠ Hapus Semua");
        clearBtn.addActionListener(e -> clearAll());

        JButton closeBtn = new JButton("Tutup");
        closeBtn.addActionListener(e -> dispose());

        btnPanel.add(deleteBtn);
        btnPanel.add(clearBtn);
        btnPanel.add(closeBtn);
        add(btnPanel, BorderLayout.SOUTH);

        // Muat data riwayat
        loadHistory();
    }

    /**
     * Memuat riwayat dari SummaryHistoryManager dan mengisi tabel.
     */
    public void loadHistory() {
        records = historyManager.loadAll();
        tableModel.setRowCount(0);

        if (records.isEmpty()) {
            detailArea.setText("Belum ada riwayat ringkasan.");
            return;
        }

        for (SummaryRecord rec : records) {
            tableModel.addRow(new Object[]{
                rec.getBookTitle(),
                rec.getFormattedDate(),
                rec.getQuality(),
                rec.getMethodUsed()
            });
        }
    }

    /**
     * Menampilkan detail ringkasan dari baris yang dipilih.
     */
    private void showSelectedDetail() {
        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < records.size()) {
            SummaryRecord rec = records.get(selectedRow);
            detailArea.setText(
                "Judul   : " + rec.getBookTitle() + "\n" +
                "Tanggal : " + rec.getFormattedDate() + "\n" +
                "Kualitas: " + rec.getQuality() + "\n" +
                "Metode  : " + rec.getMethodUsed() + "\n\n" +
                "─────────────────────────────────────\n\n" +
                rec.getSummaryText()
            );
            detailArea.setCaretPosition(0);
        }
    }

    private void deleteSelected() {
        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Pilih riwayat yang ingin dihapus.",
                "Perhatian", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Hapus riwayat ini?", "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String id = records.get(selectedRow).getId();
                historyManager.delete(id);
                loadHistory();
                detailArea.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Gagal menghapus riwayat: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearAll() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Hapus SEMUA riwayat ringkasan?", "Konfirmasi Hapus Semua",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                historyManager.clearAll();
                loadHistory();
                detailArea.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Gagal menghapus riwayat: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}