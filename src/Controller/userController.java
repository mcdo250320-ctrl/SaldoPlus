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
import View.FRNRegistro;
import View.FRNTransaccion;
import View.FRNUsuarios;
import View.FrmHistorial;
import View.frmlogin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

public class userController {

    private FRNInicio vistaInicio;
    private FRNRegistro vistaRegistro;
    private FrmHistorial vistaHistorial;
    private FRNTransaccion vistaTransaccion;
    private FRNMain vistaMain;
    private FRNUsuarios vistaUsuarios;
    private FRNAdmin vistaAdmin;

    private boolean isUpdatingTable = false;

    public userController(FRNInicio vistaInicio) {
        this.vistaInicio = vistaInicio;

        this.vistaInicio.btninicio.addActionListener(new IniciarSesionAction());
        this.vistaInicio.btnregistro.addActionListener(new AbrirRegistroAction());
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
                JOptionPane.showMessageDialog(ventanaActual, "Pantalla de Metas en construcción");
            });
        }

        if (btnPerfil != null) {
            btnPerfil.addActionListener(e -> {
                JOptionPane.showMessageDialog(ventanaActual, "Pantalla de Perfil en construcción");
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

        if (vistaMain.btnTransaccion != null) {
            vistaMain.btnTransaccion.addActionListener(e -> {
                vistaMain.dispose();
                abrirPantallaTransaccion();
            });
        }
        if (vistaMain.btnHistorial != null) {
            vistaMain.btnHistorial.addActionListener(e -> {
                vistaMain.dispose();
                abrirPantallaHistorial();
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

    public void abrirPantallaTransaccion() {
        vistaTransaccion = new FRNTransaccion();

        cargarCategoriasTransaccion();
        cargarMetasTransaccion();

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

        // EVento del botón de Generar Reporte
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

        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) vistaHistorial.tablaHistorial.getModel();
        model.setRowCount(0);

        if (datos != null) {
            for (Object[] fila : datos) {
                model.addRow(fila);
            }
        }
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