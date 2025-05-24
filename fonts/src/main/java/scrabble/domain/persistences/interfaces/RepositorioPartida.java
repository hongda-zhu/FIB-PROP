package scrabble.domain.persistences.interfaces;

import java.util.List;
import scrabble.domain.controllers.subcontrollers.ControladorJuego;
import scrabble.excepciones.ExceptionPersistenciaFallida;

/**
 * Interfaz para el repositorio de partidas.
 * Define operaciones CRUD para la persistencia de partidas de Scrabble.
 */
public interface RepositorioPartida {
    
    /**
     * Guarda una partida en el repositorio.
     * 
     * @param id Identificador único de la partida.
     * @param partida Objeto ControladorJuego con el estado de la partida.
     * @return true si la operación fue exitosa, false en caso contrario.
     * @throws ExceptionPersistenciaFallida Si ocurre un error durante la persistencia.
     */
    boolean guardar(int id, ControladorJuego partida) throws ExceptionPersistenciaFallida;
    
    /**
     * Carga una partida desde el repositorio.
     * 
     * @param id Identificador de la partida a cargar.
     * @return El objeto ControladorJuego con el estado de la partida.
     * @throws ExceptionPersistenciaFallida Si ocurre un error durante la carga.
     */
    ControladorJuego cargar(int id) throws ExceptionPersistenciaFallida;
    
    /**
     * Elimina una partida del repositorio.
     * 
     * @param id Identificador de la partida a eliminar.
     * @return true si la operación fue exitosa, false en caso contrario.
     * @throws ExceptionPersistenciaFallida Si ocurre un error durante la eliminación.
     */
    boolean eliminar(int id) throws ExceptionPersistenciaFallida;
    
    /**
     * Lista todas las partidas guardadas en el repositorio.
     * 
     * @return Lista de identificadores de partidas.
     * @throws ExceptionPersistenciaFallida Si ocurre un error al acceder al repositorio.
     */
    List<Integer> listarTodas() throws ExceptionPersistenciaFallida;
    
    /**
     * Genera un nuevo identificador único para una partida.
     * 
     * @return Nuevo identificador único.
     * @throws ExceptionPersistenciaFallida Si ocurre un error al generar el ID.
     */
    int generarNuevoId() throws ExceptionPersistenciaFallida;
}