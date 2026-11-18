package com.mycompany.yummyrisolmanager.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Query tabel pesanan untuk kebutuhan Dashboard.
 */
public class PesananRepository {

    /** Jumlah pesanan yang dibuat hari ini. */
    public static int hitungPesananHariIni() {
        int jumlah = 0;
        String sql = "SELECT COUNT(*) FROM pesanan WHERE DATE(tanggal) = CURDATE()";

        try (Connection k = Koneksi.getKoneksi();
             PreparedStatement ps = k.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                jumlah = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Gagal hitung pesanan hari ini: " + e.getMessage());
        }
        return jumlah;
    }

    /** Total uang dari pesanan berstatus SELESAI hari ini. */
    public static double hitungPenjualanHariIni() {
        double total = 0;
        String sql = "SELECT COALESCE(SUM(total_bayar), 0) FROM pesanan "
                + "WHERE DATE(tanggal) = CURDATE() AND status = 'SELESAI'";

        try (Connection k = Koneksi.getKoneksi();
             PreparedStatement ps = k.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                total = rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.out.println("Gagal hitung penjualan hari ini: " + e.getMessage());
        }
        return total;
    }

    /**
     * Penjualan (status SELESAI) per hari untuk N hari terakhir.
     * Indeks 0 = hari paling lama, indeks terakhir = hari ini.
     */
    public static int[] ambilPenjualanHarian(int jumlahHari) {
        int[] hasil = new int[jumlahHari];
        String sql = "SELECT DATE(tanggal), SUM(total_bayar) FROM pesanan "
                + "WHERE status = 'SELESAI' "
                + "AND DATE(tanggal) >= DATE_SUB(CURDATE(), INTERVAL ? DAY) "
                + "GROUP BY DATE(tanggal)";

        try (Connection k = Koneksi.getKoneksi();
             PreparedStatement ps = k.prepareStatement(sql)) {
            ps.setInt(1, jumlahHari - 1);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    isiHari(hasil, rs.getDate(1).toLocalDate(), rs.getInt(2));
                }
            }
        } catch (SQLException e) {
            System.out.println("Gagal ambil penjualan harian: " + e.getMessage());
        }
        return hasil;
    }

    private static void isiHari(int[] hasil, LocalDate tanggal, int nilai) {
        long selisih = ChronoUnit.DAYS.between(tanggal, LocalDate.now());
        int indeks = hasil.length - 1 - (int) selisih;
        if (indeks >= 0 && indeks < hasil.length) {
            hasil[indeks] = nilai;
        }
    }

    /** Pesanan terbaru: tiap baris berisi {nama pelanggan, status}. */
    public static List<String[]> ambilPesananTerbaru(int batas) {
        List<String[]> daftar = new ArrayList<>();
        String sql = "SELECT pl.nama, p.status FROM pesanan p "
                + "JOIN pelanggan pl ON pl.id_pelanggan = p.id_pelanggan "
                + "ORDER BY p.tanggal DESC, p.id_pesanan DESC LIMIT ?";

        try (Connection k = Koneksi.getKoneksi();
             PreparedStatement ps = k.prepareStatement(sql)) {
            ps.setInt(1, batas);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    daftar.add(new String[]{rs.getString(1), rs.getString(2)});
                }
            }
        } catch (SQLException e) {
            System.out.println("Gagal ambil pesanan terbaru: " + e.getMessage());
        }
        return daftar;
    }
}