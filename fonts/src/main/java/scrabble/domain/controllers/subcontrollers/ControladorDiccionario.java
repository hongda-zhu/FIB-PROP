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
 */
public class ControladorDiccionario {
    private static ControladorDiccionario instance;
    private Map<String, Diccionario> diccionarios;
    private Map<String, String> diccionarioPaths;
    private RepositorioDiccionario repositorio;
    
    /**
     * Constructor privado para implementar el patrón Singleton.
     * Inicializa los mapas de diccionarios y paths.
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
     * @pre El path debe corresponder a un directorio existente que contenga los archivos alpha.txt y words.txt.
     * @param nombre Nombre identificador del diccionario
     * @param path Ruta al directorio del diccionario
     * @throws ExceptionDiccionarioExist Si ya existe un diccionario con ese nombre
     * @throws IOException Si hay problemas con la lectura/escritura de archivos
     * @throws ExceptionPalabraInvalida Si alguna palabra contiene caracteres no válidos
     * @throws ExceptionDiccionarioOperacionFallida Si ocurre algún error durante la creación
     * @post Si no ocurre ninguna excepción, un nuevo diccionario es creado y cargado en memoria.
     * @throws NullPointerException Si alguno de los parámetros es null.
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
     * @pre Las rutas deben corresponder a archivos existentes y accesibles.
     * @param nombre Nombre identificador del diccionario
     * @param rutaArchivoAlpha Ruta al archivo de alfabeto
     * @param rutaArchivoWords Ruta al archivo de palabras
     * @throws ExceptionDiccionarioExist Si ya existe un diccionario con ese nombre
     * @throws IOException Si hay problemas con la lectura/escritura de archivos
     * @throws ExceptionPalabraInvalida Si alguna palabra contiene caracteres no válidos
     * @throws ExceptionDiccionarioOperacionFallida Si ocurre algún error durante la creación
     * @post Si no ocurre ninguna excepción, un nuevo diccionario es creado y cargado en memoria.
     * @throws NullPointerException Si alguno de los parámetros es null.
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
     * Elimina un diccionario de memoria y su directorio asociado.
     * 
     * @pre El diccionario con el nombre especificado debe existir.
     * @param nombre Nombre del diccionario a eliminar
     * @throws ExceptionDiccionarioNotExist Si el diccionario no existe
     * @throws IOException Si hay problemas eliminando los archivos
     * @throws ExceptionDiccionarioOperacionFallida Si ocurre algún error durante la eliminación
     * @post Si no ocurre ninguna excepción, el diccionario es eliminado de memoria y su directorio eliminado del sistema de archivos.
     * @throws NullPointerException Si el parámetro nombre es null.
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
     * @pre No hay precondiciones específicas.
     * @param nombre Nombre del diccionario
     * @return true si existe, false en caso contrario
     * @post Se devuelve un valor booleano indicando si el diccionario existe.
     * @throws NullPointerException Si el parámetro nombre es null.
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
     * 
     * @pre El diccionario especificado debe existir y la palabra no debe estar vacía.
     * @param nombre Nombre del diccionario
     * @param palabra Palabra a añadir o eliminar
     * @param anadir true para añadir, false para eliminar
     * @throws ExceptionDiccionarioNotExist Si el diccionario no existe
     * @throws ExceptionPalabraVacia Si la palabra está vacía
     * @throws ExceptionPalabraInvalida Si la palabra contiene caracteres no válidos
     * @throws ExceptionPalabraExist Si la palabra ya existe (al añadir)
     * @throws ExceptionPalabraNotExist Si la palabra no existe (al eliminar)
     * @throws IOException Si hay problemas con la lectura/escritura de archivos
     * @post Si no ocurre ninguna excepción, la palabra es añadida o eliminada del diccionario y los cambios se persisten.
     * @throws NullPointerException Si alguno de los parámetros es null.
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
     * 
     * @pre El diccionario debe existir, la palabra original debe existir en el diccionario, y la palabra nueva no debe existir.
     * @param nombre Nombre del diccionario
     * @param palabraOriginal Palabra a modificar
     * @param palabraNueva Nueva palabra que reemplazará a la original
     * @throws ExceptionDiccionarioNotExist Si el diccionario no existe
     * @throws ExceptionPalabraVacia Si alguna de las palabras está vacía
     * @throws ExceptionPalabraInvalida Si la nueva palabra contiene caracteres no válidos
     * @throws ExceptionPalabraNotExist Si la palabra original no existe
     * @throws ExceptionPalabraExist Si la nueva palabra ya existe en el diccionario
     * @throws IOException Si hay problemas con la lectura/escritura de archivos
     * @post Si no ocurre ninguna excepción, la palabra original es reemplazada por la nueva en el diccionario y los cambios se persisten.
     * @throws NullPointerException Si alguno de los parámetros es null.
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
     * 
     * @pre El diccionario especificado debe existir.
     * @param nombre Nombre del diccionario
     * @return Conjunto de caracteres válidos
     * @throws ExceptionDiccionarioNotExist Si el diccionario no existe
     * @throws IOException Si hay problemas leyendo alpha.txt
     * @post Si no ocurre ninguna excepción, se devuelve un conjunto no nulo con los caracteres válidos del alfabeto.
     * @throws NullPointerException Si el parámetro nombre es null.
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
     * Obtiene los caracteres válidos del alfabeto a partir de un archivo alpha.txt.
     * 
     * @param alphaPath Ruta al archivo alpha.txt
     * @return Conjunto de caracteres válidos
     * @throws IOException Si hay problemas leyendo el archivo
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
     * Verifica si una palabra contiene solo caracteres válidos.
     * Los comodines ("#") no son válidos en palabras, sólo en fichas durante el juego.
     * 
     * @param palabra Palabra a verificar
     * @param validChars Conjunto de caracteres válidos
     * @return true si la palabra es válida, false en caso contrario
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
     * Verifica si una palabra puede formarse utilizando exclusivamente los tokens completos
     * definidos en el alfabeto. Por ejemplo, si solo tenemos el token "CC" en el alfabeto,
     * solo se pueden formar palabras como "CC", "CCCC", "CCCCCC", etc.
     * 
     * @param palabra Palabra a verificar
     * @param validTokens Conjunto de tokens válidos del alfabeto
     * @return true si la palabra puede formarse con los tokens del alfabeto, false en caso contrario
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
     * Obtiene el conjunto de caracteres válidos del alfabeto de un diccionario.
     * Método público para ser usado por otros controladores.
     * 
     * @pre El diccionario especificado debe existir.
     * @param nombre Nombre del diccionario
     * @return Conjunto de caracteres válidos
     * @throws ExceptionDiccionarioNotExist Si el diccionario no existe
     * @throws IOException Si hay problemas leyendo alpha.txt
     * @post Si no ocurre ninguna excepción, se devuelve un conjunto no nulo con los caracteres válidos del alfabeto.
     * @throws NullPointerException Si el parámetro nombre es null.
     */
    public Set<Character> getCaracteresAlfabeto(String nombre) throws ExceptionDiccionarioNotExist, IOException {
        return getAlphabetChars(nombre);
    }
    
