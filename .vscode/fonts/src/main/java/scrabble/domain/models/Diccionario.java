package scrabble.domain.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Clase que implementa un diccionario de palabras para el juego de Scrabble.
 * Gestiona un solo conjunto de palabras con su alfabeto y configuración de fichas.
 */
public class Diccionario implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Estructura DAWG que almacena las palabras del diccionario.
     */
    private Dawg dawg;
    
    /**
     * Mapa que almacena los valores de puntos para cada letra del alfabeto.
     */
    private Map<String, Integer> alphabet;
    
    /**
     * Mapa que almacena la configuración de frecuencia de fichas.
     */
    private Map<String, Integer> bag;
    
    /**
     * Conjunto que almacena los caracteres comodín del alfabeto.
     */
    private Set<String> comodines;

    /**
     * Constructor por defecto que inicializa las estructuras de datos.
     */
    public Diccionario() {
        this.dawg = new Dawg();
        this.alphabet = new HashMap<>();
        this.bag = new HashMap<>();
        this.comodines = new HashSet<>();
    }

    /**
     * Inicializa el DAWG (estructura de palabras) del diccionario.
     * 
     * @param palabras Lista de palabras para insertar en el DAWG
     */
    public void setDawg(List<String> palabras) {
        Dawg newDawg = new Dawg();
        inicializarDawg(newDawg, palabras);
        this.dawg = newDawg;
    }

    /**
     * Inicializa un DAWG con una lista de palabras.
     * 
     * @param dawg Estructura DAWG a inicializar
     * @param palabras Lista de palabras para insertar en el DAWG
     */
    public void inicializarDawg(Dawg dawg, List<String> palabras) {
        for (String palabra : palabras) {
            dawg.insert(palabra);
        }
    }

    /**
     * Configura el alfabeto y la bolsa del diccionario a partir de líneas de texto.
     * Cada línea debe tener el formato: "letra frecuencia puntos"
     * Los comodines se identifican con símbolos especiales (ej: "#") y tienen valor 0.
     * 
     * @param lineas Lista de líneas con el formato indicado
     * @throws IllegalArgumentException  Si hay problemas con el formato de las líneas
     */
    public void setAlphabet(List<String> lineas) {
        Map<String, Integer> newAlphabet = new HashMap<>();
        Map<String, Integer> newBag = new HashMap<>();
        Set<String> newComodines = new HashSet<>();

        for (String linea : lineas) {
            String[] partes = linea.split(" ");
            if (partes.length == 3) {
                try {
                    String caracter = partes[0];
                    int frecuencia = Integer.parseInt(partes[1]); 
                    int puntos = Integer.parseInt(partes[2]);
                    
                    if (frecuencia < 1) {
                        throw new IllegalArgumentException ("La frecuencia no puede menor que 1: " + linea);
                    }
                    
                    if (puntos < 0) {
                        throw new IllegalArgumentException ("Los puntos no pueden ser negativos: " + linea);
                    }
                    
                    // Si el valor es 0, podría ser un comodín
                    if (puntos == 0 && caracter.equals("#")) {
                        newComodines.add(caracter);
                    }
                    
                    newAlphabet.put(caracter, puntos);
                    newBag.put(caracter, frecuencia);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException ("Formato incorrecto en línea: " + linea + ". Los valores de frecuencia y puntos deben ser números.");
                }                     
            }
            else {
                throw new IllegalArgumentException ("Línea con formato incorrecto: " + linea + ". Formato esperado: LETRA FRECUENCIA PUNTOS");                
            }
        }
        
        if (newAlphabet.isEmpty()) {
            throw new IllegalArgumentException ("El alfabeto no puede estar vacío. Debe especificar al menos una letra.");
        }
        
        this.alphabet = newAlphabet;
        this.bag = newBag;
        this.comodines = newComodines;
    }

    /**
     * Obtiene el DAWG del diccionario.
     * 
     * @return Estructura DAWG
     */
    public Dawg getDawg() {
        return this.dawg;
    }
    
    /**
     * Obtiene el mapa de puntos por letra del alfabeto.
     * 
     * @return Mapa que asocia cada letra con su valor en puntos
     */
    public Map<String, Integer> getAlphabet() {
        return this.alphabet;
    }

    /**
     * Obtiene el mapa de frecuencias de fichas.
     * 
     * @return Mapa que asocia cada letra con su frecuencia de aparición en el juego
     */
    public Map<String, Integer> getBag() {
        return this.bag;
    }
    
    /**
     * Obtiene el conjunto de caracteres comodín.
     * 
     * @return Conjunto de caracteres comodín
     */
    public Set<String> getComodines() {
        return this.comodines;
    }
    
    /**
     * Verifica si un carácter es un comodín.
     * 
     * @param caracter Carácter a verificar
     * @return true si es un comodín, false en caso contrario
     */
    public boolean esComodin(String caracter) {
        return this.comodines.contains(caracter);
    }
    
    /**
     * Verifica si una palabra existe en el diccionario.
     * 
     * @param palabra Palabra a verificar
     * @return true si la palabra existe, false en caso contrario
     */
    public boolean contienePalabra(String palabra) {
        if (dawg == null) return false;
        return dawg.search(palabra.toUpperCase());
    }
    
    /**
     * Obtiene los caracteres del alfabeto.
     * 
     * @return Conjunto de caracteres válidos del alfabeto
     */
    public Set<Character> getAlphabetChars() {
        if (alphabet == null) return new HashSet<>();
        
        Set<Character> chars = new HashSet<>();
        for (String key : alphabet.keySet()) {
            for (char c : key.toCharArray()) {
                chars.add(Character.toUpperCase(c));
            }
        }
        return chars;
    }

    /**
     * Verifica si una palabra contiene solo caracteres válidos.
     * 
     * @param palabra Palabra a verificar
     * @param validChars Conjunto de caracteres válidos
     * @return true si la palabra es válida, false en caso contrario
     */
    public boolean isValidWordSyntax(String palabra, Set<Character> validChars) {
        if (palabra == null || palabra.isEmpty()) {
            return false; 
        }
        for (char c : palabra.toCharArray()) {
            if (!validChars.contains(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Obtiene el mapa de frecuencias de fichas para un idioma.
     * 
     * @param nombre Nombre del idioma
     * @return Mapa que asocia cada letra con su frecuencia de aparición en el juego
     */
    public Map<String, Integer> getFichas() {
        Map<String, Integer> bag = getBag();
        if (bag == null) return null;
        
        return bag;
    }

    /**
     * Obtiene el puntaje asociado a un carácter dado.
     *
     * Este método recupera el puntaje del carácter especificado `c` del mapa del alfabeto.
     * Si el mapa del alfabeto es nulo o el carácter no se encuentra en el mapa, devuelve 0.
     *
     * @param c El carácter cuyo puntaje se desea obtener.
     * @return El puntaje del carácter si existe en el mapa del alfabeto; de lo contrario, 0.
     */
    public int getPuntaje(String c) {
        Map<String, Integer> alphabet = getAlphabet();
        if (alphabet == null) return 0;
        
        return alphabet.containsKey(c)? alphabet.get(c) : 0;
    }

    public Set<String> getAvailableEdges(String palabraParcial) {
        return dawg.getAvailableEdges(palabraParcial);
    }

    public boolean isFinal(String palabraParcial) {
        return dawg.isFinal(palabraParcial);
    }

    public boolean nodeExists(String palabraParcial) {
        return dawg.getNode(palabraParcial) != null;
    }



}