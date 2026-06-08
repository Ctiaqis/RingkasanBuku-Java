package gui;

import java.awt.*;
import javax.swing.*;

/**
 * Abstract base class untuk semua dialog dalam aplikasi.
 *
 * Konsep OOP: Inheritance
 * - HistoryDialog dan AboutDialog mewarisi class ini
 * - Menyediakan template method pattern: initComponents() abstract,
 *   dipanggil dari constructor melalui show()
 * - Protected fields bisa diakses oleh subclass
 */
public abstract class BaseDialog extends JDialog {

    // Protected: bisa diakses oleh subclass (Inheritance)
    protected JFrame parentFrame;
    protected String dialogTitle;

    public BaseDialog(JFrame parent, String title, boolean modal) {
        super(parent, title, modal);
        this.parentFrame = parent;
        this.dialogTitle = title;
        configureDialog();
    }

    /**
     * Konfigurasi dasar dialog — sama untuk semua dialog.
     */
    private void configureDialog() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(true);
    }

    /**
     * Template method: inisialisasi komponen GUI.
     * Wajib diimplementasikan oleh setiap subclass (abstract method).
     */
    protected abstract void initComponents();

    /**
     * Menampilkan dialog di tengah layar atau parent window.
     * Method ini memanggil initComponents() terlebih dahulu.
     */
    public void showDialog() {
        initComponents();
        pack();
        setMinimumSize(new Dimension(400, 300));
        setLocationRelativeTo(parentFrame);
        setVisible(true);
    }

    /**
     * Helper: membuat panel dengan padding.
     */
    protected JPanel createPaddedPanel(int padding) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
        return panel;
    }

    /**
     * Helper: membuat header label dengan font besar.
     */
    protected JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        return label;
    }

    /**
     * Helper: membuat separator dengan label.
     */
    protected JPanel createSectionHeader(String title) {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.add(new JLabel(title), BorderLayout.WEST);
        panel.add(new JSeparator(), BorderLayout.CENTER);
        return panel;
    }
}