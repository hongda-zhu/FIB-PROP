package scrabble.domain.controllers.subcontrollers;

import scrabble.domain.models.Configuracion;

public class ControladorConfiguracion {
    private Configuracion configuracion;

    public ControladorConfiguracion() {
        this.configuracion = new Configuracion();
    }

    public String obteneridioma() {
        return configuracion.obteneridioma();
    }

    public String obtenerTema() {
        return configuracion.obtenerTema();
    }

    public int obtenerVolumen() {
        return configuracion.obtenerVolumen();
    }

    public void setIdioma(String i) {
        configuracion.setIdioma(i);
    }

    public void setTema(String t) {
        configuracion.setTema(t);
    }

    public void setVolumen(int v) {
        configuracion.setVolumen(v);
    }
}
