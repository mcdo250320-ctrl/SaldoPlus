package Main;

import View.FRNInicio;
import Controller.userController;

public class Main {
    public static void main(String[] args) {
        // 1. Crear la vista inicial
        FRNInicio vistaInicio = new FRNInicio();
        
        // 2. Inicializar el controlador pasándole la vista
        userController controlador = new userController(vistaInicio);
        
        // 3. Hacer visible la pantalla inicial
        vistaInicio.setVisible(true);
        vistaInicio.setLocationRelativeTo(null);
    }
}