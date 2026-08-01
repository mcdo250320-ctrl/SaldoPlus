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
import View.FRNAdmin;
import View.FRNInicio;
import View.FRNMain;
import View.FRNMeta;
import View.FRNPerfil;
import View.FRNRegistro;
import View.FRNTransaccion;
import View.FRNUsuarios;
import View.FrmHistorial;
import View.frmlogin;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

public class userController {

    private FRNInicio vistaInicio;
    private FRNRegistro vistaRegistro;
    private FrmHistorial vistaHistorial;
    private FRNTransaccion vistaTransaccion;
    private FRNMain vistaMain;
    private FRNPerfil vistaPerfil;
    private FRNUsuarios vistaUsuarios;
    private FRNAdmin vistaAdmin;
    private FRNMeta vistaMeta;

    private Meta metaSeleccionadaActual = null;
    private List<Meta> listaMetasUsuario = new ArrayList<>();
    private boolean isUpdatingTable = false;

    public userController(FRNInicio vistaInicio) {
        this.vistaInicio = vistaInicio;

        this.vistaInicio.btninicio.addActionListener(new IniciarSesionAction());
        this.vistaInicio.btnregistro.addActionListener(new AbrirRegistroAction());
    }

    private void aplicarImagenEscalada(JLabel label, String rutaFoto) {
        if (label == null || rutaFoto == null || rutaFoto.isEmpty()) return;

        File archivo = new File(rutaFoto);
        if (!archivo.exists()) return;

        ImageIcon iconOriginal = new ImageIcon(rutaFoto);
        int ancho = label.getWidth() > 0 ? label.getWidth() : 120;
        int alto = label.getHeight() > 0 ? label.getHeight() : 120;

        Image imgEscalada = iconOriginal.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        label.setIcon(new ImageIcon(imgEscalada));
        label.revalidate();
        label.repaint();
    }

    private void cargarFotoEnVista(JLabel labelAvatar) {
        if (labelAvatar == null) return;
        
        User usuarioActual = SesionUsuario.getUsuarioActual();
        if (usuarioActual != null && usuarioActual.getFotoUrl() != null && !usuarioActual.getFotoUrl().trim().isEmpty()) {
            aplicarImagenEscalada(labelAvatar, usuarioActual.getFotoUrl());
        } else {
            try {
                java.net.URL defaultUrl = getClass().getResource("/Imagenes/default_avatar.png");
                if (defaultUrl != null) {
                    ImageIcon iconOriginal = new ImageIcon(defaultUrl);
                    int ancho = labelAvatar.getWidth() > 0 ? labelAvatar.getWidth() : 120;
                    int alto = labelAvatar.getHeight() > 0 ? labelAvatar.getHeight() : 120;
                    Image imgEscalada = iconOriginal.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
                    labelAvatar.setIcon(new ImageIcon(imgEscalada));
                }
            } catch (Exception e) {
                System.out.println("Error al cargar foto por defecto: " + e.getMessage());
            }
        }
        labelAvatar.repaint();
    }

