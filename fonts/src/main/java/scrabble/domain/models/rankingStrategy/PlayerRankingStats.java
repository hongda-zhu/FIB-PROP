package scrabble.domain.models.rankingStrategy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que encapsula todas las estadísticas de un jugador para el ranking.
 */
public class PlayerRankingStats implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String username;
    private List<Integer> puntuaciones;
    private int puntuacionMaxima;
    private double puntuacionMedia;
    private int partidasJugadas;
    private int victorias;
    
    /**
     * Constructor por defecto.
     * 
     * @param username Nombre del jugador
     */
    public PlayerRankingStats(String username) {
        this.username = username;
        this.puntuaciones = new ArrayList<>();
        this.puntuacionMaxima = 0;
        this.puntuacionMedia = 0.0;
        this.partidasJugadas = 0;
        this.victorias = 0;
    }
    
    /**
     * Añade una nueva puntuación y actualiza las estadísticas.
     * 
     * @param puntuacion Puntuación a añadir
     */
    public void addPuntuacion(int puntuacion) {
        if (puntuacion < 0) return;
        
        puntuaciones.add(puntuacion);
        
        // Actualizar puntuación máxima
        if (puntuacion > puntuacionMaxima) {
            puntuacionMaxima = puntuacion;
        }
        
        // Recalcular media
        double suma = 0;
        for (Integer p : puntuaciones) {
            suma += p;
        }
        puntuacionMedia = puntuaciones.isEmpty() ? 0 : suma / puntuaciones.size();
    }
    
    /**
     * Elimina una puntuación específica y actualiza las estadísticas.
     * 
     * @param puntuacion Puntuación a eliminar
     * @return true si se eliminó correctamente
     */
    public boolean removePuntuacion(int puntuacion) {
        boolean removed = puntuaciones.remove(Integer.valueOf(puntuacion));
        
        if (removed) {
            // Recalcular puntuación máxima
            puntuacionMaxima = 0;
            for (Integer p : puntuaciones) {
                if (p > puntuacionMaxima) {
                    puntuacionMaxima = p;
                }
            }
            
            // Recalcular media
            double suma = 0;
            for (Integer p : puntuaciones) {
                suma += p;
            }
            puntuacionMedia = puntuaciones.isEmpty() ? 0 : suma / puntuaciones.size();
        }
        
        return removed;
    }
    
    /**
     * Actualiza las estadísticas de partidas y victorias.
     * 
     * @param esVictoria true si el jugador ganó la partida
     */
    public void actualizarEstadisticas(boolean esVictoria) {
        partidasJugadas++;
        if (esVictoria) {
            victorias++;
        }
    }
    
    /**
     * Obtiene el total de puntuaciones acumuladas.
     * 
     * @return Suma de todas las puntuaciones
     */
    public int getPuntuacionTotal() {
        int total = 0;
        for (Integer p : puntuaciones) {
            total += p;
        }
        return total;
    }
    
    // Getters
    
    public String getUsername() {
        return username;
    }
    
    public List<Integer> getPuntuaciones() {
        return new ArrayList<>(puntuaciones);
    }
    
    public int getPuntuacionMaxima() {
        return puntuacionMaxima;
    }
    
    public double getPuntuacionMedia() {
        return puntuacionMedia;
    }
    
    public int getPartidasJugadas() {
        return partidasJugadas;
    }
    
    public int getVictorias() {
        return victorias;
    }
    
    /**
     * Comprueba si la lista de puntuaciones contiene una puntuación específica.
     * 
     * @param puntuacion Puntuación a comprobar
     * @return true si existe la puntuación
     */
    public boolean contienePuntuacion(int puntuacion) {
        return puntuaciones.contains(puntuacion);
    }
} 