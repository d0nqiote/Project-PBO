package com.mycompany.yummyrisolmanager.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Sidebar cokelat di sebelah kiri. Tombol yang aktif berwarna oranye.
 * Saat tombol diklik, nama menunya dikirim ke "saatPilih" (misalnya "Produk" atau "Keluar").
 */
public class PanelSidebar extends JPanel {

    public static final String[] MENU = {"Dashboard", "Produk", "Pelanggan", "Pesanan", "Laporan"};

    public PanelSidebar(String aktif, String peran, Consumer<String> saatPilih) {
        setLayout(new BorderLayout());
        setBackground(Tema.COKELAT_GELAP);
        setPreferredSize(new Dimension(240, 0));
        setBorder(new EmptyBorder(24, 20, 24, 20));

        JPanel atas = new JPanel(new BorderLayout());
        atas.setOpaque(false);
        atas.add(buatLogo(), BorderLayout.NORTH);
        atas.add(buatDaftarMenu(aktif, peran, saatPilih), BorderLayout.CENTER);

        JButton keluar = buatTombol("Keluar", false);
        keluar.addActionListener(e -> saatPilih.accept("Keluar"));

        add(atas, BorderLayout.NORTH);
        add(keluar, BorderLayout.SOUTH);
    }

    private JLabel buatLogo() {
        JLabel logo = new JLabel("<html>Yummy<br><font color=\"#F2B33D\">Risol</font> Manager</html>");
        logo.setFont(Tema.font(Font.BOLD, 20));
        logo.setForeground(Tema.PUTIH);
        logo.setBorder(new EmptyBorder(0, 0, 28, 0));
        return logo;
    }

    private JPanel buatDaftarMenu(String aktif, String peran, Consumer<String> saatPilih) {
        JPanel daftar = new JPanel(new GridLayout(MENU.length, 1, 0, 6));
        daftar.setOpaque(false);

        boolean admin = peran.equalsIgnoreCase("Admin");
        for (String nama : MENU) {
            JButton tombol = buatTombol(nama, nama.equals(aktif));
            // Sesuai rancangan: Produk dan Laporan hanya untuk Admin
            if (nama.equals("Produk") || nama.equals("Laporan")) {
                tombol.setEnabled(admin);
            }
            tombol.addActionListener(e -> saatPilih.accept(nama));
            daftar.add(tombol);
        }
        return daftar;
    }

    private JButton buatTombol(String teks, boolean aktif) {
        JButton tombol = new JButton(teks);
        tombol.setPreferredSize(new Dimension(200, 48));
        tombol.setFont(Tema.font(Font.PLAIN, 14));
        tombol.setForeground(Tema.KREM);
        tombol.setBackground(aktif ? Tema.ORANYE : Tema.COKELAT_GELAP);
        tombol.setHorizontalAlignment(SwingConstants.LEFT);
        tombol.setMargin(new Insets(0, 16, 0, 16));
        tombol.setBorderPainted(false);
        tombol.setFocusPainted(false);
        tombol.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return tombol;
    }
}