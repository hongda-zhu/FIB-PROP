package scrabble.domain.models;

import scrabble.helpers.Idioma;
 


public class Configuracion {
    public enum Tema {
        CLARO,
        OSCURO
    }

    private Idioma idioma;
    private int volumen; // percentage
    private Tema tema;

    public Configuracion() {
        this.idioma = Idioma.ESPANOL;
        this.volumen = 50;
        this.tema = Tema.CLARO;
    }

    public String obteneridioma() {
        return this.idioma.toString();
    }

    public String obtenerTema() {
        return this.tema.toString();
    }

    public int obtenerVolumen() {
        return this.volumen;
    }

    public void setIdioma(String i) {
        if (!i.equals("ESPANOL") && !i.equals("CATALAN") && !i.equals("INGLES")) throw new IllegalArgumentException("El idioma debe ser: ESPANOL, CATALAN o INGLES");
        switch (i) { 
            case "ESPANOL":
                this.idioma = Idioma.ESPANOL;
                break;
            case "CATALAN":
                this.idioma = Idioma.CATALAN;
                break;
            case "INGLES":
                this.idioma = Idioma.INGLES;
                break;
        }
    }

    public void setTema(String t) {
        if (!t.equals("CLARO") && !t.equals("OSCURO")) throw new IllegalArgumentException("El tema deber ser CLARO o OSCURO");
        switch (t) { 
            case "CLARO":
                this.tema = Tema.CLARO;
                break;
            case "OSCURO":
                this.tema = Tema.OSCURO;
                break;
        }
    }

    public void setVolumen(int v) {
        if (v > 100 || v < 0) throw new IllegalArgumentException("El volumen debe ser un valor entre 0 y 100");
        this.volumen = v;
    }
}
    

