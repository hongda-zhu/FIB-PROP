package scrabble.domain.persistences.interfaces;

import java.util.List;
import java.util.Map;

import scrabble.domain.models.Jugador;

/**
 * Interfaz que define las operaciones de persistencia para los jugadores.
 */
public interface RepositorioJugador {
    /**
     * Guarda un conjunto de jugadores en el sistema de persistencia.
     * 
     * @param jugadores Mapa de jugadores a guardar (nombre -> Jugador)
     * @return true si la operación fue exitosa, false en caso contrario
     */
    boolean guardarTodos(Map<String, Jugador> jugadores);
    
    /**
     * Carga todos los jugadores desde el sistema de persistencia.
     * 
     * @return Mapa con los jugadores cargados (nombre -> Jugador) o un mapa vacío si no hay datos
     */
    Map<String, Jugador> cargarTodos();
    
    /**
     * Busca un jugador por su nombre.
     * 
     * @param nombre Nombre del jugador a buscar
     * @return El jugador encontrado o null si no existe
     */
    Jugador buscarPorNombre(String nombre);
    
    /**
     * Obtiene una lista de todos los nombres de jugadores humanos.
     * 
     * @return Lista de nombres de jugadores humanos
     */
    List<String> obtenerNombresJugadoresHumanos();
    
    /**
     * Obtiene una lista de todos los nombres de jugadores IA.
     * 
     * @return Lista de nombres de jugadores IA
     */
    List<String> obtenerNombresJugadoresIA();
    
    /**
     * Obtiene una lista de todos los nombres de jugadores (humanos e IA).
     * 
     * @return Lista de nombres de todos los jugadores
     */
    List<String> obtenerNombresTodosJugadores();
}