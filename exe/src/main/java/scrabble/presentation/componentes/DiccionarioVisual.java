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
     * Constructor del diccionario visual.
     * @param nombre Nombre del diccionario
     * @param alfabeto Lista de letras del alfabeto
     * @param palabras Lista de palabras válidas del diccionario
     */    
    public DiccionarioVisual(String nombre, List<String> alfabeto, List<String> palabras) {
        this.nombre = nombre;
        this.alfabeto = new ArrayList<>(alfabeto);
        this.palabras = new ArrayList<>(palabras);
    }

    /**
     * Retorna el nombre del diccionario
     * @return El nombre del diccionario
     */
    public String getNombre() {
        return nombre;
    }


    /**
     * Retorna los carácteres que tiene un diccionario
     * @return la lista de caracteres
     */
    public List<String> getAlfabeto() {
        return alfabeto;
    }

    /**
     * Retorna las palabras que tiene un diccionario
     * @return la lista de palabras existentes
     */
    public List<String> getPalabras() {
        return palabras;
    }
}