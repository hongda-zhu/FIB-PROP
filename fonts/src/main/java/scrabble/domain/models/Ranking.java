package scrabble.domain.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import scrabble.domain.models.rankingStrategy.MaximaScoreStrategy;
import scrabble.domain.models.rankingStrategy.RankingOrderStrategy;
import scrabble.domain.models.rankingStrategy.RankingOrderStrategyFactory;

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
     * @param nombre Nombre del usuario
     * @param puntuacion Puntuación a agregar
     * @return true si se agregó correctamente, false en caso contrario
     */
    public boolean agregarPuntuacion(String nombre, int puntuacion) {
        if (puntuacion < 0) {
            return false; // No se permiten puntuaciones negativas
        }
        
        // Agregar puntuación a la lista de puntuaciones del usuario
        puntuacionesPorUsuario.computeIfAbsent(nombre, k -> new ArrayList<>()).add(puntuacion);
        
        // Actualizar puntuación máxima
        puntuacionMaximaPorUsuario.put(nombre, 
            Math.max(puntuacionMaximaPorUsuario.getOrDefault(nombre, 0), puntuacion));
        
        // Recalcular puntuación media
        List<Integer> puntuaciones = puntuacionesPorUsuario.get(nombre);
        double media = puntuaciones.stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0.0);
        puntuacionMediaPorUsuario.put(nombre, media);
        
        return true;
    }
    
    /**
     * Actualiza los contadores de partidas jugadas y victorias para un usuario.
     * 
     * @param nombre Nombre del usuario
     * @param esVictoria Indica si es una victoria
     */
    public void actualizarEstadisticasUsuario(String nombre, boolean esVictoria) {
        // Incrementar contador de partidas jugadas
        partidasJugadasPorUsuario.put(nombre, 
            partidasJugadasPorUsuario.getOrDefault(nombre, 0) + 1);
        
        // Si es victoria, incrementar contador de victorias
        if (esVictoria) {
            victoriasUsuario.put(nombre, 
                victoriasUsuario.getOrDefault(nombre, 0) + 1);
        }
    }
    
    /**
     * Elimina una puntuación específica de un usuario.
     * 
     * @param nombre Nombre del usuario
     * @param puntuacion Puntuación a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public boolean eliminarPuntuacion(String nombre, int puntuacion) {
        if (!puntuacionesPorUsuario.containsKey(nombre)) {
            return false;
        }
        
        List<Integer> puntuaciones = puntuacionesPorUsuario.get(nombre);
        boolean removed = puntuaciones.remove(Integer.valueOf(puntuacion));
        
        if (removed) {
            // Recalcular puntuación máxima
            puntuacionMaximaPorUsuario.put(nombre, 
                puntuaciones.isEmpty() ? 0 : Collections.max(puntuaciones));
            
            // Recalcular puntuación media
            double media = puntuaciones.isEmpty() ? 0.0 : puntuaciones.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
            puntuacionMediaPorUsuario.put(nombre, media);
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Verifica si existe una puntuación específica para un usuario.
     * 
     * @param nombre Nombre del usuario
     * @param puntuacion Puntuación a verificar
     * @return true si existe la puntuación, false en caso contrario
     */
    public boolean existePuntuacion(String nombre, int puntuacion) {
        return puntuacionesPorUsuario.containsKey(nombre) && 
               puntuacionesPorUsuario.get(nombre).contains(puntuacion);
    }
    
    /**
     * Obtiene todas las puntuaciones de un usuario específico.
     * 
     * @param nombre Nombre del usuario
     * @return Lista de puntuaciones del usuario
     */
    public List<Integer> getPuntuacionesUsuario(String nombre) {
        return puntuacionesPorUsuario.getOrDefault(nombre, Collections.emptyList());
    }
    
    /**
     * Obtiene la puntuación máxima de un usuario específico.
     * 
     * @param nombre Nombre del usuario
     * @return Puntuación máxima del usuario
     */
    public int getPuntuacionMaxima(String nombre) {
        return puntuacionMaximaPorUsuario.getOrDefault(nombre, 0);
    }
    
    /**
     * Obtiene la puntuación media de un usuario específico.
     * 
     * @param nombre Nombre del usuario
     * @return Puntuación media del usuario
     */
    public double getPuntuacionMedia(String nombre) {
        return puntuacionMediaPorUsuario.getOrDefault(nombre, 0.0);
    }
    
    /**
     * Obtiene el número de partidas jugadas por un usuario.
     * 
     * @param nombre Nombre del usuario
     * @return Número de partidas jugadas
     */
    public int getPartidasJugadas(String nombre) {
        return partidasJugadasPorUsuario.getOrDefault(nombre, 0);
    }
    
    /**
     * Obtiene el número de victorias de un usuario.
     * 
     * @param nombre Nombre del usuario
     * @return Número de victorias
     */
    public int getVictorias(String nombre) {
        return victoriasUsuario.getOrDefault(nombre, 0);
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
     * Comprueba si un usuario forma parte del ranking o no.
     * 
     * @return true si tiene alguna puntuación, false en caso contrario.
     */    
    public boolean perteneceRanking(String nombre) {
        return puntuacionesPorUsuario.containsKey(nombre);
    }

    /**
     * Elimina todas las puntuaciones de un usuario.
     * 
     * @param nombre Nombre del usuario
     * @return true si se eliminaron correctamente, false en caso contrario
     */
    public boolean eliminarUsuario(String nombre) {
        boolean exists = perteneceRanking(nombre);
        
        if (exists) {
            puntuacionesPorUsuario.remove(nombre);
            puntuacionMaximaPorUsuario.remove(nombre);
            puntuacionMediaPorUsuario.remove(nombre);
            partidasJugadasPorUsuario.remove(nombre);
            victoriasUsuario.remove(nombre);
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