    private void registrarEventosNavegacion(javax.swing.JButton btnInicio,
            javax.swing.JButton btnTransaccion,
            javax.swing.JButton btnMeta,
            javax.swing.JButton btnHistorial,
            javax.swing.JButton btnPerfil,
            javax.swing.JFrame ventanaActual) {

        if (btnInicio != null) {
            btnInicio.addActionListener(e -> {
                ventanaActual.dispose();
                abrirPantallaMain();
            });
        }

        if (btnTransaccion != null) {
            btnTransaccion.addActionListener(e -> {
                ventanaActual.dispose();
                abrirPantallaTransaccion();
            });
        }

        if (btnHistorial != null) {
            btnHistorial.addActionListener(e -> {
                ventanaActual.dispose();
                abrirPantallaHistorial();
            });
        }

        if (btnMeta != null) {
            btnMeta.addActionListener(e -> {
                ventanaActual.dispose();
                abrirPantallaMeta();
            });
        }

        if (btnPerfil != null) {
            btnPerfil.addActionListener(e -> {
                ventanaActual.dispose();
                abrirPantallaPerfil();
            });
        }
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

            if (vistaLogin.btnAdmin != null) {
                vistaLogin.btnAdmin.addActionListener(ev -> {
                    vistaLogin.dispose();
                    abrirPantallaAdmin();
                });
            }

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

                        vistaLogin.dispose();
                        abrirPantallaMain();
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

    public void abrirPantallaMain() {
        vistaMain = new FRNMain();

        if (SesionUsuario.getUsuarioActual() != null) {
            vistaMain.lblBienvenido.setText("Bienvenido " + SesionUsuario.getUsuarioActual().getNombre());
        }

        cargarFotoEnVista(vistaMain.avatarPerfil);

        if (vistaMain.btnCardTransaccion != null) {
            vistaMain.btnCardTransaccion.addActionListener(e -> {
                vistaMain.dispose();
                abrirPantallaTransaccion();
            });
        }
        if (vistaMain.btnCardHistorial != null) {
            vistaMain.btnCardHistorial.addActionListener(e -> {
                vistaMain.dispose();
                abrirPantallaHistorial();
            });
        }
        if (vistaMain.btnCardMeta != null) {
            vistaMain.btnCardMeta.addActionListener(e -> {
                vistaMain.dispose();
                abrirPantallaMeta();
            });
        }
        if (vistaMain.btnCardPerfil != null) {
            vistaMain.btnCardPerfil.addActionListener(e -> {
                vistaMain.dispose();
                abrirPantallaPerfil();
            });
        }

        registrarEventosNavegacion(
                vistaMain.btnNavInicio,
                vistaMain.btnNavTransaccion,
                vistaMain.btnNavMeta,
                vistaMain.btnNavHistorial,
                vistaMain.btnNavPerfil,
                vistaMain
        );

        if (vistaMain.btnNavInicio != null) {
            vistaMain.btnNavInicio.setEnabled(false);
        }

        vistaMain.setVisible(true);
        vistaMain.setLocationRelativeTo(null);
    }

    public void abrirPantallaMeta() {
        vistaMeta = new FRNMeta();

        cargarFotoEnVista(vistaMeta.avatarPerfil);
        cargarTablaMetas();

        if (vistaMeta.btnGenerarMeta != null) {
            vistaMeta.btnGenerarMeta.addActionListener(e -> guardarNuevaMeta());
        }

        if (vistaMeta.btnAportar != null) {
            vistaMeta.btnAportar.addActionListener(e -> aportarAMetaSeleccionada());
        }

        if (vistaMeta.tablaMetas != null) {
            vistaMeta.tablaMetas.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting()) {
                        int filaSeleccionada = vistaMeta.tablaMetas.getSelectedRow();
                        if (filaSeleccionada != -1 && filaSeleccionada < listaMetasUsuario.size()) {
                            metaSeleccionadaActual = listaMetasUsuario.get(filaSeleccionada);
                            actualizarTarjetaProgresoMeta(metaSeleccionadaActual);
                        }
                    }
                }
            });
        }

        registrarEventosNavegacion(
                vistaMeta.btnNavInicio,
                vistaMeta.btnNavTransaccion,
                vistaMeta.btnNavMeta,
                vistaMeta.btnNavHistorial,
                vistaMeta.btnNavPerfil,
                vistaMeta
        );

        if (vistaMeta.btnNavMeta != null) {
            vistaMeta.btnNavMeta.setEnabled(false);
        }

        vistaMeta.setVisible(true);
        vistaMeta.setLocationRelativeTo(null);
    }

    private void cargarTablaMetas() {
        if (SesionUsuario.getUsuarioActual() == null) return;

        isUpdatingTable = true;
        int idUsuario = SesionUsuario.getUsuarioActual().getId();
        MetaDB db = new MetaDB();
        listaMetasUsuario = db.obtenerMetasPorUsuario(idUsuario);

        DefaultTableModel model = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Descripción", "Monto Objetivo", "Fecha Límite"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };

        if (listaMetasUsuario != null) {
            for (Meta m : listaMetasUsuario) {
                model.addRow(new Object[]{
                    m.getDescrip(),
                    String.format("$ %.2f", m.getMonto()),
                    m.getFecha() != null ? m.getFecha() : "Sin fecha"
                });
            }
        }

        if (vistaMeta.tablaMetas != null) {
            vistaMeta.tablaMetas.setModel(model);

            model.addTableModelListener(new TableModelListener() {
                @Override
                public void tableChanged(TableModelEvent e) {
                    if (isUpdatingTable) return;

                    if (e.getType() == TableModelEvent.UPDATE) {
                        int fila = e.getFirstRow();
                        int columna = e.getColumn();

                        if (fila >= 0 && fila < listaMetasUsuario.size()) {
                            Meta meta = listaMetasUsuario.get(fila);
                            Object nuevoValor = model.getValueAt(fila, columna);

                            if (nuevoValor == null) return;

                            try {
                                if (columna == 0) {
                                    meta.setDescrip(nuevoValor.toString().trim());
                                } else if (columna == 1) {
                                    String montoClean = nuevoValor.toString().replace("$", "").replace(",", "").trim();
                                    meta.setMonto(Float.parseFloat(montoClean));
                                } else if (columna == 2) {
                                    meta.setFecha(nuevoValor.toString().trim());
                                }

                                MetaDB mdb = new MetaDB();
                                if (mdb.actualizarMeta(meta)) {
                                    actualizarTarjetaProgresoMeta(meta);
                                } else {
                                    JOptionPane.showMessageDialog(vistaMeta, "Error al actualizar la meta en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                                    cargarTablaMetas();
                                }
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(vistaMeta, "Formato de dato ingresado no válido.", "Error", JOptionPane.ERROR_MESSAGE);
                                cargarTablaMetas();
                            }
                        }
                    }
                }
            });

            if (listaMetasUsuario != null && !listaMetasUsuario.isEmpty()) {
                vistaMeta.tablaMetas.setRowSelectionInterval(0, 0);
                metaSeleccionadaActual = listaMetasUsuario.get(0);
                actualizarTarjetaProgresoMeta(metaSeleccionadaActual);
            } else {
                limpiarTarjetaProgresoMeta();
            }
        }
        isUpdatingTable = false;
    }

    private void guardarNuevaMeta() {
        if (SesionUsuario.getUsuarioActual() == null) return;

        try {
            String montoStr = vistaMeta.txtMonto.getText().trim();
            String descStr = vistaMeta.txtDescripcion.getText().trim();
            java.util.Date fechaUtil = vistaMeta.dateChooserFecha.getDate();

            if (montoStr.isEmpty() || descStr.isEmpty() || fechaUtil == null) {
                JOptionPane.showMessageDialog(vistaMeta, "Por favor llene el monto, la descripción y seleccione la fecha objetivo.", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            float montoObj = Float.parseFloat(montoStr);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fechaFormatted = sdf.format(fechaUtil);

            Meta nuevaMeta = new Meta();
            nuevaMeta.setMonto(montoObj);
            nuevaMeta.setFecha(fechaFormatted);
            nuevaMeta.setDescrip(descStr);
            nuevaMeta.setId_usuario(SesionUsuario.getUsuarioActual().getId());

            MetaDB db = new MetaDB();
            if (db.insertarMeta(nuevaMeta)) {
                JOptionPane.showMessageDialog(vistaMeta, "¡Meta de ahorro registrada exitosamente!");
                vistaMeta.txtMonto.setText("");
                vistaMeta.txtDescripcion.setText("");
                vistaMeta.dateChooserFecha.setDate(null);
                cargarTablaMetas();
            } else {
                JOptionPane.showMessageDialog(vistaMeta, "Error al guardar la meta en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vistaMeta, "Ingrese un monto numérico válido.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void actualizarTarjetaProgresoMeta(Meta meta) {
        if (meta == null || vistaMeta == null) return;

        TransaccionDB tDB = new TransaccionDB();
        float ahorrado = tDB.obtenerTotalAhorradoPorMeta(meta.getId());
        float objetivo = meta.getMonto();

        int porcentaje = 0;
        if (objetivo > 0) {
            porcentaje = (int) ((ahorrado / objetivo) * 100);
            if (porcentaje > 100) porcentaje = 100;
        }

        if (vistaMeta.lblNombreMeta != null) {
            vistaMeta.lblNombreMeta.setText(meta.getDescrip());
        }

        if (vistaMeta.lblAhorradoObjetivo != null) {
            vistaMeta.lblAhorradoObjetivo.setText(String.format("$ %.2f / $ %.2f", ahorrado, objetivo));
        }

        if (vistaMeta.barraProgreso != null) {
            vistaMeta.barraProgreso.setOpaque(true);
            vistaMeta.barraProgreso.setStringPainted(true);
            vistaMeta.barraProgreso.setValue(porcentaje);
            vistaMeta.barraProgreso.setString(porcentaje + "% Completado");

            if (porcentaje < 40) {
                vistaMeta.barraProgreso.setForeground(new java.awt.Color(204, 36, 36));
            } else if (porcentaje <= 80) {
                vistaMeta.barraProgreso.setForeground(new java.awt.Color(230, 160, 20));
            } else {
                vistaMeta.barraProgreso.setForeground(new java.awt.Color(34, 153, 84));
            }
        }
    }

    private void limpiarTarjetaProgresoMeta() {
        if (vistaMeta == null) return;

        if (vistaMeta.lblNombreMeta != null) {
            vistaMeta.lblNombreMeta.setText("Sin metas registradas");
        }
        if (vistaMeta.lblAhorradoObjetivo != null) {
            vistaMeta.lblAhorradoObjetivo.setText("$ 0.00 / $ 0.00");
        }
        if (vistaMeta.barraProgreso != null) {
            vistaMeta.barraProgreso.setValue(0);
            vistaMeta.barraProgreso.setStringPainted(true);
            vistaMeta.barraProgreso.setString("0% Completado");
            vistaMeta.barraProgreso.setForeground(new java.awt.Color(204, 36, 36));
        }
    }

    private void aportarAMetaSeleccionada() {
        if (metaSeleccionadaActual == null) {
            JOptionPane.showMessageDialog(vistaMeta, "Seleccione una meta de la tabla para aportar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        vistaMeta.dispose();
        abrirPantallaTransaccion(metaSeleccionadaActual);
    }

    public void abrirPantallaPerfil() {
        vistaPerfil = new FRNPerfil();
        User usuarioActual = SesionUsuario.getUsuarioActual();

        cargarFotoEnVista(vistaPerfil.avatarPerfil);

        if (usuarioActual != null) {
            vistaPerfil.txtNombre.setText(usuarioActual.getNombre());
            vistaPerfil.txtUsuario.setText(usuarioActual.getUsuario());
            vistaPerfil.txtTelefono.setText(usuarioActual.getTelefono() != null ? usuarioActual.getTelefono() : "");
        }

        if (vistaPerfil.btnCambiarFoto != null) {
            vistaPerfil.btnCambiarFoto.addActionListener(e -> {
                if (usuarioActual == null) return;

                String rutaGuardada = Utils.GestionFotoService.seleccionarYGuardarFoto(usuarioActual.getId());

                if (rutaGuardada != null) {
                    UserDB db = new UserDB();
                    if (db.actualizarFotoUsuario(usuarioActual.getId(), rutaGuardada)) {
                        usuarioActual.setFotoUrl(rutaGuardada);
                        cargarFotoEnVista(vistaPerfil.avatarPerfil);
                        JOptionPane.showMessageDialog(vistaPerfil, "¡Foto de perfil actualizada correctamente!");
                    } else {
                        JOptionPane.showMessageDialog(vistaPerfil, "Error al guardar la foto en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }

        if (vistaPerfil.btnActualizar != null) {
            vistaPerfil.btnActualizar.addActionListener(e -> {
                if (usuarioActual == null) return;

                String nuevoNombre = vistaPerfil.txtNombre.getText().trim();
                String nuevoUsuario = vistaPerfil.txtUsuario.getText().trim();
                String nuevoTelefono = vistaPerfil.txtTelefono.getText().trim();

                if (nuevoNombre.isEmpty() || nuevoUsuario.isEmpty()) {
                    JOptionPane.showMessageDialog(vistaPerfil, "Nombre y Usuario son obligatorios.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                usuarioActual.setNombre(nuevoNombre);
                usuarioActual.setUsuario(nuevoUsuario);
                usuarioActual.setTelefono(nuevoTelefono);

                UserDB db = new UserDB();
                if (db.actualizarUsuario(usuarioActual)) {
                    JOptionPane.showMessageDialog(vistaPerfil, "¡Datos personales actualizados correctamente!");
                } else {
                    JOptionPane.showMessageDialog(vistaPerfil, "Error al actualizar la información en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        }

        if (vistaPerfil.btnGuardar != null) {
            vistaPerfil.btnGuardar.addActionListener(e -> {
                if (usuarioActual == null) return;

                String passActual = new String(vistaPerfil.txtPassActual.getPassword()).trim();
                String passNueva = new String(vistaPerfil.txtPassNueva.getPassword()).trim();

                if (passActual.isEmpty() || passNueva.isEmpty()) {
                    JOptionPane.showMessageDialog(vistaPerfil, "Ingrese su contraseña actual y la nueva contraseña.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (!passActual.equals(usuarioActual.getPass())) {
                    JOptionPane.showMessageDialog(vistaPerfil, "La contraseña actual es incorrecta.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                usuarioActual.setPass(passNueva);
                UserDB db = new UserDB();
                if (db.actualizarUsuario(usuarioActual)) {
                    JOptionPane.showMessageDialog(vistaPerfil, "¡Contraseña modificada con éxito!");
                    vistaPerfil.txtPassActual.setText("");
                    vistaPerfil.txtPassNueva.setText("");
                } else {
                    JOptionPane.showMessageDialog(vistaPerfil, "Error al actualizar la contraseña.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        }

        if (vistaPerfil.btnEliminarCuenta != null) {
            vistaPerfil.btnEliminarCuenta.addActionListener(e -> {
                if (usuarioActual == null) return;

                int confirm = JOptionPane.showConfirmDialog(
                        vistaPerfil,
                        "¿Está seguro de que desea eliminar su cuenta permanentemente?\nEsta acción borra su cuenta y todos sus registros.",
                        "Confirmar Eliminación de Cuenta",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    UserDB udb = new UserDB();
                    if (udb.eliminarUsuario(usuarioActual.getId())) {
                        JOptionPane.showMessageDialog(vistaPerfil, "Su cuenta ha sido eliminada correctamente.");
                        SesionUsuario.cerrarSesion();
                        vistaPerfil.dispose();
                        
                        if (vistaInicio == null) {
                            vistaInicio = new FRNInicio();
                        }
                        vistaInicio.setVisible(true);
                        vistaInicio.setLocationRelativeTo(null);
                    } else {
                        JOptionPane.showMessageDialog(vistaPerfil, "Error al intentar eliminar la cuenta.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }

        if (vistaPerfil.btnCancelar != null) {
            vistaPerfil.btnCancelar.addActionListener(e -> {
                if (usuarioActual != null) {
                    vistaPerfil.txtNombre.setText(usuarioActual.getNombre());
                    vistaPerfil.txtUsuario.setText(usuarioActual.getUsuario());
                    vistaPerfil.txtTelefono.setText(usuarioActual.getTelefono() != null ? usuarioActual.getTelefono() : "");
                }
            });
        }

        if (vistaPerfil.btnCerrarSesion != null) {
            vistaPerfil.btnCerrarSesion.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(
                        vistaPerfil,
                        "¿Está seguro de que desea cerrar sesión?",
                        "Cerrar Sesión",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    SesionUsuario.cerrarSesion();
                    vistaPerfil.dispose();
                    if (vistaInicio == null) {
                        vistaInicio = new FRNInicio();
                    }
                    vistaInicio.setVisible(true);
                    vistaInicio.setLocationRelativeTo(null);
                }
            });
        }

        registrarEventosNavegacion(
                vistaPerfil.btnNavInicio,
                vistaPerfil.btnNavTransaccion,
                vistaPerfil.btnNavMeta,
                vistaPerfil.btnNavHistorial,
                vistaPerfil.btnNavPerfil,
                vistaPerfil
        );

        if (vistaPerfil.btnNavPerfil != null) {
            vistaPerfil.btnNavPerfil.setEnabled(false);
        }

        vistaPerfil.setVisible(true);
        vistaPerfil.setLocationRelativeTo(null);
    }

    public void abrirPantallaTransaccion() {
        abrirPantallaTransaccion(null);
    }

    public void abrirPantallaTransaccion(Meta metaPreseleccionada) {
        vistaTransaccion = new FRNTransaccion();

        cargarFotoEnVista(vistaTransaccion.avatarPerfil);
        cargarCategoriasTransaccion();
        cargarMetasTransaccion();

        if (metaPreseleccionada != null && vistaTransaccion.cmbmeta != null) {
            for (int i = 0; i < vistaTransaccion.cmbmeta.getItemCount(); i++) {
                Object item = vistaTransaccion.cmbmeta.getItemAt(i);
                if (item instanceof Meta) {
                    Meta m = (Meta) item;
                    if (m.getId() == metaPreseleccionada.getId()) {
                        vistaTransaccion.cmbmeta.setSelectedIndex(i);
                        break;
                    }
                }
            }
            if (vistaTransaccion.cmbtipo != null) {
                vistaTransaccion.cmbtipo.setSelectedItem("Egreso");
            }
        }

        vistaTransaccion.btnguardar.addActionListener(new GuardarTransaccionAction());

        vistaTransaccion.txtmonto.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                actualizarVistaPrevia();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                actualizarVistaPrevia();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                actualizarVistaPrevia();
            }
        });

        vistaTransaccion.txtdesc.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                actualizarVistaPrevia();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                actualizarVistaPrevia();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                actualizarVistaPrevia();
            }
        });

        vistaTransaccion.cmbtipo.addActionListener(e -> actualizarVistaPrevia());
        vistaTransaccion.cmbcategoria.addActionListener(e -> actualizarVistaPrevia());

        java.time.LocalDate hoy = java.time.LocalDate.now();
        java.time.format.DateTimeFormatter formato = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        vistaTransaccion.lblFechaPreview.setText(hoy.format(formato));

        registrarEventosNavegacion(
                vistaTransaccion.btnNavInicio,
                vistaTransaccion.btnNavTransaccion,
                vistaTransaccion.btnNavMeta,
                vistaTransaccion.btnNavHistorial,
                vistaTransaccion.btnNavPerfil,
                vistaTransaccion
        );
        if (vistaTransaccion.btnNavTransaccion != null) {
            vistaTransaccion.btnNavTransaccion.setEnabled(false);
        }

        vistaTransaccion.setVisible(true);
        vistaTransaccion.setLocationRelativeTo(null);
        actualizarResumenMensual();
    }

    private void actualizarVistaPrevia() {
        String montoStr = vistaTransaccion.txtmonto.getText().trim();
        if (montoStr.isEmpty()) {
            vistaTransaccion.lblMontoPreview.setText("$ 0.00");
        } else {
            try {
                double montoVal = Double.parseDouble(montoStr);
                vistaTransaccion.lblMontoPreview.setText(String.format("$ %.2f", montoVal));
            } catch (NumberFormatException e) {
                vistaTransaccion.lblMontoPreview.setText("$ 0.00");
            }
        }

        String descStr = vistaTransaccion.txtdesc.getText().trim();
        if (descStr.isEmpty()) {
            vistaTransaccion.lblDescPreview.setText("Descripción aparecerá aquí...");
        } else {
            vistaTransaccion.lblDescPreview.setText(descStr);
        }

        if (vistaTransaccion.cmbtipo.getSelectedItem() != null) {
            vistaTransaccion.lblTipoPreview.setText(vistaTransaccion.cmbtipo.getSelectedItem().toString());
        } else {
            vistaTransaccion.lblTipoPreview.setText("---");
        }

        if (vistaTransaccion.cmbcategoria.getSelectedItem() != null) {
            vistaTransaccion.lblCategoriaPreview.setText(vistaTransaccion.cmbcategoria.getSelectedItem().toString());
        } else {
            vistaTransaccion.lblCategoriaPreview.setText("---");
        }
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
                    actualizarResumenMensual();
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

    private void limpiarFormularioTransaccion() {
        vistaTransaccion.txtmonto.setText("");
        vistaTransaccion.txtdesc.setText("");
        vistaTransaccion.cmbtipo.setSelectedIndex(0);
        if (vistaTransaccion.cmbcategoria.getItemCount() > 0) {
            vistaTransaccion.cmbcategoria.setSelectedIndex(0);
        }
        if (vistaTransaccion.cmbmeta.getItemCount() > 0) {
            vistaTransaccion.cmbmeta.setSelectedIndex(0);
        }
    }

    private void actualizarResumenMensual() {
        int idUsuario = SesionUsuario.getUsuarioActual().getId();
        TransaccionDB tDB = new TransaccionDB();

        float[] totales = tDB.obtenerTotalesPorUsuario(idUsuario);

        float ingresos = totales[0];
        float egresos = totales[1];
        float balance = totales[2];

        vistaTransaccion.lblIngresosTotal.setText(String.format("$ %.2f", ingresos));
        vistaTransaccion.lblEgresosTotal.setText(String.format("$ %.2f", egresos));

        if (balance < 0) {
            vistaTransaccion.lblBalanceTotal.setForeground(new java.awt.Color(200, 0, 0));
            vistaTransaccion.lblBalanceTotal.setText(String.format("-$ %.2f", Math.abs(balance)));
        } else {
            vistaTransaccion.lblBalanceTotal.setForeground(new java.awt.Color(0, 150, 0));
            vistaTransaccion.lblBalanceTotal.setText(String.format("$ %.2f", balance));
        }
    }

    public void abrirPantallaHistorial() {
        vistaHistorial = new FrmHistorial();

        cargarFotoEnVista(vistaHistorial.avatarPerfil);

        java.util.Calendar cal = java.util.Calendar.getInstance();
        java.util.Date hoy = cal.getTime();
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        java.util.Date inicioMes = cal.getTime();

        if (vistaHistorial.dateInicio != null) {
            vistaHistorial.dateInicio.setDate(inicioMes);
        }
        if (vistaHistorial.dateFin != null) {
            vistaHistorial.dateFin.setDate(hoy);
        }

        vistaHistorial.btnFiltrar.addActionListener(e -> cargarTablaHistorial());

        if (vistaHistorial.btnReporte != null) {
            vistaHistorial.btnReporte.addActionListener(e -> generarReporte());
        }

        registrarEventosNavegacion(
                vistaHistorial.btnNavInicio,
                vistaHistorial.btnNavTransaccion,
                vistaHistorial.btnNavMeta,
                vistaHistorial.btnNavHistorial,
                vistaHistorial.btnNavPerfil,
                vistaHistorial
        );
        if (vistaHistorial.btnNavHistorial != null) {
            vistaHistorial.btnNavHistorial.setEnabled(false);
        }

        cargarTablaHistorial();

        vistaHistorial.setVisible(true);
        vistaHistorial.setLocationRelativeTo(null);
    }

    private void generarReporte() {
        if (vistaHistorial.tablaHistorial.getRowCount() == 0) {
            JOptionPane.showMessageDialog(vistaHistorial, "No hay datos cargados en la tabla para exportar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] opciones = {"Excel (.xlsx)", "PDF (.pdf)"};
        int seleccion = JOptionPane.showOptionDialog(
                vistaHistorial,
                "Seleccione el formato para exportar el reporte:",
                "Generar Reporte de Historial",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );

        if (seleccion == -1) return;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte");

        if (seleccion == 0) {
            fileChooser.setSelectedFile(new File("Reporte_Historial.xlsx"));
        } else {
            fileChooser.setSelectedFile(new File("Reporte_Historial.pdf"));
        }

        int userSelection = fileChooser.showSaveDialog(vistaHistorial);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File archivoGuardar = fileChooser.getSelectedFile();
            boolean exito;

            if (seleccion == 0) {
                exito = Utils.ReporteExporter.exportarExcel(vistaHistorial.tablaHistorial, archivoGuardar);
            } else {
                exito = Utils.ReporteExporter.exportarPDF(vistaHistorial.tablaHistorial, archivoGuardar);
            }

            if (exito) {
                JOptionPane.showMessageDialog(vistaHistorial, "¡Reporte exportado con éxito!\n" + archivoGuardar.getAbsolutePath());
            } else {
                JOptionPane.showMessageDialog(vistaHistorial, "Ocurrió un error al intentar generar el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cargarTablaHistorial() {
        if (SesionUsuario.getUsuarioActual() == null) {
            return;
        }

        int idUsuario = SesionUsuario.getUsuarioActual().getId();

        java.util.Date fInicio = (vistaHistorial.dateInicio != null) ? vistaHistorial.dateInicio.getDate() : null;
        java.util.Date fFin = (vistaHistorial.dateFin != null) ? vistaHistorial.dateFin.getDate() : null;

        if (fFin != null) {
            java.util.Calendar calFin = java.util.Calendar.getInstance();
            calFin.setTime(fFin);
            calFin.set(java.util.Calendar.HOUR_OF_DAY, 23);
            calFin.set(java.util.Calendar.MINUTE, 59);
            calFin.set(java.util.Calendar.SECOND, 59);
            fFin = calFin.getTime();
        }

        boolean ing = vistaHistorial.btnTipoIngreso.isSelected();
        boolean egr = vistaHistorial.btnTipoEgreso.isSelected();

        String filtroTipo = "Todos";
        if (ing && !egr) {
            filtroTipo = "Ingreso";
        } else if (egr && !ing) {
            filtroTipo = "Egreso";
        }

        List<String> seleccionadas = new ArrayList<>();
        if (vistaHistorial.btnRopa.isSelected()) {
            seleccionadas.add("Ropa");
        }
        if (vistaHistorial.btnServicios.isSelected()) {
            seleccionadas.add("Servicios");
        }
        if (vistaHistorial.btnTrans.isSelected()) {
            seleccionadas.add("Transporte");
        }
        if (vistaHistorial.btnOtros.isSelected()) {
            seleccionadas.add("Otros");
        }
        if (vistaHistorial.btnComida.isSelected()) {
            seleccionadas.add("Comida");
        }
        if (vistaHistorial.btnEntre.isSelected()) {
            seleccionadas.add("Entretenimiento");
        }

        String filtroCategoria = "Todas";
        if (!seleccionadas.isEmpty()) {
            filtroCategoria = String.join(",", seleccionadas);
        }

        TransaccionDB tDB = new TransaccionDB();
        List<Object[]> datos = tDB.obtenerTransaccionesAvanzado(idUsuario, fInicio, fFin, filtroTipo, filtroCategoria);

        boolean tieneMetaAsignada = false;
        if (datos != null) {
            for (Object[] fila : datos) {
                if (fila.length > 4 && fila[4] != null) {
                    String metaVal = fila[4].toString().trim();
                    if (!metaVal.isEmpty() && !metaVal.equalsIgnoreCase("Sin Meta") && !metaVal.equalsIgnoreCase("N/A")) {
                        tieneMetaAsignada = true;
                        break;
                    }
                }
            }
        }

        String[] columnas = tieneMetaAsignada 
                ? new String[]{"Fecha", "Tipo", "Categoría", "Monto", "Descripción", "Meta de Ahorro"}
                : new String[]{"Fecha", "Tipo", "Categoría", "Monto", "Descripción"};

        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(new Object[][]{}, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        if (datos != null) {
            for (Object[] fila : datos) {
                if (tieneMetaAsignada) {
                    Object metaDesc = (fila.length > 4 && fila[4] != null && !fila[4].toString().trim().isEmpty()) ? fila[4] : "N/A";
                    model.addRow(new Object[]{fila[0], fila[1], fila[2], fila[3], fila.length > 5 ? fila[5] : "", metaDesc});
                } else {
                    model.addRow(new Object[]{fila[0], fila[1], fila[2], fila[3], fila.length > 5 ? fila[5] : (fila.length > 4 ? fila[4] : "")});
                }
            }
        }

        vistaHistorial.tablaHistorial.setModel(model);
    }

    public void abrirPantallaAdmin() {
        vistaAdmin = new FRNAdmin();

        vistaAdmin.bntiniciar.addActionListener(e -> {
            String user = vistaAdmin.txtuser.getText().trim();
            String pass = new String(vistaAdmin.txtpass.getPassword()).trim();

            if (user.equals("admin") && pass.equals("admin123")) {
                JOptionPane.showMessageDialog(vistaAdmin, "Acceso concedido como Administrador");
                vistaAdmin.dispose();
                abrirPantallaUsuarios();
            } else {
                JOptionPane.showMessageDialog(vistaAdmin, "Usuario o contraseña de administrador incorrectos", "Error de Acceso", JOptionPane.ERROR_MESSAGE);
            }
        });

        if (vistaAdmin.btnSalir != null) {
            vistaAdmin.btnSalir.addActionListener(e -> {
                vistaAdmin.dispose();
                vistaInicio.setVisible(true);
            });
        }

        vistaAdmin.setVisible(true);
        vistaAdmin.setLocationRelativeTo(null);
    }

    public void abrirPantallaUsuarios() {
        vistaUsuarios = new FRNUsuarios();

        cargarTablaUsuarios();

        vistaUsuarios.btnEliminar.addActionListener(e -> eliminarUsuarioSeleccionado());

        if (vistaUsuarios.btnSalir != null) {
            vistaUsuarios.btnSalir.addActionListener(e -> {
                vistaUsuarios.dispose();
                vistaInicio.setVisible(true);
            });
        }

        vistaUsuarios.setVisible(true);
        vistaUsuarios.setLocationRelativeTo(null);
    }

    private void cargarTablaUsuarios() {
        isUpdatingTable = true;

        UserDB db = new UserDB();
        List<User> lista = db.consultarUsuario();

        DefaultTableModel model = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Nombre", "Teléfono", "Usuario", "Contraseña"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };

        for (User u : lista) {
            model.addRow(new Object[]{
                u.getId(),
                u.getNombre() == null ? "" : u.getNombre(),
                u.getTelefono() == null ? "" : u.getTelefono(),
                u.getUsuario() == null ? "" : u.getUsuario(),
                u.getPass() == null ? "" : u.getPass()
            });
        }

        vistaUsuarios.tablaUsuarios.setModel(model);

        model.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (isUpdatingTable) {
                    return;
                }

                if (e.getType() == TableModelEvent.UPDATE) {
                    int fila = e.getFirstRow();

                    try {
                        Object idObj = model.getValueAt(fila, 0);
                        if (idObj == null || Integer.parseInt(idObj.toString()) == 0) {
                            JOptionPane.showMessageDialog(vistaUsuarios, "El ID del usuario es inválido (0). Verifica User.java", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        int id = Integer.parseInt(idObj.toString());
                        String nombre = model.getValueAt(fila, 1) != null ? model.getValueAt(fila, 1).toString() : "";
                        String telefono = model.getValueAt(fila, 2) != null ? model.getValueAt(fila, 2).toString() : "";
                        String usuario = model.getValueAt(fila, 3) != null ? model.getValueAt(fila, 3).toString() : "";
                        String pass = model.getValueAt(fila, 4) != null ? model.getValueAt(fila, 4).toString() : "";

                        User userModificado = new User(id, nombre, usuario, pass, telefono);

                        UserDB udb = new UserDB();
                        if (udb.actualizarUsuario(userModificado)) {
                            JOptionPane.showMessageDialog(vistaUsuarios, "Usuario actualizado correctamente.");
                        } else {
                            JOptionPane.showMessageDialog(vistaUsuarios, "Error al actualizar el usuario en la BD.", "Error", JOptionPane.ERROR_MESSAGE);
                            cargarTablaUsuarios();
                        }
                    } catch (Exception ex) {
                        System.out.println("Error procesando actualización: " + ex.getMessage());
                    }
                }
            }
        });

        isUpdatingTable = false;
    }

    private void eliminarUsuarioSeleccionado() {
        int fila = vistaUsuarios.tablaUsuarios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vistaUsuarios, "Seleccione un usuario de la tabla para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idUsuario = Integer.parseInt(vistaUsuarios.tablaUsuarios.getValueAt(fila, 0).toString());
        String nombre = vistaUsuarios.tablaUsuarios.getValueAt(fila, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(vistaUsuarios,
                "¿Está seguro de eliminar al usuario " + nombre + "?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            UserDB udb = new UserDB();
            if (udb.eliminarUsuario(idUsuario)) {
                JOptionPane.showMessageDialog(vistaUsuarios, "Usuario eliminado correctamente.");
                cargarTablaUsuarios();
            } else {
                JOptionPane.showMessageDialog(vistaUsuarios, "Error al eliminar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}