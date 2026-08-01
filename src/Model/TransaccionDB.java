package Model;

import Conection.ConectionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransaccionDB {

    public boolean insertarTransaccion(Transaccion t) {
        String sql = "INSERT INTO transaccion (monto, tipo, descripcion, id_categoria, id_meta, id_usuario) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConectionDB.conexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setFloat(1, t.getMonto());
            stmt.setString(2, t.getTipo());
            stmt.setString(3, t.getDescripcion());

            if (t.getId_categoria() > 0) {
                stmt.setInt(4, t.getId_categoria());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            if (t.getId_meta() > 0) {
                stmt.setInt(5, t.getId_meta());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            stmt.setInt(6, t.getId_usuario());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar transacción: " + e.getMessage());
            return false;
        }
    }

    public float obtenerTotalAhorradoPorMeta(int idMeta) {
        float totalAhorrado = 0.0f;
        String sql = "SELECT SUM(monto) AS total FROM transaccion WHERE id_meta = ?";

        try (Connection conn = ConectionDB.conexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idMeta);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                totalAhorrado = rs.getFloat("total");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener total ahorrado por meta: " + e.getMessage());
        }

        return totalAhorrado;
    }

    public float[] obtenerTotalesPorUsuario(int idUsuario) {
        float[] totales = new float[3];
        float ingresos = 0;
        float egresos = 0;

        String sql = "SELECT tipo, SUM(monto) AS total FROM transaccion WHERE id_usuario = ? GROUP BY tipo";

        try (Connection conn = ConectionDB.conexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String tipo = rs.getString("tipo");
                float monto = rs.getFloat("total");

                if ("Ingreso".equalsIgnoreCase(tipo)) {
                    ingresos += monto;
                } else if ("Egreso".equalsIgnoreCase(tipo)) {
                    egresos += monto;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener totales por usuario: " + e.getMessage());
        }

        totales[0] = ingresos;
        totales[1] = egresos;
        totales[2] = ingresos - egresos;

        return totales;
    }

    public List<Object[]> obtenerTransaccionesAvanzado(int idUsuario, java.util.Date fInicio, java.util.Date fFin, String tipo, String categorias) {
        List<Object[]> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT t.id_transaccion, t.monto, t.tipo, t.fecha, t.descripcion, c.nombre_categoria ");
        sql.append("FROM transaccion t ");
        sql.append("LEFT JOIN categoria c ON t.id_categoria = c.id_categoria ");
        sql.append("WHERE t.id_usuario = ? ");

        if (fInicio != null) {
            sql.append("AND t.fecha >= ? ");
        }
        if (fFin != null) {
            sql.append("AND t.fecha <= ? ");
        }
        if (tipo != null && !tipo.equalsIgnoreCase("Todos")) {
            sql.append("AND t.tipo = ? ");
        }
        if (categorias != null && !categorias.equalsIgnoreCase("Todas") && !categorias.trim().isEmpty()) {
            String[] cats = categorias.split(",");
            sql.append("AND c.nombre_categoria IN (");
            for (int i = 0; i < cats.length; i++) {
                sql.append("?");
                if (i < cats.length - 1) sql.append(",");
            }
            sql.append(") ");
        }

        try (Connection conn = ConectionDB.conexion();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            stmt.setInt(paramIndex++, idUsuario);

            if (fInicio != null) {
                stmt.setTimestamp(paramIndex++, new java.sql.Timestamp(fInicio.getTime()));
            }
            if (fFin != null) {
                stmt.setTimestamp(paramIndex++, new java.sql.Timestamp(fFin.getTime()));
            }
            if (tipo != null && !tipo.equalsIgnoreCase("Todos")) {
                stmt.setString(paramIndex++, tipo);
            }
            if (categorias != null && !categorias.equalsIgnoreCase("Todas") && !categorias.trim().isEmpty()) {
                String[] cats = categorias.split(",");
                for (String cat : cats) {
                    stmt.setString(paramIndex++, cat.trim());
                }
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id_transaccion");
                float monto = rs.getFloat("monto");
                String tTipo = rs.getString("tipo");
                Timestamp fecha = rs.getTimestamp("fecha");
                String desc = rs.getString("descripcion");
                String catNombre = rs.getString("nombre_categoria");

                lista.add(new Object[]{
                    id,
                    String.format("$ %.2f", monto),
                    tTipo,
                    fecha != null ? fecha.toString() : "",
                    desc,
                    catNombre != null ? catNombre : "Sin categoría"
                });
            }

        } catch (SQLException e) {
            System.err.println("Error al realizar consulta avanzada: " + e.getMessage());
        }

        return lista;
    }
}