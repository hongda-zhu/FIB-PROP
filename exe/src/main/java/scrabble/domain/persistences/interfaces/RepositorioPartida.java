package scrabble.domain.persistences.interfaces;

import java.util.List;
import scrabble.domain.controllers.subcontrollers.ControladorJuego;
import scrabble.excepciones.ExceptionPersistenciaFallida;

/**
 * Interfaz para el repositorio de partidas.
 * 
 * Define operaciones CRUD completas para la persistencia de partidas de Scrabble,
 * incluyendo gestión de estados complejos de juego, generación automática de
 * identificadores únicos y manejo robusto de errores. Implementa el patrón
 * Repository para abstraer los detalles de almacenamiento.
 * 
 * Funcionalidades principales:
 * - Operaciones CRUD completas para partidas
 * - Generación automática de IDs únicos
 * - Serialización de estados complejos de juego
 * - Gestión de errores específicos de persistencia
 * - Listado y consulta de partidas guardadas
 * 
 * @version 2.0
 * @since 1.0
 */
public interface RepositorioPartida {
    
    /**
     * Guarda una partida completa en el repositorio con su estado actual.
     * 
     * @param id Identificador único de la partida para almacenamiento.
     * @param partida Objeto ControladorJuego con el estado completo de la partida a persistir.
     * @return true si la operación fue exitosa, false en caso contrario.
     * @throws ExceptionPersistenciaFallida Si ocurre un error durante la serialización o escritura.
     * @pre id >= 0 && partida != null
     * @post Si retorna true, la partida queda persistida con el ID especificado
     */
    boolean guardar(int id, ControladorJuego partida) throws ExceptionPersistenciaFallida;
    
    /**
     * Carga una partida completa desde el repositorio con su estado.
     * 
     * @param id Identificador de la partida a cargar desde el almacenamiento.
     * @return El objeto ControladorJuego con el estado completo de la partida restaurado.
     * @throws ExceptionPersistenciaFallida Si ocurre un error durante la lectura o deserialización.
     * @pre id >= 0
     * @post Retorna un ControladorJuego válido con el estado completo restaurado
     */
    ControladorJuego cargar(int id) throws ExceptionPersistenciaFallida;
    
    /**
     * Elimina una partida completamente del repositorio.
     * 
     * @param id Identificador de la partida a eliminar del sistema.
     * @return true si la operación fue exitosa, false en caso contrario.
     * @throws ExceptionPersistenciaFallida Si ocurre un error durante la eliminación de archivos.
     * @pre id >= 0
     * @post Si retorna true, la partida y sus archivos han sido eliminados
     */
    boolean eliminar(int id) throws ExceptionPersistenciaFallida;
    
    /**
     * Lista todas las partidas guardadas disponibles en el repositorio.
     * 
     * @return Lista de identificadores de partidas disponibles para cargar.
     * @throws ExceptionPersistenciaFallida Si ocurre un error al acceder al directorio de partidas.
     * @post Retorna una lista válida de IDs (puede estar vacía si no hay partidas)
     */
    List<Integer> listarTodas() throws ExceptionPersistenciaFallida;
    
    /**
     * Genera un nuevo identificador único para una partida.
     * 
     * @return Nuevo identificador único garantizado para uso en el sistema.
     * @throws ExceptionPersistenciaFallida Si ocurre un error al acceder al sistema de IDs.
     * @post Retorna un ID único que no está en uso por ninguna partida existente
     */
    int generarNuevoId() throws ExceptionPersistenciaFallida;
}