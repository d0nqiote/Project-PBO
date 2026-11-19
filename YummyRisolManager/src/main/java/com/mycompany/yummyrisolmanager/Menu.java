package com.mycompany.yummyrisolmanager;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.mycompany.yummyrisolmanager.ui.DashboardFrame;
import com.mycompany.yummyrisolmanager.ui.LoginFrame;
import com.mycompany.yummyrisolmanager.ui.PelangganFrame;
import com.mycompany.yummyrisolmanager.ui.ProdukFrame;
import com.mycompany.yummyrisolmanager.ui.Tema;
import com.mycompany.yummyrisolmanager.ui.LaporanFrame;
import com.mycompany.yummyrisolmanager.ui.PesananFrame;
import java.util.Collections;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Mengatur tampilan global dan perpindahan antar layar.
 * Main hanya memanggil class ini.
 */
public class Menu {

    public void mulai() {
        aturTema();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    /**
     * Membuka layar "layar" lalu menutup layar asal.
     * Layar yang belum dibuat hanya menampilkan pesan dan layar asal tetap terbuka.
     */
    public static void pindahKe(String layar, String peran, JFrame asal) {
        JFrame tujuan = switch (layar) {
            case "Dashboard" -> new DashboardFrame(peran);
            case "Produk" -> new ProdukFrame(peran);
            case "Pelanggan" -> new PelangganFrame(peran);
            case "Pesanan" -> new PesananFrame(peran);
            case "Laporan" -> new LaporanFrame(peran);
            default -> null;
        };

        if (tujuan == null) {
            JOptionPane.showMessageDialog(asal, "Layar " + layar + " belum tersedia.",
                    "Yummy Risol Manager", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // posisi dan ukuran sama dengan layar asal supaya perpindahan terasa mulus
        tujuan.setBounds(asal.getBounds());
        tujuan.setVisible(true);
        asal.dispose();
    }

    public static void keluar(JFrame asal) {
        asal.dispose();
        new LoginFrame().setVisible(true);
    }

    private void aturTema() {
        // Warna aksen (tombol utama, fokus, seleksi) jadi oranye
        FlatLaf.setGlobalExtraDefaults(Collections.singletonMap("@accentColor", "#B94A10"));
        FlatLightLaf.setup();

        // Sudut membulat
        UIManager.put("Component.arc", 10);
        UIManager.put("Button.arc", 10);
        UIManager.put("TextComponent.arc", 10);

        // Warna border field
        UIManager.put("Component.borderColor", Tema.BORDER);
    }
}