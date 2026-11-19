package com.mycompany.yummyrisolmanager.ui;

import com.mycompany.yummyrisolmanager.db.PelangganRepository;
import com.mycompany.yummyrisolmanager.model.Pelanggan;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 * Layar Kelola Pelanggan: tabel pelanggan di kiri, form tambah / ubah / hapus di kanan.
 * Kerangka (sidebar dan area krem) diwarisi dari FrameDasar.
 */
public class PelangganFrame extends FrameDasar {

    private static final String[] KOLOM = {"ID", "NAMA", "NO. HP", "ALAMAT"};

    // tabel
    private final DefaultTableModel modelTabel = new DefaultTableModel(KOLOM, 0) {
        @Override
        public boolean isCellEditable(int baris, int kolom) {
            return false;
        }
    };
    private final JTable tabel = new JTable(modelTabel);

    /** Pelanggan yang sedang tampil di tabel (urutannya sama dengan baris tabel). */
    private List<Pelanggan> dataPelanggan = new ArrayList<>();

    // pencarian
    private final JTextField kolomCari = Komponen.kolomCari(200, 38);

    // form
    private final JTextField kolomNama = Komponen.kolomTeks();
    private final JTextField kolomNoHp = Komponen.kolomTeks();
    private final JTextField kolomAlamat = Komponen.kolomTeks();

    public PelangganFrame(String peran) {
        super(peran, "Pelanggan", "Kelola Pelanggan");
        pasangKonten(buatIsi());
        muat();
    }

    // ===================== SUSUNAN LAYAR =====================

    private JPanel buatIsi() {
        JPanel tengah = new JPanel(new BorderLayout(16, 0));
        tengah.setOpaque(false);
        tengah.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        tengah.add(buatKartuTabel(), BorderLayout.CENTER);
        tengah.add(buatKolomForm(), BorderLayout.EAST);

        JPanel isi = new JPanel(new BorderLayout());
        isi.setOpaque(false);
        isi.add(buatHeader(), BorderLayout.NORTH);
        isi.add(tengah, BorderLayout.CENTER);
        return isi;
    }

    private JPanel buatHeader() {
        JButton tambah = Komponen.tombolUtama("+ Tambah", 110, 38);
        tambah.addActionListener(e -> siapkanPelangganBaru());

        // pencarian langsung: tabel diperbarui setiap kali teks berubah
        Komponen.saatBerubah(kolomCari, this::muat);

        JPanel kanan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        kanan.setOpaque(false);
        kanan.add(kolomCari);
        kanan.add(tambah);

        return Komponen.header("Kelola Pelanggan", "Simpan data langganan untuk pesanan berulang", kanan);
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
        int[] lebar = {70, 110, 130, 130};
        for (int i = 0; i < lebar.length; i++) {
            tabel.getColumnModel().getColumn(i).setPreferredWidth(lebar[i]);
        }

        // klik baris -> isi form dengan data pelanggan itu
        tabel.getSelectionModel().addListSelectionListener(e -> {
            int baris = tabel.getSelectedRow();
            if (!e.getValueIsAdjusting() && baris >= 0 && baris < dataPelanggan.size()) {
                isiForm(dataPelanggan.get(baris));
            }
        });
    }

    /** Kartu form di kanan. Dibungkus supaya tingginya mengikuti isi, tidak melar sampai bawah. */
    private JPanel buatKolomForm() {
        JButton simpan = Komponen.tombolUtama("Simpan", 80, 38);
        JButton ubah = Komponen.tombolOutline("Ubah", 80, 38);
        JButton hapus = Komponen.tombolOutline("Hapus", 80, 38);
        simpan.setMargin(new Insets(2, 6, 2, 6)); // supaya tulisan "Simpan" tidak terpotong
        simpan.addActionListener(e -> simpanBaru());
        ubah.addActionListener(e -> ubahTerpilih());
        hapus.addActionListener(e -> hapusTerpilih());

        JPanel tombol = new JPanel(new GridLayout(1, 3, 8, 0));
        tombol.setOpaque(false);
        tombol.add(simpan);
        tombol.add(ubah);
        tombol.add(hapus);
        tombol.setAlignmentX(LEFT_ALIGNMENT);

        JPanel isi = new JPanel();
        isi.setOpaque(false);
        isi.setLayout(new BoxLayout(isi, BoxLayout.Y_AXIS));
        isi.add(Komponen.label("Form Pelanggan", 17, Font.BOLD, Tema.COKELAT_GELAP));
        isi.add(Box.createVerticalStrut(12));
        isi.add(Komponen.kolomForm("Nama", kolomNama));
        isi.add(Box.createVerticalStrut(12));
        isi.add(Komponen.kolomForm("No. HP", kolomNoHp));
        isi.add(Box.createVerticalStrut(12));
        isi.add(Komponen.kolomForm("Alamat", kolomAlamat));
        isi.add(Box.createVerticalStrut(16));
        isi.add(tombol);

        PanelBulat kartu = Komponen.kartu(20, 16, 20, 16);
        kartu.add(isi, BorderLayout.CENTER);

        JPanel bungkus = new JPanel(new BorderLayout());
        bungkus.setOpaque(false);
        bungkus.setPreferredSize(new Dimension(320, 0));
        bungkus.add(kartu, BorderLayout.NORTH);
        return bungkus;
    }

