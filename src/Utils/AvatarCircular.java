package Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class AvatarCircular extends JLabel {

    private Image imagenOriginal;

    public AvatarCircular() {
        setOpaque(false);
    }

    public void setImagen(String rutaImagen) {
        try {
            if (rutaImagen != null && !rutaImagen.trim().isEmpty() && new File(rutaImagen).exists()) {
                this.imagenOriginal = ImageIO.read(new File(rutaImagen));
            } else {
                cargarImagenPorDefecto();
            }
        } catch (Exception e) {
            cargarImagenPorDefecto();
        }
        repaint();
    }

    private void cargarImagenPorDefecto() {
        try {
            // Carga desde la carpeta de recursos src/resources/default_avatar.png
            java.net.URL defaultUrl = getClass().getResource("/resources/default_avatar.png");
            if (defaultUrl != null) {
                this.imagenOriginal = ImageIO.read(defaultUrl);
            } else {
                this.imagenOriginal = null;
            }
        } catch (Exception e) {
            this.imagenOriginal = null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        int diameter = Math.min(getWidth(), getHeight());
        if (diameter <= 0) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (imagenOriginal != null) {
            // Crear máscara circular
            BufferedImage mask = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = mask.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2d.fill(new Ellipse2D.Double(0, 0, diameter, diameter));
            g2d.setComposite(AlphaComposite.SrcIn);
            g2d.drawImage(imagenOriginal, 0, 0, diameter, diameter, null);
            g2d.dispose();

            // Dibujar imagen redondeada centrada
            int x = (getWidth() - diameter) / 2;
            int y = (getHeight() - diameter) / 2;
            g2.drawImage(mask, x, y, null);
        } else {
            // Si falla todo, dibuja un círculo gris elegante
            g2.setColor(new Color(200, 200, 200));
            g2.fillOval(0, 0, diameter, diameter);
        }

        // Borde blanco sutil para dar acabado profesional
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(1, 1, diameter - 2, diameter - 2);

        g2.dispose();
    }
}