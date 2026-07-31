/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author demia
 */
public class User {
    
    private int id;
    private String nombre;
    private String usuario;
    private String pass;
    private String telefono;
    
    public User(){}
    
    public User(String nombre, String usuario, String pass, String telefono){
        this.nombre = nombre;
        this.usuario = usuario;
        this.pass = pass;
        this.telefono = telefono;
    }
    
    public User(int id, String nombre, String usuario, String pass, String telefono){
        this.id = id; // <-- Asignación corregida aquí
        this.nombre = nombre;
        this.usuario = usuario;
        this.pass = pass;
        this.telefono = telefono;
    } 

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}