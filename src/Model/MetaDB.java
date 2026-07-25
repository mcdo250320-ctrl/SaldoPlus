/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import Conection.ConectionDB;
import java.util.List;
import java.sql.*;
import java.util.ArrayList;

public class MetaDB {
    public List<Meta> obtenerMetasPorUsuario(int idUsuario) {
    List<Meta> lista = new ArrayList<>();
    String sql = "SELECT * FROM Meta WHERE id_usuario = ?";

    try (Connection conn = ConectionDB.conexion();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, idUsuario);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Meta m = new Meta();
            m.setId(rs.getInt("id_meta"));
            m.setDescrip(rs.getString("descripcion")); // Ajusta los getters/setters según tu modelo Meta
            lista.add(m);
        }
    } catch (SQLException e) {
        System.err.println("Error al cargar metas: " + e.getMessage());
    }
    return lista;
}
}