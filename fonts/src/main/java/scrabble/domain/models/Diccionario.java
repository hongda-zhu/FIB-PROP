package scrabble.domain.models;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Clase que implementa un diccionario de palabras para el juego de Scrabble.
 * Gestiona múltiples idiomas con sus respectivos DAWG (Directed Acyclic Word Graph),
 * alfabetos y configuraciones de fichas.
 */
public class Diccionario {

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
     */
    public void addDawg(String name, String filePath) {

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
     */
    public List<String> leerArchivoLineaPorLinea(String rutaArchivo) {
        List<String> lineas = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(rutaArchivo))) {
            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine().trim();
                if (!linea.isEmpty()) { 
                    lineas.add(linea);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
        return lineas;
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
     */
    public void addAlphabet(String name, String rutaArchivo) {

        if (this.allAlphabets == null) {
            this.allAlphabets = new HashMap<>();
        }
        Map<String, Integer> alphabet = new HashMap<>();
        Map<String, Integer> bag = new HashMap<>();

        List<String> lineas = leerArchivoLineaPorLinea(rutaArchivo);
        for (String linea : lineas) {
                String[] partes = linea.split(" ");
                if (partes.length == 3) {
                    String caracter = partes[0];
                    int frecuencia = Integer.parseInt(partes[1]); 
                    int puntos = Integer.parseInt(partes[2]);
                    alphabet.put(caracter, puntos);
                    bag.put(caracter, frecuencia);                     
                }
                else {
                    System.out.println("Línea con formato incorrecto: " + linea);                
                }
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

}
