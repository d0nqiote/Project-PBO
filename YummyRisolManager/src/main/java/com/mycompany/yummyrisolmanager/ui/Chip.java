package com.mycompany.yummyrisolmanager.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

/**
 * Label kecil berlatar bulat berwarna (misalnya MANIS, ASIN, "Hampir habis").
 */
public class Chip extends JLabel {

    private Color warnaLatar = Tema.PUTIH;

    public Chip() {
        setOpaque(false);
        setFont(Tema.font(Font.BOLD, 12));
        setBorder(new EmptyBorder(3, 10, 3, 10));
    }

    public void atur(String teks, Color latar, Color warnaTeks) {
        setText(teks);
        warnaLatar = latar;
        setForeground(warnaTeks);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(warnaLatar);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }
}