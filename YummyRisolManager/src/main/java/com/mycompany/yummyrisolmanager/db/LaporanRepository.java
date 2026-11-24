package com.mycompany.yummyrisolmanager.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Query untuk layar Laporan Penjualan. Parameter tanggal null = semua tanggal.
 * Method baca mencetak pesan ke konsol kalau gagal (sama seperti repository lain).
 */
public class LaporanRepository {

    /** Tanggal yang punya pesanan, dari yang terbaru. */
    public static List<LocalDate> ambilTanggal() {
        List<LocalDate> daftar = new ArrayList<>();
        String sql = "SELECT DISTINCT DATE(tanggal) AS hari FROM pesanan ORDER BY hari DESC";

        try (Connection k = Koneksi.getKoneksi();
             PreparedStatement ps = k.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                daftar.add(rs.getDate(1).toLocalDate());
            }
        } catch (SQLException e) {
            System.out.println("Gagal ambil tanggal laporan: " + e.getMessage());
        }
        return daftar;
    }

    /** Riwayat pesanan: tiap baris {id, tanggal, pelanggan, metode, total, status}. */
    public static List<Object[]> ambilRiwayat(LocalDate tanggal) {
        List<Object[]> daftar = new ArrayList<>();
        String sql = "SELECT p.id_pesanan, DATE(p.tanggal), pl.nama, p.metode_order, p.total_bayar, p.status "
                + "FROM pesanan p JOIN pelanggan pl ON pl.id_pelanggan = p.id_pelanggan "
                + (tanggal == null ? "" : "WHERE DATE(p.tanggal) = ? ")
                + "ORDER BY p.tanggal DESC, p.id_pesanan DESC";

        try (Connection k = Koneksi.getKoneksi();
             PreparedStatement ps = k.prepareStatement(sql)) {
            if (tanggal != null) {
                ps.setDate(1, Date.valueOf(tanggal));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    daftar.add(new Object[]{rs.getInt(1), rs.getDate(2).toLocalDate(), rs.getString(3),
                        rs.getString(4), rs.getDouble(5), rs.getString(6)});
                }
            }
        } catch (SQLException e) {
            System.out.println("Gagal ambil riwayat: " + e.getMessage());
        }
        return daftar;
    }

    /**
     * Mengubah status satu pesanan (BARU, DIPROSES, atau SELESAI).
     *
     * @return true kalau baris berhasil diubah
     */
    public static boolean ubahStatus(int idPesanan, String status) {
        String sql = "UPDATE pesanan SET status = ? WHERE id_pesanan = ?";

        try (Connection k = Koneksi.getKoneksi();
             PreparedStatement ps = k.prepareStatement(sql)) {
            ps.setString(1, status.toUpperCase());
            ps.setInt(2, idPesanan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Gagal ubah status pesanan: " + e.getMessage());
            return false;
        }
    }

    /** Jumlah semua pesanan pada tanggal itu. */
    public static int hitungPesanan(LocalDate tanggal) {
        String sql = "SELECT COUNT(*) FROM pesanan" + (tanggal == null ? "" : " WHERE DATE(tanggal) = ?");
        return ambilAngka(sql, tanggal).intValue();
    }

    /** Total uang dari pesanan berstatus SELESAI (sama dengan hitungan di Dashboard). */
    public static double hitungPenjualan(LocalDate tanggal) {
        String sql = "SELECT COALESCE(SUM(total_bayar), 0) FROM pesanan WHERE status = 'SELESAI'"
                + (tanggal == null ? "" : " AND DATE(tanggal) = ?");
        return ambilAngka(sql, tanggal).doubleValue();
    }

    private static Number ambilAngka(String sql, LocalDate tanggal) {
        try (Connection k = Koneksi.getKoneksi();
             PreparedStatement ps = k.prepareStatement(sql)) {
            if (tanggal != null) {
                ps.setDate(1, Date.valueOf(tanggal));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Gagal hitung laporan: " + e.getMessage());
        }
        return 0;
    }
}