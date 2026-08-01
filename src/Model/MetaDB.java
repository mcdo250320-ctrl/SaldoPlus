package Model;

import Conection.ConectionDB;
import java.util.List;
import java.sql.*;
import java.util.ArrayList;

public class MetaDB {

    public List<Meta> obtenerMetasPorUsuario(int idUsuario) {
        List<Meta> lista = new ArrayList<>();
        String sql = "SELECT id_meta, monto_objetivo, fecha_limite, descripcion, id_usuario FROM meta WHERE id_usuario = ?";

        try (Connection conn = ConectionDB.conexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Meta m = new Meta();
                m.setId(rs.getInt("id_meta"));
                m.setMonto(rs.getFloat("monto_objetivo"));
                m.setFecha(rs.getString("fecha_limite"));
                m.setDescrip(rs.getString("descripcion"));
                m.setId_usuario(rs.getInt("id_usuario"));
                lista.add(m);
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar metas: " + e.getMessage());
        }
        return lista;
    }

    public boolean insertarMeta(Meta meta) {
        String sql = "INSERT INTO meta (monto_objetivo, fecha_limite, descripcion, id_usuario) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConectionDB.conexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setFloat(1, meta.getMonto());
            stmt.setString(2, meta.getFecha());
            stmt.setString(3, meta.getDescrip());
            stmt.setInt(4, meta.getId_usuario());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar meta: " + e.getMessage());
            return false;
        }
    }
}