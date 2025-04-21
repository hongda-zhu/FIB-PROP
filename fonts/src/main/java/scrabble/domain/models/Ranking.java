package scrabble.domain.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import scrabble.domain.models.rankingStrategy.MaximaScoreStrategy;
import scrabble.domain.models.rankingStrategy.PlayerRankingStats;
import scrabble.domain.models.rankingStrategy.RankingDataProvider;
import scrabble.domain.models.rankingStrategy.RankingOrderStrategy;
import scrabble.domain.models.rankingStrategy.RankingOrderStrategyFactory;

/**
 * Clase que representa el ranking del juego Scrabble.
 * Almacena y gestiona las puntuaciones de los usuarios utilizando el patrón Strategy.
 */
public class Ranking implements Serializable, RankingDataProvider {
    private static final long serialVersionUID = 1L;
    
    // Estructura simplificada para almacenar las estadísticas de los usuarios
    private Map<String, PlayerRankingStats> estadisticasUsuarios;
    private RankingOrderStrategy estrategiaActual;
    
    /**
     * Constructor de la clase Ranking.
     * Inicializa las estructuras de datos para almacenar puntuaciones.
     */
    public Ranking() {
        this.estadisticasUsuarios = new HashMap<>();
        
        // Por defecto usamos la estrategia de puntuación máxima
        this.estrategiaActual = RankingOrderStrategyFactory.createStrategy("maxima", this);
    }
    
    /**
     * Establece la estrategia de ordenación del ranking.
     * 
     * @param criterio Criterio de ordenación
     */
    public void setEstrategia(String criterio) {
        this.estrategiaActual = RankingOrderStrategyFactory.createStrategy(criterio, this);
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
        
        // Obtener o crear estadísticas para este usuario
        PlayerRankingStats stats = estadisticasUsuarios.computeIfAbsent(nombre, 
                                                                 PlayerRankingStats::new);
        // Agregar puntuación
        stats.addPuntuacion(puntuacion);
        
        return true;
    }
    
    /**
     * Actualiza los contadores de partidas jugadas y victorias para un usuario.
     * 
     * @param nombre Nombre del usuario
     * @param esVictoria Indica si es una victoria
     */
    public void actualizarEstadisticasUsuario(String nombre, boolean esVictoria) {
        // Obtener o crear estadísticas para este usuario
        PlayerRankingStats stats = estadisticasUsuarios.computeIfAbsent(nombre, 
                                                                 PlayerRankingStats::new);
        // Actualizar estadísticas
        stats.actualizarEstadisticas(esVictoria);
    }
    
    /**
     * Elimina una puntuación específica de un usuario.
     * 
     * @param nombre Nombre del usuario
     * @param puntuacion Puntuación a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public boolean eliminarPuntuacion(String nombre, int puntuacion) {
        if (!perteneceRanking(nombre)) {
            return false;
        }
        
        // Eliminar puntuación
        return estadisticasUsuarios.get(nombre).removePuntuacion(puntuacion);
    }
    
    /**
     * Verifica si existe una puntuación específica para un usuario.
     * 
     * @param nombre Nombre del usuario
     * @param puntuacion Puntuación a verificar
     * @return true si existe la puntuación, false en caso contrario
     */
    public boolean existePuntuacion(String nombre, int puntuacion) {
        return perteneceRanking(nombre) && 
               estadisticasUsuarios.get(nombre).contienePuntuacion(puntuacion);
    }
    
    /**
     * Obtiene todas las puntuaciones de un usuario específico.
     * 
     * @param nombre Nombre del usuario
     * @return Lista de puntuaciones del usuario
     */
    public List<Integer> getPuntuacionesUsuario(String nombre) {
        return perteneceRanking(nombre) ? 
               estadisticasUsuarios.get(nombre).getPuntuaciones() : 
               Collections.emptyList();
    }
    
    /**
     * Obtiene la puntuación máxima de un usuario específico.
     * 
     * @param nombre Nombre del usuario
     * @return Puntuación máxima del usuario
     */
    @Override
    public int getPuntuacionMaxima(String nombre) {
        return perteneceRanking(nombre) ? 
               estadisticasUsuarios.get(nombre).getPuntuacionMaxima() : 0;
    }
    
    /**
     * Obtiene la puntuación media de un usuario específico.
     * 
     * @param nombre Nombre del usuario
     * @return Puntuación media del usuario
     */
    @Override
    public double getPuntuacionMedia(String nombre) {
        return perteneceRanking(nombre) ? 
               estadisticasUsuarios.get(nombre).getPuntuacionMedia() : 0.0;
    }
    
