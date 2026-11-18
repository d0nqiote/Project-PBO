package com.mycompany.yummyrisolmanager.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * JPanel dengan sudut membulat. Dipakai untuk kartu putih di Dashboard.
 * Warna diatur lewat Properties > background di tab Design.
 */
public class PanelBulat extends JPanel {

    private final int radius;

    public PanelBulat() {
        this(14);
    }

    public PanelBulat(int radius) {
        this.radius = radius;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius * 2, radius * 2);
        g2.dispose();
        super.paintComponent(g);
    }
}