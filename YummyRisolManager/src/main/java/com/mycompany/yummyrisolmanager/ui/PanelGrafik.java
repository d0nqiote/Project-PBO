package com.mycompany.yummyrisolmanager.ui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * Grafik batang penjualan 7 hari terakhir.
 * Batang tertinggi berwarna oranye, sisanya kuning.
 * Isi datanya nanti lewat setData(...) dari DashboardFrame.
 */
public class PanelGrafik extends JPanel {

    private String[] label = {"Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min"};
    private int[] nilai = {45, 70, 55, 85, 100, 65, 40}; // data contoh

    public PanelGrafik() {
        setOpaque(false);
    }

    public void setData(String[] label, int[] nilai) {
        this.label = label;
        this.nilai = nilai;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int n = nilai.length;
        int max = hitungMaks();
        int jarak = 14;
        int lebarBatang = (getWidth() - jarak * (n + 1)) / n;
        int garisDasar = getHeight() - 28; // sisakan ruang buat label hari

        g2.setFont(Tema.font(java.awt.Font.PLAIN, 12));
        FontMetrics fm = g2.getFontMetrics();

        for (int i = 0; i < n; i++) {
            int tinggi = (int) ((double) nilai[i] / max * (garisDasar - 10));
            int x = jarak + i * (lebarBatang + jarak);
            int y = garisDasar - tinggi;

            Color warna = (nilai[i] == max) ? Tema.ORANYE : Tema.KUNING;
            g2.setColor(warna);
            g2.fillRoundRect(x, y, lebarBatang, tinggi, 8, 8);

            g2.setColor(Tema.TEKS_ABU);
            int xTeks = x + (lebarBatang - fm.stringWidth(label[i])) / 2;
            g2.drawString(label[i], xTeks, getHeight() - 8);
        }
        g2.dispose();
    }

    private int hitungMaks() {
        int max = 1;
        for (int v : nilai) {
            if (v > max) {
                max = v;
            }
        }
        return max;
    }
}