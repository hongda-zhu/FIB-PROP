package scrabble.domain.models;

import scrabble.helpers.Idioma;
import scrabble.helpers.Tema;

public class Configuracion {
    private Idioma idioma;
    private int volumen; // percentage
    private Tema tema;

    public Configuracion() {
        this.idioma = Idioma.ESPANOL;
        this.volumen = 50;
        this.tema = Tema.CLARO;
    }

    public Idioma obteneridioma() {
        return this.idioma;
    }

    public Tema obtenerTema() {
        return this.tema;
    }

    public int obtenerVolumen() {
        return this.volumen;
    }

    public void setIdioma(Idioma i) {
        this.idioma = i;
    }

    public void setTema(Tema t) {
        this.tema = t;
    }

    public void setVolumen(int v) {
        this.volumen = v;
    }

}
