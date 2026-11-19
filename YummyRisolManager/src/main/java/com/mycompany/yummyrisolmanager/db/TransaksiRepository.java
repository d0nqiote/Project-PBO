package com.mycompany.yummyrisolmanager.db;

import com.mycompany.yummyrisolmanager.model.DetailPesanan;
import com.mycompany.yummyrisolmanager.model.Pesanan;
import com.mycompany.yummyrisolmanager.model.Produk;
import com.mycompany.yummyrisolmanager.model.ProdukFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Akses data untuk layar Buat Pesanan: ambil produk sebagai objek, lalu simpan pesanan.
 * Pelanggan diambil lewat PelangganRepository.ambilSemua("").
 */
public class TransaksiRepository {

    /** Semua produk sebagai objek turunan Produk (RisolCoklat, RisolMayo, dst). */
    public static List<Produk> ambilProduk() {
        List<Produk> daftar = new ArrayList<>();
        String sql = "SELECT id_produk, nama_produk, jenis, rasa, tipe, isi, harga, stok, stok_minimum "
                + "FROM produk ORDER BY jenis, nama_produk";

        try (Connection k = Koneksi.getKoneksi();
             PreparedStatement ps = k.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                daftar.add(ProdukFactory.buat(rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getString(5), rs.getInt(6), rs.getDouble(7),
                        rs.getInt(8), rs.getInt(9)));
            }
        } catch (SQLException e) {
            System.out.println("Gagal ambil produk: " + e.getMessage());
        }
        return daftar;
    }

    /**
     * Menyimpan pesanan, isi detailnya, dan mengurangi stok dalam SATU transaksi:
     * kalau satu langkah gagal (misalnya stok kurang), semuanya dibatalkan.
     *
     * @return id pesanan yang baru dibuat
     */
    public static int simpan(Pesanan pesanan, String jabatan) throws SQLException {
        try (Connection k = Koneksi.getKoneksi()) {
            k.setAutoCommit(false);
            try {
                int idPesanan = sisipPesanan(k, pesanan, ambilIdPengguna(k, jabatan));
                sisipDetail(k, pesanan, idPesanan);
                k.commit();
                return idPesanan;
            } catch (SQLException e) {
                k.rollback();
                throw e;
            }
        }
    }

    /** Sementara: pengguna pertama dengan jabatan yang sedang login (Login belum menyimpan id). */
    private static int ambilIdPengguna(Connection k, String jabatan) throws SQLException {
        String sql = "SELECT id_pengguna FROM pengguna WHERE jabatan = ? ORDER BY id_pengguna LIMIT 1";
        try (PreparedStatement ps = k.prepareStatement(sql)) {
            ps.setString(1, jabatan.toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Akun dengan jabatan " + jabatan + " tidak ditemukan.");
    }

    private static int sisipPesanan(Connection k, Pesanan p, int idPengguna) throws SQLException {
        String sql = "INSERT INTO pesanan (id_pelanggan, id_pengguna, tanggal, metode_order, status, total_bayar) "
                + "VALUES (?, ?, NOW(), ?, ?, ?)";
        try (PreparedStatement ps = k.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getPelanggan().getIdPelanggan());
            ps.setInt(2, idPengguna);
            ps.setString(3, p.getMetodeOrder());
            ps.setString(4, p.getStatus());
            ps.setLong(5, Math.round(p.hitungTotal()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("ID pesanan tidak terbentuk.");
    }

    private static void sisipDetail(Connection k, Pesanan p, int idPesanan) throws SQLException {
        String sqlDetail = "INSERT INTO detail_pesanan (id_pesanan, id_produk, jumlah, subtotal) VALUES (?, ?, ?, ?)";
        String sqlStok = "UPDATE produk SET stok = stok - ? WHERE id_produk = ? AND stok >= ?";

        try (PreparedStatement detail = k.prepareStatement(sqlDetail);
             PreparedStatement stok = k.prepareStatement(sqlStok)) {
            for (DetailPesanan d : p.getDaftarItem()) {
                int idProduk = d.getProduk().getIdProduk();

                stok.setInt(1, d.getJumlah());
                stok.setInt(2, idProduk);
                stok.setInt(3, d.getJumlah());
                if (stok.executeUpdate() == 0) {
                    throw new SQLException("Stok " + d.getProduk().getNama() + " tidak cukup.");
                }

                detail.setInt(1, idPesanan);
                detail.setInt(2, idProduk);
                detail.setInt(3, d.getJumlah());
                detail.setLong(4, Math.round(d.getSubtotal()));
                detail.executeUpdate();
            }
        }
    }
}