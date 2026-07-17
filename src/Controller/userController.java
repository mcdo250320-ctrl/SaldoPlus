package Controller;

import Model.User;
import Model.UserDB;
import View.FRNInicio;
import View.FRNRegistro;
import View.frmlogin;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import View.FrmHistorial;

public class userController {
    
    private FRNInicio vistaInicio;
    private FRNRegistro vistaRegistro;
    private FrmHistorial vistaHistorial;
    
    public userController(FRNInicio vistaInicio) {
        this.vistaInicio = vistaInicio;
        
        this.vistaInicio.btminicio.addActionListener(new IniciarSesionAction());
        this.vistaInicio.btmregistro.addActionListener(new AbrirRegistroAction());
    }

    // LISTENER PARA REGISTRO (Abre la ventana de registro)
    class AbrirRegistroAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            
            vistaRegistro = new FRNRegistro();
            
            vistaRegistro.tbnregistro.addActionListener(new RegistrarUsuarioAction());
            
            vistaRegistro.setVisible(true);
            vistaRegistro.setLocationRelativeTo(null);
            vistaInicio.setVisible(false); 
        }
    }

    // LISTENER PARA EL REGISTRO DE USUARIO (Guarda en la BD)
    class RegistrarUsuarioAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
          
            String nombre = vistaRegistro.txtnombre.getText().trim();
            String user = vistaRegistro.txtuser.getText().trim();
            
            String pass = new String(vistaRegistro.txtpass.getPassword()).trim();

            if (nombre.isEmpty() || user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(vistaRegistro, 
                    "Por favor, complete todos los campos obligatorios.", 
                    "Campos Vacíos", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            User nuevoUsuario = new User();
            nuevoUsuario.setNombre(nombre);
            nuevoUsuario.setUsuario(user); 
            nuevoUsuario.setPass(pass);      

            UserDB db = new UserDB();
            
            if (db.Insert(nuevoUsuario)) {
                JOptionPane.showMessageDialog(vistaRegistro, 
                    "¡Usuario registrado con éxito en SaldoPlus!", 
                    "Registro Exitoso", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                vistaRegistro.dispose();
                
                vistaInicio.setVisible(true);
                
            } else {
                JOptionPane.showMessageDialog(vistaRegistro, 
                    "Error al registrar en la base de datos. Revisa la consola.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // LISTENER COMPLETO PARA INICIAR SESIÓN (Se eliminó la versión duplicada)
    class IniciarSesionAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            
            frmlogin vistaLogin = new frmlogin();
            
            vistaLogin.bntiniciar.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ev) {
                    String usuario = vistaLogin.txtuser.getText().trim();
                    String pass = new String(vistaLogin.txtpass.getPassword()).trim();
                    
                    if (usuario.isEmpty() || pass.isEmpty()) {
                        JOptionPane.showMessageDialog(vistaLogin, 
                            "Por favor, ingrese su usuario y contraseña.", 
                            "Campos Vacíos", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    UserDB db = new UserDB();
                    String nombreObtenido = db.validarLogin(usuario, pass);
                    
                    if (nombreObtenido != null) {
                        JOptionPane.showMessageDialog(vistaLogin, 
                            "¡Bienvenido de nuevo, " + nombreObtenido + " a SaldoPlus!", 
                            "Inicio de Sesión Exitoso", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        vistaHistorial = new FrmHistorial();
                        
                        vistaHistorial.setVisible(true);
                        vistaHistorial.setLocationRelativeTo(null);
                        vistaLogin.dispose(); 
                    } else {
                        JOptionPane.showMessageDialog(vistaLogin, 
                            "Usuario o contraseña incorrectos.", 
                            "Error de Autenticación", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            
            vistaLogin.setVisible(true);
            vistaLogin.setLocationRelativeTo(null);
            vistaInicio.setVisible(false);
        }
    }
}