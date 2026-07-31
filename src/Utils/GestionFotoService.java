package Utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GestionFotoService {

    public static String seleccionarYGuardarFoto(int idUsuario) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Selecciona tu foto de perfil");
        chooser.setFileFilter(new FileNameExtensionFilter("Imágenes (JPG, PNG)", "jpg", "jpeg", "png"));

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File archivoOrigen = chooser.getSelectedFile();

            try {
                File carpetaDestino = new File("uploads/profiles");
                if (!carpetaDestino.exists()) {
                    carpetaDestino.mkdirs();
                }

                String nombreOriginal = archivoOrigen.getName();
                String extension = nombreOriginal.contains(".") ? nombreOriginal.substring(nombreOriginal.lastIndexOf(".")) : ".jpg";
                String nuevoNombre = "user_" + idUsuario + "_" + System.currentTimeMillis() + extension;

                File destino = new File(carpetaDestino, nuevoNombre);
                Files.copy(archivoOrigen.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);

                return destino.getPath();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error al guardar la imagen: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        return null;
    }
}