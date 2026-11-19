package com.mycompany.yummyrisolmanager.ui;

import com.mycompany.yummyrisolmanager.Menu;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * Kerangka layar yang punya sidebar di kiri dan area konten krem di kanan.
 * Layar baru (Produk, Pelanggan) cukup mewarisi class ini lalu memanggil
 * pasangKonten() di akhir constructor-nya.
 */
public abstract class FrameDasar extends JFrame {

    protected final String peran;
    private final String layarAktif;

    protected FrameDasar(String peran, String layarAktif, String judulJendela) {
        this.peran = peran;
        this.layarAktif = layarAktif;

        setTitle("Yummy Risol Manager – " + judulJendela);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 680);
        setMinimumSize(new Dimension(1000, 620));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(new PanelSidebar(layarAktif, peran, this::pindah), BorderLayout.WEST);
    }

    /** Memasang isi layar di area krem. Dipanggil sekali di akhir constructor turunan. */
    protected void pasangKonten(JComponent isi) {
        JPanel konten = new JPanel(new BorderLayout());
        konten.setBackground(Tema.KREM);
        konten.setBorder(new EmptyBorder(28, 28, 28, 28));
        konten.add(isi, BorderLayout.CENTER);
        add(konten, BorderLayout.CENTER);
    }

    private void pindah(String tujuan) {
        if (tujuan.equals("Keluar")) {
            Menu.keluar(this);
        } else if (!tujuan.equals(layarAktif)) {
            Menu.pindahKe(tujuan, peran, this);
        }
    }
}