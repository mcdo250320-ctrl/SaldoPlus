/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

public class SesionUsuario {
    private static User usuarioActual;

    public static User getUsuarioActual() {
        return usuarioActual;
    }

    public static void setUsuarioActual(User usuario) {
        usuarioActual = usuario;
    }
}