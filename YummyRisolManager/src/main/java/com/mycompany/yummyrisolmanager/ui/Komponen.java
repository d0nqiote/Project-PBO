package com.mycompany.yummyrisolmanager.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.AbstractBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.text.JTextComponent;

/**
 * Pembuat komponen yang dipakai bersama oleh layar Produk dan Pelanggan
 * (judul halaman, tombol, kolom isian, kartu putih, dan gaya tabel).
 */
public class Komponen {

    /** Warna baris tabel yang sedang dipilih. */
    public static final Color WARNA_PILIH = new Color(0xF6E7D8);

    private static final int TINGGI_ISIAN = 38;

    /** Jarak isi sel tabel dari tepi kolom (berlaku juga untuk judul kolom). */
    private static final int JARAK_KIRI_SEL = 12;
    private static final int JARAK_KANAN_SEL = 8;

    /** Padding kiri-kanan di dalam Chip (lihat Chip.java). */
    private static final int PADDING_CHIP = 10;

    // ===================== LABEL, TOMBOL, ISIAN =====================

    public static JLabel label(String teks, int ukuran, int gaya, Color warna) {
        JLabel label = new JLabel(teks);
        label.setFont(Tema.font(gaya, ukuran));
        label.setForeground(warna);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    /** Tombol oranye berisi (Simpan, + Tambah). */
    public static JButton tombolUtama(String teks, int lebar, int tinggi) {
        JButton tombol = new JButton(teks);
        tombol.setPreferredSize(new Dimension(lebar, tinggi));
        tombol.setFont(Tema.font(Font.BOLD, 14));
        tombol.setForeground(Tema.PUTIH);
        tombol.setBackground(Tema.ORANYE);
        tombol.setBorderPainted(false);
        tombol.setFocusPainted(false);
        tombol.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return tombol;
    }

    /** Tombol putih dengan garis oranye (Ubah, Hapus). */
    public static JButton tombolOutline(String teks, int lebar, int tinggi) {
        JButton tombol = new JButton(teks);
        tombol.setPreferredSize(new Dimension(lebar, tinggi));
        tombol.setFont(Tema.font(Font.BOLD, 14));
        tombol.setForeground(Tema.ORANYE);
        tombol.setBackground(Tema.PUTIH);
        tombol.setBorder(new BordaBulat(Tema.ORANYE, 10));
        tombol.setFocusPainted(false);
        tombol.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return tombol;
    }

    public static JTextField kolomTeks() {
        JTextField kolom = new JTextField();
        kolom.setFont(Tema.font(Font.PLAIN, 14));
        kolom.setForeground(Tema.COKELAT_GELAP);
        return kolom;
    }

    /** Kolom pencarian dengan teks petunjuk "Cari..." (petunjuk tampil lewat FlatLaf). */
    public static JTextField kolomCari(int lebar, int tinggi) {
        JTextField kolom = kolomTeks();
        kolom.putClientProperty("JTextField.placeholderText", "Cari...");
        kolom.setPreferredSize(new Dimension(lebar, tinggi));
        return kolom;
    }

    public static JComboBox<String> pilihan(String... isi) {
        JComboBox<String> kotak = new JComboBox<>(isi);
        kotak.setFont(Tema.font(Font.PLAIN, 14));
        return kotak;
    }

    /** Label di atas, kolom isian di bawahnya, tinggi isian dibuat seragam. */
    public static JPanel kolomForm(String judul, JComponent isian) {
        JPanel kolom = new JPanel();
        kolom.setOpaque(false);
        kolom.setLayout(new BoxLayout(kolom, BoxLayout.Y_AXIS));

        isian.setAlignmentX(Component.LEFT_ALIGNMENT);
        isian.setPreferredSize(new Dimension(isian.getPreferredSize().width, TINGGI_ISIAN));
        isian.setMaximumSize(new Dimension(Integer.MAX_VALUE, TINGGI_ISIAN));

        kolom.add(label(judul, 13, Font.BOLD, Tema.COKELAT_GELAP));
        kolom.add(Box.createVerticalStrut(4));
        kolom.add(isian);
        return kolom;
    }

    /** Jalankan aksi setiap kali isi kolom teks berubah (dipakai untuk pencarian langsung). */
    public static void saatBerubah(JTextComponent kolom, Runnable aksi) {
        kolom.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                aksi.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                aksi.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                aksi.run();
            }
        });
    }

    // ===================== PANEL =====================

    /** Judul halaman di kiri, komponen (pencarian, tombol) di kanan. */
    public static JPanel header(String judul, String subjudul, JComponent kanan) {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel teks = new JPanel();
        teks.setOpaque(false);
        teks.setLayout(new BoxLayout(teks, BoxLayout.Y_AXIS));
        teks.add(label(judul, 26, Font.BOLD, Tema.COKELAT_GELAP));
        teks.add(label(subjudul, 14, Font.PLAIN, Tema.TEKS_ABU));

        header.add(teks, BorderLayout.WEST);
        header.add(kanan, BorderLayout.EAST);
        return header;
    }

    /** Kartu putih bersudut bulat dengan jarak isi yang diberikan. */
    public static PanelBulat kartu(int atas, int kiri, int bawah, int kanan) {
        PanelBulat kartu = new PanelBulat(14);
        kartu.setBackground(Tema.PUTIH);
        kartu.setLayout(new BorderLayout(0, 12));
        kartu.setBorder(new EmptyBorder(atas, kiri, bawah, kanan));
        return kartu;
    }

    // ===================== TABEL =====================

    /** Memberi gaya tabel sesuai desain dan mengembalikan JScrollPane pembungkusnya. */
    public static JScrollPane gayakanTabel(JTable tabel) {
        tabel.setRowHeight(40);
        tabel.setShowGrid(false);
        tabel.setIntercellSpacing(new Dimension(0, 0));
        tabel.setFont(Tema.font(Font.PLAIN, 14));
        tabel.setForeground(Tema.COKELAT_GELAP);
        tabel.setBackground(Tema.PUTIH);
        tabel.setFillsViewportHeight(true);
        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabel.setDefaultRenderer(Object.class, new RendererTeks());

        JTableHeader header = tabel.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 36));
        header.setDefaultRenderer(new RendererHeader());

        JScrollPane scroll = new JScrollPane(tabel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Tema.PUTIH);
        return scroll;
    }

    /** Jarak kiri-kanan sel dan garis tipis di bawah baris. */
    public static CompoundBorder borderBaris() {
        return new CompoundBorder(new MatteBorder(0, 0, 1, 0, Tema.BORDER),
                new EmptyBorder(0, JARAK_KIRI_SEL, 0, JARAK_KANAN_SEL));
    }

    /**
     * Sama seperti borderBaris(), tapi untuk sel yang diawali chip: jarak kiri dikurangi
     * lebar padding chip, supaya TEKS di dalam chip sejajar dengan teks sel lain.
     */
    public static CompoundBorder borderBarisChip() {
        return new CompoundBorder(new MatteBorder(0, 0, 1, 0, Tema.BORDER),
                new EmptyBorder(0, JARAK_KIRI_SEL - PADDING_CHIP, 0, JARAK_KANAN_SEL));
    }

    public static String formatRupiah(double nilai) {
        Locale indonesia = new Locale.Builder().setLanguage("id").setRegion("ID").build();
        NumberFormat format = NumberFormat.getInstance(indonesia);
        format.setMaximumFractionDigits(0);
        return "Rp " + format.format(nilai);
    }

    // ===================== RENDERER & BORDER =====================

    /** Sel teks biasa. Turunannya cukup menimpa ubah() untuk mengatur teks yang tampil. */
    public static class RendererTeks extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable tabel, Object nilai,
                boolean terpilih, boolean fokus, int baris, int kolom) {
            super.getTableCellRendererComponent(tabel, ubah(nilai), false, false, baris, kolom);
            setHorizontalAlignment(SwingConstants.LEFT);   // semua isi tabel rata kiri
            setBorder(borderBaris());
            setForeground(Tema.COKELAT_GELAP);
            setBackground(terpilih ? WARNA_PILIH : Tema.PUTIH);
            return this;
        }

        /** Mengubah nilai di model menjadi teks yang ditampilkan. */
        protected Object ubah(Object nilai) {
            return nilai;
        }
    }

    private static class RendererHeader extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable tabel, Object nilai,
                boolean terpilih, boolean fokus, int baris, int kolom) {
            super.getTableCellRendererComponent(tabel, nilai, false, false, baris, kolom);
            setFont(Tema.font(Font.BOLD, 12));
            setForeground(Tema.TEKS_ABU);
            setBackground(Tema.PUTIH);
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(new CompoundBorder(new MatteBorder(0, 0, 2, 0, Tema.BORDER),
                    new EmptyBorder(0, JARAK_KIRI_SEL, 0, JARAK_KANAN_SEL)));
            return this;
        }
    }

    /** Garis sudut bulat untuk tombol outline. */
    private static class BordaBulat extends AbstractBorder {

        private final Color warna;
        private final int arc;

        BordaBulat(Color warna, int arc) {
            this.warna = warna;
            this.arc = arc;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int lebar, int tinggi) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // garis 1px digambar di tengah piksel (+0.5) supaya keempat sisi sama tegas;
            // tanpa ini sisi kiri dan atas terlihat pudar / seperti kepotong
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.setColor(warna);
            g2.draw(new RoundRectangle2D.Double(x + 0.5, y + 0.5, lebar - 1, tinggi - 1, arc, arc));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 16, 4, 16);
        }
    }
}