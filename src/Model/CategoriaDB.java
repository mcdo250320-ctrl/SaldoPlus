package Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import Conection.ConectionDB;

public class CategoriaDB {

    public List<Categoria> obtenerCategorias() {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM categoria";

        try (Connection con = ConectionDB.conexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_categoria");
                String nombre = rs.getString("nombre");
                
                lista.add(new Categoria(id, nombre));
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener categorías: " + e.getMessage());
        }

        return lista;
    }
}