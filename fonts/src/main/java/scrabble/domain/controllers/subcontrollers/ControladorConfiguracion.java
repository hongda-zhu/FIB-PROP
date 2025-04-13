package domain.controllers.subcontrollers;

import scrabble.helpers.Idioma;
import scrabble.helpers.Tema;
import scrabble.domain.models.Configuracion;

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
