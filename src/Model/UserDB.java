package Model;

import Conection.ConectionDB;
import java.util.List;
import java.sql.*;
import java.util.ArrayList;

public class UserDB {

    public boolean Insert(User User) {
        String sql_query = "INSERT INTO Usuario(nombre, usuario, pass, telefono, foto_url) VALUES (?,?,?,?,?)";

        try {
            Connection conn = ConectionDB.conexion();
            PreparedStatement stmt = conn.prepareStatement(sql_query);

            stmt.setString(1, User.getNombre());
            stmt.setString(2, User.getUsuario());
            stmt.setString(3, User.getPass());
            stmt.setString(4, User.getTelefono());
            stmt.setString(5, User.getFotoUrl());

            stmt.executeUpdate();

            stmt.close();
            conn.close();

            return true;

        } catch (SQLException e) {
            System.out.println("Error al insertar: " + e.getMessage());
            return false;
        }
    }

    public List<User> consultarUsuario() {
        List<User> listaUsuarios = new ArrayList<>();
        String query_sql = "SELECT id_usuario, nombre, usuario, pass, telefono, foto_url FROM Usuario";

        try {
            Connection conn = ConectionDB.conexion();
            PreparedStatement stmt = conn.prepareStatement(query_sql);
            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                int id = result.getInt("id_usuario");
                String nombre = result.getString("nombre");
                String usuario = result.getString("usuario");
                String pass = result.getString("pass");
                String telefono = result.getString("telefono");
                String fotoUrl = result.getString("foto_url");

                User Usuario = new User(id, nombre, usuario, pass, telefono);
                Usuario.setFotoUrl(fotoUrl);

                listaUsuarios.add(Usuario);
            }

            result.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("Error en consulta: " + e.getMessage());
        }
        return listaUsuarios;
    }

    public User validarLogin(String usuario, String pass) {
        // Incluimos la columna 'pass' para mantener el objeto completo en la SesionUsuario
        String sql = "SELECT id_usuario, nombre, usuario, pass, telefono, foto_url FROM Usuario WHERE usuario = ? AND pass = ?";
        try (Connection conn = ConectionDB.conexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario);
            stmt.setString(2, pass);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id_usuario"));
                u.setNombre(rs.getString("nombre"));
                u.setUsuario(rs.getString("usuario"));
                u.setPass(rs.getString("pass")); // <-- IMPORTANTE: Asignamos pass
                u.setTelefono(rs.getString("telefono"));
                u.setFotoUrl(rs.getString("foto_url"));
                return u;
            }
        } catch (SQLException e) {
            System.err.println("Error en login: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizarUsuario(User user) {
        String sql = "UPDATE Usuario SET nombre = ?, usuario = ?, pass = ?, telefono = ?, foto_url = ? WHERE id_usuario = ?";
        try (Connection conn = ConectionDB.conexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getNombre());
            stmt.setString(2, user.getUsuario());
            stmt.setString(3, user.getPass());
            stmt.setString(4, user.getTelefono());
            stmt.setString(5, user.getFotoUrl());
            stmt.setInt(6, user.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarFotoUsuario(int idUsuario, String fotoUrl) {
        String sql = "UPDATE Usuario SET foto_url = ? WHERE id_usuario = ?";
        try (Connection conn = ConectionDB.conexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fotoUrl);
            stmt.setInt(2, idUsuario);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar foto de usuario: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarUsuario(int idUsuario) {
        String sql = "DELETE FROM Usuario WHERE id_usuario = ?";
        try (Connection conn = ConectionDB.conexion(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }
}