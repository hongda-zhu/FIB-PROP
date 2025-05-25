package scrabble.domain.controllers.subcontrollers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import scrabble.domain.models.Diccionario;
import scrabble.domain.persistences.implementaciones.RepositorioDiccionarioImpl;
import scrabble.domain.persistences.interfaces.RepositorioDiccionario;
import scrabble.excepciones.ExceptionDiccionarioExist;
import scrabble.excepciones.ExceptionDiccionarioNotExist;
import scrabble.excepciones.ExceptionDiccionarioOperacionFallida;
import scrabble.excepciones.ExceptionLoggingOperacion;
import scrabble.excepciones.ExceptionPalabraExist;
import scrabble.excepciones.ExceptionPalabraInvalida;
import scrabble.excepciones.ExceptionPalabraNotExist;
import scrabble.excepciones.ExceptionPalabraVacia;

/**
 * Controlador para la gestión de diccionarios en el juego de Scrabble.
 * Implementa el patrón Singleton para garantizar una única instancia.
 * Este controlador es responsable de la lectura/escritura de archivos relacionados con diccionarios.
 * 
 * Gestiona la creación, carga, modificación y eliminación de diccionarios, incluyendo
 * la validación de palabras y alfabetos, el manejo de estructuras DAWG para búsquedas
 * eficientes, y la persistencia de datos mediante el patrón Repository. Proporciona
 * métodos para consultar palabras, obtener estadísticas de alfabetos y gestionar
 * la integridad de los diccionarios del sistema.
 * 
 * 
 * @version 2.0
 * @since 1.0
 */
public class ControladorDiccionario {
    private static ControladorDiccionario instance;
    private Map<String, Diccionario> diccionarios;
    private Map<String, String> diccionarioPaths;
    private RepositorioDiccionario repositorio;
    
    /**
     * Constructor privado para implementar el patrón Singleton.
     * Inicializa los mapas de diccionarios y paths, y verifica los diccionarios existentes.
     * @throws ExceptionPersistenciaFallida Si ocurre un error al inicializar el repositorio de diccionarios o al verificar/cargar diccionarios existentes.
     *
     * @pre No hay precondiciones específicas.
     * @post Se inicializa una nueva instancia con mapas vacíos y se cargan o verifican los diccionarios persistidos.
     */
    private ControladorDiccionario() {
        this.diccionarios = new HashMap<>();
        this.diccionarioPaths = new HashMap<>();
        this.repositorio = new RepositorioDiccionarioImpl();
        // Verificar diccionarios existentes al inicializar
        verificarTodosDiccionarios();
    }
    
    /**
     * Obtiene la instancia única del controlador (Singleton).
     * 
     * @pre No hay precondiciones específicas.
     * @return Instancia de ControladorDiccionario
     * @post Se devuelve la única instancia de ControladorDiccionario que existe en la aplicación.
     */
    public static synchronized ControladorDiccionario getInstance() {
        if (instance == null) {
            instance = new ControladorDiccionario();
        }
        return instance;
    }
    
    /**
     * Lee un archivo línea por línea y devuelve su contenido como una lista de strings.
     * 
     * @param rutaArchivo Ruta del archivo a leer
     * @return Lista de líneas no vacías del archivo
     * @throws IOException Si hay problemas al leer el archivo
     */
    private List<String> leerArchivoLineaPorLinea(String rutaArchivo) throws IOException {
        List<String> lineas = new ArrayList<>();
        Path path = Paths.get(rutaArchivo);
        lineas = Files.readAllLines(path, StandardCharsets.UTF_8);
        return lineas.stream()
                .map(String::trim)
                .filter(linea -> !linea.isEmpty())
                .toList();
    }
    
    /**
     * Crea o carga un diccionario en memoria desde el path especificado.
     * 
     * @pre El path debe corresponder a un directorio existente que contenga los archivos alpha.txt y words.txt, y el nombre del diccionario no debe existir previamente.
     * @param nombre Nombre identificador del diccionario
     * @param path Ruta al directorio del diccionario
     * @throws ExceptionDiccionarioExist Si ya existe un diccionario con ese nombre.
     * @throws IOException Si hay problemas con la lectura/escritura de archivos (directorios/archivos no encontrados, permisos, etc.).
     * @throws ExceptionPalabraInvalida Si alguna palabra en words.txt contiene caracteres no válidos según alpha.txt.
     * @throws ExceptionDiccionarioOperacionFallida Si ocurre algún error durante la creación o al guardar en el repositorio.
     * @throws NullPointerException Si alguno de los parámetros es null.
     * @post Si no ocurre ninguna excepción, un nuevo diccionario es creado, validado, cargado en memoria y guardado persistentemente.
     */
    public void crearDiccionario(String nombre, String path) throws ExceptionDiccionarioExist, IOException, ExceptionPalabraInvalida, ExceptionDiccionarioOperacionFallida {
        if (repositorio.existe(nombre) || diccionarios.containsKey(nombre)) {
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
            
            // Leer archivos
            List<String> lineasAlpha = leerArchivoLineaPorLinea(alphaPath.toString());
            List<String> palabras = leerArchivoLineaPorLinea(wordsPath.toString());
            
            // Crear el diccionario
            Diccionario dict = new Diccionario();
            dict.setAlphabet(lineasAlpha);
            
            // Validar que todas las palabras en words.txt contienen solo caracteres del alfabeto
            Set<Character> validChars = getAlphabetChars(alphaPath);
            
            List<String> palabrasInvalidas = new ArrayList<>();
            for (String palabra : palabras) {
                palabra = palabra.trim().toUpperCase();
                if (!palabra.isEmpty() && !isValidWordSyntax(palabra, validChars)) {
                    palabrasInvalidas.add(palabra);
                }
            }
            
            if (!palabrasInvalidas.isEmpty()) {
                throw new ExceptionPalabraInvalida("El archivo words.txt contiene palabras con caracteres no definidos en alpha.txt: " + 
                                                  String.join(", ", palabrasInvalidas.subList(0, Math.min(10, palabrasInvalidas.size()))) + 
                                                  (palabrasInvalidas.size() > 10 ? "... y " + (palabrasInvalidas.size() - 10) + " más" : ""));
            }
            
            // Si todas las palabras son válidas, añadir el DAWG
            dict.setDawg(palabras);
            
            // Guardar el diccionario y su path en memoria
            diccionarios.put(nombre, dict);
            diccionarioPaths.put(nombre, path);
            
            // Guardar en el repositorio
            boolean guardado = repositorio.guardar(nombre, dict, path);
            if (!guardado) {
                // Si falla, eliminar de memoria
                diccionarios.remove(nombre);
                diccionarioPaths.remove(nombre);
                throw new ExceptionDiccionarioOperacionFallida("Error al guardar el diccionario en el repositorio", "creación");
            }
            
            // Mensaje informativo movido a la capa de presentación
        } catch (IOException e) {
            throw new ExceptionDiccionarioOperacionFallida("Error al crear el diccionario: " + e.getMessage(), "creación");
        }
    }
    
