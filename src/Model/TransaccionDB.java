package Model;

import Conection.ConectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransaccionDB {

    public boolean insertarTransaccion(Transaccion t) {
        String sql = "INSERT INTO transaccion (monto, tipo, descripcion, id_categoria, id_meta, id_usuario) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConectionDB.conexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setFloat(1, t.getMonto());
            ps.setString(2, t.getTipo());
            ps.setString(3, t.getDescripcion());
            ps.setInt(4, t.getId_categoria());

            if (t.getId_meta() > 0) {
                ps.setInt(5, t.getId_meta());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }

            ps.setInt(6, t.getId_usuario());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al insertar transacción: " + e.getMessage());
            return false;
        }
    }

    public float[] obtenerTotalesPorUsuario(int idUsuario) {
        float[] totales = new float[3];
        String sql = "SELECT tipo, SUM(monto) AS total FROM transaccion WHERE id_usuario = ? GROUP BY tipo";

        try (Connection con = ConectionDB.conexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String tipo = rs.getString("tipo");
                    float total = rs.getFloat("total");
                    if ("Ingreso".equalsIgnoreCase(tipo)) {
                        totales[0] = total;
                    } else if ("Egreso".equalsIgnoreCase(tipo)) {
                        totales[1] = total;
                    }
                }
            }
            totales[2] = totales[0] - totales[1];

        } catch (SQLException e) {
            System.out.println("Error al obtener totales: " + e.getMessage());
        }

        return totales;
    }

    public float obtenerTotalAhorradoPorMeta(int idMeta) {
        float total = 0;
        String sql = "SELECT SUM(monto) AS total FROM transaccion WHERE id_meta = ?";

        try (Connection con = ConectionDB.conexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idMeta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getFloat("total");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener total ahorrado por meta: " + e.getMessage());
        }

        return total;
    }

    public List<Object[]> obtenerTransaccionesAvanzado(int idUsuario, java.util.Date fInicio, java.util.Date fFin, String tipo, String categorias) {
        List<Object[]> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT t.fecha, t.tipo, c.nombre AS categoria, t.monto, m.descripcion AS meta_desc, t.descripcion ");
        sql.append("FROM transaccion t ");
        sql.append("LEFT JOIN categoria c ON t.id_categoria = c.id_categoria ");
        sql.append("LEFT JOIN meta m ON t.id_meta = m.id_meta ");
        sql.append("WHERE t.id_usuario = ? ");

        if (fInicio != null) {
            sql.append("AND t.fecha >= ? ");
        }
        if (fFin != null) {
            sql.append("AND t.fecha <= ? ");
        }
        if (!"Todos".equalsIgnoreCase(tipo)) {
            sql.append("AND t.tipo = ? ");
        }
        if (!"Todas".equalsIgnoreCase(categorias) && !categorias.isEmpty()) {
            String[] catArr = categorias.split(",");
            sql.append("AND c.nombre IN (");
            for (int i = 0; i < catArr.length; i++) {
                sql.append("?");
                if (i < catArr.length - 1) {
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

            if (fInicio != null) {
                ps.setDate(paramIndex++, new java.sql.Date(fInicio.getTime()));
            }
            if (fFin != null) {
                ps.setDate(paramIndex++, new java.sql.Date(fFin.getTime()));
            }
            if (!"Todos".equalsIgnoreCase(tipo)) {
                ps.setString(paramIndex++, tipo);
            }
            if (!"Todas".equalsIgnoreCase(categorias) && !categorias.isEmpty()) {
                String[] catArr = categorias.split(",");
                for (String cat : catArr) {
                    ps.setString(paramIndex++, cat.trim());
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String fecha = rs.getString("fecha");
                    String tipoTr = rs.getString("tipo");
                    String catTr = rs.getString("categoria");
                    float montoTr = rs.getFloat("monto");
                    String metaTr = rs.getString("meta_desc");
                    String descTr = rs.getString("descripcion");

                    lista.add(new Object[]{
                        fecha != null ? fecha : "",
                        tipoTr != null ? tipoTr : "",
                        catTr != null ? catTr : "General",
                        String.format("$ %.2f", montoTr),
                        metaTr != null ? metaTr : "",
                        descTr != null ? descTr : ""
                    });
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al realizar consulta avanzada: " + e.getMessage());
        }

        return lista;
    }
}