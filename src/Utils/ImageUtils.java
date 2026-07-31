package Utils;

import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class ImageUtils {

    public static void setFotoEnLabel(JLabel label, String rutaFoto) {
        if (rutaFoto == null || rutaFoto.isEmpty()) return;

        ImageIcon iconOriginal = new ImageIcon(rutaFoto);
        if (iconOriginal.getImageLoadStatus() == java.awt.MediaTracker.ERRORED) {
            return; // Si no existe el archivo, no hace nada
        }

        // Obtener dimensiones del JLabel (o 100x100 por defecto)
        int ancho = label.getWidth() > 0 ? label.getWidth() : 100;
        int alto = label.getHeight() > 0 ? label.getHeight() : 100;

        // Escalar la imagen suavemente
        Image imgEscalada = iconOriginal.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        
        // Asignar al JLabel y refrescar
        label.setIcon(new ImageIcon(imgEscalada));
        label.revalidate();
        label.repaint();
    }
}