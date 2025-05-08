package scrabble.domain.persistences.interfaces;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import scrabble.domain.models.Diccionario;

/**
 * Interfaz que define las operaciones de persistencia para los diccionarios.
 */
public interface RepositorioDiccionario {
    
    /**
     * Guarda un diccionario en el sistema de persistencia.
     * 
     * @param nombre Nombre del diccionario
     * @param diccionario Diccionario a guardar
     * @param path Ruta donde se encuentra el diccionario
     * @return true si la operación fue exitosa, false en caso contrario
     */
    boolean guardar(String nombre, Diccionario diccionario, String path);
    
    /**
     * Guarda el índice de diccionarios disponibles.
     * 
     * @param diccionariosPaths Mapa que asocia nombres de diccionarios con sus rutas
     * @return true si la operación fue exitosa, false en caso contrario
     */
    boolean guardarIndice(Map<String, String> diccionariosPaths);
    
    /**
     * Carga un diccionario específico desde el sistema de persistencia.
     * 
     * @param nombre Nombre del diccionario a cargar
     * @return El diccionario cargado o null si no existe
     * @throws IOException Si hay problemas al leer el diccionario
     */
    Diccionario cargar(String nombre) throws IOException;
    
    /**
     * Carga el índice de diccionarios disponibles.
     * 
     * @return Mapa que asocia nombres de diccionarios con sus rutas
     */
    Map<String, String> cargarIndice();
    
    /**
     * Elimina un diccionario del sistema de persistencia.
     * 
     * @param nombre Nombre del diccionario a eliminar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    boolean eliminar(String nombre);
    
    /**
     * Verifica si un diccionario existe en el sistema de persistencia.
     * 
     * @param nombre Nombre del diccionario a verificar
     * @return true si el diccionario existe, false en caso contrario
     */
    boolean existe(String nombre);
    
    /**
     * Obtiene una lista de los nombres de todos los diccionarios disponibles.
     * 
     * @return Lista de nombres de diccionarios
     */
    List<String> listarDiccionarios();
    
    /**
     * Verifica si un diccionario sigue siendo válido (sus archivos existen).
     * 
     * @param nombre Nombre del diccionario a verificar
     * @return true si el diccionario es válido, false si falta algún archivo necesario
     */
    boolean verificarDiccionarioValido(String nombre);
}