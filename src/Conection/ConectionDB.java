/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Conection;

import java.sql.*;

/**
 *
 * @author demia
 */
public class ConectionDB {
     //Manejo de las constantes de datos de la conexion
    
    private static final String URL = "jdbc:mysql://localhost:3306/saldoplus";
    private static final String USER = "root";
    private static final String PASS = "";
    
    public static Connection conexion(){
        
        Connection conn = null;
        
        try{
            
            conn = DriverManager.getConnection(URL, USER, PASS);
            System.out.print("Conexion exitosa");
            
            System.out.println("Cambio en el repositorio local");
            
            System.out.println("Hola amor de mi vida");
            
            System.out.println("Argentina campeon del mundo 2026");
            
        }catch(SQLException e){
            //Manjer error
            System.out.print("Error en la conexión: " + e.getMessage());
        }
        return conn;
    } 
    
}
