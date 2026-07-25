package Controller;

import Model.Categoria;
import Model.CategoriaDB;
import Model.Meta;
import Model.MetaDB;
import Model.SesionUsuario;
import Model.Transaccion;
import Model.TransaccionDB;
import Model.User;
import Model.UserDB;
import View.FRNInicio;
import View.FRNRegistro;
import View.FRNTransaccion;
import View.FrmHistorial;
import View.frmlogin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class userController {

    private FRNInicio vistaInicio;
    private FRNRegistro vistaRegistro;
    private FrmHistorial vistaHistorial;
    private FRNTransaccion vistaTransaccion;
    private int idTransaccionSeleccionada = -1;

    public userController(FRNInicio vistaInicio) {
        this.vistaInicio = vistaInicio;

        this.vistaInicio.btninicio.addActionListener(new IniciarSesionAction());
        this.vistaInicio.btnregistro.addActionListener(new AbrirRegistroAction());
    }

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
                    User usuarioLogueado = db.validarLogin(usuario, pass);

                    if (usuarioLogueado != null) {
                        SesionUsuario.setUsuarioActual(usuarioLogueado);

                        JOptionPane.showMessageDialog(vistaLogin,
                                "¡Bienvenido de nuevo, " + usuarioLogueado.getNombre() + " a SaldoPlus!",
                                "Inicio de Sesión Exitoso",
                                JOptionPane.INFORMATION_MESSAGE);

                        abrirPantallaTransaccion();
                        
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

   public void abrirPantallaTransaccion() {
    vistaTransaccion = new FRNTransaccion();

    cargarCategoriasTransaccion();
    cargarMetasTransaccion();
    listarTransaccionesTabla(); // Primero se carga el modelo

    vistaTransaccion.btnguardar.addActionListener(new GuardarTransaccionAction());
    vistaTransaccion.btnactualizar.addActionListener(new ActualizarTransaccionAction());
    vistaTransaccion.btneliminar.addActionListener(new EliminarTransaccionAction());
    vistaTransaccion.btnlimpiar.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            limpiarFormularioTransaccion();
        }
    });

    // Agregar el Listener directamente a la tabla
    vistaTransaccion.tblHistorial.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            seleccionarFilaTabla();
        }
    });

    vistaTransaccion.setVisible(true);
    vistaTransaccion.setLocationRelativeTo(null);
}
    private void cargarCategoriasTransaccion() {
        CategoriaDB catDB = new CategoriaDB();
        List<Categoria> categorias = catDB.obtenerCategorias();

        vistaTransaccion.cmbcategoria.removeAllItems();
        for (Categoria cat : categorias) {
            ((javax.swing.DefaultComboBoxModel) vistaTransaccion.cmbcategoria.getModel()).addElement(cat);
        }
    }

    private void cargarMetasTransaccion() {
        MetaDB metaDB = new MetaDB();
        int idUsuario = SesionUsuario.getUsuarioActual().getId();

        List<Meta> metas = metaDB.obtenerMetasPorUsuario(idUsuario);

        vistaTransaccion.cmbmeta.removeAllItems();
        ((javax.swing.DefaultComboBoxModel) vistaTransaccion.cmbmeta.getModel()).addElement("Sin Meta");

        for (Meta m : metas) {
            ((javax.swing.DefaultComboBoxModel) vistaTransaccion.cmbmeta.getModel()).addElement(m);
        }
    }

