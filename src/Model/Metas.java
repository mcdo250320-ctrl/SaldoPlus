/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author bleac
 */
public class Metas {
     private String nombre;
    private double montoObjetivo;
    private double montoActual;

    public Metas(String nombre, double montoObjetivo, double montoActual) {
        this.nombre = nombre;
        this.montoObjetivo = montoObjetivo;
        this.montoActual = montoActual;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getMontoObjetivo() {
        return montoObjetivo;
    }

    public void setMontoObjetivo(double montoObjetivo) {
        this.montoObjetivo = montoObjetivo;
    }

    public double getMontoActual() {
        return montoActual;
    }

    public void setMontoActual(double montoActual) {
        this.montoActual = montoActual;
    }

   
    public void aportar(double cantidad) {
        this.montoActual += cantidad;
        if (this.montoActual > this.montoObjetivo) {
            this.montoActual = this.montoObjetivo;
        }
    }

   
    public int calcularProgreso() {
        if (montoObjetivo <= 0) {
            return 0;
        }
        double porcentaje = (montoActual / montoObjetivo) * 100.0;
        if (porcentaje > 100) {
            porcentaje = 100;
        }
        return (int) Math.round(porcentaje);
    }

   
    @Override
    public String toString() {
        return nombre + " (" + calcularProgreso() + "%)";
    }
}
