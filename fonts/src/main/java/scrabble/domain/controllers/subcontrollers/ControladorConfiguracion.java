package domain.controllers.subcontrollers;

import domain.helpers.Idioma;
import domain.helpers.Tema;
import domain.models.Configuracion;

public class ControladorConfiguracion {
    private Configuracion configuracion;

    public ControladorConfiguracion() {
        this.configuracion = new Configuracion();
    }

    public Idioma obteneridioma() {
        return configuracion.obteneridioma();
    }

    public Tema obtenerTema() {
        return configuracion.obtenerTema();
    }

    public int obtenerVolumen() {
        return configuracion.obtenerVolumen();
    }

    public void setIdioma(Idioma i) {
        configuracion.setIdioma(i);
    }

    public void setTema(Tema t) {
        configuracion.setTema(t);
    }

    public void setVolumen(int v) {
        configuracion.setVolumen(v);
    }
}
