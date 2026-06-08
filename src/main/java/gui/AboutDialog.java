package gui;

import java.awt.*;
import javax.swing.*;

/**
 * Dialog "Tentang Aplikasi".
 *
 * Konsep OOP: Inheritance
 * - Extends BaseDialog (Inheritance dari class induk abstrak)
 * - Override abstract method initComponents()
 */
public class AboutDialog extends BaseDialog {

    // Field tambahan spesifik untuk AboutDialog
    private final String appVersion;
    private final String appName;

    public AboutDialog(JFrame parent) {
        super(parent, "Tentang Aplikasi", true);
        this.appVersion = "1.0.0";
        this.appName = "Aplikasi Ringkasan Buku Otomatis";
    }

    /**
     * Implementasi abstract method dari BaseDialog (Inheritance + Override).
     */
    @Override
    protected void initComponents() {
        setSize(420, 340);
        setResizable(false);
        setLayout(new BorderLayout(10, 10));

        // ─── Icon & judul ───
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(24, 20, 10, 20));
        topPanel.setBackground(UIManager.getColor("Panel.background"));

        JLabel iconLabel = new JLabel("📚", SwingConstants.CENTER);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 52));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = createHeaderLabel(appName);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel versionLabel = new JLabel("Versi " + appVersion, SwingConstants.CENTER);
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        versionLabel.setForeground(Color.GRAY);

        topPanel.add(iconLabel);
        topPanel.add(Box.createVerticalStrut(8));
        topPanel.add(nameLabel);
        topPanel.add(Box.createVerticalStrut(4));
        topPanel.add(versionLabel);

        add(topPanel, BorderLayout.NORTH);

        // ─── Info detail ───
        JPanel infoPanel = new JPanel(new GridLayout(6, 2, 6, 4));
        infoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Informasi Teknis"
        ));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(4, 16, 4, 16),
            BorderFactory.createTitledBorder("Informasi Teknis")
        ));

        addInfoRow(infoPanel, "Bahasa", "Java 17");
        addInfoRow(infoPanel, "Framework GUI", "Java Swing");
        addInfoRow(infoPanel, "Build Tool", "Apache Maven");
        addInfoRow(infoPanel, "AI API", "Hugging Face Inference API");
        addInfoRow(infoPanel, "PDF Export", "Apache PDFBox 3.x");
        addInfoRow(infoPanel, "Mata Kuliah", "Pemrograman Berbasis Objek");

        add(infoPanel, BorderLayout.CENTER);

        // ─── Konsep OOP ───
        JPanel oopPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 6));
        oopPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 4, 16));
        for (String konsep : new String[]{"Encapsulation", "Interface", "Polymorphism", "Inheritance"}) {
            JLabel badge = new JLabel(konsep);
            badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(24, 95, 165), 1, true),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)
            ));
            badge.setForeground(new Color(24, 95, 165));
            badge.setFont(new Font("SansSerif", Font.PLAIN, 11));
            oopPanel.add(badge);
        }
        add(oopPanel, BorderLayout.SOUTH);
    }

    private void addInfoRow(JPanel panel, String key, String value) {
        JLabel keyLabel = new JLabel(key + " :");
        keyLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        JLabel valLabel = new JLabel(value);
        valLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panel.add(keyLabel);
        panel.add(valLabel);
    }
}