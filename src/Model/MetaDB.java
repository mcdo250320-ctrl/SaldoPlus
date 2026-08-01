package Model;

import Conection.ConectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MetaDB {

    public boolean insertarMeta(Meta meta) {
        String sql = "INSERT INTO meta (monto_objetivo, fecha_limite, descripcion, id_usuario) VALUES (?, ?, ?, ?)";
        
        try (Connection con = ConectionDB.conexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setFloat(1, meta.getMonto());
            ps.setString(2, meta.getFecha());
            ps.setString(3, meta.getDescrip());
            ps.setInt(4, meta.getId_usuario());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al insertar meta: " + e.getMessage());
            return false;
        }
    }

    public List<Meta> obtenerMetasPorUsuario(int idUsuario) {
        List<Meta> lista = new ArrayList<>();
        String sql = "SELECT id_meta, monto_objetivo, fecha_limite, descripcion, id_usuario FROM meta WHERE id_usuario = ?";

        try (Connection con = ConectionDB.conexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Meta m = new Meta();
                    m.setId(rs.getInt("id_meta"));
                    m.setMonto(rs.getFloat("monto_objetivo"));
                    m.setFecha(rs.getString("fecha_limite"));
                    m.setDescrip(rs.getString("descripcion"));
                    m.setId_usuario(rs.getInt("id_usuario"));
                    lista.add(m);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener metas por usuario: " + e.getMessage());
        }

        return lista;
    }

    public boolean actualizarMeta(Meta meta) {
        String sql = "UPDATE meta SET monto_objetivo = ?, fecha_limite = ?, descripcion = ? WHERE id_meta = ?";

        try (Connection con = ConectionDB.conexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setFloat(1, meta.getMonto());
            ps.setString(2, meta.getFecha());
            ps.setString(3, meta.getDescrip());
            ps.setInt(4, meta.getId());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar meta: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarMeta(int idMeta) {
        String sql = "DELETE FROM meta WHERE id_meta = ?";

        try (Connection con = ConectionDB.conexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idMeta);
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar meta: " + e.getMessage());
            return false;
        }
    }
}