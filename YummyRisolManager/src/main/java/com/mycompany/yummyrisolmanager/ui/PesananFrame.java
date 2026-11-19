package com.mycompany.yummyrisolmanager.ui;
 
import com.mycompany.yummyrisolmanager.db.PelangganRepository;
import com.mycompany.yummyrisolmanager.db.TransaksiRepository;
import com.mycompany.yummyrisolmanager.model.DetailPesanan;
import com.mycompany.yummyrisolmanager.model.Pelanggan;
import com.mycompany.yummyrisolmanager.model.Pesanan;
import com.mycompany.yummyrisolmanager.model.Produk;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
 
/**
 * Layar Buat Pesanan: input pelanggan dan produk + daftar item di kiri, ringkasan di kanan.
 * Kerangka (sidebar dan area krem) diwarisi dari FrameDasar.
 */
public class PesananFrame extends FrameDasar {
 
    private static final String[] KOLOM = {"PRODUK", "HARGA", "JML", "SUBTOTAL"};
 
    // tabel item
    private final DefaultTableModel modelTabel = new DefaultTableModel(KOLOM, 0) {
        @Override
        public boolean isCellEditable(int baris, int kolom) {
            return false;
        }
    };
    private final JTable tabel = new JTable(modelTabel);
 
    // input
    private final JComboBox<Pelanggan> cmbPelanggan = new JComboBox<>();
    private final JComboBox<Produk> cmbProduk = new JComboBox<>();
    private final JSpinner spnJumlah = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
 
    // ringkasan
    private final JComboBox<String> cmbStatus = Komponen.pilihan("Baru", "Diproses", "Selesai");
    private final JComboBox<String> cmbMetode = Komponen.pilihan("Langsung", "WhatsApp", "Titip");
    private final JLabel lblTotal = Komponen.label("Rp 0", 30, Font.BOLD, Tema.ORANYE);
 
    /** Pesanan yang sedang disusun (Composition: berisi daftar DetailPesanan). */
    private Pesanan pesanan = new Pesanan();
 
    public PesananFrame(String peran) {
        super(peran, "Pesanan", "Buat Pesanan");
        cmbPelanggan.setFont(Tema.font(Font.PLAIN, 14));
        cmbProduk.setFont(Tema.font(Font.PLAIN, 14));
        spnJumlah.setFont(Tema.font(Font.PLAIN, 14));
        cmbPelanggan.setRenderer(new RendererPilihan("Pilih pelanggan"));
        cmbProduk.setRenderer(new RendererPilihan("Pilih produk"));
 
        pasangKonten(buatIsi());
        muatPilihan();
    }
 
    // ===================== SUSUNAN LAYAR =====================
 
    private JPanel buatIsi() {
        JPanel kosong = new JPanel();
        kosong.setOpaque(false);
 
        JPanel tengah = new JPanel(new BorderLayout(16, 0));
        tengah.setOpaque(false);
        tengah.setBorder(new EmptyBorder(16, 0, 0, 0));
        tengah.add(buatKolomKiri(), BorderLayout.CENTER);
        tengah.add(buatKolomKanan(), BorderLayout.EAST);
 
        JPanel isi = new JPanel(new BorderLayout());
        isi.setOpaque(false);
        isi.add(Komponen.header("Buat Pesanan", "Pilih pelanggan dan produk, total dihitung otomatis", kosong),
                BorderLayout.NORTH);
        isi.add(tengah, BorderLayout.CENTER);
        return isi;
    }
 
    private JPanel buatKolomKiri() {
        JPanel kiri = new JPanel(new BorderLayout(0, 16));
        kiri.setOpaque(false);
        kiri.add(buatKartuInput(), BorderLayout.NORTH);
        kiri.add(buatKartuItem(), BorderLayout.CENTER);
        return kiri;
    }
 
