package com.mycompany.yummyrisolmanager.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;

/**
 * Warna dan font yang dipakai bersama oleh semua layar.
 */
public class Tema {

    // warna utama
    public static final Color ORANYE = new Color(0xB94A10);
    public static final Color KUNING = new Color(0xF2B33D);
    public static final Color COKELAT_TUA = new Color(0x8F3A0C);
    public static final Color KREM = new Color(0xFBF3EA);
    public static final Color COKELAT_GELAP = new Color(0x2B1A10);
    public static final Color BORDER = new Color(0xE5D9CC);
    public static final Color TEKS_ABU = new Color(0x6B5B4E);
    public static final Color PUTIH = Color.WHITE;

    // banner peringatan
    public static final Color BANNER_BG = new Color(0xFCE0DE);
    public static final Color BANNER_TEKS = new Color(0xB3261E);

    // chip status dan ikon kartu
    public static final Color BIRU_BG = new Color(0xDCE8FA);
    public static final Color BIRU_TEKS = new Color(0x1F4E96);
    public static final Color KUNING_BG = new Color(0xFDEFC4);
    public static final Color KUNING_TEKS = new Color(0x8A6100);
    public static final Color HIJAU_BG = new Color(0xD8F2DF);
    public static final Color HIJAU_TEKS = new Color(0x1B6B34);

    private static final String NAMA_FONT = pilihNamaFont();

    /**
     * Pakai Inter kalau filenya ada di src/main/resources/fonts/Inter-Regular.ttf,
     * kalau tidak ada pakai Segoe UI.
     */
    private static String pilihNamaFont() {
        String nama = "Segoe UI";
        try (InputStream in = Tema.class.getResourceAsStream("/fonts/Inter-Regular.ttf")) {
            if (in != null) {
                Font inter = Font.createFont(Font.TRUETYPE_FONT, in);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(inter);
                nama = inter.getFamily();
            }
        } catch (Exception e) {
            System.out.println("Font Inter tidak bisa dimuat: " + e.getMessage());
        }
        return nama;
    }

    public static Font font(int gaya, int ukuran) {
        return new Font(NAMA_FONT, gaya, ukuran);
    }
}