    // ===================== AKSI =====================

    /** Mengisi ulang tabel dari database sesuai kata kunci pencarian. */
    private void muat() {
        dataPelanggan = PelangganRepository.ambilSemua(kolomCari.getText().trim());
        modelTabel.setRowCount(0);
        for (Pelanggan p : dataPelanggan) {
            modelTabel.addRow(new Object[]{p.getKode(), p.getNama(), p.getNoHp(), p.getAlamat()});
        }
    }

    /** Tombol "+ Tambah": kosongkan form supaya siap diisi pelanggan baru. */
    private void siapkanPelangganBaru() {
        tabel.clearSelection();
        bersihkanForm();
        kolomNama.requestFocusInWindow();
    }

    /** Tombol "Simpan": menyimpan isi form sebagai pelanggan baru. */
    private void simpanBaru() {
        Pelanggan p = bacaForm(0);
        if (p == null) {
            return;
        }
        try {
            PelangganRepository.tambah(p);
        } catch (SQLException e) {
            gagal("menyimpan", e);
            return;
        }
        selesaiAksi("Pelanggan berhasil disimpan.");
    }

    /** Tombol "Ubah": memperbarui pelanggan yang dipilih di tabel dengan isi form. */
    private void ubahTerpilih() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            peringatan("Pilih dulu pelanggan yang mau diubah dari tabel.");
            return;
        }
        Pelanggan p = bacaForm(dataPelanggan.get(baris).getIdPelanggan());
        if (p == null) {
            return;
        }
        try {
            PelangganRepository.ubah(p);
        } catch (SQLException e) {
            gagal("mengubah", e);
            return;
        }
        selesaiAksi("Pelanggan berhasil diubah.");
    }

    /** Tombol "Hapus": menghapus pelanggan yang dipilih setelah dikonfirmasi. */
    private void hapusTerpilih() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            peringatan("Pilih dulu pelanggan yang mau dihapus dari tabel.");
            return;
        }
        Pelanggan pilih = dataPelanggan.get(baris);
        int jawab = JOptionPane.showConfirmDialog(this,
                "Hapus pelanggan \"" + pilih.getNama() + "\"?", "Konfirmasi hapus",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (jawab != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            PelangganRepository.hapus(pilih.getIdPelanggan());
        } catch (SQLIntegrityConstraintViolationException e) {
            peringatan("Pelanggan ini sudah punya pesanan, jadi tidak bisa dihapus.");
            return;
        } catch (SQLException e) {
            gagal("menghapus", e);
            return;
        }
        selesaiAksi("Pelanggan berhasil dihapus.");
    }

    private void selesaiAksi(String pesan) {
        muat();
        bersihkanForm();
        JOptionPane.showMessageDialog(this, pesan, "Yummy Risol Manager", JOptionPane.INFORMATION_MESSAGE);
    }

    // ===================== FORM =====================

    private void isiForm(Pelanggan p) {
        kolomNama.setText(p.getNama());
        kolomNoHp.setText(p.getNoHp());
        kolomAlamat.setText(p.getAlamat());
    }

    private void bersihkanForm() {
        kolomNama.setText("");
        kolomNoHp.setText("");
        kolomAlamat.setText("");
    }

    /**
     * Membaca dan memeriksa isi form menjadi objek Pelanggan.
     * Kalau ada yang salah, tampilkan pesan dan kembalikan null.
     */
    private Pelanggan bacaForm(int id) {
        String nama = kolomNama.getText().trim();
        String noHp = kolomNoHp.getText().trim();
        String alamat = kolomAlamat.getText().trim();

        if (nama.isEmpty()) {
            peringatan("Nama wajib diisi.");
            kolomNama.requestFocusInWindow();
            return null;
        }
        // No. HP boleh kosong, tapi kalau diisi harus berupa angka (boleh ada + dan -)
        if (!noHp.isEmpty() && !noHp.matches("[0-9+\\- ]{8,20}")) {
            peringatan("No. HP hanya boleh berisi angka (boleh ada + dan -), 8 sampai 20 karakter.");
            kolomNoHp.requestFocusInWindow();
            return null;
        }
        return new Pelanggan(id, nama, noHp, alamat);
    }

    private void peringatan(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Yummy Risol Manager", JOptionPane.WARNING_MESSAGE);
    }

    private void gagal(String aksi, SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal " + aksi + " pelanggan: " + e.getMessage(),
                "Yummy Risol Manager", JOptionPane.ERROR_MESSAGE);
    }
}