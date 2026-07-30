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
import java.util.List;
import javax.swing.JOptionPane;

public class userController {

    private FRNInicio vistaInicio;
    private FRNRegistro vistaRegistro;
    private FrmHistorial vistaHistorial;
    private FRNTransaccion vistaTransaccion;

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

        vistaTransaccion.btnguardar.addActionListener(new GuardarTransaccionAction());

        // 2. Escuchar cambios en los componentes para actualizar la Vista Previa en tiempo real

        // Texto de Monto (al escribir)
        vistaTransaccion.txtmonto.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { actualizarVistaPrevia(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { actualizarVistaPrevia(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { actualizarVistaPrevia(); }
        });

        // Texto de Descripción (al escribir)
        vistaTransaccion.txtdesc.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { actualizarVistaPrevia(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { actualizarVistaPrevia(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { actualizarVistaPrevia(); }
        });

        // Combos de Tipo y Categoría (al seleccionar)
        vistaTransaccion.cmbtipo.addActionListener(e -> actualizarVistaPrevia());
        vistaTransaccion.cmbcategoria.addActionListener(e -> actualizarVistaPrevia());

        // Cargar la fecha actual de hoy en la vista previa
        java.time.LocalDate hoy = java.time.LocalDate.now();
        java.time.format.DateTimeFormatter formato = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        vistaTransaccion.lblFechaPreview.setText(hoy.format(formato));

        vistaTransaccion.setVisible(true);
        vistaTransaccion.setLocationRelativeTo(null);
        actualizarResumenMensual();
    }

    // Método que actualiza las etiquetas de la Vista Previa
    private void actualizarVistaPrevia() {
        // 1. Actualizar Monto
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

        // 2. Actualizar Descripción
        String descStr = vistaTransaccion.txtdesc.getText().trim();
        if (descStr.isEmpty()) {
            vistaTransaccion.lblDescPreview.setText("Descripción del ingreso aparecerá aquí...");
        } else {
            vistaTransaccion.lblDescPreview.setText(descStr);
        }

        // 3. Actualizar Tipo
        if (vistaTransaccion.cmbtipo.getSelectedItem() != null) {
            vistaTransaccion.lblTipoPreview.setText(vistaTransaccion.cmbtipo.getSelectedItem().toString());
        } else {
            vistaTransaccion.lblTipoPreview.setText("---");
        }

        // 4. Actualizar Categoría
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
                    actualizarResumenMensual(); // Actualiza los totales en pantalla
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
        if (vistaTransaccion.cmbcategoria.getItemCount() > 0) vistaTransaccion.cmbcategoria.setSelectedIndex(0);
        if (vistaTransaccion.cmbmeta.getItemCount() > 0) vistaTransaccion.cmbmeta.setSelectedIndex(0);
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
            // Negativo: Rojo con signo "-"
            vistaTransaccion.lblBalanceTotal.setForeground(new java.awt.Color(200, 0, 0));
            vistaTransaccion.lblBalanceTotal.setText(String.format("-$ %.2f", Math.abs(balance)));
        } else {
            // Positivo: Verde sin signo "-"
            vistaTransaccion.lblBalanceTotal.setForeground(new java.awt.Color(0, 150, 0));
            vistaTransaccion.lblBalanceTotal.setText(String.format("$ %.2f", balance));
        }
    }
    
private String filtroTipo = "Todos"; 
private String filtroCategoria = "Todas";

public void abrirPantallaHistorial() {
    vistaHistorial = new FrmHistorial();

    java.util.Date hoy = new java.util.Date();
    if (vistaHistorial.dateInicio != null) vistaHistorial.dateInicio.setDate(hoy);
    if (vistaHistorial.dateFin != null) vistaHistorial.dateFin.setDate(hoy);

    vistaHistorial.btnTipoIngreso.addActionListener(e -> {
        filtroTipo = "Ingreso";
        cargarTablaHistorial();
    });
    
    vistaHistorial.btnTipoEgreso.addActionListener(e -> {
        filtroTipo = "Egreso";
        cargarTablaHistorial();
    });

    vistaHistorial.btnRopa.addActionListener(e -> { filtroCategoria = "Ropa"; cargarTablaHistorial(); });
    vistaHistorial.btnServicios.addActionListener(e -> { filtroCategoria = "Servicios"; cargarTablaHistorial(); });
    vistaHistorial.btnTrans.addActionListener(e -> { filtroCategoria = "Transporte"; cargarTablaHistorial(); });
    vistaHistorial.btnOtros.addActionListener(e -> { filtroCategoria = "Otros"; cargarTablaHistorial(); });
    vistaHistorial.btnComida.addActionListener(e -> { filtroCategoria = "Comida"; cargarTablaHistorial(); });
    vistaHistorial.btnEntre.addActionListener(e -> { filtroCategoria = "Entretenimiento"; cargarTablaHistorial(); });

    // 4. Botón de reset/filtrar principal
    vistaHistorial.btnFiltrar.addActionListener(e -> cargarTablaHistorial());

    // Carga inicial
    cargarTablaHistorial();

    vistaHistorial.setVisible(true);
    vistaHistorial.setLocationRelativeTo(null);
}

private void cargarTablaHistorial() {
    int idUsuario = SesionUsuario.getUsuarioActual().getId();

    java.util.Date fInicio = vistaHistorial.dateInicio.getDate();
    java.util.Date fFin = vistaHistorial.dateFin.getDate();

    TransaccionDB tDB = new TransaccionDB();
    List<Object[]> datos = tDB.obtenerTransaccionesAvanzado(idUsuario, fInicio, fFin, filtroTipo, filtroCategoria);

    javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) vistaHistorial.tablaHistorial.getModel();
    model.setRowCount(0);

    for (Object[] fila : datos) {
        model.addRow(fila);
    }
}
}