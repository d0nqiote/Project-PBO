package com.mycompany.yummyrisolmanager.ui;

import com.mycompany.yummyrisolmanager.db.ProdukRepository;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 * Layar Kelola Produk: tabel produk, pencarian, dan form tambah / ubah / hapus.
 * Kerangka (sidebar dan area krem) diwarisi dari FrameDasar.
 */
public class ProdukFrame extends FrameDasar {

    private static final String[] KOLOM = {"ID", "NAMA", "JENIS", "RASA", "TIPE", "ISI", "HARGA", "STOK"};

    // tabel
    private final DefaultTableModel modelTabel = new DefaultTableModel(KOLOM, 0) {
        @Override
        public boolean isCellEditable(int baris, int kolom) {
            return false;
        }
    };
    private final JTable tabel = new JTable(modelTabel);

    /** Data lengkap sesuai isi tabel: {id, nama, jenis, rasa, tipe, isi, harga, stok, stokMinimum}. */
    private List<Object[]> dataProduk = new ArrayList<>();

    // pencarian
    private final JTextField kolomCari = Komponen.kolomCari(200, 38);

    // form
    private final JTextField kolomNama = Komponen.kolomTeks();
    private final JComboBox<String> pilihJenis = Komponen.pilihan("MANIS", "ASIN");
    private final JTextField kolomRasa = Komponen.kolomTeks();
    private final JComboBox<String> pilihTipe = Komponen.pilihan("SATUAN", "PAKET");
    private final JTextField kolomIsi = Komponen.kolomTeks();
    private final JTextField kolomHarga = Komponen.kolomTeks();
    private final JTextField kolomStok = Komponen.kolomTeks();
    private final JTextField kolomStokMinimum = Komponen.kolomTeks();

    public ProdukFrame(String peran) {
        super(peran, "Produk", "Kelola Produk");
        pasangKonten(buatIsi());
        muat();
    }

    // ===================== SUSUNAN LAYAR =====================

    private JPanel buatIsi() {
        JPanel isi = new JPanel(new BorderLayout());
        isi.setOpaque(false);

        JPanel tengah = new JPanel(new BorderLayout(0, 16));
        tengah.setOpaque(false);
        tengah.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        tengah.add(buatKartuTabel(), BorderLayout.CENTER);
        tengah.add(buatKartuForm(), BorderLayout.SOUTH);

        isi.add(buatHeader(), BorderLayout.NORTH);
        isi.add(tengah, BorderLayout.CENTER);
        return isi;
    }

    private JPanel buatHeader() {
        JButton tambah = Komponen.tombolUtama("+ Tambah", 110, 38);
        tambah.addActionListener(e -> siapkanProdukBaru());

        // pencarian langsung: tabel diperbarui setiap kali teks berubah
        Komponen.saatBerubah(kolomCari, this::muat);

        JPanel kanan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        kanan.setOpaque(false);
        kanan.add(kolomCari);
        kanan.add(tambah);

        return Komponen.header("Kelola Produk", "Tambah, ubah, hapus, dan cari produk", kanan);
    }

    private PanelBulat buatKartuTabel() {
        JScrollPane scroll = Komponen.gayakanTabel(tabel);
        aturKolom();
        PanelBulat kartu = Komponen.kartu(8, 16, 8, 16);
        kartu.add(scroll, BorderLayout.CENTER);
        return kartu;
    }

    /** Lebar kolom, tampilan sel khusus, dan reaksi saat baris dipilih. */
    private void aturKolom() {
        int[] lebar = {70, 150, 90, 90, 85, 40, 100, 140};
        for (int i = 0; i < lebar.length; i++) {
            tabel.getColumnModel().getColumn(i).setPreferredWidth(lebar[i]);
        }
        // kolom stok memuat angka + chip "Hampir habis", jangan sampai terpotong
        tabel.getColumnModel().getColumn(7).setMinWidth(140);

        // ID tampil sebagai P001, harga sebagai Rp 4.000
        tabel.getColumnModel().getColumn(0).setCellRenderer(new Komponen.RendererTeks() {
            @Override
            protected Object ubah(Object nilai) {
                return String.format("P%03d", (Integer) nilai);
            }
        });
        tabel.getColumnModel().getColumn(6).setCellRenderer(new Komponen.RendererTeks() {
            @Override
            protected Object ubah(Object nilai) {
                return Komponen.formatRupiah((Integer) nilai);
            }
        });
        tabel.getColumnModel().getColumn(2).setCellRenderer(new RendererChip(false));
        tabel.getColumnModel().getColumn(7).setCellRenderer(new RendererChip(true));

        // klik baris -> isi form dengan data produk itu
        tabel.getSelectionModel().addListSelectionListener(e -> {
            int baris = tabel.getSelectedRow();
            if (!e.getValueIsAdjusting() && baris >= 0 && baris < dataProduk.size()) {
                isiForm(dataProduk.get(baris));
            }
        });
    }