public void listarTransaccionesTabla() {
    TransaccionDB tDB = new TransaccionDB();
    int idUsuario = SesionUsuario.getUsuarioActual().getId();
    
    List<Object[]> lista = tDB.obtenerTransaccionesConNombresPorUsuario(idUsuario);

    DefaultTableModel modelo = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        };
    
    };

    modelo.addColumn("ID");
    modelo.addColumn("Monto");
    modelo.addColumn("Tipo");
    modelo.addColumn("Descripción");
    modelo.addColumn("Categoría");
    modelo.addColumn("Meta");

    for (Object[] fila : lista) {
        modelo.addRow(fila);
    }

    vistaTransaccion.tblHistorial.setModel(modelo);
}    private void seleccionarFilaTabla() {
    int fila = vistaTransaccion.tblHistorial.getSelectedRow();
    
    if (fila != -1) {
        idTransaccionSeleccionada = Integer.parseInt(vistaTransaccion.tblHistorial.getValueAt(fila, 0).toString());
        
        vistaTransaccion.txtmonto.setText(vistaTransaccion.tblHistorial.getValueAt(fila, 1).toString());
        
        if (vistaTransaccion.tblHistorial.getValueAt(fila, 2) != null) {
            vistaTransaccion.cmbtipo.setSelectedItem(vistaTransaccion.tblHistorial.getValueAt(fila, 2).toString());
        }
        
        if (vistaTransaccion.tblHistorial.getValueAt(fila, 3) != null) {
            vistaTransaccion.txtdesc.setText(vistaTransaccion.tblHistorial.getValueAt(fila, 3).toString());
        }
    }
}

    class GuardarTransaccionAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (vistaTransaccion.cmbtipo.getSelectedIndex() == 0) {
                    JOptionPane.showMessageDialog(vistaTransaccion, "Seleccione un Tipo (Ingreso/Egreso).");
                    return;
                }

                if (vistaTransaccion.cmbcategoria.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(vistaTransaccion, "Seleccione una Categoría.");
                    return;
                }

                float monto = Float.parseFloat(vistaTransaccion.txtmonto.getText().trim());
                String tipo = vistaTransaccion.cmbtipo.getSelectedItem().toString();
                String descripcion = vistaTransaccion.txtdesc.getText().trim();

                int idCategoria = 0;
                Object itemCat = vistaTransaccion.cmbcategoria.getSelectedItem();
                if (itemCat instanceof Categoria) {
                    idCategoria = ((Categoria) itemCat).getIdCategoria();
                }

                int idMeta = 0;
                Object itemMeta = vistaTransaccion.cmbmeta.getSelectedItem();
                if (itemMeta instanceof Meta) {
                    idMeta = ((Meta) itemMeta).getId();
                }

                int idUsuario = SesionUsuario.getUsuarioActual().getId();

                Transaccion t = new Transaccion();
                t.setMonto(monto);
                t.setTipo(tipo);
                t.setDescripcion(descripcion);
                t.setId_categoria(idCategoria);
                t.setId_meta(idMeta);
                t.setId_usuario(idUsuario);

                TransaccionDB tDB = new TransaccionDB();
                if (tDB.insertarTransaccion(t)) {
                    JOptionPane.showMessageDialog(vistaTransaccion, "Transacción guardada exitosamente.");
                    limpiarFormularioTransaccion();
                    listarTransaccionesTabla();
                } else {
                    JOptionPane.showMessageDialog(vistaTransaccion, "Error al insertar en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(vistaTransaccion, "Ingrese un monto válido.", "Monto Incumplido", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vistaTransaccion, "Ocurrió un error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    class ActualizarTransaccionAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (idTransaccionSeleccionada == -1) {
                JOptionPane.showMessageDialog(vistaTransaccion, "Seleccione un registro de la tabla para actualizar.");
                return;
            }

            try {
                float monto = Float.parseFloat(vistaTransaccion.txtmonto.getText().trim());
                String tipo = vistaTransaccion.cmbtipo.getSelectedItem().toString();
                String descripcion = vistaTransaccion.txtdesc.getText().trim();

                int idCategoria = 0;
                Object itemCat = vistaTransaccion.cmbcategoria.getSelectedItem();
                if (itemCat instanceof Categoria) {
                    idCategoria = ((Categoria) itemCat).getIdCategoria();
                }

                int idMeta = 0;
                Object itemMeta = vistaTransaccion.cmbmeta.getSelectedItem();
                if (itemMeta instanceof Meta) {
                    idMeta = ((Meta) itemMeta).getId();
                }

                Transaccion t = new Transaccion();
                t.setId(idTransaccionSeleccionada);
                t.setMonto(monto);
                t.setTipo(tipo);
                t.setDescripcion(descripcion);
                t.setId_categoria(idCategoria);
                t.setId_meta(idMeta);

                TransaccionDB tDB = new TransaccionDB();
                if (tDB.actualizarTransaccion(t)) {
                    JOptionPane.showMessageDialog(vistaTransaccion, "Transacción actualizada correctamente.");
                    limpiarFormularioTransaccion();
                    listarTransaccionesTabla();
                } else {
                    JOptionPane.showMessageDialog(vistaTransaccion, "Error al actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vistaTransaccion, "Ocurrió un error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    class EliminarTransaccionAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (idTransaccionSeleccionada == -1) {
                JOptionPane.showMessageDialog(vistaTransaccion, "Seleccione un registro de la tabla para eliminar.");
                return;
            }

            int confirmacion = JOptionPane.showConfirmDialog(vistaTransaccion, "¿Está seguro de eliminar esta transacción?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirmacion == JOptionPane.YES_OPTION) {
                TransaccionDB tDB = new TransaccionDB();
                if (tDB.eliminarTransaccion(idTransaccionSeleccionada)) {
                    JOptionPane.showMessageDialog(vistaTransaccion, "Transacción eliminada correctamente.");
                    limpiarFormularioTransaccion();
                    listarTransaccionesTabla();
                } else {
                    JOptionPane.showMessageDialog(vistaTransaccion, "Error al eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void limpiarFormularioTransaccion() {
        idTransaccionSeleccionada = -1;
        vistaTransaccion.txtmonto.setText("");
        vistaTransaccion.txtdesc.setText("");
        vistaTransaccion.cmbtipo.setSelectedIndex(0);
        if (vistaTransaccion.cmbcategoria.getItemCount() > 0) vistaTransaccion.cmbcategoria.setSelectedIndex(0);
        if (vistaTransaccion.cmbmeta.getItemCount() > 0) vistaTransaccion.cmbmeta.setSelectedIndex(0);
        vistaTransaccion.tblHistorial.clearSelection();
    }
}