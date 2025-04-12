package domain.controllers.subcontrollers;

import domain.models.Configuracion;
import domain.models.Idioma;
import domain.models.Tema;

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
