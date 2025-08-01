package scrabble.domain.persistences.implementaciones;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scrabble.domain.models.Diccionario;
import scrabble.domain.persistences.interfaces.RepositorioDiccionario;

/**
 * Implementación del repositorio de diccionarios con gestión completa de estructuras DAWG.
 * 
 * Gestiona la persistencia de diccionarios utilizando un enfoque híbrido: serialización Java
 * para un índice centralizado y almacenamiento directo de archivos de diccionario
 * ({@code alpha.txt}, {@code words.txt}). El índice de diccionarios ({@code diccionarios_index.dat})
 * mantiene una correspondencia entre el nombre del diccionario y la ruta al directorio
 * que contiene sus archivos.
 * 
 * Funcionalidades principales:
 * - Gestión de índice centralizado de diccionarios
 * - Validación de integridad de archivos de diccionario
 * - Operaciones CRUD completas para diccionarios
 * - Manejo automático de directorios y rutas
 * - Verificación de validez de estructuras DAWG
 * - Gestión robusta de errores y excepciones
 * 
 * La arquitectura permite escalabilidad y mantenimiento eficiente de múltiples
 * diccionarios con diferentes idiomas y configuraciones de alfabeto.
 * 
 * @version 2.0
 * @since 1.0
 */
public class RepositorioDiccionarioImpl implements RepositorioDiccionario {
    
    private static final String DICCIONARIOS_INDEX_FILE = "src/main/resources/persistencias/diccionarios_index.dat";
    
