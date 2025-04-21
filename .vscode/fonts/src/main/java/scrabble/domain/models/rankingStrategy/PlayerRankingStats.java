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
    private int puntuacionTotalAcumulada;
    
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
        this.puntuacionTotalAcumulada = 0;
    }
    
    /**
     * Añade una nueva puntuación y actualiza las estadísticas.
     * También suma esta puntuación a la puntuación total acumulada.
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
        
        // Actualizar puntuación total acumulada
        puntuacionTotalAcumulada += puntuacion;
    }
    
    /**
     * Elimina una puntuación específica y actualiza las estadísticas.
     * También resta esta puntuación de la puntuación total acumulada.
     * 
     * @param puntuacion Puntuación a eliminar
     * @return true si se eliminó correctamente
     */
    public boolean removePuntuacion(int puntuacion) {
        boolean removed = puntuaciones.remove(Integer.valueOf(puntuacion));
        
        if (removed) {
            // Restar de la puntuación total acumulada
            puntuacionTotalAcumulada -= puntuacion;
            if (puntuacionTotalAcumulada < 0) {
                puntuacionTotalAcumulada = 0; // Evitar puntuaciones negativas
            }
            
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
     * @return Puntuación total acumulada
     */
    public int getPuntuacionTotal() {
        return puntuacionTotalAcumulada;
    }
    
    /**
     * Establece la puntuación total acumulada directamente.
     * Este método NO afecta a las puntuaciones individuales ni a sus estadísticas (máxima, media).
     * 
     * @param puntuacionAgregada La nueva puntuación total acumulada
     */
    public void setPuntuacionTotal(int puntuacionAgregada) {
        if (puntuacionAgregada < 0) return;
        this.puntuacionTotalAcumulada = puntuacionAgregada;
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