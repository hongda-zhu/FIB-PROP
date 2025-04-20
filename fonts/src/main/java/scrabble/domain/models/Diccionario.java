package scrabble.domain.models;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Clase que implementa un diccionario de palabras para el juego de Scrabble.
 * Gestiona múltiples idiomas con sus respectivos DAWG (Directed Acyclic Word Graph),
 * alfabetos y configuraciones de fichas.
 */
public class Diccionario implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Mapa que almacena todos los DAWG disponibles, indexados por nombre de idioma.
     */
    private Map<String, Dawg> allDawgs;
    
    /**
     * Mapa que almacena los alfabetos con sus valores de puntos, indexados por nombre de idioma.
     */
    private Map<String, Map<String, Integer>> allAlphabets;
    
    /**
     * Mapa que almacena la configuración de frecuencia de fichas para cada idioma.
     */
    private Map<String, Map<String, Integer>> allBag;

    /**
     * Constructor por defecto que inicializa las estructuras de datos.
     */
    public Diccionario() {
        this.allDawgs = new HashMap<>();
        this.allAlphabets = new HashMap<>();
        this.allBag = new HashMap<>();
    }

    /**
     * Añade un nuevo DAWG (estructura de palabras) al diccionario.
     * 
     * @param name Nombre del idioma o conjunto de palabras
     * @param filePath Ruta del archivo que contiene las palabras. Si es null o vacío, permite entrada manual
     * @throws IOException Si hay problemas al leer el archivo
     */
    public void addDawg(String name, String filePath) throws IOException {
        Dawg dawg = new Dawg();
        if (filePath == null || filePath.isEmpty()) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Introduce palabras separadas por enter. Escribe 'FIN' para terminar:");
        
            List<String> palabras = new ArrayList<>();
            while (true) {
                String palabra = scanner.nextLine();
                if (palabra.equalsIgnoreCase("FIN")) {
                    break;
                }
                palabras.add(palabra);
            }
            inicializarDawg(dawg, palabras);
        } else {
            // Si se proporciona una ruta de archivo, inicializa el Dawg desde el archivo
            List<String> palabras = leerArchivoLineaPorLinea(filePath);
            inicializarDawg(dawg, palabras);
        }
        this.allDawgs.put(name, dawg);
    }

    /**
     * Lee un archivo línea por línea y devuelve su contenido como una lista de strings.
     * 
     * @param rutaArchivo Ruta del archivo a leer
     * @return Lista de líneas no vacías del archivo
     * @throws IOException Si hay problemas al leer el archivo
     */
    public List<String> leerArchivoLineaPorLinea(String rutaArchivo) throws IOException {
        List<String> lineas = new ArrayList<>();
        Path path = Paths.get(rutaArchivo);
        lineas = Files.readAllLines(path, StandardCharsets.UTF_8);
        return lineas.stream()
                .map(String::trim)
                .filter(linea -> !linea.isEmpty())
                .toList();
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
     * Añade un nuevo alfabeto al diccionario desde un archivo.
     * El archivo debe tener el formato: "letra frecuencia puntos" por línea.
     * 
     * @param name Nombre del idioma o conjunto de caracteres
     * @param rutaArchivo Ruta del archivo que contiene la configuración del alfabeto
     * @throws IOException Si hay problemas al leer el archivo
     */
    public void addAlphabet(String name, String rutaArchivo) throws IOException {
        if (this.allAlphabets == null) {
            this.allAlphabets = new HashMap<>();
        }
        Map<String, Integer> alphabet = new HashMap<>();
        Map<String, Integer> bag = new HashMap<>();

        List<String> lineas = leerArchivoLineaPorLinea(rutaArchivo);
        for (String linea : lineas) {
            String[] partes = linea.split(" ");
            if (partes.length == 3) {
                try {
                    String caracter = partes[0];
                    int frecuencia = Integer.parseInt(partes[1]); 
                    int puntos = Integer.parseInt(partes[2]);
                    
                    if (frecuencia < 0) {
                        throw new IllegalArgumentException("La frecuencia no puede ser negativa: " + linea);
                    }
                    
                    if (puntos < 0) {
                        throw new IllegalArgumentException("Los puntos no pueden ser negativos: " + linea);
                    }
                    
                    alphabet.put(caracter, puntos);
                    bag.put(caracter, frecuencia);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Formato incorrecto en línea: " + linea + ". Los valores de frecuencia y puntos deben ser números.");
                }                     
            }
            else {
                throw new IllegalArgumentException("Línea con formato incorrecto: " + linea + ". Formato esperado: LETRA FRECUENCIA PUNTOS");                
            }
        }
        
        if (alphabet.isEmpty()) {
            throw new IllegalArgumentException("El alfabeto no puede estar vacío. Debe especificar al menos una letra.");
        }
        
        this.allAlphabets.put(name, alphabet);
        this.allBag.put(name, bag);
    }

    /**
     * Elimina un DAWG del diccionario.
     * 
     * @param name Nombre del idioma o conjunto de palabras a eliminar
     */
    public void deleteDawg(String name) {
        if (this.allDawgs != null) {
            this.allDawgs.remove(name);
        }
    }

    /**
     * Elimina un alfabeto del diccionario.
     * 
     * @param name Nombre del idioma o conjunto de caracteres a eliminar
     */
    public void deleteAlphabet(String name) {
        if (this.allAlphabets != null) {
            this.allAlphabets.remove(name);
        }
    }

    /**
     * Obtiene el DAWG asociado a un idioma.
     * 
     * @param nombre Nombre del idioma
     * @return Estructura DAWG correspondiente o null si no existe
     */
    public Dawg getDawg(String nombre) {
        return this.allDawgs.get(nombre);
    }

    /**
     * Devuelve la lista de nombres de los diccionarios disponibles actualmente en el sistema.
     * 
     * @return Lista de nombres de diccionarios disponibles.
     */
    public List<String> getDiccionariosDisponibles() {
        return new ArrayList<>(allDawgs.keySet());
    }
    
    /**
     * Verifica si un DAWG existe en el diccionario.
     * 
     * @param nombre Nombre del idioma
     * @return true si el DAWG existe, false en caso contrario
     */
    public boolean existeDawg(String nombre) {
        return this.allDawgs.containsKey(nombre);
    }

    /**
     * Obtiene el mapa de puntos por letra para un idioma.
     * 
     * @param nombre Nombre del idioma
     * @return Mapa que asocia cada letra con su valor en puntos
     */
    public Map<String, Integer> getAlphabet(String nombre) {
        return this.allAlphabets.get(nombre);
    }

    /**
     * Obtiene el mapa de frecuencias de fichas para un idioma.
     * 
     * @param nombre Nombre del idioma
     * @return Mapa que asocia cada letra con su frecuencia de aparición en el juego
     */
    public Map<String, Integer> getBag(String nombre) {
        return this.allBag.get(nombre);
    }

    /**
     * Obtiene todos los DAWG disponibles en el diccionario.
     * 
     * @return Mapa con todos los DAWG indexados por nombre de idioma
     */
    public Map<String, Dawg> getAllDawgs() {
        return this.allDawgs;
    }

    /**
     * Obtiene todos los alfabetos disponibles en el diccionario.
     * 
     * @return Mapa con todos los alfabetos indexados por nombre de idioma
     */
    public Map<String, Map<String, Integer>> getAllAlphabets() {
        return this.allAlphabets;
    }
    
    /**
     * Verifica si una palabra existe en el diccionario.
     * 
     * @param palabra Palabra a verificar
     * @param idioma Nombre del idioma o diccionario
     * @return true si la palabra existe, false en caso contrario
     */
    public boolean contienePalabra(String palabra, String idioma) {
        Dawg dawg = getDawg(idioma);
        if (dawg == null) return false;
        return dawg.search(palabra.toUpperCase());
    }
    
    /**
     * Obtiene los caracteres del alfabeto para un idioma específico.
     * 
     * @param idioma Nombre del idioma
     * @return Conjunto de caracteres válidos del alfabeto
     */
    public Set<Character> getAlphabetChars(String idioma) {
        Map<String, Integer> alphabet = getAlphabet(idioma);
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
    private boolean isValidWordSyntax(String palabra, Set<Character> validChars) {
        for (char c : palabra.toCharArray()) {
            if (!validChars.contains(c)) {
                return false;
            }
        }
        return true;
    }
}