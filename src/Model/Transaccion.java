/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author demia
 */
public class Transaccion {
    
    private int id;
    private float monto;
    private String tipo;
    private String fecha;
    private String descripcion;
    private String comprobanteRuta;
    private int id_usuario;
    private int id_meta;
    private int id_categoria;
    
    public Transaccion(){};
   
    public Transaccion(float monto, String tipo, String fecha, String descripcion, int id_usuario, int id_meta, int id_categoria) {
        this.monto = monto;
        this.tipo = tipo;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.id_usuario = id_usuario;
        this.id_meta = id_meta;
        this.id_categoria = id_categoria;
    }

    public Transaccion(int id, float monto, String tipo, String fecha, String descripcion, int id_usuario, int id_meta, int id_categoria) {
        this.id = id;
        this.monto = monto;
        this.tipo = tipo;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.id_usuario = id_usuario;
        this.id_meta = id_meta;
        this.id_categoria = id_categoria;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getMonto() {
        return monto;
    }

    public void setMonto(float monto) {
        this.monto = monto;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public int getId_meta() {
        return id_meta;
    }

    public void setId_meta(int id_meta) {
        this.id_meta = id_meta;
    }

    public int getId_categoria() {
        return id_categoria;
    }

    public void setId_categoria(int id_categoria) {
        this.id_categoria = id_categoria;
    }
    
    
}
