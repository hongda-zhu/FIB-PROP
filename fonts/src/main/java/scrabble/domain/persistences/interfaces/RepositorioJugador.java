package scrabble.domain.persistences.interfaces;

import java.util.List;
import java.util.Map;

import scrabble.domain.models.Jugador;

/**
 * Interfaz que define las operaciones de persistencia para los jugadores.
 * 
 * Esta interfaz establece el contrato para la gestión de persistencia de jugadores
 * en el sistema, incluyendo operaciones de almacenamiento, carga y consulta tanto
 * para jugadores humanos como IA. Implementa el patrón Repository para abstraer
 * los detalles de almacenamiento y proporcionar una interfaz uniforme.
 * 
 * Funcionalidades principales:
 * - Operaciones de persistencia masiva de jugadores
 * - Búsqueda y consulta de jugadores por nombre
 * - Filtrado por tipo de jugador (humano/IA)
 * - Listado de jugadores por categorías
 * - Gestión unificada de usuarios del sistema
 * 
 * @version 2.0
 * @since 1.0
 */
public interface RepositorioJugador {
    /**
     * Guarda un conjunto de jugadores en el sistema de persistencia.
     * 
     * @param jugadores Mapa de jugadores a persistir (nombre -> Jugador) incluyendo humanos e IA.
     * @return true si la operación fue exitosa, false en caso contrario.
     * @pre jugadores != null
     * @post Si retorna true, todos los jugadores quedan persistidos en el sistema
     */
    boolean guardarTodos(Map<String, Jugador> jugadores);
    
    /**
     * Carga todos los jugadores desde el sistema de persistencia.
     * 
     * @return Mapa con los jugadores cargados (nombre -> Jugador) o un mapa vacío si no hay datos.
     * @post Retorna un mapa válido con todos los jugadores del sistema
     */
    Map<String, Jugador> cargarTodos();
    
    /**
     * Busca un jugador específico por su nombre en el sistema.
     * 
     * @param nombre Nombre único del jugador a buscar en el sistema.
     * @return El jugador encontrado con todos sus datos, o null si no existe.
     * @pre nombre != null && !nombre.isEmpty()
     * @post Retorna el jugador sin modificar el estado del sistema
     */
    Jugador buscarPorNombre(String nombre);
    
    /**
     * Obtiene una lista de todos los nombres de jugadores humanos registrados.
     * 
     * @return Lista de nombres de jugadores humanos únicamente.
     * @post Retorna una lista válida filtrada por tipo humano
     */
    List<String> obtenerNombresJugadoresHumanos();
    
    /**
     * Obtiene una lista de todos los nombres de jugadores IA del sistema.
     * 
     * @return Lista de nombres de jugadores IA únicamente.
     * @post Retorna una lista válida filtrada por tipo IA
     */
    List<String> obtenerNombresJugadoresIA();
    
    /**
     * Obtiene una lista de todos los nombres de jugadores (humanos e IA).
     * 
     * @return Lista completa de nombres de todos los jugadores del sistema.
     * @post Retorna una lista válida con todos los jugadores sin filtrar
     */
    List<String> obtenerNombresTodosJugadores();
}