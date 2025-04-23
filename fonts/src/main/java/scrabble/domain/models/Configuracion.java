package scrabble.domain.models;

import java.io.Serializable;

import scrabble.helpers.Idioma;
 
/**
 *Clase que implementa todos los parámetros que tengan que ver con la aplicación. 
 */

public class Configuracion implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
    * Enum que representa el tema de la aplicación
    * 
    */
    public enum Tema {
        CLARO,
        OSCURO
    }

    private Idioma idioma;
    private int volumen; // percentage
    private Tema tema;

    /**
     * Constructor por defecto de la clase configuración.
     * Por defecto: el idioma es español, el volumen es del 50% y el tema es claro. 
     * 
     * @pre No hay precondiciones específicas.
     * @post Se crea una nueva instancia de Configuracion con valores por defecto:
     *       - Idioma: ESPANOL
     *       - Volumen: 50%
     *       - Tema: CLARO
     */
    public Configuracion() {
        this.idioma = Idioma.ESPANOL;
        this.volumen = 50;
        this.tema = Tema.CLARO;
    }

    
    /**
     * Obtiene idioma de la aplicación actual. 
     * 
     * @pre No hay precondiciones específicas.
     * @return Idioma de la aplicación
     * @post Se devuelve un String que representa el idioma actual de la aplicación.
     */
    public String obteneridioma() {
        return this.idioma.toString();
    }

    /**
     * Obtiene tema de la aplicación actual.
     * 
     * @pre No hay precondiciones específicas.
     * @return Tema de la aplicación
     * @post Se devuelve un String que representa el tema actual de la aplicación.
     */
    public String obtenerTema() {
        return this.tema.toString();
    }

    /**
     * Obtiene volumen de la aplicación actual. 
     * 
     * @pre No hay precondiciones específicas.
     * @return Volumen en porcentaje de la aplicación
     * @post Se devuelve un entero entre 0 y 100 que representa el volumen actual de la aplicación.
     */
    public int obtenerVolumen() {
        return this.volumen;
    }

    /**
     * Establece el idioma de la aplicación.
     * 
     * @pre El idioma debe ser una de las opciones válidas: "ESPANOL", "CATALAN" o "INGLES".
     * @param i Nuevo idioma
     * @post El idioma de la configuración se actualiza al valor especificado.
     * @throws IllegalArgumentException si el idioma no es uno de los valores permitidos
     * @throws NullPointerException si el parámetro i es null
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
     * Establece el tema de la aplicación.
     * 
     * @pre El tema debe ser una de las opciones válidas: "CLARO" o "OSCURO".
     * @param t Nuevo tema
     * @post El tema de la configuración se actualiza al valor especificado.
     * @throws IllegalArgumentException si el tema no es uno de los valores permitidos
     * @throws NullPointerException si el parámetro t es null
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
     * Establece el volumen de la aplicación.
     * 
     * @pre El volumen debe estar en el rango [0, 100].
     * @param v Nuevo valor de volumen
     * @post El nivel de volumen de la configuración se actualiza al valor especificado.
     * @throws IllegalArgumentException si el volumen está fuera del rango permitido
     */
    public void setVolumen(int v) {
        if (v > 100 || v < 0) throw new IllegalArgumentException ("El volumen debe ser un valor entre 0 y 100");
        this.volumen = v;
    }
}