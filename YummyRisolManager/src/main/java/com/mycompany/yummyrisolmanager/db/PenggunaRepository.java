package com.mycompany.yummyrisolmanager.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PenggunaRepository {

    /**
     * Mengecek apakah kombinasi username, password, dan peran ada di tabel pengguna.
     * Memakai PreparedStatement supaya aman dari SQL injection.
     */
    public static boolean cekLogin(String username, String password, String peran) {
        boolean cocok = false;
        String sql = "SELECT 1 FROM pengguna WHERE username = ? AND password = ? AND jabatan = ?";

        try (Connection k = Koneksi.getKoneksi();
             PreparedStatement ps = k.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, peran.toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                cocok = rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Gagal cek login: " + e.getMessage());
        }
        return cocok;
    }
}