    /**
     * Obtiene el número de partidas jugadas por un usuario.
     * 
     * @param nombre Nombre del usuario
     * @return Número de partidas jugadas
     */
    @Override
    public int getPartidasJugadas(String nombre) {
        return perteneceRanking(nombre) ? 
               estadisticasUsuarios.get(nombre).getPartidasJugadas() : 0;
    }
    
    /**
     * Obtiene el número de victorias de un usuario.
     * 
     * @param nombre Nombre del usuario
     * @return Número de victorias
     */
    @Override
    public int getVictorias(String nombre) {
        return perteneceRanking(nombre) ? 
               estadisticasUsuarios.get(nombre).getVictorias() : 0;
    }
    
    /**
     * Obtiene el ranking ordenado según la estrategia actual.
     * 
     * @return Lista de usuarios ordenados según la estrategia actual
     */
    public List<String> getRanking() {
        // Obtener los nombres de usuario
        List<String> usuarios = new ArrayList<>(estadisticasUsuarios.keySet());
        
        // Ordenar la lista usando la estrategia actual como Comparator
        Collections.sort(usuarios, estrategiaActual);
        
        return usuarios;
    }
    
    /**
     * Obtiene el ranking ordenado según una estrategia específica.
     * 
     * @param criterio Criterio de ordenación
     * @return Lista de usuarios ordenados según el criterio especificado
     */
    public List<String> getRanking(String criterio) {
        // Obtener los nombres de usuario
        List<String> usuarios = new ArrayList<>(estadisticasUsuarios.keySet());
        
        // Crear la estrategia específica
        RankingOrderStrategy estrategia = RankingOrderStrategyFactory.createStrategy(criterio, this);
        
        // Ordenar la lista usando la estrategia como Comparator
        Collections.sort(usuarios, estrategia);
        
        return usuarios;
    }
    
    /**
     * Obtiene una representación en formato mapa del ranking por puntuación máxima.
     * Para compatibilidad con código existente.
     * 
     * @return Mapa con usuarios y sus puntuaciones máximas
     */
    public Map<String, Integer> getMapaPuntuacionesMaximas() {
        Map<String, Integer> resultado = new HashMap<>();
        for (Map.Entry<String, PlayerRankingStats> entry : estadisticasUsuarios.entrySet()) {
            resultado.put(entry.getKey(), entry.getValue().getPuntuacionMaxima());
        }
        return resultado;
    }
    
    /**
     * Obtiene una representación en formato mapa del ranking por puntuación media.
     * Para compatibilidad con código existente.
     * 
     * @return Mapa con usuarios y sus puntuaciones medias
     */
    public Map<String, Double> getMapaPuntuacionesMedias() {
        Map<String, Double> resultado = new HashMap<>();
        for (Map.Entry<String, PlayerRankingStats> entry : estadisticasUsuarios.entrySet()) {
            resultado.put(entry.getKey(), entry.getValue().getPuntuacionMedia());
        }
        return resultado;
    }
    
    /**
     * Obtiene una representación en formato mapa del ranking por partidas jugadas.
     * Para compatibilidad con código existente.
     * 
     * @return Mapa con usuarios y su número de partidas jugadas
     */
    public Map<String, Integer> getMapaPartidasJugadas() {
        Map<String, Integer> resultado = new HashMap<>();
        for (Map.Entry<String, PlayerRankingStats> entry : estadisticasUsuarios.entrySet()) {
            resultado.put(entry.getKey(), entry.getValue().getPartidasJugadas());
        }
        return resultado;
    }
    
    /**
     * Obtiene una representación en formato mapa del ranking por victorias.
     * Para compatibilidad con código existente.
     * 
     * @return Mapa con usuarios y su número de victorias
     */
    public Map<String, Integer> getMapaVictorias() {
        Map<String, Integer> resultado = new HashMap<>();
        for (Map.Entry<String, PlayerRankingStats> entry : estadisticasUsuarios.entrySet()) {
            resultado.put(entry.getKey(), entry.getValue().getVictorias());
        }
        return resultado;
    }
    
    /**
     * Comprueba si un usuario forma parte del ranking o no.
     * 
     * @return true si tiene alguna puntuación, false en caso contrario.
     */    
    public boolean perteneceRanking(String nombre) {
        return estadisticasUsuarios.containsKey(nombre);
    }

    /**
     * Elimina todas las puntuaciones de un usuario.
     * 
     * @param nombre Nombre del usuario
     * @return true si se eliminaron correctamente, false en caso contrario
     */
    public boolean eliminarUsuario(String nombre) {
        return estadisticasUsuarios.remove(nombre) != null;
    }
    
    /**
     * Obtiene todos los nombres de usuario registrados en el ranking.
     * 
     * @return Conjunto de nombres de usuario
     */
    public Set<String> getUsuarios() {
        return new HashSet<>(estadisticasUsuarios.keySet());
    }
}