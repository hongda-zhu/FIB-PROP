package scrabble.domain.persistences.interfaces;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import scrabble.domain.models.Diccionario;

/**
 * Interfaz que define las operaciones de persistencia para los diccionarios.
 * 
 * Esta interfaz establece el contrato para la gestión de persistencia de diccionarios
 * en el sistema, incluyendo operaciones CRUD completas, gestión de índices y validación
 * de integridad. Implementa el patrón Repository para abstraer los detalles de
 * almacenamiento y proporcionar una interfaz uniforme para el acceso a datos.
 * 
 * Funcionalidades principales:
 * - Operaciones CRUD completas para diccionarios
 * - Gestión de índices de diccionarios disponibles
 * - Validación de integridad de estructuras DAWG
 * - Verificación de existencia y validez de archivos
 * - Listado y consulta de diccionarios disponibles
 * 
 * @version 2.0
 * @since 1.0
 */
public interface RepositorioDiccionario {
    

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
    String crearDesdeTexto(String nombre, List<String> alfabeto, List<String> palabras);   

    /**
     * Guarda un diccionario en el sistema de persistencia.
     * 
     * @param nombre Nombre único del diccionario para identificación en el sistema.
     * @param diccionario Objeto Diccionario con la estructura DAWG a persistir.
     * @param path Ruta del directorio donde se almacenarán los archivos del diccionario.
     * @return true si la operación fue exitosa, false en caso contrario.
     * @pre nombre != null && !nombre.isEmpty() && diccionario != null && path != null
     * @post Si retorna true, el diccionario queda persistido en el sistema de archivos
     */
    boolean guardar(String nombre, Diccionario diccionario, String path);
    
    /**
     * Guarda el índice de diccionarios disponibles en el sistema.
     * 
     * @param diccionariosPaths Mapa que asocia nombres de diccionarios con sus rutas de almacenamiento.
     * @return true si la operación fue exitosa, false en caso contrario.
     * @pre diccionariosPaths != null
     * @post El índice queda actualizado con la información proporcionada
     */
    boolean guardarIndice(Map<String, String> diccionariosPaths);
    
    /**
     * Carga un diccionario específico desde el sistema de persistencia.
     * 
     * @param nombre Nombre del diccionario a cargar desde el almacenamiento.
     * @return El diccionario cargado con su estructura DAWG completa, o null si no existe.
     * @throws IOException Si hay problemas al leer los archivos del diccionario.
     * @pre nombre != null && !nombre.isEmpty()
     * @post Si no lanza excepción, retorna un diccionario válido o null
     */
    Diccionario cargar(String nombre) throws IOException;
    
    /**
     * Carga el índice de diccionarios disponibles en el sistema.
     * 
     * @return Mapa que asocia nombres de diccionarios con sus rutas de almacenamiento.
     * @post Retorna un mapa válido (puede estar vacío si no hay diccionarios)
     */
    Map<String, String> cargarIndice();
    
    /**
     * Elimina un diccionario del sistema de persistencia.
     * 
     * @param nombre Nombre del diccionario a eliminar completamente del sistema.
     * @return true si la operación fue exitosa, false en caso contrario.
     * @pre nombre != null && !nombre.isEmpty()
     * @post Si retorna true, el diccionario y sus archivos han sido eliminados
     */
    boolean eliminar(String nombre);
    
    /**
     * Verifica si un diccionario existe en el sistema de persistencia.
     * 
     * @param nombre Nombre del diccionario a verificar en el índice del sistema.
     * @return true si el diccionario existe en el índice, false en caso contrario.
     * @pre nombre != null && !nombre.isEmpty()
     * @post Retorna el estado de existencia sin modificar el sistema
     */
    boolean existe(String nombre);
    
    /**
     * Obtiene una lista de los nombres de todos los diccionarios disponibles.
     * 
     * @return Lista de nombres de diccionarios registrados en el sistema.
     * @post Retorna una lista válida (puede estar vacía si no hay diccionarios)
     */
    List<String> listarDiccionarios();
    
    /**
     * Verifica si un diccionario sigue siendo válido (sus archivos existen).
     * 
     * @param nombre Nombre del diccionario a verificar la integridad de sus archivos.
     * @return true si el diccionario es válido, false si falta algún archivo necesario.
     * @pre nombre != null && !nombre.isEmpty()
     * @post Retorna el estado de validez sin modificar el sistema
     */
    boolean verificarDiccionarioValido(String nombre);
}