    private PanelBulat buatKartuForm() {
        JButton simpan = Komponen.tombolUtama("Simpan", 90, 38);
        JButton ubah = Komponen.tombolOutline("Ubah", 80, 38);
        JButton hapus = Komponen.tombolOutline("Hapus", 80, 38);
        simpan.addActionListener(e -> simpanBaru());
        ubah.addActionListener(e -> ubahTerpilih());
        hapus.addActionListener(e -> hapusTerpilih());

        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tombol.setOpaque(false);
        tombol.add(simpan);
        tombol.add(ubah);
        tombol.add(hapus);

        JPanel atas = new JPanel(new BorderLayout());
        atas.setOpaque(false);
        atas.add(Komponen.label("Form Produk", 17, Font.BOLD, Tema.COKELAT_GELAP), BorderLayout.WEST);
        atas.add(tombol, BorderLayout.EAST);

        JPanel isian = new JPanel(new GridLayout(2, 4, 16, 12));
        isian.setOpaque(false);
        isian.add(Komponen.kolomForm("Nama produk", kolomNama));
        isian.add(Komponen.kolomForm("Jenis", pilihJenis));
        isian.add(Komponen.kolomForm("Rasa", kolomRasa));
        isian.add(Komponen.kolomForm("Tipe", pilihTipe));
        isian.add(Komponen.kolomForm("Isi (pcs)", kolomIsi));
        isian.add(Komponen.kolomForm("Harga", kolomHarga));
        isian.add(Komponen.kolomForm("Stok", kolomStok));
        isian.add(Komponen.kolomForm("Stok minimum", kolomStokMinimum));

        PanelBulat kartu = Komponen.kartu(16, 16, 16, 16);
        kartu.add(atas, BorderLayout.NORTH);
        kartu.add(isian, BorderLayout.CENTER);
        return kartu;
    }

    // ===================== AKSI =====================

    /** Mengisi ulang tabel dari database sesuai kata kunci pencarian. */
    private void muat() {
        dataProduk = ProdukRepository.ambilSemua(kolomCari.getText().trim());
        modelTabel.setRowCount(0);
        for (Object[] baris : dataProduk) {
            modelTabel.addRow(Arrays.copyOf(baris, KOLOM.length));
        }
    }

    /** Tombol "+ Tambah": kosongkan form supaya siap diisi produk baru. */
    private void siapkanProdukBaru() {
        tabel.clearSelection();
        bersihkanForm();
        kolomNama.requestFocusInWindow();
    }

    /** Tombol "Simpan": menyimpan isi form sebagai produk baru. */
    private void simpanBaru() {
        IsianProduk p = bacaForm();
        if (p == null) {
            return;
        }
        try {
            ProdukRepository.tambah(p.nama, p.jenis, p.rasa, p.tipe, p.isi, p.harga, p.stok, p.stokMinimum);
        } catch (SQLException e) {
            gagal("menyimpan", e);
            return;
        }
        selesaiAksi("Produk berhasil disimpan.");
    }