    /** Kartu putih berisi Pelanggan, Produk, Jumlah, dan tombol Tambah. */
    private PanelBulat buatKartuInput() {
        cmbPelanggan.setPreferredSize(new Dimension(150, 38));
        cmbProduk.setPreferredSize(new Dimension(150, 38));
        spnJumlah.setPreferredSize(new Dimension(90, 38));
 
        JButton tambah = Komponen.tombolUtama("Tambah", 100, 38);
        tambah.addActionListener(e -> tambahItem());
        JPanel tombol = new JPanel(new BorderLayout());   // tombol rata bawah, sejajar kolom isian
        tombol.setOpaque(false);
        tombol.add(tambah, BorderLayout.SOUTH);
 
        JPanel baris = new JPanel(new GridBagLayout());
        baris.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.insets = new Insets(0, 0, 0, 12);
 
        c.gridx = 0;
        c.weightx = 1.2;
        baris.add(Komponen.kolomForm("Pelanggan", cmbPelanggan), c);
        c.gridx = 1;
        baris.add(Komponen.kolomForm("Produk", cmbProduk), c);
        c.gridx = 2;
        c.weightx = 0.4;
        baris.add(Komponen.kolomForm("Jumlah", spnJumlah), c);
        c.gridx = 3;
        c.weightx = 0;
        c.insets = new Insets(0, 0, 0, 0);
        baris.add(tombol, c);
 
        PanelBulat kartu = Komponen.kartu(16, 16, 16, 16);
        kartu.add(baris, BorderLayout.CENTER);
        return kartu;
    }
 
    /** Kartu "Daftar item": judul + tombol Hapus item, lalu tabel. */
    private PanelBulat buatKartuItem() {
        JButton hapus = Komponen.tombolOutline("Hapus item", 120, 34);
        hapus.addActionListener(e -> hapusItem());
 
        JPanel atas = new JPanel(new BorderLayout());
        atas.setOpaque(false);
        atas.add(Komponen.label("Daftar item", 16, Font.BOLD, Tema.COKELAT_GELAP), BorderLayout.WEST);
        atas.add(hapus, BorderLayout.EAST);
 
        JScrollPane scroll = Komponen.gayakanTabel(tabel);
        int[] lebar = {200, 110, 50, 120};
        for (int i = 0; i < lebar.length; i++) {
            tabel.getColumnModel().getColumn(i).setPreferredWidth(lebar[i]);
        }
 
        PanelBulat kartu = Komponen.kartu(16, 16, 16, 16);
        kartu.add(atas, BorderLayout.NORTH);
        kartu.add(scroll, BorderLayout.CENTER);
        return kartu;
    }
 
    /** Kartu Ringkasan di kanan. Dibungkus supaya tingginya mengikuti isi. */
    private JPanel buatKolomKanan() {
        JButton simpan = lebarPenuh(Komponen.tombolUtama("Simpan Pesanan", 100, 44));
        JButton batal = lebarPenuh(Komponen.tombolOutline("Batal", 100, 44));
        simpan.addActionListener(e -> simpanPesanan());
        batal.addActionListener(e -> batalkan());
 
        JPanel isi = new JPanel();
        isi.setOpaque(false);
        isi.setLayout(new BoxLayout(isi, BoxLayout.Y_AXIS));
        isi.add(Komponen.label("Ringkasan", 17, Font.BOLD, Tema.COKELAT_GELAP));
        isi.add(Box.createVerticalStrut(12));
        isi.add(Komponen.kolomForm("Status", cmbStatus));
        isi.add(Box.createVerticalStrut(12));
        isi.add(Komponen.kolomForm("Metode order", cmbMetode));
        isi.add(Box.createVerticalStrut(18));
        isi.add(Komponen.label("Total bayar", 13, Font.PLAIN, Tema.TEKS_ABU));
        isi.add(lblTotal);
        isi.add(Box.createVerticalStrut(4));
        isi.add(Komponen.label("Dicatat oleh: " + peran, 12, Font.PLAIN, Tema.TEKS_ABU));
        isi.add(Box.createVerticalStrut(18));
        isi.add(simpan);
        isi.add(Box.createVerticalStrut(10));
        isi.add(batal);
 
        PanelBulat kartu = Komponen.kartu(20, 16, 20, 16);
        kartu.add(isi, BorderLayout.CENTER);
 
        JPanel bungkus = new JPanel(new BorderLayout());
        bungkus.setOpaque(false);
        bungkus.setPreferredSize(new Dimension(290, 0));
        bungkus.add(kartu, BorderLayout.NORTH);
        return bungkus;
    }
 
    private JButton lebarPenuh(JButton tombol) {
        tombol.setAlignmentX(LEFT_ALIGNMENT);
        tombol.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        return tombol;
    }
 
    // ===================== DATA =====================
 
    /** Mengisi pilihan pelanggan dan produk dari database (juga menyegarkan stok). */
    private void muatPilihan() {
        cmbPelanggan.removeAllItems();
        for (Pelanggan p : PelangganRepository.ambilSemua("")) {
            cmbPelanggan.addItem(p);
        }
        cmbProduk.removeAllItems();
        for (Produk p : TransaksiRepository.ambilProduk()) {
            cmbProduk.addItem(p);
        }
        cmbPelanggan.setSelectedIndex(-1);
        cmbProduk.setSelectedIndex(-1);
    }
 
