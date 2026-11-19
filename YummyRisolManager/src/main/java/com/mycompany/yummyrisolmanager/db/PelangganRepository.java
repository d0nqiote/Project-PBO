package com.mycompany.yummyrisolmanager.db;

import com.mycompany.yummyrisolmanager.model.Pelanggan;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Query tabel pelanggan untuk layar Kelola Pelanggan.
 * Method baca mencetak pesan ke konsol kalau gagal (sama seperti repository lain),
 * sedangkan method tambah/ubah/hapus melempar SQLException supaya layar bisa memberi tahu pengguna.
 */
public class PelangganRepository {

    /** Semua pelanggan yang nama, no HP, atau alamatnya mengandung kata kunci (kosong = semua). */
    public static List<Pelanggan> ambilSemua(String kataKunci) {
        List<Pelanggan> daftar = new ArrayList<>();
        String sql = "SELECT id_pelanggan, nama, no_hp, alamat FROM pelanggan "
                + "WHERE nama LIKE ? OR no_hp LIKE ? OR alamat LIKE ? ORDER BY id_pelanggan";

        try (Connection k = Koneksi.getKoneksi();
             PreparedStatement ps = k.prepareStatement(sql)) {
            String pola = "%" + kataKunci + "%";
            ps.setString(1, pola);
            ps.setString(2, pola);
            ps.setString(3, pola);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    daftar.add(new Pelanggan(rs.getInt(1), rs.getString(2),
                            kosongKalauNull(rs.getString(3)), kosongKalauNull(rs.getString(4))));
                }
            }
        } catch (SQLException e) {
            System.out.println("Gagal ambil pelanggan: " + e.getMessage());
        }
        return daftar;
    }

    public static void tambah(Pelanggan p) throws SQLException {
        String sql = "INSERT INTO pelanggan (nama, no_hp, alamat) VALUES (?, ?, ?)";

        try (Connection k = Koneksi.getKoneksi();
             PreparedStatement ps = k.prepareStatement(sql)) {
            ps.setString(1, p.getNama());
            ps.setString(2, p.getNoHp());
            ps.setString(3, p.getAlamat());
            ps.executeUpdate();
        }
    }

    public static void ubah(Pelanggan p) throws SQLException {
        String sql = "UPDATE pelanggan SET nama = ?, no_hp = ?, alamat = ? WHERE id_pelanggan = ?";

        try (Connection k = Koneksi.getKoneksi();
             PreparedStatement ps = k.prepareStatement(sql)) {
            ps.setString(1, p.getNama());
            ps.setString(2, p.getNoHp());
            ps.setString(3, p.getAlamat());
            ps.setInt(4, p.getIdPelanggan());
            ps.executeUpdate();
        }
    }

    /** Gagal dengan SQLIntegrityConstraintViolationException kalau pelanggan sudah punya pesanan. */
    public static void hapus(int idPelanggan) throws SQLException {
        String sql = "DELETE FROM pelanggan WHERE id_pelanggan = ?";

        try (Connection k = Koneksi.getKoneksi();
             PreparedStatement ps = k.prepareStatement(sql)) {
            ps.setInt(1, idPelanggan);
            ps.executeUpdate();
        }
    }

    private static String kosongKalauNull(String teks) {
        return teks == null ? "" : teks;
    }
}