    /** Tombol "Ubah": memperbarui produk yang dipilih di tabel dengan isi form. */
    private void ubahTerpilih() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            peringatan("Pilih dulu produk yang mau diubah dari tabel.");
            return;
        }
        IsianProduk p = bacaForm();
        if (p == null) {
            return;
        }
        int id = (Integer) dataProduk.get(baris)[0];
        try {
            ProdukRepository.ubah(id, p.nama, p.jenis, p.rasa, p.tipe, p.isi, p.harga, p.stok, p.stokMinimum);
        } catch (SQLException e) {
            gagal("mengubah", e);
            return;
        }
        selesaiAksi("Produk berhasil diubah.");
    }

    /** Tombol "Hapus": menghapus produk yang dipilih setelah dikonfirmasi. */
    private void hapusTerpilih() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            peringatan("Pilih dulu produk yang mau dihapus dari tabel.");
            return;
        }
        Object[] produk = dataProduk.get(baris);
        int jawab = JOptionPane.showConfirmDialog(this,
                "Hapus produk \"" + produk[1] + "\"?", "Konfirmasi hapus",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (jawab != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            ProdukRepository.hapus((Integer) produk[0]);
        } catch (SQLIntegrityConstraintViolationException e) {
            peringatan("Produk ini sudah dipakai di pesanan, jadi tidak bisa dihapus.");
            return;
        } catch (SQLException e) {
            gagal("menghapus", e);
            return;
        }
        selesaiAksi("Produk berhasil dihapus.");
    }

    private void selesaiAksi(String pesan) {
        muat();
        bersihkanForm();
        JOptionPane.showMessageDialog(this, pesan, "Yummy Risol Manager", JOptionPane.INFORMATION_MESSAGE);
    }

    // ===================== FORM =====================

    private void isiForm(Object[] p) {
        kolomNama.setText((String) p[1]);
        pilihJenis.setSelectedItem(p[2]);
        kolomRasa.setText((String) p[3]);
        pilihTipe.setSelectedItem(p[4]);
        kolomIsi.setText(String.valueOf(p[5]));
        kolomHarga.setText(String.valueOf(p[6]));
        kolomStok.setText(String.valueOf(p[7]));
        kolomStokMinimum.setText(String.valueOf(p[8]));
    }

    private void bersihkanForm() {
        kolomNama.setText("");
        pilihJenis.setSelectedIndex(0);
        kolomRasa.setText("");
        pilihTipe.setSelectedIndex(0);
        kolomIsi.setText("");
        kolomHarga.setText("");
        kolomStok.setText("");
        kolomStokMinimum.setText("");
    }

    /** Hasil baca form yang sudah divalidasi. */
    private record IsianProduk(String nama, String jenis, String rasa, String tipe,
            int isi, int harga, int stok, int stokMinimum) {
    }

    /** Membaca dan memeriksa isi form. Kalau ada yang salah, tampilkan pesan dan kembalikan null. */
    private IsianProduk bacaForm() {
        String nama = kolomNama.getText().trim();
        if (nama.isEmpty()) {
            peringatan("Nama produk wajib diisi.");
            kolomNama.requestFocusInWindow();
            return null;
        }
        Integer isi = bacaAngka(kolomIsi, "Isi", 1);
        Integer harga = isi == null ? null : bacaAngka(kolomHarga, "Harga", 1);
        Integer stok = harga == null ? null : bacaAngka(kolomStok, "Stok", 0);
        Integer stokMinimum = stok == null ? null : bacaAngka(kolomStokMinimum, "Stok minimum", 0);
        if (stokMinimum == null) {
            return null;
        }
        return new IsianProduk(nama, (String) pilihJenis.getSelectedItem(), kolomRasa.getText().trim(),
                (String) pilihTipe.getSelectedItem(), isi, harga, stok, stokMinimum);
    }

    /** Membaca bilangan bulat dari kolom (titik pemisah ribuan boleh). Null kalau tidak valid. */
    private Integer bacaAngka(JTextField kolom, String nama, int minimal) {
        String teks = kolom.getText().replace(".", "").trim();
        try {
            int nilai = Integer.parseInt(teks);
            if (nilai >= minimal) {
                return nilai;
            }
            peringatan(nama + " minimal " + minimal + ".");
        } catch (NumberFormatException e) {
            peringatan(nama + " harus berupa angka bulat.");
        }
        kolom.requestFocusInWindow();
        return null;
    }

    private void peringatan(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Yummy Risol Manager", JOptionPane.WARNING_MESSAGE);
    }

    private void gagal(String aksi, SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal " + aksi + " produk: " + e.getMessage(),
                "Yummy Risol Manager", JOptionPane.ERROR_MESSAGE);
    }

    // ===================== RENDERER CHIP =====================

    /**
     * Sel berisi chip berwarna.
     * Kolom Jenis: chip MANIS (kuning) atau ASIN (biru).
     * Kolom Stok: angka stok, ditambah chip merah "Hampir habis" kalau stok <= stok minimum.
     */
    private class RendererChip implements TableCellRenderer {

        private final boolean untukStok;
        private final JPanel panel = new JPanel();
        private final JLabel angka = new JLabel();
        private final Chip chip = new Chip();

        RendererChip(boolean untukStok) {
            this.untukStok = untukStok;
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            angka.setFont(Tema.font(Font.PLAIN, 14));
            angka.setForeground(Tema.COKELAT_GELAP);
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object nilai,
                boolean terpilih, boolean fokus, int baris, int kolom) {
            panel.removeAll();
            panel.setBackground(terpilih ? Komponen.WARNA_PILIH : Tema.PUTIH);
            // kolom stok diawali angka (teks biasa); kolom jenis diawali chip, jaraknya disesuaikan
            panel.setBorder(untukStok ? Komponen.borderBaris() : Komponen.borderBarisChip());

            if (untukStok) {
                angka.setText(String.valueOf(nilai));
                panel.add(angka);
                if (baris < dataProduk.size() && (Integer) nilai <= (Integer) dataProduk.get(baris)[8]) {
                    chip.atur("Hampir habis", Tema.BANNER_BG, Tema.BANNER_TEKS);
                    panel.add(Box.createHorizontalStrut(6));
                    panel.add(chip);
                }
            } else if ("MANIS".equals(nilai)) {
                chip.atur("MANIS", Tema.KUNING_BG, Tema.KUNING_TEKS);
                panel.add(chip);
            } else {
                chip.atur(String.valueOf(nilai), Tema.BIRU_BG, Tema.BIRU_TEKS);
                panel.add(chip);
            }
            return panel;
        }
    }
}