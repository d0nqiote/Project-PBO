package com.mycompany.yummyrisolmanager.ui;

import com.mycompany.yummyrisolmanager.db.PesananRepository;
import com.mycompany.yummyrisolmanager.db.ProdukRepository;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import com.mycompany.yummyrisolmanager.Menu;

public class DashboardFrame extends JFrame {

    private static final int JUMLAH_PESANAN_TERBARU = 4;

    /** Jarak kiri-kanan isi sel tabel "Pesanan terbaru". */
    private static final int JARAK_SEL = 16;

    private final String peran;

    // sidebar
    private JButton btnDashboard;
    private JButton btnProduk;
    private JButton btnPelanggan;
    private JButton btnPesanan;
    private JButton btnLaporan;
    private JButton btnKeluar;

    // header
    private JButton btnBuatPesanan;

    // banner dan kartu
    private JPanel panelBanner;
    private JLabel lblPeringatan;
    private JLabel lblPesananHariIni;
    private JLabel lblTotalPenjualan;
    private JLabel lblStokHampir;

    // tabel
    private JTable tblPesananTerbaru;
    private DefaultTableModel modelTabel;

    public DashboardFrame(String peran) {
        this.peran = peran;

        setTitle("Yummy Risol Manager – Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 680);
        setMinimumSize(new Dimension(1000, 620));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buatSidebar(), BorderLayout.WEST);
        add(buatKonten(), BorderLayout.CENTER);

        aturHakAkses();
        pasangAksiTombol();
        muatData();
    }

    // ===================== SIDEBAR =====================

    private JPanel buatSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(Tema.COKELAT_GELAP);
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(new EmptyBorder(24, 20, 24, 20));

        btnKeluar = buatTombolMenu("Keluar", false);

