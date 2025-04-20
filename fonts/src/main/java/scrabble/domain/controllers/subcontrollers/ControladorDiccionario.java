package scrabble.domain.controllers.subcontrollers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import scrabble.domain.models.Diccionario;
import scrabble.excepciones.*;

/**
 * Controlador para la gestión de diccionarios en el juego de Scrabble.
 * Implementa el patrón Singleton para garantizar una única instancia.
 */
public class ControladorDiccionario {
    private static ControladorDiccionario instance;
    private Map<String, Diccionario> diccionarios;
    private Map<String, String> diccionarioPaths;
    
    /**
     * Constructor privado para implementar el patrón Singleton.
     * Inicializa los mapas de diccionarios y paths.
     */
    private ControladorDiccionario() {
        this.diccionarios = new HashMap<>();
        this.diccionarioPaths = new HashMap<>();
        // En el futuro, se podría implementar cargarDatos() aquí
    }
    
    /**
     * Obtiene la instancia única del controlador (Singleton).
     * 
     * @return Instancia de ControladorDiccionario
     */
    public static synchronized ControladorDiccionario getInstance() {
        if (instance == null) {
            instance = new ControladorDiccionario();
        }
        return instance;
    }
    
    /**
     * Crea o carga un diccionario en memoria desde el path especificado.
     * 
     * @param nombre Nombre identificador del diccionario
     * @param path Ruta al directorio del diccionario
     * @throws ExceptionDiccionarioExist Si ya existe un diccionario con ese nombre
     * @throws IOException Si hay problemas con la lectura/escritura de archivos
     */
    public void crearDiccionario(String nombre, String path) throws ExceptionDiccionarioExist, IOException {
        if (diccionarios.containsKey(nombre)) {
            throw new ExceptionDiccionarioExist("Ya existe un diccionario con el nombre: " + nombre);
        }
        
        try {
            // Verificar que el directorio existe
            Path dirPath = Paths.get(path);
            if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
                throw new IOException("La ruta indicada no existe o no es un directorio: " + path);
            }
            
            // Verificar que existen alpha.txt y words.txt
            Path alphaPath = dirPath.resolve("alpha.txt");
            Path wordsPath = dirPath.resolve("words.txt");
            
            if (!Files.exists(alphaPath)) {
                throw new IOException("No se encuentra el archivo alpha.txt en el directorio del diccionario");
            }
            
            if (!Files.exists(wordsPath)) {
                throw new IOException("No se encuentra el archivo words.txt en el directorio del diccionario");
            }
            
            // Crear el diccionario
            Diccionario dict = new Diccionario();
            dict.addAlphabet(nombre, alphaPath.toString());
            dict.addDawg(nombre, wordsPath.toString());
            
            // Guardar el diccionario y su path
            diccionarios.put(nombre, dict);
            diccionarioPaths.put(nombre, path);
            
            System.out.println("Diccionario '" + nombre + "' creado correctamente.");
        } catch (IOException e) {
            System.err.println("Error al crear el diccionario: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Crea un diccionario a partir de las rutas específicas de los archivos de alfabeto y palabras.
     * Este método es utilizado principalmente por ControladorJuego.
     * 
     * @param nombre Nombre identificador del diccionario
     * @param rutaArchivoAlpha Ruta al archivo de alfabeto
     * @param rutaArchivoWords Ruta al archivo de palabras
     * @throws ExceptionDiccionarioExist Si ya existe un diccionario con ese nombre
     * @throws IOException Si hay problemas con la lectura/escritura de archivos
     */
    public void crearDiccionario(String nombre, String rutaArchivoAlpha, String rutaArchivoWords) throws ExceptionDiccionarioExist, IOException {
        if (diccionarios.containsKey(nombre)) {
            throw new ExceptionDiccionarioExist("Ya existe un diccionario con el nombre: " + nombre);
        }
        
        try {
            // Verificar que los archivos existen
            Path alphaPath = Paths.get(rutaArchivoAlpha);
            Path wordsPath = Paths.get(rutaArchivoWords);
            
            if (!Files.exists(alphaPath)) {
                throw new IOException("No se encuentra el archivo de alfabeto en: " + rutaArchivoAlpha);
            }
            
            if (!Files.exists(wordsPath)) {
                throw new IOException("No se encuentra el archivo de palabras en: " + rutaArchivoWords);
            }
            
            // Crear el diccionario
            Diccionario dict = new Diccionario();
            dict.addAlphabet(nombre, rutaArchivoAlpha);
            dict.addDawg(nombre, wordsPath.toString());
            
            // Guardar el diccionario
            diccionarios.put(nombre, dict);
            
            // Obtener el directorio padre de la ruta del archivo
            String parentDir = alphaPath.getParent() != null ? 
                               alphaPath.getParent().toString() : 
                               ".";
            
            diccionarioPaths.put(nombre, parentDir);
            
            System.out.println("Diccionario '" + nombre + "' creado correctamente.");
        } catch (IOException e) {
            System.err.println("Error al crear el diccionario: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Elimina un diccionario de memoria y su directorio asociado.
     * 
     * @param nombre Nombre del diccionario a eliminar
     * @throws ExceptionDiccionarioNotExist Si el diccionario no existe
     * @throws IOException Si hay problemas eliminando los archivos
     */
    public void eliminarDiccionario(String nombre) throws ExceptionDiccionarioNotExist, IOException {
        if (!diccionarios.containsKey(nombre)) {
            throw new ExceptionDiccionarioNotExist("No existe un diccionario con el nombre: " + nombre);
        }
        
        String path = diccionarioPaths.get(nombre);
        if (path == null) {
            // Si hay inconsistencia interna
            diccionarios.remove(nombre);
            throw new ExceptionDiccionarioNotExist("Error interno: Path no encontrado para el diccionario '" + nombre + "'. Eliminado de memoria.");
        }
        
        // Eliminar de memoria
        diccionarios.remove(nombre);
        diccionarioPaths.remove(nombre);
        
        // Eliminar archivos
        Path dirPath = Paths.get(path);
        if (Files.exists(dirPath)) {
            try {
                Files.walk(dirPath)
                    .sorted(Comparator.reverseOrder()) // Importante para borrar contenido antes que directorios
                    .map(Path::toFile)
                    .forEach(java.io.File::delete);
                
                System.out.println("Diccionario '" + nombre + "' y sus archivos eliminados correctamente.");
            } catch (IOException e) {
                System.err.println("Error al eliminar los archivos del diccionario: " + e.getMessage());
                throw e;
            }
        } else {
            System.out.println("El directorio del diccionario ya no existe en la ruta esperada: " + path);
        }
    }
    
    /**
     * Verifica si existe un diccionario con el nombre especificado.
     * 
     * @param nombre Nombre del diccionario
     * @return true si existe, false en caso contrario
     */
    public boolean existeDiccionario(String nombre) {
        return diccionarios.containsKey(nombre);
    }
    
    /**
     * Obtiene el diccionario con el nombre especificado.
     * 
     * @param nombre Nombre del diccionario
     * @return El objeto Diccionario
     * @throws ExceptionDiccionarioNotExist Si el diccionario no existe
     */
    public Diccionario getDiccionario(String nombre) throws ExceptionDiccionarioNotExist {
        if (!diccionarios.containsKey(nombre)) {
            throw new ExceptionDiccionarioNotExist("No existe un diccionario con el nombre: " + nombre);
        }
        return diccionarios.get(nombre);
    }
    
    /**
     * Obtiene la lista de nombres de diccionarios disponibles.
     * 
     * @return Lista de nombres de diccionarios
     */
    public List<String> getDiccionariosDisponibles() {
        return new ArrayList<>(diccionarios.keySet());
    }
    
    /**
     * Modifica un diccionario añadiendo o eliminando una palabra.
     * 
     * @param nombre Nombre del diccionario
     * @param palabra Palabra a añadir o eliminar
     * @param anadir true para añadir, false para eliminar
     * @throws ExceptionDiccionarioNotExist Si el diccionario no existe
     * @throws ExceptionPalabraVacia Si la palabra está vacía
     * @throws ExceptionPalabraInvalida Si la palabra contiene caracteres no válidos
     * @throws ExceptionPalabraExist Si la palabra ya existe (al añadir)
     * @throws ExceptionPalabraNotExist Si la palabra no existe (al eliminar)
     * @throws IOException Si hay problemas con la lectura/escritura de archivos
     */
    public void modificarPalabraDiccionario(String nombre, String palabra, boolean anadir)
            throws ExceptionDiccionarioNotExist, ExceptionPalabraVacia, ExceptionPalabraInvalida, 
                  ExceptionPalabraExist, ExceptionPalabraNotExist, IOException {
        
        // Normalizar palabra
        palabra = palabra.trim().toUpperCase();
        if (palabra.isEmpty()) {
            throw new ExceptionPalabraVacia("No se puede procesar una palabra vacía.");
        }
        
        // Obtener diccionario y su path
        if (!diccionarios.containsKey(nombre)) {
            throw new ExceptionDiccionarioNotExist("No existe un diccionario con el nombre: " + nombre);
        }
        
        String path = diccionarioPaths.get(nombre);
        if (path == null) {
            throw new IllegalStateException("Error interno: Path no encontrado para el diccionario '" + nombre + "'.");
        }
        
        Path wordsPath = Paths.get(path, "words.txt");
        if (!Files.exists(wordsPath)) {
            throw new IOException("No se encuentra el archivo words.txt para el diccionario '" + nombre + "'.");
        }
        
        // Validar sintaxis si es añadir
        if (anadir) {
            // Aquí iría la validación contra alpha.txt
            Set<Character> validChars = getAlphabetChars(nombre);
            if (!isValidWordSyntax(palabra, validChars)) {
                throw new ExceptionPalabraInvalida("La palabra '" + palabra + "' contiene caracteres no válidos para el diccionario '" + nombre + "'.");
            }
        }
        
        // Leer words.txt
        List<String> words = Files.readAllLines(wordsPath, StandardCharsets.UTF_8);
        Set<String> wordSet = new HashSet<>(words);
        
        boolean modificado = false;
        if (anadir) {
            modificado = wordSet.add(palabra);
            if (!modificado) {
                throw new ExceptionPalabraExist("La palabra '" + palabra + "' ya existe en el diccionario.");
            }
        } else {
            modificado = wordSet.remove(palabra);
            if (!modificado) {
                throw new ExceptionPalabraNotExist("La palabra '" + palabra + "' no existe en el diccionario.");
            }
        }
        
        // Si hubo modificación, actualizar archivo y DAWG
        List<String> wordList = new ArrayList<>(wordSet);
        Collections.sort(wordList); // Ordenar para mantener orden lexicográfico
        
        // Escribir al archivo
        Files.write(wordsPath, wordList, StandardCharsets.UTF_8);
        
        // Actualizar el DAWG
        Diccionario dict = diccionarios.get(nombre);
        dict.deleteDawg(nombre);
        dict.addDawg(nombre, wordsPath.toString());
        
        System.out.println("Diccionario '" + nombre + "' modificado correctamente.");
    }
    
    /**
     * Obtiene los caracteres válidos del alfabeto de un diccionario.
     * 
     * @param nombre Nombre del diccionario
     * @return Conjunto de caracteres válidos
     * @throws ExceptionDiccionarioNotExist Si el diccionario no existe
     * @throws IOException Si hay problemas leyendo alpha.txt
     */
    private Set<Character> getAlphabetChars(String nombre) throws ExceptionDiccionarioNotExist, IOException {
        if (!diccionarios.containsKey(nombre)) {
            throw new ExceptionDiccionarioNotExist("No existe un diccionario con el nombre: " + nombre);
        }
        
        String path = diccionarioPaths.get(nombre);
        Path alphaPath = Paths.get(path, "alpha.txt");
        
        Set<Character> chars = new HashSet<>();
        List<String> lines = Files.readAllLines(alphaPath, StandardCharsets.UTF_8);
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            
            String[] parts = line.split("\\s+", 2);
            if (parts.length > 0 && !parts[0].isEmpty()) {
                for (char c : parts[0].toUpperCase().toCharArray()) {
                    chars.add(c);
                }
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