package com.mycompany.yummyrisolmanager.ui;

import com.mycompany.yummyrisolmanager.db.PenggunaRepository;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class LoginFrame extends JFrame {

    private static final int LEBAR_FORM = 330;
    private static final int TINGGI_FIELD = 40;
    private static final int TINGGI_TOMBOL = 44;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbPeran;
    private JButton btnMasuk;

    public LoginFrame() {
        setTitle("Yummy Risol Manager – Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 680);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2));

        add(buatPanelKiri());
        add(buatPanelKanan());

        // Tekan Enter di mana saja = klik tombol Masuk
        getRootPane().setDefaultButton(btnMasuk);
    }

    // ===================== PANEL KIRI (oranye) =====================

    private JPanel buatPanelKiri() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                g2.setColor(Tema.ORANYE);
                g2.fillRect(0, 0, w, h);

                // lingkaran kuning besar (kanan atas)
                int r1 = (int) (w * 0.29);
                g2.setColor(Tema.KUNING);
                g2.fillOval((int) (w * 0.87) - r1, (int) (h * 0.12) - r1, r1 * 2, r1 * 2);

                // lingkaran cokelat (kiri tengah)
                int r2 = (int) (w * 0.185);
                g2.setColor(Tema.COKELAT_TUA);
                g2.fillOval((int) (w * 0.06) - r2, (int) (h * 0.44) - r2, r2 * 2, r2 * 2);

                g2.dispose();
            }
        };
        panel.add(buatTeksPanelKiri(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buatTeksPanelKiri() {
        JPanel teks = new JPanel();
        teks.setOpaque(false);
        teks.setLayout(new BoxLayout(teks, BoxLayout.Y_AXIS));
        teks.setBorder(new EmptyBorder(0, 56, 56, 56));

        teks.add(buatLabel("Yummy", 40, Font.BOLD, Tema.PUTIH));
        teks.add(buatLabel("Risol Manager", 40, Font.BOLD, Tema.PUTIH));
        teks.add(Box.createRigidArea(new Dimension(0, 12)));
        teks.add(buatLabel("<html>Catat pesanan, pantau stok, dan lihat<br>"
                + "rekap penjualan dalam satu aplikasi.</html>", 15, Font.PLAIN, Tema.PUTIH));
        return teks;
    }

    // ===================== PANEL KANAN (form) =====================

    private JPanel buatPanelKanan() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Tema.KREM);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        form.add(buatLabel("Selamat datang", 26, Font.BOLD, Tema.COKELAT_GELAP));
        form.add(buatLabel("Masuk untuk melanjutkan", 14, Font.PLAIN, Tema.TEKS_ABU));
        form.add(Box.createRigidArea(new Dimension(0, 24)));

        form.add(buatLabel("Username", 13, Font.BOLD, Tema.COKELAT_GELAP));
        form.add(Box.createRigidArea(new Dimension(0, 6)));
        txtUsername = new JTextField();
        txtUsername.putClientProperty("JTextField.placeholderText", "Masukkan username");
        form.add(aturUkuran(txtUsername, TINGGI_FIELD));
        form.add(Box.createRigidArea(new Dimension(0, 16)));

        form.add(buatLabel("Password", 13, Font.BOLD, Tema.COKELAT_GELAP));
        form.add(Box.createRigidArea(new Dimension(0, 6)));
        txtPassword = new JPasswordField();
        txtPassword.putClientProperty("JTextField.placeholderText", "Masukkan password");
        form.add(aturUkuran(txtPassword, TINGGI_FIELD));
        form.add(Box.createRigidArea(new Dimension(0, 16)));

        form.add(buatLabel("Masuk sebagai", 13, Font.BOLD, Tema.COKELAT_GELAP));
        form.add(Box.createRigidArea(new Dimension(0, 6)));
        cmbPeran = new JComboBox<>(new String[]{"Admin", "Kasir"});
        form.add(aturUkuran(cmbPeran, TINGGI_FIELD));
        form.add(Box.createRigidArea(new Dimension(0, 20)));

        btnMasuk = new JButton("Masuk");
        btnMasuk.setFont(Tema.font(Font.BOLD, 14));
        btnMasuk.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnMasuk.setFocusPainted(false);
        btnMasuk.addActionListener(e -> aksiMasuk());
        form.add(aturUkuran(btnMasuk, TINGGI_TOMBOL));

        panel.add(form);
        return panel;
    }

    // ===================== AKSI =====================

    private void aksiMasuk() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String peran = cmbPeran.getSelectedItem().toString();

        if (username.isEmpty() || password.isEmpty()) {
            tampilkanPesan("Username dan password wajib diisi.", JOptionPane.WARNING_MESSAGE);
        } else if (PenggunaRepository.cekLogin(username, password, peran)) {
            bukaDashboard(peran);
        } else {
            tampilkanPesan("Username, password, atau peran salah.", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bukaDashboard(String peran) {
        new DashboardFrame(peran).setVisible(true);
        dispose();
        tampilkanPesan("Login berhasil sebagai " + peran + ".", JOptionPane.INFORMATION_MESSAGE);
    }

    private void tampilkanPesan(String pesan, int jenis) {
        JOptionPane.showMessageDialog(this, pesan, "Yummy Risol Manager", jenis);
    }

    // ===================== HELPER =====================

    private JLabel buatLabel(String teks, int ukuran, int gaya, java.awt.Color warna) {
        JLabel label = new JLabel(teks);
        label.setFont(Tema.font(gaya, ukuran));
        label.setForeground(warna);
        label.setAlignmentX(LEFT_ALIGNMENT);
        return label;
    }

    private JComponent aturUkuran(JComponent komponen, int tinggi) {
        Dimension ukuran = new Dimension(LEBAR_FORM, tinggi);
        komponen.setPreferredSize(ukuran);
        komponen.setMinimumSize(ukuran);
        komponen.setMaximumSize(ukuran);
        komponen.setAlignmentX(LEFT_ALIGNMENT);
        if (!(komponen instanceof JButton)) {
            komponen.setFont(Tema.font(Font.PLAIN, 14));
        }
        return komponen;
    }
}