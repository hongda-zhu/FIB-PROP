package domain.controllers.subcontrollers;

import domain.controllers.subcontrollers.managers.GestorConfiguracionApp;
import domain.models.Configuracion;

public class ControladorConfiguracion {
    private GestorConfiguracionApp gestorConfiguracionApp;

    public ControladorConfiguracion() {
        this.gestorConfiguracionApp = new GestorConfiguracionApp();
    }
    
    public void agregarConfiguracion(String clave, String valor) {
        boolean exito = gestorConfiguracionApp.agregarConfiguracion(clave, valor);
        System.out.println(exito ? "Configuración agregada." : "Ya existe esa clave.");
    }

    public void verConfiguracion(String clave) {
        Configuracion config = gestorConfiguracionApp.obtenerConfiguracion(clave);
        if (config == null) {
            System.out.println("Configuración no encontrada.");
        } else {
            System.out.println(config);
        }
    }

    public void actualizarConfiguracion(String clave, String nuevoValor) {
        boolean exito = gestorConfiguracionApp.actualizarConfiguracion(clave, nuevoValor);
        System.out.println(exito ? "Actualizado correctamente." : "No se encontró esa clave.");
    }

    public void eliminarConfiguracion(String clave) {
        boolean exito = gestorConfiguracionApp.eliminarConfiguracion(clave);
        System.out.println(exito ? "Configuración eliminada." : "No se encontró esa clave.");
    }

    public void listarConfiguraciones() {
        gestorConfiguracionApp.listarConfiguraciones();
    }
}
