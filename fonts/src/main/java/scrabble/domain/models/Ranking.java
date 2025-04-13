package domain.models;

import domain.models.rankingStrategy.*;

import java.io.Serializable;
import java.util.*;

/**
 * Clase que representa el ranking del juego Scrabble.
 * Almacena y gestiona las puntuaciones de los usuarios utilizando el patrón Strategy.
 */
public class Ranking implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Map<String, List<Integer>> puntuacionesPorUsuario;
    private Map<String, Integer> puntuacionMaximaPorUsuario;
    private Map<String, Double> puntuacionMediaPorUsuario;
    private Map<String, Integer> partidasJugadasPorUsuario;
    private Map<String, Integer> victoriasUsuario;
    
    private RankingOrderStrategy estrategiaActual;
    
    /**
     * Constructor de la clase Ranking.
     * Inicializa las estructuras de datos para almacenar puntuaciones.
     */
    public Ranking() {
        this.puntuacionesPorUsuario = new HashMap<>();
        this.puntuacionMaximaPorUsuario = new HashMap<>();
        this.puntuacionMediaPorUsuario = new HashMap<>();
        this.partidasJugadasPorUsuario = new HashMap<>();
        this.victoriasUsuario = new HashMap<>();
        
        // Por defecto usamos la estrategia de puntuación máxima
        this.estrategiaActual = new MaximaScoreStrategy();
    }
    
    /**
     * Establece la estrategia de ordenación del ranking.
     * 
     * @param criterio Criterio de ordenación
     */
    public void setEstrategia(String criterio) {
        this.estrategiaActual = RankingOrderStrategyFactory.createStrategy(criterio);
    }
    
    /**
     * Obtiene el nombre de la estrategia actual.
     * 
     * @return Nombre de la estrategia
     */
    public String getEstrategiaActual() {
        return estrategiaActual.getNombre();
    }
    
    /**
     * Agrega una nueva puntuación para un usuario.
     * 
     * @param username Nombre del usuario
     * @param puntuacion Puntuación a agregar
     * @return true si se agregó correctamente, false en caso contrario
     */
    public boolean agregarPuntuacion(String username, int puntuacion) {
        if (puntuacion < 0) {
            return false; // No se permiten puntuaciones negativas
        }
        
        // Agregar puntuación a la lista de puntuaciones del usuario
        puntuacionesPorUsuario.computeIfAbsent(username, k -> new ArrayList<>()).add(puntuacion);
        
        // Actualizar puntuación máxima
        puntuacionMaximaPorUsuario.put(username, 
            Math.max(puntuacionMaximaPorUsuario.getOrDefault(username, 0), puntuacion));
        
        // Recalcular puntuación media
        List<Integer> puntuaciones = puntuacionesPorUsuario.get(username);
        double media = puntuaciones.stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0.0);
        puntuacionMediaPorUsuario.put(username, media);
        
        return true;
    }
    
    /**
     * Actualiza los contadores de partidas jugadas y victorias para un usuario.
     * 
     * @param username Nombre del usuario
     * @param esVictoria Indica si es una victoria
     */
    public void actualizarEstadisticasUsuario(String username, boolean esVictoria) {
        // Incrementar contador de partidas jugadas
        partidasJugadasPorUsuario.put(username, 
            partidasJugadasPorUsuario.getOrDefault(username, 0) + 1);
        
        // Si es victoria, incrementar contador de victorias
        if (esVictoria) {
            victoriasUsuario.put(username, 
                victoriasUsuario.getOrDefault(username, 0) + 1);
        }
    }
    
    /**
     * Elimina una puntuación específica de un usuario.
     * 
     * @param username Nombre del usuario
     * @param puntuacion Puntuación a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public boolean eliminarPuntuacion(String username, int puntuacion) {
        if (!puntuacionesPorUsuario.containsKey(username)) {
            return false;
        }
        
        List<Integer> puntuaciones = puntuacionesPorUsuario.get(username);
        boolean removed = puntuaciones.remove(Integer.valueOf(puntuacion));
        
        if (removed) {
            // Recalcular puntuación máxima
            puntuacionMaximaPorUsuario.put(username, 
                puntuaciones.isEmpty() ? 0 : Collections.max(puntuaciones));
            
            // Recalcular puntuación media
            double media = puntuaciones.isEmpty() ? 0.0 : puntuaciones.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
            puntuacionMediaPorUsuario.put(username, media);
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Verifica si existe una puntuación específica para un usuario.
     * 
     * @param username Nombre del usuario
     * @param puntuacion Puntuación a verificar
     * @return true si existe la puntuación, false en caso contrario
     */
    public boolean existePuntuacion(String username, int puntuacion) {
        return puntuacionesPorUsuario.containsKey(username) && 
               puntuacionesPorUsuario.get(username).contains(puntuacion);
    }
    
    /**
     * Obtiene todas las puntuaciones de un usuario específico.
     * 
     * @param username Nombre del usuario
     * @return Lista de puntuaciones del usuario
     */
    public List<Integer> getPuntuacionesUsuario(String username) {
        return puntuacionesPorUsuario.getOrDefault(username, Collections.emptyList());
    }
    
    /**
     * Obtiene la puntuación máxima de un usuario específico.
     * 
     * @param username Nombre del usuario
     * @return Puntuación máxima del usuario
     */
    public int getPuntuacionMaxima(String username) {
        return puntuacionMaximaPorUsuario.getOrDefault(username, 0);
    }
    
    /**
     * Obtiene la puntuación media de un usuario específico.
     * 
     * @param username Nombre del usuario
     * @return Puntuación media del usuario
     */
    public double getPuntuacionMedia(String username) {
        return puntuacionMediaPorUsuario.getOrDefault(username, 0.0);
    }
    
    /**
     * Obtiene el número de partidas jugadas por un usuario.
     * 
     * @param username Nombre del usuario
     * @return Número de partidas jugadas
     */
    public int getPartidasJugadas(String username) {
        return partidasJugadasPorUsuario.getOrDefault(username, 0);
    }
    
    /**
     * Obtiene el número de victorias de un usuario.
     * 
     * @param username Nombre del usuario
     * @return Número de victorias
     */
    public int getVictorias(String username) {
        return victoriasUsuario.getOrDefault(username, 0);
    }
    
    /**
     * Obtiene el ranking ordenado según la estrategia actual.
     * 
     * @return Lista de usuarios ordenados según la estrategia actual
     */
    public List<String> getRanking() {
        return estrategiaActual.ordenarRanking(
            puntuacionesPorUsuario,
            puntuacionMaximaPorUsuario,
            puntuacionMediaPorUsuario,
            partidasJugadasPorUsuario,
            victoriasUsuario
        );
    }
    
    /**
     * Obtiene el ranking ordenado según una estrategia específica.
     * 
     * @param criterio Criterio de ordenación
     * @return Lista de usuarios ordenados según el criterio especificado
     */
    public List<String> getRanking(String criterio) {
        RankingOrderStrategy estrategia = RankingOrderStrategyFactory.createStrategy(criterio);
        
        return estrategia.ordenarRanking(
            puntuacionesPorUsuario,
            puntuacionMaximaPorUsuario,
            puntuacionMediaPorUsuario,
            partidasJugadasPorUsuario,
            victoriasUsuario
        );
    }
    
    /**
     * Obtiene una representación en formato mapa del ranking por puntuación máxima.
     * 
     * @return Mapa con usuarios y sus puntuaciones máximas
     */
    public Map<String, Integer> getMapaPuntuacionesMaximas() {
        return new HashMap<>(puntuacionMaximaPorUsuario);
    }
    
    /**
     * Obtiene una representación en formato mapa del ranking por puntuación media.
     * 
     * @return Mapa con usuarios y sus puntuaciones medias
     */
    public Map<String, Double> getMapaPuntuacionesMedias() {
        return new HashMap<>(puntuacionMediaPorUsuario);
    }
    
    /**
     * Obtiene una representación en formato mapa del ranking por partidas jugadas.
     * 
     * @return Mapa con usuarios y su número de partidas jugadas
     */
    public Map<String, Integer> getMapaPartidasJugadas() {
        return new HashMap<>(partidasJugadasPorUsuario);
    }
    
    /**
     * Obtiene una representación en formato mapa del ranking por victorias.
     * 
     * @return Mapa con usuarios y su número de victorias
     */
    public Map<String, Integer> getMapaVictorias() {
        return new HashMap<>(victoriasUsuario);
    }
    
    /**
     * Elimina todas las puntuaciones de un usuario.
     * 
     * @param username Nombre del usuario
     * @return true si se eliminaron correctamente, false en caso contrario
     */
    public boolean eliminarUsuario(String username) {
        boolean exists = puntuacionesPorUsuario.containsKey(username);
        
        if (exists) {
            puntuacionesPorUsuario.remove(username);
            puntuacionMaximaPorUsuario.remove(username);
            puntuacionMediaPorUsuario.remove(username);
            partidasJugadasPorUsuario.remove(username);
            victoriasUsuario.remove(username);
        }
        
        return exists;
    }
    
    /**
     * Obtiene todos los nombres de usuario registrados en el ranking.
     * 
     * @return Conjunto de nombres de usuario
     */
    public Set<String> getUsuarios() {
        return new HashSet<>(puntuacionesPorUsuario.keySet());
    }
}