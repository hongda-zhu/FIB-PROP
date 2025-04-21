package scrabble.domain.models;

import scrabble.helpers.Idioma;
 
/**
 *Clase que implementa todos los parámetros que tengan que ver con la aplicación. 
 */

public class Configuracion {
    public enum Tema {
        CLARO,
        OSCURO
    }

    private Idioma idioma;
    private int volumen; // percentage
    private Tema tema;

    /**
     *Constructura por defecto de la clase configuración, por defecto: el idioma es español, el volumen es del 50% y el tema es claro. 
     */
    public Configuracion() {
        this.idioma = Idioma.ESPANOL;
        this.volumen = 50;
        this.tema = Tema.CLARO;
    }

    
    /**
     *Obtiene idioma de la aplicación actual. 
     * @return Idioma de la aplicación
     */
    public String obteneridioma() {
        return this.idioma.toString();
    }

    /**
     *Obtiene tema de la aplicación actual.
     * @return Tema de la aplicación
     */
    public String obtenerTema() {
        return this.tema.toString();
    }

    /**
     *Obtiene volumen de la aplicación actual. 
     * @return Volumen en porcentaje de la aplicación
     */
    public int obtenerVolumen() {
        return this.volumen;
    }

    /**
     * Establece  el idioma de la aplicación.
     * 
     * @param i Nuevo idioma
     */
    public void setIdioma(String i) {
        if (!i.equals("ESPANOL") && !i.equals("CATALAN") && !i.equals("INGLES")) throw new IllegalArgumentException ("El idioma debe ser: ESPANOL, CATALAN o INGLES");
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

    /**
     * Establece  el tema de la aplicación.
     * 
     * @param t Nuevo tema
     */
    public void setTema(String t) {
        if (!t.equals("CLARO") && !t.equals("OSCURO")) throw new IllegalArgumentException ("El tema deber ser CLARO o OSCURO");
        switch (t) { 
            case "CLARO":
                this.tema = Tema.CLARO;
                break;
            case "OSCURO":
                this.tema = Tema.OSCURO;
                break;
        }
    }

    /**
     * Establece  el volumen de la aplicación.
     * 
     * @param v Nuevo valor de volumen
     */
    public void setVolumen(int v) {
        if (v > 100 || v < 0) throw new IllegalArgumentException ("El volumen debe ser un valor entre 0 y 100");
        this.volumen = v;
    }
}
    

