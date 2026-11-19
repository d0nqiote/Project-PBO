package com.mycompany.yummyrisolmanager.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Query tabel produk untuk Dashboard dan layar Kelola Produk.
 * Method baca mencetak pesan ke konsol kalau gagal, sedangkan method tambah/ubah/hapus
 * melempar SQLException supaya layar bisa memberi tahu pengguna.
 */
public class ProdukRepository {

    /** Produk yang stoknya sudah sama dengan atau di bawah batas minimum, format "Nama (stok)". */
    public static List<String> ambilStokMenipis() {
        List<String> daftar = new ArrayList<>();
        String sql = "SELECT nama_produk, stok FROM produk WHERE stok <= stok_minimum ORDER BY stok";

        try (Connection k = Koneksi.getKoneksi();
             PreparedStatement ps = k.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                daftar.add(rs.getString(1) + " (" + rs.getInt(2) + ")");
            }
        } catch (SQLException e) {
            System.out.println("Gagal ambil stok menipis: " + e.getMessage());
        }
        return daftar;
    }

    /**
     * Semua produk yang nama atau rasanya mengandung kata kunci (kosong = semua).
     * Tiap baris berisi {id, nama, jenis, rasa, tipe, isi, harga, stok, stokMinimum}.
     */
    public static List<Object[]> ambilSemua(String kataKunci) {
        List<Object[]> daftar = new ArrayList<>();
        String sql = "SELECT id_produk, nama_produk, jenis, rasa, tipe, isi, harga, stok, stok_minimum "
                + "FROM produk WHERE nama_produk LIKE ? OR rasa LIKE ? ORDER BY id_produk";

        try (Connection k = Koneksi.getKoneksi();
             PreparedStatement ps = k.prepareStatement(sql)) {
            String pola = "%" + kataKunci + "%";
            ps.setString(1, pola);
            ps.setString(2, pola);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    daftar.add(new Object[]{
                        rs.getInt(1), rs.getString(2), rs.getString(3), kosongKalauNull(rs.getString(4)),
                        rs.getString(5), rs.getInt(6), rs.getInt(7), rs.getInt(8), rs.getInt(9)
                    });
                }
            }
        } catch (SQLException e) {
            System.out.println("Gagal ambil produk: " + e.getMessage());
        }
        return daftar;
    }

    public static void tambah(String nama, String jenis, String rasa, String tipe,
            int isi, int harga, int stok, int stokMinimum) throws SQLException {
        String sql = "INSERT INTO produk (nama_produk, jenis, rasa, tipe, isi, harga, stok, stok_minimum) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection k = Koneksi.getKoneksi();
             PreparedStatement ps = k.prepareStatement(sql)) {
            isiParameter(ps, nama, jenis, rasa, tipe, isi, harga, stok, stokMinimum);
            ps.executeUpdate();
        }
    }

    public static void ubah(int idProduk, String nama, String jenis, String rasa, String tipe,
            int isi, int harga, int stok, int stokMinimum) throws SQLException {
        String sql = "UPDATE produk SET nama_produk = ?, jenis = ?, rasa = ?, tipe = ?, "
                + "isi = ?, harga = ?, stok = ?, stok_minimum = ? WHERE id_produk = ?";

        try (Connection k = Koneksi.getKoneksi();
             PreparedStatement ps = k.prepareStatement(sql)) {
            isiParameter(ps, nama, jenis, rasa, tipe, isi, harga, stok, stokMinimum);
            ps.setInt(9, idProduk);
            ps.executeUpdate();
        }
    }

    /** Gagal dengan SQLIntegrityConstraintViolationException kalau produk sudah dipakai di pesanan. */
    public static void hapus(int idProduk) throws SQLException {
        String sql = "DELETE FROM produk WHERE id_produk = ?";

        try (Connection k = Koneksi.getKoneksi();
             PreparedStatement ps = k.prepareStatement(sql)) {
            ps.setInt(1, idProduk);
            ps.executeUpdate();
        }
    }

    private static void isiParameter(PreparedStatement ps, String nama, String jenis, String rasa,
            String tipe, int isi, int harga, int stok, int stokMinimum) throws SQLException {
        ps.setString(1, nama);
        ps.setString(2, jenis);
        ps.setString(3, rasa);
        ps.setString(4, tipe);
        ps.setInt(5, isi);
        ps.setInt(6, harga);
        ps.setInt(7, stok);
        ps.setInt(8, stokMinimum);
    }

    private static String kosongKalauNull(String teks) {
        return teks == null ? "" : teks;
    }
}