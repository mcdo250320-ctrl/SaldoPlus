package Utils;

import java.awt.Image;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class ImageUtils {

    /**
     * Carga una imagen, la escala al tamaño exacto del JLabel y la asigna.
     * Si la ruta es nula o no existe, carga una foto por defecto (avatar genérico).
     */
    public static void setFotoPerfil(JLabel lblContenedor, String rutaFoto) {
        int width = lblContenedor.getWidth() > 0 ? lblContenedor.getWidth() : 100;
        int height = lblContenedor.getHeight() > 0 ? lblContenedor.getHeight() : 100;

        ImageIcon icon = null;

        if (rutaFoto != null && !rutaFoto.trim().isEmpty() && new File(rutaFoto).exists()) {
            icon = new ImageIcon(rutaFoto);
        } else {
            // Imagen por defecto si el usuario no tiene foto aún (coloca un avatar.png en tus recursos)
            java.net.URL defaultUrl = ImageUtils.class.getResource("/resources/avatar_default.png");
            if (defaultUrl != null) {
                icon = new ImageIcon(defaultUrl);
            }
        }

        if (icon != null) {
            // Escalar suavemente (SCALE_SMOOTH) para mantener alta calidad
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            lblContenedor.setIcon(new ImageIcon(img));
            lblContenedor.setText(""); // Limpiar texto si lo hubiera
        }
    }
}