    /**
     * Constructor para la clase {@code RepositorioDiccionarioImpl}.
     * Asegura que el directorio de persistencia para los diccionarios existe.
     * Si el directorio no existe, se crea automáticamente.
     * 
     * @pre No hay precondiciones específicas.
     * @post Se ha creado una instancia de {@code RepositorioDiccionarioImpl} y
     *       se ha asegurado la existencia del directorio de persistencia base.
     */
    public RepositorioDiccionarioImpl() {
        // Asegurar que el directorio existe
        File directory = new File("src/main/resources/persistencias");
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
    
    /**
     * Crea el directorio "/Nombre" con alpha.txt y words.txt.
     *
     * @pre El nombre debe ser único y no nulo. Las listas de alfabeto y palabras no deben ser nulas.
     * @param nombre Nombre único del diccionario.
     * @param alfabeto Lista de strings con formato "letra frecuencia puntuacion".
     * @param palabras Lista de palabras del diccionario.
     * @return path de directorio del diccionario si se creó exitosamente, null en caso contrario.
     * @throws ExceptionDiccionarioOperacionFallida Si ocurre un error al crear los archivos o el directorio.
     * @post Si no ocurre ninguna excepción, se crea un nuevo directorio con los archivos alpha.txt y words.txt correctamente inicializados.
     */

    @Override
    public String crearDesdeTexto(String nombre, List<String> alfabeto, List<String> palabras) {
        try {
            // La capa de datos decide dónde y cómo almacenar
            String RESOURCE_BASE_PATH = "src/main/resources/diccionarios/";
            Path dictPath = Paths.get(RESOURCE_BASE_PATH, nombre);
            
            // Crear estructura de directorios
            Files.createDirectories(dictPath);
            
            // Crear alpha.txt
            Path alphaFile = dictPath.resolve("alpha.txt");
            Files.write(alphaFile, alfabeto, StandardCharsets.UTF_8);
            
            // Crear words.txt con ordenación
            List<String> palabrasOrdenadas = palabras.stream()
                    .map(String::toUpperCase)
                    .distinct()
                    .sorted()
                    .toList();
            Path wordsFile = dictPath.resolve("words.txt");
            Files.write(wordsFile, palabrasOrdenadas, StandardCharsets.UTF_8);
            
            return dictPath.toString();
            
        } catch (IOException e) {
            System.err.println("Error creando diccionario: " + e.getMessage());
            return null;
        }
    } 
   
    /**
     * Guarda la referència a un diccionari a l'índex de diccionaris.
     * Aquest mètode assumeix que els fitxers del diccionari ({@code alpha.txt}, {@code words.txt})
     * ja existeixen a la ruta especificada ({@code path}). Només s'actualitza l'índex.
     * 
     * @pre {@code nombre} no ha de ser nul ni buit.
     * @pre {@code diccionario} no ha de ser nul (tot i que no s'utilitza directament per guardar, 
     *      la seva presència suggereix que el diccionari és vàlid).
     * @pre {@code path} no ha de ser nul ni buit i ha de ser una ruta vàlida al directori del diccionari.
     * @param nombre El nom identificador del diccionari.
     * @param diccionario L'objecte {@link Diccionario} (actualment no serialitzat directament aquí).
     * @param path La ruta al directori on es troben els fitxers del diccionari.
     * @return {@code true} si la referència al diccionari s'ha guardat correctament a l'índex,
     *         {@code false} si s'ha produït un error.
     * @post L'índex de diccionaris s'actualitza amb la nova entrada si l'operació té èxit.
     */
    @Override
    public boolean guardar(String nombre, Diccionario diccionario, String path) {
        // Esta implementación asume que el diccionario ya ha sido guardado como archivos
        // en la ruta especificada y solo necesitamos guardar la referencia en el índice
        Map<String, String> diccionariosPaths = cargarIndice();
        diccionariosPaths.put(nombre, path);
        return guardarIndice(diccionariosPaths);
    }
    
    /**
     * Guarda el mapa complet de l'índex de diccionaris al sistema de persistència.
     * Serialitza l'objecte {@code Map<String, String>} que representa l'índex.
     * 
     * @pre {@code diccionariosPaths} no ha de ser nul.
     * @param diccionariosPaths El mapa que conté els noms dels diccionaris i les seves rutes.
     * @return {@code true} si l'índex s'ha guardat correctament, 
     *         {@code false} si s'ha produït un error.
     * @post El fitxer d'índex ({@code DICCIONARIOS_INDEX_FILE}) es crea o se sobreescriu
     *       amb el contingut de {@code diccionariosPaths}.
     */
    @Override
    public boolean guardarIndice(Map<String, String> diccionariosPaths) {
        try {
            // Asegurar que el directorio existe
            File indexDir = new File(DICCIONARIOS_INDEX_FILE).getParentFile();
            if (indexDir != null && !indexDir.exists()) {
                indexDir.mkdirs();
            }
            
            try (FileOutputStream fos = new FileOutputStream(DICCIONARIOS_INDEX_FILE);
                 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(diccionariosPaths);
                return true;
            }
        } catch (IOException e) {
            System.err.println("Error al guardar el índice de diccionarios: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Carrega l'índex de diccionaris des del sistema de persistència.
     * Deserialitza el mapa de noms de diccionari a rutes des del fitxer d'índex.
     * 
     * @pre No hi ha precondicions específiques.
     * @return Un {@code Map<String, String>} amb l'índex dels diccionarios. Si el fitxer
     *         d'índex no existeix o hi ha un error durant la càrrega, es retorna un mapa buit.
     * @post Es retorna el mapa de l'índex carregat o un mapa buit en cas d'error o inexistència.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, String> cargarIndice() {
        File indexFile = new File(DICCIONARIOS_INDEX_FILE);
        if (!indexFile.exists()) {
            return new HashMap<>(); // Retornar un mapa vacío si no existe el índice
        }
        
        try (FileInputStream fis = new FileInputStream(DICCIONARIOS_INDEX_FILE);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (Map<String, String>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al cargar el índice de diccionarios: " + e.getMessage());
            return new HashMap<>(); // Retornar un mapa vacío en caso de error
        }
    }
    
    /**
     * Carrega un diccionari específic a partir del seu nom.
     * Primer consulta l'índex per obtenir la ruta del diccionari, i després llegeix
     * els fitxers {@code alpha.txt} i {@code words.txt} des d'aquesta ruta.
     * 
     * @pre {@code nombre} no ha de ser nul ni buit.
     * @param nombre El nom del diccionari a carregar.
     * @return Un objecte {@link Diccionario} inicialitzat amb les dades llegides.
     *         Retorna {@code null} si el diccionari no es troba a l'índex.
     * @throws IOException Si hi ha un problema llegint els fitxers del diccionari 
     *                     (p.ex., fitxers no trobats, problemes de permisos).
     * @post Si té èxit, es retorna un objecte {@code Diccionario} complet.
     */
    @Override
    public Diccionario cargar(String nombre) throws IOException {
        Map<String, String> diccionariosPaths = cargarIndice();
        String path = diccionariosPaths.get(nombre);
        
        if (path == null) {
            return null; // No existe ese diccionario en el índice
        }
        
        // Cargar el diccionario desde los archivos
        Diccionario diccionario = new Diccionario();
        
        // Leer alpha.txt
        Path alphaPath = Paths.get(path, "alpha.txt");
        if (!Files.exists(alphaPath)) {
            throw new IOException("No se encuentra el archivo alpha.txt para el diccionario '" + nombre + "'.");
        }
        List<String> lineasAlpha = Files.readAllLines(alphaPath);
        diccionario.setAlphabet(lineasAlpha);
        
        // Leer words.txt
        Path wordsPath = Paths.get(path, "words.txt");
        if (!Files.exists(wordsPath)) {
            throw new IOException("No se encuentra el archivo words.txt para el diccionario '" + nombre + "'.");
        }
        List<String> palabras = Files.readAllLines(wordsPath);
        diccionario.setDawg(palabras);
        
        return diccionario;
    }
    
    /**
     * Elimina un diccionari del sistema de persistència.
     * Això implica eliminar la seva entrada de l'índex i esborrar el directori
     * i els fitxers associats ({@code alpha.txt}, {@code words.txt}).
     * 
     * @pre {@code nombre} no ha de ser nul ni buit.
     * @param nombre El nom del diccionari a eliminar.
     * @return {@code true} si el diccionari s'ha eliminat correctament (de l'índex i del sistema de fitxers),
     *         {@code false} si el diccionari no existia a l'índex o si hi ha hagut un error durant l'eliminació.
     * @post Si té èxit, l'entrada del diccionari s'elimina de l'índex i els seus fitxers
     *       són esborrats del sistema de fitxers.
     */
    @Override
    public boolean eliminar(String nombre) {
        Map<String, String> diccionariosPaths = cargarIndice();
        String path = diccionariosPaths.remove(nombre);
        
        if (path == null) {
            return false; // No existe ese diccionario en el índice
        }
        
        // Guardar el índice actualizado
        boolean indexGuardado = guardarIndice(diccionariosPaths);
        
        // Eliminar los archivos físicos
        try {
            Path dirPath = Paths.get(path);
            if (Files.exists(dirPath)) {
                Files.walk(dirPath)
                    .sorted(java.util.Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
                return indexGuardado;
            }
            return indexGuardado;
        } catch (IOException e) {
            System.err.println("Error al eliminar los archivos del diccionario: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verifica si un diccionari amb el nom especificat existeix a l'índex.
     * 
     * @pre {@code nombre} no ha de ser nul ni buit.
     * @param nombre El nom del diccionari a verificar.
     * @return {@code true} si el diccionari existeix a l'índex, {@code false} altrament.
     * @post Es retorna un booleà indicant l'existència del diccionari a l'índex.
     */
    @Override
    public boolean existe(String nombre) {
        Map<String, String> diccionariosPaths = cargarIndice();
        return diccionariosPaths.containsKey(nombre);
    }
    
    /**
     * Retorna una llista amb els noms de tots els diccionaris disponibles a l'índex.
     * 
     * @pre No hi ha precondicions específiques.
     * @return Una {@code List<String>} que conté els noms de tots els diccionaris registrats.
     *         Si no hi ha diccionaris, la llista serà buida.
     * @post Es retorna una llista (possiblement buida) de noms de diccionaris.
     */
    @Override
    public List<String> listarDiccionarios() {
        Map<String, String> diccionariosPaths = cargarIndice();
        return new ArrayList<>(diccionariosPaths.keySet());
    }
    
    /**
     * Verifica la validesa d'un diccionari comprovant l'existència dels seus fitxers 
     * (directori, {@code alpha.txt}, {@code words.txt}) a la ruta especificada a l'índex.
     * 
     * @pre {@code nombre} no ha de ser nul ni buit.
     * @param nombre El nom del diccionari a verificar.
     * @return {@code true} si el diccionari es considera vàlid (existeix a l'índex i 
     *         tots els seus fitxers necessaris existeixen), {@code false} altrament.
     * @post Es retorna un booleà indicant la validesa del diccionari.
     */
    @Override
    public boolean verificarDiccionarioValido(String nombre) {
        Map<String, String> diccionariosPaths = cargarIndice();
        String path = diccionariosPaths.get(nombre);
        
        if (path == null) {
            return false; // No existe ese diccionario en el índice
        }
        
        Path dirPath = Paths.get(path);
        Path alphaPath = dirPath.resolve("alpha.txt");
        Path wordsPath = dirPath.resolve("words.txt");
        
        return Files.exists(dirPath) && Files.isDirectory(dirPath) && 
               Files.exists(alphaPath) && Files.exists(wordsPath);
    }
}