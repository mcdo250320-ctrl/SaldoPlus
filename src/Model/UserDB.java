/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author demia
 */
import Conection.ConectionDB;
import java.util.List;
import java.sql.*;
import java.util.ArrayList;

public class UserDB {
    
    public boolean Insert(User User){
        String sql_query = "INSERT INTO Usuario(nombre, usuario, pass, telefono) VALUES (?,?,?, ?)";
        
        try{
            Connection conn = ConectionDB.conexion();
            
            PreparedStatement stmt = conn.prepareStatement(sql_query);
            
            stmt.setString(1, User.getNombre());
            stmt.setString(2, User.getUsuario());
            stmt.setString(3, User.getPass()); 
            stmt.setString(4, User.getTelefono());
           
            stmt.executeUpdate();
           
            stmt.close();
            conn.close();
            
            return true;
        
        }catch(SQLException e){
            System.out.println("Error al insertar: " + e.getMessage());
            return false;
        }
        
    }
    
    public List<User> consultarUsuario(){
        List<User> listaUsuarios = new ArrayList<>();
                
        String query_sql = "SELECT * FROM Usuario";
        
        try{
            Connection conn = ConectionDB.conexion();
            PreparedStatement stmt = conn.prepareStatement(query_sql);
            ResultSet result = stmt.executeQuery();
            
            //Ciclo para obtener todos los registros
            while(result.next()){
                
                int id = result.getInt("id_usuario");
                String nombre = result.getString("nombre");
                String usuario = result.getString("usuario");
                String pass = result.getString("pass");
                String telefono = result.getString("telefono");
               
                //Crear un objeto y guardarlo en la lista
                User Usuario = new User(id, nombre, usuario, pass, telefono);
                
                
                //Guardar el objeto en la lista
                
                listaUsuarios.add(Usuario);
                       
            }
        }catch(SQLException e){
            System.out.println("Error em consulta: " + e.getMessage());
        }
        return listaUsuarios;
    }
    
    
    public User validarLogin(String usuario, String pass) {
    String sql = "SELECT id_usuario, nombre, usuario FROM Usuario WHERE usuario = ? AND pass = ?";
    try (Connection conn = ConectionDB.conexion();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, usuario);
        stmt.setString(2, pass);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            User u = new User();
            u.setId(rs.getInt("id_usuario"));
            u.setNombre(rs.getString("nombre"));
            u.setUsuario(rs.getString("usuario"));
            return u;
        }
    } catch (SQLException e) {
        System.err.println("Error en login: " + e.getMessage());
    }
    return null; // Si no lo encuentra o falla
}
}
