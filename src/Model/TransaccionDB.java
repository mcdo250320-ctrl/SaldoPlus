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
}