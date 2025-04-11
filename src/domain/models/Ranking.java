package domain.models;

import java.io.Serializable;
import java.util.*;

/**
 * Clase que representa el ranking del juego Scrabble.
 * Almacena y gestiona las puntuaciones de los usuarios.
 */
public class Ranking implements Serializable {
    private Map<String, List<Integer>> puntuacionesPorUsuario;
    private Map<String, Integer> puntuacionMaximaPorUsuario;
    private Map<String, Double> puntuacionMediaPorUsuario;
    
    /**
     * Constructor de la clase Ranking.
     * Inicializa las estructuras de datos para almacenar puntuaciones.
     */
    public Ranking() {
        this.puntuacionesPorUsuario = new HashMap<>();
        this.puntuacionMaximaPorUsuario = new HashMap<>();
        this.puntuacionMediaPorUsuario = new HashMap<>();
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
     * Obtiene el ranking ordenado por puntuación máxima (de mayor a menor).
     * 
     * @return Lista de usuarios ordenados por puntuación máxima
     */
    public List<String> getRankingPorPuntuacionMaxima() {
        return puntuacionMaximaPorUsuario.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .map(Map.Entry::getKey)
            .toList();
    }
    
    /**
     * Obtiene el ranking ordenado por puntuación media (de mayor a menor).
     * 
     * @return Lista de usuarios ordenados por puntuación media
     */
    public List<String> getRankingPorPuntuacionMedia() {
        return puntuacionMediaPorUsuario.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .map(Map.Entry::getKey)
            .toList();
    }
    
    /**
     * Obtiene el ranking ordenado por número de partidas jugadas (de mayor a menor).
     * 
     * @return Lista de usuarios ordenados por número de partidas
     */
    public List<String> getRankingPorPartidasJugadas() {
        return puntuacionesPorUsuario.entrySet().stream()
            .sorted((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()))
            .map(Map.Entry::getKey)
            .toList();
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
        }
        
        return exists;
    }
}