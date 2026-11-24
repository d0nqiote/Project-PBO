package com.mycompany.yummyrisolmanager.ui;

import com.mycompany.yummyrisolmanager.db.LaporanRepository;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 * Layar Laporan Penjualan: pilih tanggal, lihat rekap dan riwayat pesanan.
 * Kerangka (sidebar dan area krem) diwarisi dari FrameDasar.
 */
public class LaporanFrame extends FrameDasar {

    private static final String[] KOLOM = {"ID", "TANGGAL", "PELANGGAN", "METODE", "TOTAL", "STATUS"};
    private static final int KOLOM_STATUS = 5;
    private static final DateTimeFormatter FORMAT_TANGGAL
            = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.forLanguageTag("id-ID"));

    /** Hanya Admin yang boleh mengubah status; kolom lain tidak bisa diedit. */
    private final DefaultTableModel modelTabel = new DefaultTableModel(KOLOM, 0) {
        @Override
        public boolean isCellEditable(int baris, int kolom) {
            return kolom == KOLOM_STATUS && peran.equalsIgnoreCase("Admin");
        }
    };
    private final JTable tabel = new JTable(modelTabel);

    private final JComboBox<String> cmbTanggal = Komponen.pilihan();
    private final JLabel lblJumlah = Komponen.label("0", 28, Font.BOLD, Tema.COKELAT_GELAP);
    private final JLabel lblTotal = Komponen.label("Rp 0", 28, Font.BOLD, Tema.COKELAT_GELAP);

    /** Tanggal untuk tiap item combo. Indeks 0 = "Semua tanggal" (null). */
    private List<LocalDate> tanggalPilihan = new ArrayList<>();

    /** Id pesanan dan status (huruf besar, seperti di database) untuk tiap baris tabel. */
    private final List<Integer> idBaris = new ArrayList<>();
    private final List<String> statusBaris = new ArrayList<>();

    public LaporanFrame(String peran) {
        super(peran, "Laporan", "Laporan Penjualan");
        pasangKonten(buatIsi());
        modelTabel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == KOLOM_STATUS) {
                simpanStatus(e.getFirstRow());
            }
        });
        muatTanggal();
        muatLaporan();
    }

    // ===================== SUSUNAN LAYAR =====================

    private JPanel buatIsi() {
        JPanel bawah = new JPanel(new BorderLayout(0, 16));
        bawah.setOpaque(false);
        bawah.setBorder(new EmptyBorder(16, 0, 0, 0));
        bawah.add(buatBarisKartu(), BorderLayout.NORTH);
        bawah.add(buatKartuRiwayat(), BorderLayout.CENTER);

        JPanel isi = new JPanel(new BorderLayout());
        isi.setOpaque(false);
        isi.add(Komponen.header("Laporan Penjualan", "Riwayat pesanan dan rekap per tanggal", buatFilter()),
                BorderLayout.NORTH);
        isi.add(bawah, BorderLayout.CENTER);
        return isi;
    }

    /** Pilihan Tanggal + tombol Tampilkan di kanan atas. */
    private JPanel buatFilter() {
        cmbTanggal.setPreferredSize(new Dimension(190, 38));

        JButton tampilkan = Komponen.tombolUtama("Tampilkan", 110, 38);
        tampilkan.addActionListener(e -> muatLaporan());
        JPanel tombol = new JPanel(new BorderLayout());   // rata bawah, sejajar combo
        tombol.setOpaque(false);
        tombol.add(tampilkan, BorderLayout.SOUTH);

        JPanel filter = new JPanel(new BorderLayout(10, 0));
        filter.setOpaque(false);
        filter.add(Komponen.kolomForm("Tanggal", cmbTanggal), BorderLayout.CENTER);
        filter.add(tombol, BorderLayout.EAST);
        return filter;
    }

    private JPanel buatBarisKartu() {
        JPanel baris = new JPanel(new GridLayout(1, 2, 16, 0));
        baris.setOpaque(false);
        baris.setPreferredSize(new Dimension(0, 88));
        baris.add(buatKartuAngka("Jumlah pesanan", lblJumlah, Tema.BIRU_BG));
        baris.add(buatKartuAngka("Total penjualan", lblTotal, Tema.HIJAU_BG));
        return baris;
    }

    /** Kartu putih: kotak ikon berwarna di kiri, keterangan kecil + angka besar di kanan. */
    private PanelBulat buatKartuAngka(String keterangan, JLabel nilai, Color warnaIkon) {
        PanelBulat kartu = new PanelBulat(14);
        kartu.setBackground(Tema.PUTIH);
        kartu.setLayout(new BorderLayout(14, 0));
        kartu.setBorder(new EmptyBorder(0, 16, 0, 16));

        JLabel lblKeterangan = Komponen.label(keterangan, 13, Font.PLAIN, Tema.TEKS_ABU);
        lblKeterangan.setVerticalAlignment(SwingConstants.BOTTOM);
        nilai.setVerticalAlignment(SwingConstants.TOP);

        JPanel teks = new JPanel(new GridLayout(2, 1));
        teks.setOpaque(false);
        teks.add(lblKeterangan);
        teks.add(nilai);

        PanelBulat ikon = new PanelBulat(10);
        ikon.setBackground(warnaIkon);
        ikon.setPreferredSize(new Dimension(40, 40));
        JPanel pembungkus = new JPanel(new GridBagLayout());   // ikon di tengah secara vertikal
        pembungkus.setOpaque(false);
        pembungkus.setPreferredSize(new Dimension(40, 40));
        pembungkus.add(ikon);

        kartu.add(pembungkus, BorderLayout.WEST);
        kartu.add(teks, BorderLayout.CENTER);
        return kartu;
    }

    private PanelBulat buatKartuRiwayat() {
        JScrollPane scroll = Komponen.gayakanTabel(tabel);
        tabel.setRowSelectionAllowed(false);
        tabel.getColumnModel().getColumn(KOLOM_STATUS).setCellRenderer(new RendererStatus());
        tabel.getColumnModel().getColumn(KOLOM_STATUS).setCellEditor(buatEditorStatus());
        int[] lebar = {70, 110, 130, 100, 110, 100};
        for (int i = 0; i < lebar.length; i++) {
            tabel.getColumnModel().getColumn(i).setPreferredWidth(lebar[i]);
        }

        JPanel atas = new JPanel(new BorderLayout());
        atas.setOpaque(false);
        atas.add(Komponen.label("Riwayat pesanan", 16, Font.BOLD, Tema.COKELAT_GELAP), BorderLayout.WEST);
        if (peran.equalsIgnoreCase("Admin")) {
            atas.add(Komponen.label("Klik status untuk mengubahnya", 12, Font.PLAIN, Tema.TEKS_ABU),
                    BorderLayout.EAST);
        }

        PanelBulat kartu = Komponen.kartu(16, 20, 16, 20);
        kartu.add(atas, BorderLayout.NORTH);
        kartu.add(scroll, BorderLayout.CENTER);
        return kartu;
    }

    /** Kotak pilihan Baru / Diproses / Selesai yang muncul saat sel status diklik. */
    private DefaultCellEditor buatEditorStatus() {
        JComboBox<String> kotak = Komponen.pilihan("Baru", "Diproses", "Selesai");
        kotak.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
        DefaultCellEditor editor = new DefaultCellEditor(kotak);
        editor.setClickCountToStart(1);   // cukup satu klik
        return editor;
    }

    // ===================== DATA =====================

    /** Mengisi combo tanggal: "Semua tanggal" lalu tanggal yang punya pesanan (terbaru di atas). */
    private void muatTanggal() {
        cmbTanggal.removeAllItems();
        tanggalPilihan = new ArrayList<>();

        tanggalPilihan.add(null);
        cmbTanggal.addItem("Semua tanggal");
        for (LocalDate tanggal : LaporanRepository.ambilTanggal()) {
            tanggalPilihan.add(tanggal);
            cmbTanggal.addItem(FORMAT_TANGGAL.format(tanggal));
        }
        // awalnya tampilkan tanggal terbaru kalau ada
        cmbTanggal.setSelectedIndex(tanggalPilihan.size() > 1 ? 1 : 0);
    }

    /** Tombol Tampilkan: isi ulang kartu rekap dan tabel untuk tanggal yang dipilih. */
    private void muatLaporan() {
        if (tabel.isEditing()) {
            tabel.getCellEditor().cancelCellEditing();
        }
        muatRekap();

        LocalDate tanggal = tanggalTerpilih();
        idBaris.clear();
        statusBaris.clear();
        modelTabel.setRowCount(0);
        for (Object[] b : LaporanRepository.ambilRiwayat(tanggal)) {
            idBaris.add((Integer) b[0]);
            statusBaris.add(((String) b[5]).toUpperCase());
            modelTabel.addRow(new Object[]{
                String.format("#%04d", (Integer) b[0]),
                FORMAT_TANGGAL.format((LocalDate) b[1]),
                b[2],
                kapital((String) b[3]),
                Komponen.formatRupiah((Double) b[4]),
                kapital((String) b[5])   // BARU -> Baru, supaya cocok dengan pilihan di editor
            });
        }
    }

    /** Kartu Jumlah pesanan dan Total penjualan (Total hanya menghitung pesanan Selesai). */
    private void muatRekap() {
        LocalDate tanggal = tanggalTerpilih();
        lblJumlah.setText(String.valueOf(LaporanRepository.hitungPesanan(tanggal)));
        lblTotal.setText(Komponen.formatRupiah(LaporanRepository.hitungPenjualan(tanggal)));
    }

    private LocalDate tanggalTerpilih() {
        int indeks = cmbTanggal.getSelectedIndex();
        return indeks >= 0 && indeks < tanggalPilihan.size() ? tanggalPilihan.get(indeks) : null;
    }

    /** Dipanggil setelah status di tabel diubah: simpan ke database lalu segarkan total. */
    private void simpanStatus(int baris) {
        if (baris < 0 || baris >= idBaris.size()) {
            return;
        }
        String statusBaru = String.valueOf(modelTabel.getValueAt(baris, KOLOM_STATUS)).toUpperCase();
        if (statusBaru.equals(statusBaris.get(baris))) {
            return;   // tidak ada perubahan
        }
        if (LaporanRepository.ubahStatus(idBaris.get(baris), statusBaru)) {
            statusBaris.set(baris, statusBaru);
            muatRekap();   // pesanan yang jadi Selesai ikut masuk Total penjualan
        } else {
            JOptionPane.showMessageDialog(this, "Status pesanan gagal diubah.",
                    "Yummy Risol Manager", JOptionPane.ERROR_MESSAGE);
            SwingUtilities.invokeLater(this::muatLaporan);   // kembalikan tampilan ke data database
        }
    }

    /** LANGSUNG -> Langsung. */
    private static String kapital(String teks) {
        return teks.substring(0, 1).toUpperCase() + teks.substring(1).toLowerCase();
    }

    // ===================== RENDERER =====================

    /** Kolom status: label bulat berwarna (Baru, Diproses, Selesai). */
    private static class RendererStatus implements TableCellRenderer {

        private final JPanel sel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 9));
        private final Chip chip = new Chip();

        RendererStatus() {
            sel.setBorder(new MatteBorder(0, 0, 1, 0, Tema.BORDER));
            sel.add(chip);
        }

        @Override
        public Component getTableCellRendererComponent(JTable tabel, Object nilai,
                boolean terpilih, boolean fokus, int baris, int kolom) {
            String status = String.valueOf(nilai).toUpperCase();
            if (status.equals("BARU")) {
                chip.atur("Baru", Tema.BIRU_BG, Tema.BIRU_TEKS);
            } else if (status.equals("DIPROSES")) {
                chip.atur("Diproses", Tema.KUNING_BG, Tema.KUNING_TEKS);
            } else {
                chip.atur("Selesai", Tema.HIJAU_BG, Tema.HIJAU_TEKS);
            }
            sel.setBackground(Tema.PUTIH);
            return sel;
        }
    }
}