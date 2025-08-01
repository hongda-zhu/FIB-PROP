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
 * 
 * Esta clase encapsula toda la funcionalidad relacionada con el manejo de diccionarios,
 * incluyendo la estructura DAWG para almacenamiento eficiente de palabras, el alfabeto
 * con valores de puntuación, la configuración de fichas y la gestión de comodines.
 * Proporciona métodos para validación de palabras, consulta de estadísticas y
 * manipulación del contenido del diccionario.
 * 
 * 
 * @version 2.0
 * @since 1.0
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
     * Crea un diccionario vacío listo para ser configurado con palabras, alfabeto y fichas.
     * 
     * @pre No hay precondiciones específicas.
     * @post Se crea una nueva instancia de Diccionario con las estructuras DAWG, alphabet, bag y comodines vacías.
     *       Todas las estructuras están inicializadas y listas para recibir datos.
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
     * @pre La lista de palabras no debe ser null.
     * @param palabras Lista de palabras para insertar en el DAWG
     * @post Se crea e inicializa un nuevo DAWG con las palabras especificadas.
     * @throws NullPointerException si la lista de palabras es null
     * 
     * @apiNote Este método está diseñado principalmente para uso interno del controlador.
     *          Para manipular palabras individuales, considere usar los métodos
     *          {@link #addWord(String)} y {@link #removeWord(String)}
     */
    public void setDawg(List<String> palabras) {
        if (palabras == null) {
            throw new NullPointerException("La lista de palabras no puede ser null");
        }
        
        Dawg newDawg = new Dawg(this.alphabet.keySet());
        inicializarDawg(newDawg, palabras);
        this.dawg = newDawg;
    }

    /**
     * Inicializa un DAWG con una lista de palabras.
     * 
     * @pre El DAWG y la lista de palabras no deben ser null.
     * @param dawg Estructura DAWG a inicializar
     * @param palabras Lista de palabras para insertar en el DAWG
     * @post El DAWG proporcionado contiene todas las palabras especificadas.
     * @throws NullPointerException si dawg o palabras son null
     */
    public void inicializarDawg(Dawg dawg, List<String> palabras) {
        if (dawg == null) {
            throw new NullPointerException("El DAWG no puede ser null");
        }
        
        if (palabras == null) {
            throw new NullPointerException("La lista de palabras no puede ser null");
        }
        
        for (String palabra : palabras) {
            dawg.insert(palabra);
        }

        dawg.finish(); // Finaliza la construcción del DAWG
    }

    /**
     * Configura el alfabeto y la bolsa del diccionario a partir de líneas de texto.
     * Cada línea debe tener el formato: "letra frecuencia puntos"
     * Los comodines se identifican con símbolos especiales (ej: "#") y tienen valor 0.
     * 
     * @pre La lista de líneas no debe ser null.
     * @param lineas Lista de líneas con el formato indicado
     * @post Se inicializan el alfabeto, la bolsa y los comodines según las líneas proporcionadas.
     * @throws NullPointerException si la lista de líneas es null
     * @throws IllegalArgumentException Si hay problemas con el formato de las líneas
     */
    public void setAlphabet(List<String> lineas) {
        if (lineas == null) {
            throw new NullPointerException("La lista de líneas no puede ser null");
        }
        
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
                        throw new IllegalArgumentException("La frecuencia no puede menor que 1: " + linea);
                    }
                    
                    if (puntos < 0) {
                        throw new IllegalArgumentException("Los puntos no pueden ser negativos: " + linea);
                    }
                    
                    // Si el valor es 0, podría ser un comodín
                    if (puntos == 0 && caracter.equals("#")) {
                        newComodines.add(caracter);
                    }
                    
                    newAlphabet.put(caracter, puntos);
                    newBag.put(caracter, frecuencia);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Formato incorrecto en línea: " + linea + ". Los valores de frecuencia y puntos deben ser números.");
                }                     
            }
            else {
                throw new IllegalArgumentException("Línea con formato incorrecto: " + linea + ". Formato esperado: LETRA FRECUENCIA PUNTOS");                
            }
        }
        
        if (newAlphabet.isEmpty()) {
            throw new IllegalArgumentException("El alfabeto no puede estar vacío. Debe especificar al menos una letra.");
        }
        
        this.alphabet = newAlphabet;
        this.bag = newBag;
        this.comodines = newComodines;
    }

    /**
     * Obtiene el DAWG del diccionario.
     * 
     * @pre No hay precondiciones específicas.
     * @return Estructura DAWG
     * @post Se devuelve la estructura DAWG actual del diccionario, puede ser una estructura vacía pero nunca null.
     */
    public Dawg getDawg() {
        return this.dawg;
    }
    
    /**
     * Obtiene el mapa de puntos por letra del alfabeto.
     * 
     * @pre No hay precondiciones específicas.
     * @return Mapa que asocia cada letra con su valor en puntos
     * @post Se devuelve el mapa de alfabeto actual, puede ser un mapa vacío pero nunca null.
     */
    public Map<String, Integer> getAlphabet() {
        return this.alphabet;
    }

    /**
     * Obtiene el mapa de frecuencias de fichas.
     * 
     * @pre No hay precondiciones específicas.
     * @return Mapa que asocia cada letra con su frecuencia de aparición en el juego
     * @post Se devuelve el mapa de frecuencias actual, puede ser un mapa vacío pero nunca null.
     */
    public Map<String, Integer> getBag() {
        return this.bag;
    }
    
    /**
     * Obtiene el conjunto de caracteres comodín.
     * 
     * @pre No hay precondiciones específicas.
     * @return Conjunto de caracteres comodín
     * @post Se devuelve el conjunto de comodines actual, puede ser un conjunto vacío pero nunca null.
     */
    public Set<String> getComodines() {
        return this.comodines;
    }
    
    /**
     * Verifica si un carácter es un comodín.
     * 
     * @pre El carácter no debe ser null.
     * @param caracter Carácter a verificar
     * @return true si es un comodín, false en caso contrario
     * @post Se devuelve true si el carácter está en el conjunto de comodines, false en caso contrario.
     * @throws NullPointerException si caracter es null
     */
    public boolean esComodin(String caracter) {
        if (caracter == null) {
            throw new NullPointerException("El carácter no puede ser null");
        }
        return this.comodines.contains(caracter);
    }
    
    /**
     * Verifica si una palabra existe en el diccionario.
     * 
     * @pre La palabra no debe ser null.
     * @param palabra Palabra a verificar
     * @return true si la palabra existe, false en caso contrario
     * @post Se devuelve true si la palabra existe en el DAWG, false en caso contrario.
     * @throws NullPointerException si palabra es null
     */
    public boolean contienePalabra(String palabra) {
        if (palabra == null) {
            throw new NullPointerException("La palabra no puede ser null");
        }
        if (dawg == null) return false;
        return dawg.search(palabra.toUpperCase());
    }
    
    /**
     * Obtiene los caracteres del alfabeto.
     * 
     * @pre No hay precondiciones específicas.
     * @return Conjunto de caracteres válidos del alfabeto
     * @post Se devuelve un conjunto con todos los caracteres individuales presentes en las claves del alfabeto.
     *       Si el alfabeto es null, se devuelve un conjunto vacío.
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
     * Obtiene las claves (tokens) completas del alfabeto.
     * A diferencia de getAlphabetChars(), este método preserva tokens multicarácter como "CH" o "RR".
     * 
     * @pre No hay precondiciones específicas.
     * @return Conjunto de tokens/claves del alfabeto (como A, B, CH, RR, etc.)
     * @post Se devuelve un conjunto con todas las claves presentes en el alfabeto.
     *       Si el alfabeto es null, se devuelve un conjunto vacío.
     */
    public Set<String> getAlphabetKeys() {
        if (alphabet == null) return new HashSet<>();
        return new HashSet<>(alphabet.keySet());
    }

    /**
     * Verifica si una palabra contiene solo caracteres válidos.
     * 
     * @pre No hay precondiciones específicas fuertes.
     * @param palabra Palabra a verificar
     * @param validChars Conjunto de caracteres válidos
     * @return true si la palabra es válida, false en caso contrario
     * @post Se devuelve true si la palabra no es vacía y contiene solo caracteres válidos, false en caso contrario.
     */
    public boolean isValidWordSyntax(String palabra, Set<Character> validChars) {
        if (palabra == null || validChars == null) {
            return false;
        }
        
        if (palabra.isEmpty()) {
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
     * @pre No hay precondiciones específicas.
     * @return Mapa que asocia cada letra con su frecuencia de aparición en el juego
     * @post Se devuelve el mapa de frecuencias actual del diccionario, puede ser null si no hay bag configurado.
     */
    public Map<String, Integer> getFichas() {
        Map<String, Integer> bag = getBag();
        if (bag == null) return null;
        
        return bag;
    }

    /**
     * Obtiene el puntaje asociado a un carácter dado.
     *
     * @pre No hay precondiciones específicas fuertes.
     * @param c El carácter cuyo puntaje se desea obtener.
     * @return El puntaje del carácter si existe en el mapa del alfabeto; de lo contrario, 0.
     * @post Se devuelve el valor de puntos asociado al carácter o 0 si el carácter es null, no existe en el alfabeto, o el alfabeto es null.
     */
    public int getPuntaje(String c) {
        if (c == null) {
            return 0;
        }
        
        Map<String, Integer> alphabet = getAlphabet();
        if (alphabet == null) return 0;
        
        return alphabet.containsKey(c) ? alphabet.get(c) : 0;
    }

    /**
     * Obtiene las aristas (letras) disponibles a partir de una palabra parcial en el DAWG.
     *
     * @pre La palabra parcial no debe ser null.
     * @param palabraParcial La palabra parcial para la que se buscan letras disponibles.
     * @return Conjunto de letras (aristas) que pueden continuar la palabra parcial.
     * @post Se devuelve un conjunto con todas las letras que pueden continuar la palabra parcial en el DAWG.
     *       Si la palabra parcial no existe en el DAWG, se devuelve un conjunto vacío.
     * @throws NullPointerException si palabraParcial es null
     */
    public Set<String> getAvailableEdges(String palabraParcial) {
        if (palabraParcial == null) {
            throw new NullPointerException("La palabra parcial no puede ser null");
        }
        
        return dawg.getAvailableEdges(palabraParcial);
    }

    /**
     * Verifica si una palabra parcial forma una palabra completa en el diccionario.
     *
     * @pre La palabra parcial no debe ser null.
     * @param palabraParcial La palabra parcial a verificar.
     * @return true si la palabra parcial forma una palabra completa, false en caso contrario.
     * @post Se devuelve true si la palabra parcial forma una palabra completa (es un nodo final en el DAWG), false en caso contrario.
     * @throws NullPointerException si palabraParcial es null
     */
    public boolean isFinal(String palabraParcial) {
        if (palabraParcial == null) {
            throw new NullPointerException("La palabra parcial no puede ser null");
        }
        
        return dawg.isFinal(palabraParcial);
    }

    /**
     * Verifica si existe un nodo para una palabra parcial en el DAWG.
     *
     * @pre La palabra parcial no debe ser null.
     * @param palabraParcial La palabra parcial a verificar.
     * @return true si existe un nodo para la palabra parcial, false en caso contrario.
     * @post Se devuelve true si existe un nodo para la palabra parcial en el DAWG, false en caso contrario.
     * @throws NullPointerException si palabraParcial es null
     */
    public boolean nodeExists(String palabraParcial) {
        if (palabraParcial == null) {
            throw new NullPointerException("La palabra parcial no puede ser null");
        }
        
        return dawg.getNode(palabraParcial) != null;
    }

    /**
     * Añade una palabra al diccionario.
     * 
     * @pre La palabra no debe ser null o vacía.
     * @param palabra Palabra a añadir
     * @return true si la palabra fue añadida, false si ya existía
     * @post La palabra se añade al DAWG si no existía previamente.
     * @throws NullPointerException si palabra es null
     * @throws IllegalArgumentException si palabra está vacía
     */
    public boolean addWord(String palabra) {
        if (palabra == null) {
            throw new NullPointerException("La palabra no puede ser null");
        }
        
        palabra = palabra.trim().toUpperCase();
        if (palabra.isEmpty()) {
            throw new IllegalArgumentException("La palabra no puede estar vacía");
        }
        
        if (contienePalabra(palabra)) {
            return false; // La palabra ya existe
        }
        
        dawg.insert(palabra);
        return true;
    }

    /**
     * Elimina una palabra del diccionario.
     * 
     * @pre La palabra no debe ser null o vacía.
     * @param palabra Palabra a eliminar
     * @return true si la palabra fue eliminada, false si no existía
     * @post La palabra se elimina del DAWG si existía previamente.
     * @throws NullPointerException si palabra es null
     * @throws IllegalArgumentException si palabra está vacía
     * 
     * @apiNote Esta operación puede ser costosa ya que requiere reconstruir el DAWG.
     */
    public boolean removeWord(String palabra) {
        if (palabra == null) {
            throw new NullPointerException("La palabra no puede ser null");
        }
        
        palabra = palabra.trim().toUpperCase();
        if (palabra.isEmpty()) {
            throw new IllegalArgumentException("La palabra no puede estar vacía");
        }
        
        if (!contienePalabra(palabra)) {
            return false; // La palabra no existe
        }
        
        // Para eliminar una palabra necesitamos reconstruir el DAWG
        // Obtenemos todas las palabras excepto la que queremos eliminar
        List<String> palabras = dawg.getAllWords();
        palabras.remove(palabra);
        
        // Reconstruimos el DAWG
        Dawg newDawg = new Dawg(this.alphabet.keySet());
        inicializarDawg(newDawg, palabras);
        this.dawg = newDawg;
        
        return true;
    }
}