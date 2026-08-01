package Main;

import View.FRNInicio;
import Controller.userController;

public class Main {
    public static void main(String[] args) {
        FRNInicio vistaInicio = new FRNInicio();
        
        userController controlador = new userController(vistaInicio);
        
        vistaInicio.setVisible(true);
        vistaInicio.setLocationRelativeTo(null);
    }
}