    /**
     * Obtiene el conjunto de tokens (letras, incluyendo multicarácter como CH, RR) del alfabeto de un diccionario.
     * 
     * @pre El diccionario especificado debe existir.
     * @param nombre Nombre del diccionario
     * @return Conjunto de tokens del alfabeto (ejemplo: A, B, CH, RR)
     * @throws ExceptionDiccionarioNotExist Si el diccionario no existe
     * @post Si no ocurre ninguna excepción, se devuelve un conjunto no nulo con los tokens del alfabeto.
     * @throws NullPointerException Si el parámetro nombre es null.
     */
    public Set<String> getTokensAlfabeto(String nombre) throws ExceptionDiccionarioNotExist {
        if (!diccionarios.containsKey(nombre)) {
            throw new ExceptionDiccionarioNotExist("No existe un diccionario con el nombre: " + nombre);
        }
        
        Diccionario diccionario = getDiccionario(nombre);
        return diccionario.getAlphabetKeys();
    }
    
    /**
     * Verifica si una palabra existe en el diccionario.
     * Comprueba en DAWG, en el archivo words.txt, y en la caché en memoria.
     * 
     * @pre No hay precondiciones específicas fuertes, pero el nombre del diccionario y la palabra no deberían ser null.
     * @param nombre Nombre del diccionario
     * @param palabra Palabra a verificar
     * @return true si la palabra existe, false en caso contrario
     * @post Se devuelve un valor booleano indicando si la palabra existe en el diccionario.
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
     * Verifica si un diccionario continúa siendo válido (sus archivos existen).
     * 
     * @pre No hay precondiciones específicas fuertes.
     * @param nombre Nombre del diccionario a verificar
     * @return true si el diccionario es válido, false si falta algún archivo necesario
     * @post Se devuelve un valor booleano indicando si el diccionario sigue siendo válido.
     * @throws NullPointerException Si el parámetro nombre es null.
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
     * Verifica todos los diccionarios y elimina aquellos que ya no son válidos.
     * Se ejecuta al iniciar el controlador.
     * @throws ExceptionLoggingOperacion Con información sobre los diccionarios eliminados
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
     * @pre El diccionario especificado debe existir.
     * @param nombre Nombre del diccionario
     * @param caracter Carácter a verificar
     * @return true si es un comodín, false en caso contrario
     * @throws ExceptionDiccionarioNotExist Si el diccionario no existe
     * @post Se devuelve un valor booleano indicando si el carácter es un comodín en el diccionario.
     * @throws NullPointerException Si alguno de los parámetros es null.
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
     * @pre El diccionario debe existir para devolver un mapa válido, en caso contrario devolverá null.
     * @param nombreDiccionario El nombre del diccionario del cual se desean obtener las fichas.
     * @return Un mapa que asocia las fichas (como claves) con su cantidad (como valores) 
     *         si el diccionario existe, o {@code null} si el diccionario no está registrado.
     * @post Si el diccionario existe, se devuelve un mapa no nulo con las fichas y sus cantidades.
     * @throws NullPointerException Si el parámetro nombreDiccionario es null.
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
     * Obtiene el puntaje de una palabra en un diccionario específico.
     *
     * @pre La palabra debe existir en el diccionario para devolver un puntaje válido, en caso contrario devolverá 0.
     * @param nombreDiccionario El nombre del diccionario donde se buscará la palabra.
     * @param valueOf La palabra cuyo puntaje se desea obtener.
     * @return El puntaje de la palabra si el diccionario existe y contiene la palabra,
     *         o 0 si el diccionario no existe o no contiene la palabra.
     * @post Se devuelve un entero no negativo que representa el puntaje de la palabra.
     * @throws NullPointerException Si cualquiera de los parámetros es null.
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
     * Obtiene los bordes disponibles para continuar una palabra parcial en un diccionario específico.
     *
     * @pre La palabra parcial debe ser válida según la estructura del diccionario para obtener bordes disponibles.
     * @param nombreDiccionario El nombre del diccionario en el que se buscarán los bordes disponibles.
     * @param palabraParcial La palabra parcial para la cual se desean obtener los bordes disponibles.
     * @return Un conjunto de cadenas que representan los bordes disponibles para continuar la palabra parcial.
     *         Devuelve {@code null} si el diccionario especificado no existe.
     * @post Si el diccionario existe y la palabra parcial es válida, se devuelve un conjunto (posiblemente vacío) 
     *       de caracteres que pueden continuar la palabra.
     * @throws NullPointerException Si cualquiera de los parámetros es null.
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
     * Verifica si una palabra parcial es el final de una palabra válida en el diccionario especificado.
     *
     * @pre La palabra parcial debe ser una cadena no nula para realizar la verificación.
     * @param nombreDiccionario El nombre del diccionario en el que se realizará la búsqueda.
     * @param palabraParcial La palabra parcial que se desea verificar.
     * @return {@code true} si la palabra parcial es el final de una palabra válida en el diccionario,
     *         {@code false} si el diccionario no existe o si la palabra parcial no es un final válido.
     * @post Se devuelve un valor booleano que indica si la palabra parcial es un final válido.
     * @throws NullPointerException Si cualquiera de los parámetros es null.
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
     * Verifica si existe un nodo en el diccionario especificado que coincide con 
     * la palabra parcial proporcionada.
     *
     * @pre La palabra parcial debe ser una cadena no nula para realizar la verificación.
     * @param nombreDiccionario El nombre del diccionario en el que se buscará.
     * @param palabraParcial La palabra parcial que se desea verificar.
     * @return {@code true} si el nodo existe en el diccionario, {@code false} en caso contrario 
     *         o si el diccionario no está registrado.
     * @post Se devuelve un valor booleano que indica si el nodo correspondiente a la palabra parcial existe.
     * @throws NullPointerException Si cualquiera de los parámetros es null.
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
     * Obtiene un diccionario por su nombre.
     * 
     * @pre No hay precondiciones específicas fuertes.
     * @param nombre Nombre del diccionario
     * @return El objeto Diccionario correspondiente
     * @throws ExceptionDiccionarioNotExist Si el diccionario no existe
     * @post Si el diccionario existe, devuelve una referencia al objeto Diccionario; en caso contrario, lanza una excepción.
     * @throws NullPointerException Si el parámetro nombre es null.
     */
    public Diccionario getDiccionario(String nombre) throws ExceptionDiccionarioNotExist {
        if (!diccionarios.containsKey(nombre)) {
            throw new ExceptionDiccionarioNotExist("No existe un diccionario con el nombre: " + nombre);
        }
        return diccionarios.get(nombre);
    }

}