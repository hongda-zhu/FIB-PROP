package scrabble.presentation.componentes;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa un diccionario visual.
 * Contiene el nombre del diccionario, el alfabeto y las palabras.
 */

public class DiccionarioVisual {
    private String nombre;
    private List<String> alfabeto;
    private List<String> palabras;

    /**
     * Constructor de la clase DiccionarioVisual.
     *
     * @param nombre   Nombre del diccionario.
     * @param alfabeto Alfabeto del diccionario.
     * @param palabras  Lista de palabras del diccionario.
     */
    
    public DiccionarioVisual(String nombre, List<String> alfabeto, List<String> palabras) {
        this.nombre = nombre;
        this.alfabeto = new ArrayList<>(alfabeto);
        this.palabras = new ArrayList<>(palabras);
    }

    public String getNombre() {
        return nombre;
    }

    public List<String> getAlfabeto() {
        return alfabeto;
    }

    public List<String> getPalabras() {
        return palabras;
    }
}