    /** Menggambar ulang tabel item dan total dari objek pesanan. */
    private void segarkan() {
        modelTabel.setRowCount(0);
        for (DetailPesanan d : pesanan.getDaftarItem()) {
            modelTabel.addRow(new Object[]{
                d.getProduk().getNama(),
                Komponen.formatRupiah(d.getProduk().getHarga()),
                d.getJumlah(),
                Komponen.formatRupiah(d.getSubtotal())
            });
        }
        lblTotal.setText(Komponen.formatRupiah(pesanan.hitungTotal()));
    }
 
    // ===================== AKSI =====================
 
    /** Tombol Tambah: masukkan produk terpilih ke daftar item. */
    private void tambahItem() {
        Produk produk = (Produk) cmbProduk.getSelectedItem();
        if (produk == null) {
            peringatan("Pilih produk dulu.");
            return;
        }
        int jumlah = (Integer) spnJumlah.getValue();
        if (pesanan.hitungJumlah(produk) + jumlah > produk.getStok()) {
            peringatan("Stok " + produk.getNama() + " tinggal " + produk.getStok() + ".");
            return;
        }
        pesanan.tambahItem(produk, jumlah);
        spnJumlah.setValue(1);
        segarkan();
    }
 
    /** Tombol Hapus item: buang baris yang dipilih di tabel. */
    private void hapusItem() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            peringatan("Pilih dulu item yang mau dihapus dari tabel.");
            return;
        }
        pesanan.hapusItem(baris);
        segarkan();
    }
 
    /** Tombol Simpan Pesanan: periksa isian lalu simpan ke database. */
    private void simpanPesanan() {
        Pelanggan pelanggan = (Pelanggan) cmbPelanggan.getSelectedItem();
        if (pelanggan == null) {
            peringatan("Pilih pelanggan dulu.");
            return;
        }
        if (pesanan.getDaftarItem().isEmpty()) {
            peringatan("Tambahkan minimal satu item.");
            return;
        }
        pesanan.setPelanggan(pelanggan);
        pesanan.setStatus(cmbStatus.getSelectedItem().toString().toUpperCase());
        pesanan.setMetodeOrder(cmbMetode.getSelectedItem().toString().toUpperCase());
 
        try {
            int id = TransaksiRepository.simpan(pesanan, peran);
            JOptionPane.showMessageDialog(this,
                    "Pesanan #" + id + " tersimpan.\nTotal " + Komponen.formatRupiah(pesanan.hitungTotal()),
                    "Yummy Risol Manager", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Pesanan gagal disimpan: " + e.getMessage(),
                    "Yummy Risol Manager", JOptionPane.ERROR_MESSAGE);
            return;
        }
        kosongkan();
        muatPilihan();
    }
 
    /** Tombol Batal: kosongkan form (minta konfirmasi kalau sudah ada item). */
    private void batalkan() {
        if (!pesanan.getDaftarItem().isEmpty()) {
            int jawab = JOptionPane.showConfirmDialog(this, "Batalkan pesanan ini?", "Konfirmasi",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (jawab != JOptionPane.YES_OPTION) {
                return;
            }
        }
        kosongkan();
    }
 
    private void kosongkan() {
        pesanan = new Pesanan();
        cmbPelanggan.setSelectedIndex(-1);
        cmbProduk.setSelectedIndex(-1);
        cmbStatus.setSelectedIndex(0);
        cmbMetode.setSelectedIndex(0);
        spnJumlah.setValue(1);
        segarkan();
    }
 
    private void peringatan(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Yummy Risol Manager", JOptionPane.WARNING_MESSAGE);
    }
 
    // ===================== RENDERER =====================
 
    /** Menampilkan nama pelanggan / produk, atau teks petunjuk kalau belum ada yang dipilih. */
    private static class RendererPilihan extends DefaultListCellRenderer {
 
        private final String petunjuk;
 
        RendererPilihan(String petunjuk) {
            this.petunjuk = petunjuk;
        }
 
        @Override
        public Component getListCellRendererComponent(JList<?> daftar, Object nilai, int indeks,
                boolean terpilih, boolean fokus) {
            super.getListCellRendererComponent(daftar, nilai, indeks, terpilih, fokus);
            if (nilai == null) {
                setText(petunjuk);
                setForeground(Tema.TEKS_ABU);
            } else if (nilai instanceof Pelanggan p) {
                setText(p.getNama());
            } else if (nilai instanceof Produk p) {
                setText(p.getNama());
            }
            return this;
        }
    }
}
