package com.mycompany.yummyrisolmanager.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Query tabel produk. Untuk sekarang baru dipakai Dashboard.
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
}