    /**
     * Crea un diccionario a partir de las rutas específicas de los archivos de alfabeto y palabras.
     * Este método es utilizado principalmente por ControladorJuego.
     * 
     * @pre Las rutas deben corresponder a archivos existentes y accesibles, y el nombre del diccionario no debe existir previamente en memoria.
     * @param nombre Nombre identificador del diccionario
     * @param rutaArchivoAlpha Ruta al archivo de alfabeto
     * @param rutaArchivoWords Ruta al archivo de palabras
     * @throws ExceptionDiccionarioExist Si ya existe un diccionario con ese nombre en memoria.
     * @throws IOException Si hay problemas con la lectura de los archivos (archivos no encontrados, permisos, etc.).
     * @throws ExceptionPalabraInvalida Si alguna palabra en el archivo de palabras contiene caracteres no válidos según el alfabeto.
     * @throws ExceptionDiccionarioOperacionFallida Si ocurre algún error durante la creación.
     * @throws NullPointerException Si alguno de los parámetros es null.
     * @post Si no ocurre ninguna excepción, un nuevo diccionario es creado, validado y cargado en memoria con su path derivado.
     */
    public void crearDiccionario(String nombre, String rutaArchivoAlpha, String rutaArchivoWords) throws ExceptionDiccionarioExist, IOException, ExceptionPalabraInvalida, ExceptionDiccionarioOperacionFallida {
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
            
            // Leer archivos
            List<String> lineasAlpha = leerArchivoLineaPorLinea(rutaArchivoAlpha);
            List<String> palabras = leerArchivoLineaPorLinea(rutaArchivoWords);
            
            // Crear el diccionario
            Diccionario dict = new Diccionario();
            dict.setAlphabet(lineasAlpha);
            
            // Validar que todas las palabras contienen solo caracteres del alfabeto
            Set<Character> validChars = getAlphabetChars(alphaPath);
            
            List<String> palabrasInvalidas = new ArrayList<>();
            for (String palabra : palabras) {
                palabra = palabra.trim().toUpperCase();
                if (!palabra.isEmpty() && !isValidWordSyntax(palabra, validChars)) {
                    palabrasInvalidas.add(palabra);
                }
            }
            
            if (!palabrasInvalidas.isEmpty()) {
                throw new ExceptionPalabraInvalida("El archivo de palabras contiene palabras con caracteres no definidos en el alfabeto: " + 
                                                  String.join(", ", palabrasInvalidas.subList(0, Math.min(10, palabrasInvalidas.size()))) + 
                                                  (palabrasInvalidas.size() > 10 ? "... y " + (palabrasInvalidas.size() - 10) + " más" : ""));
            }
            
            // Si todas las palabras son válidas, añadir el DAWG
            dict.setDawg(palabras);
            
            // Guardar el diccionario
            diccionarios.put(nombre, dict);
            
            // Obtener el directorio padre de la ruta del archivo
            String parentDir = alphaPath.getParent() != null ? 
                               alphaPath.getParent().toString() : 
                               ".";
            
            diccionarioPaths.put(nombre, parentDir);
            
            // Mensaje informativo movido a la capa de presentación
        } catch (IOException e) {
            throw new ExceptionDiccionarioOperacionFallida("Error al crear el diccionario: " + e.getMessage(), "creación");
        }
    }
    
    /**
     * Elimina un diccionario de memoria y su persistencia asociada.
     * 
     * @pre El diccionario con el nombre especificado debe existir en memoria.
     * @param nombre Nombre del diccionario a eliminar
     * @throws ExceptionDiccionarioNotExist Si el diccionario no existe en memoria.
     * @throws IOException Si hay problemas eliminando los archivos físicos asociados.
     * @throws ExceptionDiccionarioOperacionFallida Si ocurre algún error al eliminar del repositorio (lo que también implica un fallo en la eliminación física).
     * @throws NullPointerException Si el parámetro nombre es null.
     * @post Si no ocurre ninguna excepción, el diccionario es eliminado de memoria y de su representación persistente (archivos y índice del repositorio).
     */
    public void eliminarDiccionario(String nombre) throws ExceptionDiccionarioNotExist, IOException, ExceptionDiccionarioOperacionFallida {
        if (!diccionarios.containsKey(nombre)) {
            throw new ExceptionDiccionarioNotExist("No existe un diccionario con el nombre: " + nombre);
        }
        
        // Eliminar de memoria
        diccionarios.remove(nombre);
        String path = diccionarioPaths.remove(nombre);
        
        // Eliminar del repositorio
        boolean eliminado = repositorio.eliminar(nombre);
        if (!eliminado) {
            // Si falla la eliminación del repositorio pero ya lo hemos quitado de memoria,
            // intentamos recuperar el estado previo
            try {
                Diccionario dict = repositorio.cargar(nombre);
                if (dict != null && path != null) {
                    diccionarios.put(nombre, dict);
                    diccionarioPaths.put(nombre, path);
                }
            } catch (Exception e) {
                // Si no podemos recuperar, al menos informamos del error
            }
            
            throw new ExceptionDiccionarioOperacionFallida("Error al eliminar los archivos del diccionario desde el repositorio", "eliminación");
        }
        
        // Mensaje informativo movido a la capa de presentación
    }
    
    /**
     * Verifica si existe un diccionario con el nombre especificado.
     * 
     * @pre El nombre del diccionario no debe ser null.
     * @param nombre Nombre del diccionario
     * @return true si existe en memoria o en el repositorio, false en caso contrario.
     * @throws NullPointerException Si el parámetro nombre es null.
     * @post Se devuelve un valor booleano indicando si el diccionario existe en el sistema (memoria o persistencia) sin modificar su estado.
     */
    public boolean existeDiccionario(String nombre) {
        // Verificar primero en memoria
        if (diccionarios.containsKey(nombre)) {
            return true;
        }
        // Si no está en memoria, verificar en el repositorio
        return repositorio.existe(nombre);
    }
    
    
    /**
     * Obtiene la lista de nombres de diccionarios disponibles.
     * 
     * @pre No hay precondiciones específicas.
     * @return Lista de nombres de diccionarios
     * @post Se devuelve una lista no nula con los nombres de los diccionarios disponibles (posiblemente vacía).
     */
    public List<String> getDiccionariosDisponibles() {
        // Obtener de la memoria
        Set<String> enMemoria = diccionarios.keySet();
        
        // Obtener del repositorio
        List<String> todosDisponibles = repositorio.listarDiccionarios();
        
        // Combinar ambos conjuntos (en caso de que hubiera diccionarios en memoria pero no en repositorio)
        Set<String> resultado = new HashSet<>(todosDisponibles);
        resultado.addAll(enMemoria);
        
        return new ArrayList<>(resultado);
    }
    
    /**
     * Modifica un diccionario añadiendo o eliminando una palabra.
     * 待办: Clarificar si actualiza el DAWG y si la ExceptionLoggingOperacion se lanza siempre o solo en caso de error específico no controlado.
     * 
     * @pre El diccionario especificado debe existir en memoria, la palabra no debe estar vacía, y la palabra a añadir/eliminar debe ser sintácticamente válida con el alfabeto del diccionario.
     * @param nombre Nombre del diccionario (debe estar en memoria).
     * @param palabra Palabra a añadir o eliminar (no vacía).
     * @param anadir true para añadir la palabra, false para eliminarla.
     * @throws ExceptionDiccionarioNotExist Si el diccionario no existe en memoria.
     * @throws ExceptionPalabraVacia Si la palabra está vacía después de trim.
     * @throws ExceptionPalabraInvalida Si la palabra no puede formarse con los tokens del alfabeto del diccionario.
     * @throws ExceptionPalabraExist Si al añadir, la palabra ya existe en el archivo words.txt.
     * @throws ExceptionPalabraNotExist Si al eliminar, la palabra no existe en el archivo words.txt.
     * @throws IOException Si hay problemas con la lectura/escritura del archivo words.txt.
     * @throws IllegalStateException Si el path del diccionario no se encuentra en memoria (error interno).
     * @throws ExceptionLoggingOperacion Indicando el resultado de la operación (éxito o fallo específico durante la persistencia/DAWG).
     * @throws NullPointerException Si alguno de los parámetros es null.
     * @post Si no ocurre ninguna excepción (excepto ExceptionLoggingOperacion informativa), la palabra es añadida/eliminada del archivo words.txt, el DAWG en memoria se actualiza, y los cambios se persisten en el repositorio.
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
        
        // Leer todas las palabras existentes primero
        List<String> palabrasExistentes = Files.readAllLines(wordsPath, StandardCharsets.UTF_8)
                                           .stream()
                                           .map(String::trim)
                                           .filter(w -> !w.isEmpty())
                                           .toList();
        
        // Verificar si la palabra existe en el archivo
        boolean existePalabra = palabrasExistentes.contains(palabra);
        
        // Obtener el diccionario para actualizarlo también
        Diccionario dict = getDiccionario(nombre);
        
        if (anadir) {
            // Validar si la palabra puede formarse con los tokens del alfabeto
            Set<String> validTokens = getTokensAlfabeto(nombre);
            if (!isValidWordWithTokens(palabra, validTokens)) {
                throw new ExceptionPalabraInvalida("La palabra '" + palabra + "' no puede formarse con los tokens disponibles en el alfabeto '" + nombre + "'.");
            }
            
            // Verificar que la palabra no existe
            if (existePalabra) {
                throw new ExceptionPalabraExist("La palabra '" + palabra + "' ya existe en el diccionario.");
            }
            
            // Añadir al DAWG (intentar)
            try {
                dict.addWord(palabra);
            } catch (Exception e) {
                // Si falla, lo ignoramos (lo importante es actualizarlo en el archivo)
            }
            
            // Añadir a la lista y guardar en archivo
            List<String> wordList = new ArrayList<>(palabrasExistentes);
            wordList.add(palabra);
            Collections.sort(wordList);
            Files.write(wordsPath, wordList, StandardCharsets.UTF_8);
            
            // Actualizar el repositorio
            repositorio.guardar(nombre, dict, path);
        } else {
            // Verificar que la palabra existe
            if (!existePalabra) {
                throw new ExceptionPalabraNotExist("La palabra '" + palabra + "' no existe en el diccionario.");
            }
            
            // Eliminar del DAWG (intentar)
            try {
                dict.removeWord(palabra);
            } catch (Exception e) {
                // Si falla, lo ignoramos (lo importante es actualizarlo en el archivo)
            }
            
            // Eliminar de la lista y guardar en archivo
            List<String> wordList = new ArrayList<>(palabrasExistentes);
            wordList.remove(palabra);
            Files.write(wordsPath, wordList, StandardCharsets.UTF_8);
            
            // Actualizar el repositorio
            repositorio.guardar(nombre, dict, path);
        }
        
        System.out.println("Diccionario '" + nombre + "' actualizado: " + (anadir ? "Palabra añadida" : "Palabra eliminada") + ": " + palabra);
    }
    
    /**
     * Modifica una palabra existente en el diccionario.
     * 待办: Clarificar si actualiza el DAWG y si la ExceptionLoggingOperacion es solo informativa o indica un error.
     * 
     * @pre El diccionario debe existir en memoria, las palabras original y nueva no deben estar vacías y ser sintácticamente válidas, la palabra original debe existir en el archivo words.txt, y la palabra nueva no debe existir en el archivo words.txt.
     * @param nombre Nombre del diccionario (debe estar en memoria).
     * @param palabraOriginal Palabra a modificar (debe existir en words.txt y no vacía).
     * @param palabraNueva Nueva palabra que reemplazará a la original (no vacía y no debe existir en words.txt).
     * @throws ExceptionDiccionarioNotExist Si el diccionario no existe en memoria.
     * @throws ExceptionPalabraVacia Si alguna de las palabras está vacía después de trim.
     * @throws ExceptionPalabraInvalida Si la nueva palabra no puede formarse con los tokens del alfabeto.
     * @throws ExceptionPalabraNotExist Si la palabra original no existe en el archivo words.txt.
     * @throws ExceptionPalabraExist Si la nueva palabra ya existe en el archivo words.txt.
     * @throws IOException Si hay problemas con la lectura/escritura del archivo words.txt.
     * @throws IllegalStateException Si el path del diccionario no se encuentra en memoria (error interno).
     * @throws ExceptionLoggingOperacion Indicando el resultado de la operación (éxito).
     * @throws NullPointerException Si alguno de los parámetros es null.
     * @post Si no ocurre ninguna excepción (excepto ExceptionLoggingOperacion informativa), la palabra original es reemplazada por la nueva en el archivo words.txt, el DAWG en memoria se actualiza, y los cambios se persisten en el repositorio.
     */
    public void modificarPalabra(String nombre, String palabraOriginal, String palabraNueva) 
            throws ExceptionDiccionarioNotExist, ExceptionPalabraVacia, ExceptionPalabraInvalida,
                   ExceptionPalabraNotExist, ExceptionPalabraExist, IOException {
        
        // Normalizar palabras
        palabraOriginal = palabraOriginal.trim().toUpperCase();
        palabraNueva = palabraNueva.trim().toUpperCase();
        
        if (palabraOriginal.isEmpty() || palabraNueva.isEmpty()) {
            throw new ExceptionPalabraVacia("Las palabras no pueden estar vacías.");
        }
        
        // Verificar que el diccionario existe
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
        
        // Leer todas las palabras existentes primero
        List<String> palabrasExistentes = Files.readAllLines(wordsPath, StandardCharsets.UTF_8)
                                          .stream()
                                          .map(String::trim)
                                          .filter(w -> !w.isEmpty())
                                          .toList();
        
        // Verificar que la palabra original existe directamente en el archivo
        boolean existePalabraOriginal = palabrasExistentes.contains(palabraOriginal);
        if (!existePalabraOriginal) {
            throw new ExceptionPalabraNotExist("La palabra '" + palabraOriginal + "' no existe en el diccionario.");
        }
        
        // Verificar que la palabra nueva no existe directamente en el archivo
        boolean existePalabraNueva = palabrasExistentes.contains(palabraNueva);
        if (existePalabraNueva) {
            throw new ExceptionPalabraExist("La palabra '" + palabraNueva + "' ya existe en el diccionario.");
        }
        
        Diccionario dict = getDiccionario(nombre);
        
        // Validar que la nueva palabra puede formarse con los tokens del alfabeto
        Set<String> validTokens = getTokensAlfabeto(nombre);
        if (!isValidWordWithTokens(palabraNueva, validTokens)) {
            throw new ExceptionPalabraInvalida("La palabra '" + palabraNueva + "' no puede formarse con los tokens disponibles en el alfabeto '" + nombre + "'.");
        }
        
        // Actualizar el DAWG también
        try {
            dict.removeWord(palabraOriginal);
            dict.addWord(palabraNueva);
        } catch (Exception e) {
            // Si falla la operación en el DAWG, continuamos con el archivo
        }
        
        // Actualizar el archivo manualmente para garantizar la integridad
        List<String> wordList = new ArrayList<>(palabrasExistentes);
        wordList.remove(palabraOriginal);
        wordList.add(palabraNueva);
        
        // Ordenar para mantener orden lexicográfico
        Collections.sort(wordList);
        
        // Escribir al archivo
        Files.write(wordsPath, wordList, StandardCharsets.UTF_8);
        
        // Actualizar en el repositorio
        repositorio.guardar(nombre, dict, path);
        
        throw new ExceptionLoggingOperacion("Palabra '" + palabraOriginal + "' modificada a '" + palabraNueva + "' en el diccionario '" + nombre + "'.", "modificación");
    }
    
    /**
     * Obtiene los caracteres válidos del alfabeto de un diccionario.
     * Método privado auxiliar.
     * 
     * @pre El nombre del diccionario debe ser válido y su path debe estar en diccionarioPaths. El archivo alpha.txt debe existir en ese path.
     * @param nombre Nombre del diccionario.
     * @return Conjunto de caracteres válidos (mayúsculas) definidos en el alpha.txt del diccionario.
     * @throws ExceptionDiccionarioNotExist Si el diccionario no existe en memoria.
     * @throws IOException Si hay problemas leyendo alpha.txt (archivo no encontrado, permisos, etc.).
     * @throws NullPointerException Si el parámetro nombre es null.
     * @throws IllegalStateException Si el path del diccionario no se encuentra en memoria.
     * @post Se devuelve un conjunto no nulo (posiblemente vacío) con los caracteres válidos del alfabeto sin modificar el estado del diccionario.
     */
    private Set<Character> getAlphabetChars(String nombre) throws ExceptionDiccionarioNotExist, IOException {
        if (!diccionarios.containsKey(nombre)) {
            throw new ExceptionDiccionarioNotExist("No existe un diccionario con el nombre: " + nombre);
        }
        
        String path = diccionarioPaths.get(nombre);
        Path alphaPath = Paths.get(path, "alpha.txt");
        
        return getAlphabetChars(alphaPath);
    }
    
    /**
     * Obtiene los caracteres y sus valores asociados del alfabeto de un diccionario.
     * 
     * @pre El diccionario especificado debe existir en memoria.
     * @param nombre Nombre del diccionario.
     * @return Mapa donde las claves son los tokens del alfabeto (String) y los valores son sus puntuaciones (Integer).
     * @throws ExceptionDiccionarioNotExist Si el diccionario no existe en memoria.
     * @throws IOException Si hay problemas internos al obtener el alfabeto del objeto Diccionario.
     * @throws NullPointerException Si el parámetro nombre es null.
     * @post Si no ocurre ninguna excepción, se devuelve un mapa no nulo (posiblemente vacío) con el alfabeto y sus valores sin modificar el estado del diccionario.
     */
    public Map<String, Integer> getAlphabet(String nombre) throws ExceptionDiccionarioNotExist, IOException {
        if (!diccionarios.containsKey(nombre)) {
            throw new ExceptionDiccionarioNotExist("No existe un diccionario con el nombre: " + nombre);
        }
        Diccionario d = getDiccionario(nombre);
        
        return d.getAlphabet();
    }

    /**
     * Obtiene los caracteres válidos del alfabeto a partir de un archivo alpha.txt.
     * Método privado auxiliar.
     * 待办: Este método no usa el objeto Diccionario, solo lee el archivo directamente.
     * 
     * @param alphaPath Ruta al archivo alpha.txt.
     * @return Conjunto de caracteres válidos (mayúsculas) definidos en el archivo.
     * @throws IOException Si hay problemas leyendo el archivo.
     * @throws NullPointerException Si el parámetro alphaPath es null.
     * @post Se devuelve un conjunto no nulo (posiblemente vacío) con los caracteres válidos extraídos del archivo.
     */
    private Set<Character> getAlphabetChars(Path alphaPath) throws IOException {
        Set<Character> chars = new HashSet<>();
        List<String> lines = Files.readAllLines(alphaPath, StandardCharsets.UTF_8);
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            
            String[] parts = line.split("\\s+", 2);
            if (parts.length > 0 && !parts[0].isEmpty()) {
                // Si es un comodín "#", lo agregamos al conjunto para validar el alfabeto
                // pero al validar palabras se excluye en isValidWordSyntax
                for (char c : parts[0].toUpperCase().toCharArray()) {
                    chars.add(c);
                }
            }
        }
        
        return chars;
    }

    /**
     * Devuelve una lista con todas las palabras del diccionario leyendo directamente desde el archivo words.txt.
     * @pre El directorio del diccionario debe existir y contener el archivo words.txt.
     * @param  dic Nombre de diccionario (se usa para construir la ruta).
     * @return Lista de palabras (String) del archivo words.txt. Si el archivo no existe o hay error, devuelve una lista vacía.
     * @post Se devuelve una lista con las palabras del archivo words.txt sin modificar el estado del diccionario en memoria.
     * @throws NullPointerException Si el parámetro dic es null.
     */
    public List<String> getListaPalabras(String dic) {
        List<String> palabras = new ArrayList<>();
        try {
            Path wordsPath = Paths.get("src/main/resources/diccionarios/", dic, "words.txt");
                if (Files.exists(wordsPath)) {
                    List<String> lineas = Files.readAllLines(wordsPath, StandardCharsets.UTF_8);
                    for (String linea : lineas) {
                        linea = linea.trim();
                        if (!linea.isEmpty()) {
                            palabras.add(linea);
                        }
                    }
                }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo words del diccionario '" + dic + "': " + e.getMessage());
        }
        return palabras;
    }

    /**
     * Devuelve una lista de letras (tokens) con su puntuación y frecuencia leyendo directamente desde el archivo alpha.txt.
     * El formato de cada String en la lista es "letra puntuacion frecuencia".
     * @pre El directorio del diccionario debe existir y contener el archivo alpha.txt.
     * @param  dic Nombre de diccionario (se usa para construir la ruta).
     * @return Lista de Strings, cada uno representando una entrada del alfabeto del archivo alpha.txt (excluyendo líneas que empiezan con #).
     * @post Se devuelve una lista con las entradas del alfabeto del archivo alpha.txt sin modificar el estado del diccionario en memoria.
     * @throws NullPointerException Si el parámetro dic es null.
     */
    public List<String> getListaAlfabeto(String dic) {
        List<String> alfabeto = new ArrayList<>();
        try {
           Path alphaPath = Paths.get("src/main/resources/diccionarios/", dic, "alpha.txt");
                if (Files.exists(alphaPath)) {
                    List<String> lineas = Files.readAllLines(alphaPath, StandardCharsets.UTF_8);
                    for (String linea : lineas) {
                        linea = linea.trim();
                        if (!linea.isEmpty() && !linea.startsWith("#")) {
                            alfabeto.add(linea);
                        }
                    }
                }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo alpha del diccionario '" + dic + "': " + e.getMessage());
        }
        return alfabeto;
    }    
    
    /**
     * Verifica si una palabra contiene solo caracteres válidos definidos en el alfabeto (excluyendo comodines).
     * Método privado auxiliar para la validación sintáctica de palabras.
     * 
     * @pre La palabra no debe ser null y el conjunto de caracteres válidos no debe ser null.
     * @param palabra Palabra a verificar.
     * @param validChars Conjunto de caracteres válidos permitidos en palabras.
     * @return true si la palabra solo contiene caracteres de validChars y no contiene '#', false en caso contrario.
     * @throws NullPointerException Si alguno de los parámetros es null.
     * @post Se devuelve un valor booleano indicando la validez sintáctica de la palabra sin modificar ningún estado.
     */
    private boolean isValidWordSyntax(String palabra, Set<Character> validChars) {
        for (char c : palabra.toCharArray()) {
            // El comodín "#" no se permite en palabras del diccionario
            if (c == '#') {
                return false;
            }
            
            if (!validChars.contains(c)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Verifica si una palabra puede formarse concatenando tokens válidos del alfabeto.
     * Método privado auxiliar para la validación de la estructura de palabras compuestas.
     * 
     * @pre La palabra no debe ser null y el conjunto de tokens válidos no debe ser null.
     * @param palabra Palabra a verificar (no debe contener '#').
     * @param validTokens Conjunto de tokens válidos del alfabeto (String).
     * @return true si la palabra puede ser completamente construida a partir de concatenaciones de validTokens, false en caso contrario.
     * @throws NullPointerException Si alguno de los parámetros es null.
     * @post Se devuelve un valor booleano indicando si la palabra puede formarse con los tokens sin modificar ningún estado.
     */
    private boolean isValidWordWithTokens(String palabra, Set<String> validTokens) {
        if (palabra == null || palabra.isEmpty()) {
            return false;
        }
        
        // El comodín "#" no se permite en palabras del diccionario
        if (palabra.contains("#")) {
            return false;
        }
        
        String palabraPendiente = palabra;
        
        // Ordenar tokens por longitud (descendente) para intentar consumir primero los tokens más largos
        List<String> tokensPorLongitud = new ArrayList<>(validTokens);
        tokensPorLongitud.sort((t1, t2) -> Integer.compare(t2.length(), t1.length()));
        
        // Intentar consumir la palabra token por token
        while (!palabraPendiente.isEmpty()) {
            boolean consumido = false;
            
            for (String token : tokensPorLongitud) {
                if (palabraPendiente.startsWith(token)) {
                    palabraPendiente = palabraPendiente.substring(token.length());
                    consumido = true;
                    break;
                }
            }
            
            // Si no pudimos consumir ningún token en esta iteración, la palabra no es válida
            if (!consumido) {
                return false;
            }
        }
        
        // Si hemos consumido toda la palabra, es válida
        return true;
    }
    
    /**
     * Obtiene el conjunto de caracteres válidos (individuales) del alfabeto de un diccionario.
     * Método público que delega en el método privado getAlphabetChars(String nombre).
     * 
     * @pre El diccionario especificado debe existir en memoria y su archivo alpha.txt debe ser accesible.
     * @param nombre Nombre del diccionario.
     * @return Conjunto de caracteres válidos (mayúsculas) del alfabeto.
     * @throws ExceptionDiccionarioNotExist Si el diccionario no existe en memoria.
     * @throws IOException Si hay problemas leyendo alpha.txt.
     * @throws NullPointerException Si el parámetro nombre es null.
     * @post Se devuelve un conjunto no nulo (posiblemente vacío) con los caracteres individuales válidos del alfabeto sin modificar el estado del diccionario.
     */
    public Set<Character> getCaracteresAlfabeto(String nombre) throws ExceptionDiccionarioNotExist, IOException {
        return getAlphabetChars(nombre);
    }
    
    /**
     * Obtiene el conjunto de tokens (letras, incluyendo multicarácter como CH, RR) del alfabeto de un diccionario.
     * 
     * @pre El diccionario especificado debe existir en memoria.
     * @param nombre Nombre del diccionario.
     * @return Conjunto de tokens del alfabeto (String).
     * @throws ExceptionDiccionarioNotExist Si el diccionario no existe en memoria.
     * @throws NullPointerException Si el parámetro nombre es null.
     * @post Se devuelve un conjunto no nulo (posiblemente vacío) con los tokens del alfabeto sin modificar el estado del diccionario.
     */
    public Set<String> getTokensAlfabeto(String nombre) throws ExceptionDiccionarioNotExist {
        if (!diccionarios.containsKey(nombre)) {
            throw new ExceptionDiccionarioNotExist("No existe un diccionario con el nombre: " + nombre);
        }
        
        Diccionario diccionario = getDiccionario(nombre);
        return diccionario.getAlphabetKeys();
    }
    
    /**
     * Verifica si una palabra existe en el diccionario utilizando el DAWG en memoria.
     * 
     * @pre El diccionario especificado debe existir en memoria.
     * @param nombre Nombre del diccionario (debe estar en memoria).
     * @param palabra Palabra a verificar (no nula).
     * @return true si la palabra existe en el DAWG del diccionario, false en caso contrario o si el diccionario no existe en memoria o la palabra es vacía.
     * @throws NullPointerException Si el parámetro nombre o palabra es null.
     * @post Se devuelve un valor booleano indicando si la palabra existe en el DAWG sin modificar el estado del diccionario.
     */
    public boolean existePalabra(String nombre, String palabra) {
        if (!diccionarios.containsKey(nombre)) {
            return false;
        }
        
        palabra = palabra.trim().toUpperCase();
        if (palabra.isEmpty()) {
            return false;
        }
        
        try {
            // 1. Verificar en el DAWG
            Diccionario diccionario = getDiccionario(nombre);
            if (diccionario.contienePalabra(palabra)) {
                return true;
            } 
            return false;
            
        } catch (Exception e) {
            // En caso de error, devolver false
            return false;
        }
    }
    
    /**
     * Verifica si un diccionario configurado sigue siendo válido (sus archivos existen) comprobando el repositorio.
     * 
     * @pre El nombre del diccionario no debe ser null.
     * @param nombre Nombre del diccionario a verificar.
     * @return true si el diccionario existe en el repositorio y sus archivos son válidos, false en caso contrario.
     * @throws NullPointerException Si el parámetro nombre es null.
     * @post Se devuelve un valor booleano indicando si el diccionario es válido en el sistema persistente sin modificar su estado.
     */
    public boolean verificarDiccionarioValido(String nombre) {
        // Si está en memoria, lo consideramos válido
        if (diccionarios.containsKey(nombre)) {
            return true;
        }
        
        // Si no está en memoria, verificar en el repositorio
        return repositorio.verificarDiccionarioValido(nombre);
    }
    
    /**
     * Verifica todos los diccionarios registrados en el repositorio al iniciar el controlador, eliminando aquellos cuyos archivos no son válidos y cargando los válidos en memoria.
     * 
     * @pre El repositorio de diccionarios debe estar inicializado.
     * @throws ExceptionLoggingOperacion Con información sobre los diccionarios que fueron encontrados como inválidos y eliminados.
     * @post El mapa de diccionarios en memoria (diccionarios) y sus paths (diccionarioPaths) se actualizan para contener solo los diccionarios válidos encontrados en el repositorio. Los diccionarios inválidos se eliminan del repositorio y sus paths de memoria.
     */
    private void verificarTodosDiccionarios() {
        // Cargar índice de diccionarios desde el repositorio
        Map<String, String> indice = repositorio.cargarIndice();
        
        // Actualizar el diccionarioPaths con la información del repositorio
        diccionarioPaths.clear();
        diccionarioPaths.putAll(indice);
        
        List<String> diccionariosInvalidos = new ArrayList<>();
        
        // Identificar diccionarios inválidos
        for (String nombre : new ArrayList<>(diccionarioPaths.keySet())) {
            boolean esValido = repositorio.verificarDiccionarioValido(nombre);
            if (!esValido) {
                diccionariosInvalidos.add(nombre);
                continue;
            }
            
            // Cargar diccionario válido a memoria
            try {
                Diccionario dict = repositorio.cargar(nombre);
                diccionarios.put(nombre, dict);
            } catch (Exception e) {
                diccionariosInvalidos.add(nombre);
            }
        }
        
        // Eliminar diccionarios inválidos
        for (String nombre : diccionariosInvalidos) {
            diccionarios.remove(nombre);
            diccionarioPaths.remove(nombre);
            repositorio.eliminar(nombre);
        }
        
        if (!diccionariosInvalidos.isEmpty()) {
            throw new ExceptionLoggingOperacion(
                "Eliminando " + diccionariosInvalidos.size() + " diccionario(s) inválido(s): " 
                + String.join(", ", diccionariosInvalidos) + " (archivos no encontrados)", 
                "verificación");
        }
    }
    
    /**
     * Verifica si un carácter es un comodín en el diccionario especificado.
     * 
     * @pre El diccionario especificado debe existir en memoria y el carácter no debe ser null.
     * @param nombre Nombre del diccionario (debe estar en memoria).
     * @param caracter Carácter a verificar (no null).
     * @return true si el carácter es un comodín definido en el alfabeto del diccionario, false en caso contrario o si el diccionario no existe en memoria.
     * @throws ExceptionDiccionarioNotExist Si el diccionario no existe en memoria.
     * @throws NullPointerException Si alguno de los parámetros es null.
     * @post Se devuelve un valor booleano indicando si el carácter es un comodín sin modificar el estado del diccionario.
     */
    public boolean esComodin(String nombre, String caracter) throws ExceptionDiccionarioNotExist {
        if (!diccionarios.containsKey(nombre)) {
            throw new ExceptionDiccionarioNotExist("No existe un diccionario con el nombre: " + nombre);
        }
        
        Diccionario diccionario = getDiccionario(nombre);
        return diccionario.esComodin(caracter);
    }

    /**
     * Obtiene las fichas asociadas a un diccionario específico.
     *
     * @pre El diccionario especificado debe existir en memoria.
     * @param nombreDiccionario El nombre del diccionario (debe estar en memoria).
     * @return Un mapa donde las claves son los tokens de las fichas (String) y los valores son su cantidad inicial (Integer) si el diccionario existe. Devuelve {@code null} si el diccionario no está en memoria.
     * @throws NullPointerException Si el parámetro nombreDiccionario es null.
     * @post Si el diccionario existe en memoria, se devuelve un mapa no nulo con las fichas y sus cantidades sin modificar el estado del diccionario.
     */
    public Map<String, Integer> getFichas(String nombreDiccionario) {
        if (!diccionarios.containsKey(nombreDiccionario)) {
            return null;
        }
        
        try {
            Diccionario diccionario = getDiccionario(nombreDiccionario);
            return diccionario.getFichas();
        } catch (ExceptionDiccionarioNotExist e) {
            // No debería ocurrir ya que verificamos existencia previamente
            return null;
        }
    }

    /**
     * Obtiene el puntaje de un token (letra o multi-carácter) en un diccionario específico.
     *
     * @pre El diccionario debe existir en memoria y el token (valueOf) no debe ser null.
     * @param nombreDiccionario El nombre del diccionario (debe estar en memoria).
     * @param valueOf El token (String) cuyo puntaje se desea obtener.
     * @return El puntaje del token si el diccionario existe y contiene el token, o 0 si el diccionario no existe en memoria o no contiene el token.
     * @throws NullPointerException Si cualquiera de los parámetros es null.
     * @post Se devuelve un entero no negativo que representa el puntaje del token sin modificar el estado del diccionario.
     */
    public int getPuntaje(String nombreDiccionario, String valueOf) {
        if (!diccionarios.containsKey(nombreDiccionario)) {
            return 0;
        }
        
        try {
            Diccionario diccionario = getDiccionario(nombreDiccionario);
            return diccionario.getPuntaje(valueOf);
        } catch (ExceptionDiccionarioNotExist e) {
            // No debería ocurrir ya que verificamos existencia previamente
            return 0;
        }
    }

    /**
     * Obtiene los bordes disponibles para continuar una palabra parcial en el DAWG de un diccionario específico.
     *
     * @pre El diccionario especificado debe existir en memoria y la palabra parcial no debe ser null.
     * @param nombreDiccionario El nombre del diccionario (debe estar en memoria).
     * @param palabraParcial La palabra parcial (String) para la cual se desean obtener los bordes disponibles.
     * @return Un conjunto de cadenas (String) que representan los tokens disponibles para continuar la palabra parcial en el DAWG. Devuelve {@code null} si el diccionario especificado no existe en memoria.
     * @throws NullPointerException Si cualquiera de los parámetros es null.
     * @post Si el diccionario existe en memoria y la palabra parcial corresponde a un nodo válido en el DAWG, se devuelve un conjunto (posiblemente vacío) de tokens que pueden continuar la palabra. En caso contrario, devuelve null.
     */
    public Set<String> getAvailableEdges(String nombreDiccionario, String palabraParcial) {
        if (!diccionarios.containsKey(nombreDiccionario)) {
            return null;
        }
        
        try {
            Diccionario diccionario = getDiccionario(nombreDiccionario);
            return diccionario.getAvailableEdges(palabraParcial);
        } catch (ExceptionDiccionarioNotExist e) {
            // No debería ocurrir ya que verificamos existencia previamente
            return null;
        }
    }

    /**
     * Verifica si una palabra parcial es el final de una palabra válida en el DAWG del diccionario especificado.
     *
     * @pre El diccionario especificado debe existir en memoria y la palabra parcial no debe ser null.
     * @param nombreDiccionario El nombre del diccionario (debe estar en memoria).
     * @param palabraParcial La palabra parcial (String) que se desea verificar.
     * @return {@code true} si la palabra parcial corresponde a un nodo final en el DAWG del diccionario, {@code false} si el diccionario no existe en memoria o si la palabra parcial no es un nodo final válido.
     * @throws NullPointerException Si cualquiera de los parámetros es null.
     * @post Se devuelve un valor booleano que indica si la palabra parcial es un final válido en el DAWG sin modificar el estado del diccionario.
     */
    public boolean isFinal(String nombreDiccionario, String palabraParcial) {
        if (!diccionarios.containsKey(nombreDiccionario)) {
            return false;
        }
        
        try {
            Diccionario diccionario = getDiccionario(nombreDiccionario);
            return diccionario.isFinal(palabraParcial);
        } catch (ExceptionDiccionarioNotExist e) {
            // No debería ocurrir ya que verificamos existencia previamente
            return false;
        }
    }

    /**
     * Verifica si existe un nodo en el DAWG del diccionario especificado que coincide con la palabra parcial proporcionada.
     *
     * @pre El diccionario especificado debe existir en memoria y la palabra parcial no debe ser null.
     * @param nombreDiccionario El nombre del diccionario (debe estar en memoria).
     * @param palabraParcial La palabra parcial (String) que se desea verificar.
     * @return {@code true} si el nodo correspondiente a la palabra parcial existe en el DAWG del diccionario, {@code false} en caso contrario o si el diccionario no está en memoria.
     * @throws NullPointerException Si cualquiera de los parámetros es null.
     * @post Se devuelve un valor booleano que indica si el nodo correspondiente a la palabra parcial existe en el DAWG sin modificar el estado del diccionario.
     */
    public boolean nodeExists(String nombreDiccionario, String palabraParcial) {
        if (!diccionarios.containsKey(nombreDiccionario)) {
            return false;
        }
        
        try {
            Diccionario diccionario = getDiccionario(nombreDiccionario);
            return diccionario.nodeExists(palabraParcial);
        } catch (ExceptionDiccionarioNotExist e) {
            // No debería ocurrir ya que verificamos existencia previamente
            return false;
        }
    }

    /**
     * Obtiene un diccionario por su nombre desde la memoria.
     * 
     * @pre El diccionario con el nombre especificado debe existir en memoria.
     * @param nombre Nombre del diccionario (debe estar en memoria).
     * @return El objeto Diccionario correspondiente a la clave dada.
     * @throws ExceptionDiccionarioNotExist Si el diccionario no existe en memoria.
     * @throws NullPointerException Si el parámetro nombre es null.
     * @post Si el diccionario existe en memoria, devuelve una referencia al objeto Diccionario; en caso contrario, lanza una excepción.
     */
    public Diccionario getDiccionario(String nombre) throws ExceptionDiccionarioNotExist {
        if (!diccionarios.containsKey(nombre)) {
            throw new ExceptionDiccionarioNotExist("No existe un diccionario con el nombre: " + nombre);
        }
        return diccionarios.get(nombre);
    }

}