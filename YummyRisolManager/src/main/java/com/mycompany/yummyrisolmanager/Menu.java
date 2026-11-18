package com.mycompany.yummyrisolmanager;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.mycompany.yummyrisolmanager.ui.LoginFrame;
import com.mycompany.yummyrisolmanager.ui.Tema;
import java.util.Collections;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Mengatur tampilan global dan perpindahan antar layar.
 * Main hanya memanggil class ini.
 */
public class Menu {

    public void mulai() {
        aturTema();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    private void aturTema() {
        // Warna aksen (tombol utama, fokus, seleksi) jadi oranye
        FlatLaf.setGlobalExtraDefaults(Collections.singletonMap("@accentColor", "#B94A10"));
        FlatLightLaf.setup();

        // Sudut membulat
        UIManager.put("Component.arc", 10);
        UIManager.put("Button.arc", 10);
        UIManager.put("TextComponent.arc", 10);

        // Warna border field
        UIManager.put("Component.borderColor", Tema.BORDER);
    }
}