        sidebar.add(buatBagianAtasSidebar(), BorderLayout.NORTH);
        sidebar.add(btnKeluar, BorderLayout.SOUTH);
        return sidebar;
    }

    private JPanel buatBagianAtasSidebar() {
        JPanel atas = new JPanel(new BorderLayout());
        atas.setOpaque(false);

        JLabel logo = new JLabel("<html>Yummy<br><font color=\"#F2B33D\">Risol</font> Manager</html>");
        logo.setFont(Tema.font(Font.BOLD, 20));
        logo.setForeground(Tema.PUTIH);
        logo.setBorder(new EmptyBorder(0, 0, 28, 0));

        atas.add(logo, BorderLayout.NORTH);
        atas.add(buatDaftarMenu(), BorderLayout.CENTER);
        return atas;
    }

    private JPanel buatDaftarMenu() {
        JPanel menu = new JPanel(new GridLayout(5, 1, 0, 6));
        menu.setOpaque(false);

        btnDashboard = buatTombolMenu("Dashboard", true);
        btnProduk = buatTombolMenu("Produk", false);
        btnPelanggan = buatTombolMenu("Pelanggan", false);
        btnPesanan = buatTombolMenu("Pesanan", false);
        btnLaporan = buatTombolMenu("Laporan", false);

        menu.add(btnDashboard);
        menu.add(btnProduk);
        menu.add(btnPelanggan);
        menu.add(btnPesanan);
        menu.add(btnLaporan);
        return menu;
    }

    private JButton buatTombolMenu(String teks, boolean aktif) {
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

    // ===================== KONTEN =====================

    private JPanel buatKonten() {
        JPanel konten = new JPanel(new BorderLayout(0, 20));
        konten.setBackground(Tema.KREM);
        konten.setBorder(new EmptyBorder(28, 28, 28, 28));

        konten.add(buatHeader(), BorderLayout.NORTH);
        konten.add(buatIsi(), BorderLayout.CENTER);
        return konten;
    }

    private JPanel buatHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel judul = new JPanel();
        judul.setOpaque(false);
        judul.setLayout(new BoxLayout(judul, BoxLayout.Y_AXIS));
        judul.add(buatLabel("Dashboard", 26, Font.BOLD, Tema.COKELAT_GELAP));
        judul.add(buatLabel("Ringkasan penjualan hari ini", 14, Font.PLAIN, Tema.TEKS_ABU));

        btnBuatPesanan = new JButton("+ Buat Pesanan");
        btnBuatPesanan.setPreferredSize(new Dimension(170, 44));
        btnBuatPesanan.setFont(Tema.font(Font.BOLD, 14));
        btnBuatPesanan.setForeground(Tema.PUTIH);
        btnBuatPesanan.setBackground(Tema.ORANYE);
        btnBuatPesanan.setBorderPainted(false);
        btnBuatPesanan.setFocusPainted(false);
        btnBuatPesanan.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelTombol.setOpaque(false);
        panelTombol.add(btnBuatPesanan);

        header.add(judul, BorderLayout.WEST);
        header.add(panelTombol, BorderLayout.EAST);
        return header;
    }

    private JPanel buatIsi() {
        JPanel isi = new JPanel(new BorderLayout(0, 16));
        isi.setOpaque(false);

        isi.add(buatBagianAtasIsi(), BorderLayout.NORTH);
        isi.add(buatBagianBawahIsi(), BorderLayout.CENTER);
        return isi;
    }

    private JPanel buatBagianAtasIsi() {
        JPanel atas = new JPanel(new BorderLayout(0, 16));
        atas.setOpaque(false);

        atas.add(buatBanner(), BorderLayout.NORTH);
        atas.add(buatBarisKartu(), BorderLayout.CENTER);
        return atas;
    }

    // ===================== BANNER & KARTU =====================

    private JPanel buatBanner() {
        panelBanner = new PanelBulat(10);
        panelBanner.setBackground(Tema.BANNER_BG);
        panelBanner.setLayout(new BorderLayout());
        panelBanner.setPreferredSize(new Dimension(0, 42));

        lblPeringatan = buatLabel("", 13, Font.BOLD, Tema.BANNER_TEKS);
        lblPeringatan.setBorder(new EmptyBorder(0, 16, 0, 16));
        panelBanner.add(lblPeringatan, BorderLayout.CENTER);
        return panelBanner;
    }

    private JPanel buatBarisKartu() {
        JPanel baris = new JPanel(new GridLayout(1, 3, 16, 0));
        baris.setOpaque(false);
        baris.setPreferredSize(new Dimension(0, 96));

        lblPesananHariIni = buatLabel("0", 28, Font.BOLD, Tema.COKELAT_GELAP);
        lblTotalPenjualan = buatLabel("Rp 0", 28, Font.BOLD, Tema.COKELAT_GELAP);
        lblStokHampir = buatLabel("0 produk", 28, Font.BOLD, Tema.COKELAT_GELAP);

        baris.add(buatKartu("Pesanan hari ini", lblPesananHariIni, Tema.BIRU_BG));
        baris.add(buatKartu("Total penjualan", lblTotalPenjualan, Tema.HIJAU_BG));
        baris.add(buatKartu("Stok hampir habis", lblStokHampir, Tema.BANNER_BG));
        return baris;
    }

    private JPanel buatKartu(String keterangan, JLabel nilai, Color warnaIkon) {
        PanelBulat kartu = new PanelBulat(14);
        kartu.setBackground(Tema.PUTIH);
        kartu.setLayout(new BorderLayout(14, 0));
        kartu.setBorder(new EmptyBorder(0, 16, 0, 16));

        JLabel lblKeterangan = buatLabel(keterangan, 13, Font.PLAIN, Tema.TEKS_ABU);
        lblKeterangan.setVerticalAlignment(SwingConstants.BOTTOM);
        nilai.setVerticalAlignment(SwingConstants.TOP);

        JPanel teks = new JPanel(new GridLayout(2, 1));
        teks.setOpaque(false);
        teks.add(lblKeterangan);
        teks.add(nilai);

        kartu.add(buatIkon(warnaIkon), BorderLayout.WEST);
        kartu.add(teks, BorderLayout.CENTER);
        return kartu;
    }

    /** Kotak bulat berwarna 40x40, diletakkan di tengah secara vertikal. */
    private JPanel buatIkon(Color warna) {
        PanelBulat ikon = new PanelBulat(10);
        ikon.setBackground(warna);
        ikon.setPreferredSize(new Dimension(40, 40));

        JPanel pembungkus = new JPanel(new GridBagLayout());
        pembungkus.setOpaque(false);
        pembungkus.setPreferredSize(new Dimension(40, 40));
        pembungkus.add(ikon);
        return pembungkus;
    }

    // ===================== GRAFIK & TABEL =====================

    private JPanel buatBagianBawahIsi() {
        return buatKartuTabel();
    }

    private JPanel buatKartuTabel() {
        PanelBulat kartu = buatKartuBesar();

        kartu.add(buatLabel("Pesanan terbaru", 16, Font.BOLD, Tema.COKELAT_GELAP), BorderLayout.NORTH);
        kartu.add(buatTabel(), BorderLayout.CENTER);
        return kartu;
    }

    private PanelBulat buatKartuBesar() {
        PanelBulat kartu = new PanelBulat(14);
        kartu.setBackground(Tema.PUTIH);
        kartu.setLayout(new BorderLayout(0, 12));
        kartu.setBorder(new EmptyBorder(20, 20, 20, 20));
        return kartu;
    }

    private JScrollPane buatTabel() {
        modelTabel = new DefaultTableModel(new String[]{"PELANGGAN", "STATUS"}, 0) {
            @Override
            public boolean isCellEditable(int baris, int kolom) {
                return false;
            }
        };

        tblPesananTerbaru = new JTable(modelTabel);
        tblPesananTerbaru.setRowHeight(44);
        tblPesananTerbaru.setShowGrid(false);
        tblPesananTerbaru.setIntercellSpacing(new Dimension(0, 0));
        tblPesananTerbaru.setFont(Tema.font(Font.PLAIN, 14));
        tblPesananTerbaru.setForeground(Tema.COKELAT_GELAP);
        tblPesananTerbaru.setBackground(Tema.PUTIH);
        tblPesananTerbaru.setRowSelectionAllowed(false);
        tblPesananTerbaru.setFocusable(false);
        tblPesananTerbaru.getColumnModel().getColumn(0).setCellRenderer(new RendererNama());
        tblPesananTerbaru.getColumnModel().getColumn(1).setCellRenderer(new RendererStatus());

        JTableHeader header = tblPesananTerbaru.getTableHeader();
        header.setDefaultRenderer(new RendererHeader());
        header.setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(tblPesananTerbaru);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Tema.PUTIH);
        return scroll;
    }

    // ===================== DATA =====================

    private void muatData() {
        muatKartu();
        muatBanner();
        muatTabel();
    }

    private void muatKartu() {
        lblPesananHariIni.setText(String.valueOf(PesananRepository.hitungPesananHariIni()));
        lblTotalPenjualan.setText(formatRupiah(PesananRepository.hitungPenjualanHariIni()));
    }

    private void muatBanner() {
        List<String> menipis = ProdukRepository.ambilStokMenipis();
        lblStokHampir.setText(menipis.size() + " produk");
        lblPeringatan.setText(buatTeksPeringatan(menipis));
        panelBanner.setVisible(!menipis.isEmpty());
    }

    private void muatTabel() {
        modelTabel.setRowCount(0);
        for (String[] baris : PesananRepository.ambilPesananTerbaru(JUMLAH_PESANAN_TERBARU)) {
            modelTabel.addRow(baris);
        }
    }

    private String buatTeksPeringatan(List<String> menipis) {
        return "Stok di bawah minimum: " + String.join(", ", menipis) + " – segera produksi ulang";
    }

    private String formatRupiah(double nilai) {
        Locale indonesia = new Locale.Builder().setLanguage("id").setRegion("ID").build();
        NumberFormat format = NumberFormat.getInstance(indonesia);
        format.setMaximumFractionDigits(0);
        return "Rp " + format.format(nilai);
    }

    // ===================== HAK AKSES & AKSI =====================

    /** Sesuai rancangan: Produk dan Laporan hanya untuk Admin. */
    private void aturHakAkses() {
        boolean admin = peran.equalsIgnoreCase("Admin");
        btnProduk.setEnabled(admin);
        btnLaporan.setEnabled(admin);
    }

    private void pasangAksiTombol() {
        btnDashboard.addActionListener(e -> muatData());
        btnProduk.addActionListener(e -> Menu.pindahKe("Produk", peran, this));
        btnPelanggan.addActionListener(e -> Menu.pindahKe("Pelanggan", peran, this));
        btnPesanan.addActionListener(e -> Menu.pindahKe("Pesanan", peran, this));
        btnLaporan.addActionListener(e -> Menu.pindahKe("Laporan", peran, this));
        btnBuatPesanan.addActionListener(e -> Menu.pindahKe("Pesanan", peran, this));
        btnKeluar.addActionListener(e -> keluar());
    }

    private void keluar() {
        dispose();
        new LoginFrame().setVisible(true);
    }

    private void tampilkanBelumTersedia(String namaLayar) {
        JOptionPane.showMessageDialog(this, "Layar " + namaLayar + " belum tersedia.",
                "Yummy Risol Manager", JOptionPane.INFORMATION_MESSAGE);
    }

    // ===================== HELPER =====================

    private JLabel buatLabel(String teks, int ukuran, int gaya, Color warna) {
        JLabel label = new JLabel(teks);
        label.setFont(Tema.font(gaya, ukuran));
        label.setForeground(warna);
        label.setAlignmentX(LEFT_ALIGNMENT);
        return label;
    }

    // ===================== RENDERER TABEL =====================

    /** Kolom nama: teks biasa dengan garis tipis di bawah tiap baris. */
    private static class RendererNama extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable tabel, Object nilai,
                boolean terpilih, boolean fokus, int baris, int kolom) {
            Component hasil = super.getTableCellRendererComponent(tabel, nilai, terpilih, fokus, baris, kolom);
            setBorder(borderSel());
            setBackground(Tema.PUTIH);
            return hasil;
        }
    }

    /** Garis tipis di bawah baris + jarak kiri-kanan supaya isi tidak menempel ke tepi kolom. */
    private static CompoundBorder borderSel() {
        return new CompoundBorder(new MatteBorder(0, 0, 1, 0, Tema.BORDER),
                new EmptyBorder(0, JARAK_SEL, 0, JARAK_SEL));
    }

    /** Judul kolom: rata kiri dengan jarak yang sama seperti isi sel. */
    private static class RendererHeader extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable tabel, Object nilai,
                boolean terpilih, boolean fokus, int baris, int kolom) {
            super.getTableCellRendererComponent(tabel, nilai, false, false, baris, kolom);
            setHorizontalAlignment(SwingConstants.LEFT);
            setFont(Tema.font(Font.BOLD, 12));
            setForeground(Tema.TEKS_ABU);
            setBackground(Tema.PUTIH);
            setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, Tema.BORDER),
                    new EmptyBorder(0, JARAK_SEL, 0, JARAK_SEL)));
            return this;
        }
    }

    /** Kolom status: label bulat berwarna (Baru, Diproses, Selesai). */
    private static class RendererStatus implements TableCellRenderer {

        private final JPanel sel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
        private final ChipStatus chip = new ChipStatus();

        RendererStatus() {
            sel.setBackground(Tema.PUTIH);
            sel.setBorder(borderSel());
            sel.add(chip);
        }

        @Override
        public Component getTableCellRendererComponent(JTable tabel, Object nilai,
                boolean terpilih, boolean fokus, int baris, int kolom) {
            chip.atur(String.valueOf(nilai));
            return sel;
        }
    }

    private static class ChipStatus extends JLabel {

        private Color warnaLatar = Tema.PUTIH;

        ChipStatus() {
            setOpaque(false);
            setFont(Tema.font(Font.BOLD, 12));
            setBorder(new EmptyBorder(3, 10, 3, 10));
        }

        void atur(String status) {
            String huruf = status.toUpperCase();
            if (huruf.equals("BARU")) {
                warnaLatar = Tema.BIRU_BG;
                setForeground(Tema.BIRU_TEKS);
            } else if (huruf.equals("DIPROSES")) {
                warnaLatar = Tema.KUNING_BG;
                setForeground(Tema.KUNING_TEKS);
            } else {
                warnaLatar = Tema.HIJAU_BG;
                setForeground(Tema.HIJAU_TEKS);
            }
            setText(huruf.substring(0, 1) + huruf.substring(1).toLowerCase());
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(warnaLatar);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }
}