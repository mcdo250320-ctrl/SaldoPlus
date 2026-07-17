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

public class TransaccionDB {
    
    public boolean Insert(Transaccion Transaccion){
        String sql_query = "INSERT INTO Transaccion(monto, tipo, fecha, descripcion, id_usuario, id_meta, id_categoria) VALUES (?,?,?,?,?,?,?)";
        
        try{
            Connection conn = ConectionDB.conexion();
            
            PreparedStatement stmt = conn.prepareStatement(sql_query);
            
            stmt.setFloat(1, Transaccion.getMonto());
            stmt.setString(2, Transaccion.getTipo());
            stmt.setString(3, Transaccion.getFecha());
            stmt.setString(4, Transaccion.getDescripcion());
            stmt.setInt(5, Transaccion.getId_usuario());
            stmt.setInt(6, Transaccion.getId_meta());
            stmt.setInt(7, Transaccion.getId_categoria());
            
           
            stmt.executeUpdate();
           
            stmt.close();
            conn.close();
            
            return true;
        
        }catch(SQLException e){
            System.out.println("Error al insertar: " + e.getMessage());
            return false;
        }
        
    }
    
    public List<Transaccion> consultarTransacciones(){
        List<Transaccion> listaTransacciones = new ArrayList<>();
                
        String query_sql = "SELECT * FROM Transaccion";
        
        try{
            Connection conn = ConectionDB.conexion();
            PreparedStatement stmt = conn.prepareStatement(query_sql);
            ResultSet result = stmt.executeQuery();
            
            //Ciclo para obtener todos los registros
            while(result.next()){
                
                int id = result.getInt("id_transaccion");
                float monto = result.getFloat("monto");
                String tipo = result.getString("tipo");
                String fecha = result.getString("fecha");
                String descripcion = result.getString("descripcion");
                int id_usuario = result.getInt("id_usuario");
                int id_meta = result.getInt("id_meta");
                int id_categoria = result.getInt("id_categoria");
                
                //Crear un objeto y guardarlo en la lista
                Transaccion transaccion = new Transaccion(id, monto, tipo, fecha, descripcion, id_usuario, id_meta, id_categoria);
                
                
                //Guardar el objeto en la lista
                
                listaTransacciones.add(transaccion);
                       
            }
        }catch(SQLException e){
            System.out.println("Error em consulta: " + e.getMessage());
        }
        return listaTransacciones;
    }
    
    
}
