package main.java.app;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import main.java.gui.MainFrame;

/**
 * Main entry point untuk Aplikasi Ringkasan Buku Otomatis.
 * Kelas ini menginisialisasi aplikasi dan menjalankan GUI di Event Dispatch Thread.
 *
 * @author Mahasiswa PBO
 * @version 1.0.0
 */
public class Main {

    public static void main(String[] args) {
        // Set Look and Feel ke Nimbus untuk tampilan lebih modern
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Fallback ke default Look and Feel
            System.err.println("Nimbus L&F tidak tersedia, menggunakan default: " + e.getMessage());
        }

        // Jalankan GUI di Event Dispatch Thread (EDT) - praktik terbaik Swing
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}