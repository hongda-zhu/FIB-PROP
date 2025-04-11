package domain.controllers.subcontrollers.managers;

import java.util.HashMap;
import java.util.Map;

import domain.models.*;

public class GestorConfiguracionApp {
    private Map<String, Configuracion> configuraciones; // Usuario -> configuración, (aún no está implementado usuario)

    public GestorConfiguracionApp() {
        this.configuraciones = new HashMap<>();
    }

    public boolean agregarConfiguracion(String clave, String valor) {
        if (configuraciones.containsKey(clave)) return false;
        configuraciones.put(clave, new Configuracion(clave, valor));
        return true;
    }

    public Configuracion obtenerConfiguracion(String clave) {
        return configuraciones.get(clave);
    }


    public boolean actualizarConfiguracion(String clave, String nuevoValor) {
        Configuracion config = configuraciones.get(clave);
        if (config == null) return false;
        config.setValor(nuevoValor);
        return true;
    }

    public boolean eliminarConfiguracion(String clave) {
        return configuraciones.remove(clave) != null;
    }

    public void listarConfiguraciones() {
        configuraciones.values().forEach(System.out::println);
    }
}
