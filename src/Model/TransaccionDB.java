package Model;

import Conection.ConectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class TransaccionDB {

    public boolean insertarTransaccion(Transaccion t) {
        String sql = "INSERT INTO Transaccion (monto, tipo, fecha, descripcion, id_usuario, id_meta, id_categoria) "
                   + "VALUES (?, ?, CURRENT_DATE, ?, ?, ?, ?)";

        try (Connection con = ConectionDB.conexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setFloat(1, t.getMonto());
            ps.setString(2, t.getTipo());
            ps.setString(3, t.getDescripcion());
            ps.setInt(4, t.getId_usuario());

            if (t.getId_meta() > 0) {
                ps.setInt(5, t.getId_meta());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            ps.setInt(6, t.getId_categoria());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar transacción: " + e.getMessage());
            return false;
        }
    }

   public List<Object[]> obtenerTransaccionesConNombresPorUsuario(int idUsuario) {
    List<Object[]> lista = new ArrayList<>();
    String sql = "SELECT t.id_transaccion, t.monto, t.tipo, t.descripcion, "
               + "COALESCE(c.nombre, 'Sin Categoria') AS categoria, "
               + "COALESCE(m.descripcion, 'Sin Meta') AS meta "
               + "FROM transaccion t "
               + "LEFT JOIN categoria c ON t.id_categoria = c.id_categoria "
               + "LEFT JOIN meta m ON t.id_meta = m.id_meta "
               + "WHERE t.id_usuario = ?";

    try (Connection con = ConectionDB.conexion();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, idUsuario);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Object[] fila = new Object[6];
                fila[0] = rs.getInt("id_transaccion");
                fila[1] = rs.getFloat("monto");
                fila[2] = rs.getString("tipo");
                fila[3] = rs.getString("descripcion");
                fila[4] = rs.getString("categoria");
                fila[5] = rs.getString("meta");
                
                lista.add(fila);
            }
        }

    } catch (SQLException e) {
        System.err.println("Error al consultar transacciones con nombres: " + e.getMessage());
    }

    return lista;
}
    
    public boolean actualizarTransaccion(Transaccion t) {
        String sql = "UPDATE Transaccion SET monto = ?, tipo = ?, descripcion = ?, id_categoria = ?, id_meta = ? "
                   + "WHERE id_transaccion = ?";

        try (Connection con = ConectionDB.conexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setFloat(1, t.getMonto());
            ps.setString(2, t.getTipo());
            ps.setString(3, t.getDescripcion());
            ps.setInt(4, t.getId_categoria());

            if (t.getId_meta() > 0) {
                ps.setInt(5, t.getId_meta());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            ps.setInt(6, t.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar transacción: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarTransaccion(int idTransaccion) {
        String sql = "DELETE FROM Transaccion WHERE id_transaccion = ?";

        try (Connection con = ConectionDB.conexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idTransaccion);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar transacción: " + e.getMessage());
            return false;
        }
    }
    
    public float[] obtenerTotalesPorUsuario(int idUsuario) {
        float totalIngresos = 0;
        float totalEgresos = 0;

        String sql = "SELECT tipo, SUM(monto) AS total FROM transaccion WHERE id_usuario = ? GROUP BY tipo";

        try (Connection con = ConectionDB.conexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String tipo = rs.getString("tipo");
                    if (tipo != null) {
                        tipo = tipo.trim(); // Eliminar espacios accidentales
                    }

                    float total = rs.getFloat("total");

                    // Evaluamos variaciones comunes por si acaso
                    if ("Ingreso".equalsIgnoreCase(tipo) || "Ingresos".equalsIgnoreCase(tipo)) {
                        totalIngresos = total;
                    } else if ("Egreso".equalsIgnoreCase(tipo) || "Egresos".equalsIgnoreCase(tipo) || "Gasto".equalsIgnoreCase(tipo)) {
                        totalEgresos = total;
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener totales: " + e.getMessage());
        }

        // Balance real: Ingresos - Egresos
        float balance = totalIngresos - totalEgresos;

        return new float[]{totalIngresos, totalEgresos, balance};
    }
    
    public List<Object[]> obtenerTransaccionesAvanzado(int idUsuario, java.util.Date fInicio, java.util.Date fFin, String filtroTipo, String filtroCategoria) {
    List<Object[]> lista = new ArrayList<>();
    
    // Consulta base
    StringBuilder sql = new StringBuilder();
    sql.append("SELECT t.fecha, t.tipo, c.nombre, t.monto ");
    sql.append("FROM transaccion t ");
    sql.append("JOIN categoria c ON t.id_categoria = c.id_categoria ");
    sql.append("WHERE t.id_usuario = ? ");

    // 1. Filtro por Rango de Fechas
    if (fInicio != null && fFin != null) {
        sql.append("AND t.fecha BETWEEN ? AND ? ");
    }

    // 2. Filtro por Tipo (Ingreso / Egreso)
    if (!filtroTipo.equalsIgnoreCase("Todos")) {
        sql.append("AND t.tipo = ? ");
    }

    // 3. Filtro Múltiple por Categorías usando IN (...)
    String[] categoriasArray = null;
    if (!filtroCategoria.equalsIgnoreCase("Todas") && !filtroCategoria.trim().isEmpty()) {
        categoriasArray = filtroCategoria.split(",");
        sql.append("AND c.nombre IN (");
        for (int i = 0; i < categoriasArray.length; i++) {
            sql.append("?");
            if (i < categoriasArray.length - 1) {
                sql.append(",");
            }
        }
        sql.append(") ");
    }

    sql.append("ORDER BY t.fecha DESC");

    try (Connection con = ConectionDB.conexion();
         PreparedStatement ps = con.prepareStatement(sql.toString())) {

        int paramIndex = 1;
        ps.setInt(paramIndex++, idUsuario);

        if (fInicio != null && fFin != null) {
            ps.setTimestamp(paramIndex++, new java.sql.Timestamp(fInicio.getTime()));
            ps.setTimestamp(paramIndex++, new java.sql.Timestamp(fFin.getTime()));
        }

        if (!filtroTipo.equalsIgnoreCase("Todos")) {
            ps.setString(paramIndex++, filtroTipo);
        }

        if (categoriasArray != null) {
            for (String cat : categoriasArray) {
                ps.setString(paramIndex++, cat.trim());
            }
        }

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Object[] fila = new Object[4];
                fila[0] = rs.getDate("fecha");
                fila[1] = rs.getString("tipo");
                fila[2] = rs.getString("nombre");
                fila[3] = String.format("$ %.2f", rs.getDouble("monto"));
                lista.add(fila);
            }
        }

    } catch (SQLException e) {
        System.out.println("Error al consultar transacciones: " + e.getMessage());
    }

    return lista;
}
}