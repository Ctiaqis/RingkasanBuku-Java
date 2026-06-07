package app;

import gui.MainFrame;

import javax.swing.*;

/**
 * Entry point aplikasi Ringkasan Buku Otomatis.
 * Menginisialisasi Look and Feel dan menampilkan MainFrame.
 */
public class MainApp {

    public static void main(String[] args) {
        // Jalankan GUI di Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                // Gunakan system look and feel agar tampilan lebih natural
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Jika gagal, gunakan default Swing L&F
                System.err.println("Gagal mengatur Look and Feel: " + e.getMessage());
            }

            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}