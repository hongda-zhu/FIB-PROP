package scrabble.domain.models;
/*
import scrabble.helpers.Idioma;
 
public enum Tema {
    CLARO,
    OSCURO
}

public class Configuracion {
    private Idioma idioma;
    private int volumen; // percentage
    private Tema tema;

    public Configuracion() {
        this.idioma = Idioma.ESPANOL;
        this.volumen = 50;
        this.tema = Tema.CLARO;
    }

    public String obteneridioma() {
        return switch (this.idioma) {
            case ESPANOL -> "ESPANOL";
            case CATALAN -> "CATALAN";
            case INGLES -> "INGLES";
            default -> "ERROR EN EL IDIOMA!";
        };
    }

    public String obtenerTema() {
        return switch (this.tema) {
            case CLARO -> "CLARO";
            case OSCURO -> "OSCURO";
            default -> "?";
        };
    }

    public int obtenerVolumen() {
        return this.volumen;
    }

    public void setIdioma(String i) {
        this.idioma = switch(i.toUpperCase()) {
            case "ESPANOL" -> Idioma.ESPANOL;
            case "CATALAN" -> Idioma.CATALAN;
            case "INGLES" -> Idioma.INGLES;
            default -> {
                System.err.println("Idioma no válido: " + i);
                yield Idioma.ESPANOL;
            }
        };
    }

    public void setTema(String t) {
        this.tema = switch(t.toUpperCase()) {
            case "CLARO" -> Tema.CLARO;
            case "OSCURO" -> Tema.OSCURO;
            default -> {
                System.err.println("Tema no válido: " + t);
                yield Tema.CLARO;
            }
        };
    }

    public void setVolumen(int v) {
        if (v < 0 || v > 100) {
            throw new IllegalArgumentException("El volumen debe estar entre 0 y 100");
        }
        this.volumen = v;
    }
}
    */

