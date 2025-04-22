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
     * @pre El username no debe ser null.
     * @param username Nombre del jugador
     * @post Se inicializa una nueva instancia con el username especificado y todos los valores estadísticos a cero.
     * @throws NullPointerException si username es null
     */
    public PlayerRankingStats(String username) {
        if (username == null) {
            throw new NullPointerException("El nombre de usuario no puede ser null");
        }
        
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
     * @pre La puntuación debe ser no negativa.
     * @param puntuacion Puntuación a añadir
     * @post Si la puntuación es válida, se añade a la lista de puntuaciones y se actualizan las estadísticas.
     *       Si la puntuación es negativa, no se realiza ninguna acción.
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
     * @pre No hay precondiciones específicas.
     * @param puntuacion Puntuación a eliminar
     * @return true si se eliminó correctamente
     * @post Si la puntuación existía en la lista, se elimina y se actualizan todas las estadísticas,
     *       devolviendo true. Si no existía, no se realiza ninguna acción y se devuelve false.
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
     * @pre No hay precondiciones específicas.
     * @param esVictoria true si el jugador ganó la partida
     * @post Se incrementa el contador de partidas jugadas en 1.
     *       Si esVictoria es true, también se incrementa el contador de victorias en 1.
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
     * @pre No hay precondiciones específicas.
     * @return Puntuación total acumulada
     * @post Se devuelve un entero no negativo que representa la puntuación total acumulada del jugador.
     */
    public int getPuntuacionTotal() {
        return puntuacionTotalAcumulada;
    }
    
    /**
     * Establece la puntuación total acumulada directamente.
     * Este método NO afecta a las puntuaciones individuales ni a sus estadísticas (máxima, media).
     * 
     * @pre La puntuación debe ser no negativa.
     * @param puntuacionAgregada La nueva puntuación total acumulada
     * @post Si la puntuación es válida, se establece como la nueva puntuación total acumulada.
     *       Si la puntuación es negativa, no se realiza ninguna acción.
     */
    public void setPuntuacionTotal(int puntuacionAgregada) {
        if (puntuacionAgregada < 0) return;
        this.puntuacionTotalAcumulada = puntuacionAgregada;
    }
    
    // Getters
    
    /**
     * Obtiene el nombre de usuario.
     * 
     * @pre No hay precondiciones específicas.
     * @return Nombre de usuario
     * @post Se devuelve el string que representa el nombre de usuario asociado a estas estadísticas.
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Obtiene una copia de la lista de puntuaciones.
     * 
     * @pre No hay precondiciones específicas.
     * @return Lista de puntuaciones
     * @post Se devuelve una nueva lista que contiene todas las puntuaciones registradas para este usuario.
     */
    public List<Integer> getPuntuaciones() {
        return new ArrayList<>(puntuaciones);
    }
    
    /**
     * Obtiene la puntuación máxima.
     * 
     * @pre No hay precondiciones específicas.
     * @return Puntuación máxima
     * @post Se devuelve un entero no negativo que representa la puntuación máxima registrada.
     */
    public int getPuntuacionMaxima() {
        return puntuacionMaxima;
    }
    
    /**
     * Obtiene la puntuación media.
     * 
     * @pre No hay precondiciones específicas.
     * @return Puntuación media
     * @post Se devuelve un número decimal no negativo que representa la puntuación media.
     */
    public double getPuntuacionMedia() {
        return puntuacionMedia;
    }
    
    /**
     * Obtiene el número de partidas jugadas.
     * 
     * @pre No hay precondiciones específicas.
     * @return Número de partidas jugadas
     * @post Se devuelve un entero no negativo que representa el número de partidas jugadas.
     */
    public int getPartidasJugadas() {
        return partidasJugadas;
    }
    
    /**
     * Obtiene el número de victorias.
     * 
     * @pre No hay precondiciones específicas.
     * @return Número de victorias
     * @post Se devuelve un entero no negativo que representa el número de victorias.
     */
    public int getVictorias() {
        return victorias;
    }
    
    /**
     * Comprueba si la lista de puntuaciones contiene una puntuación específica.
     * 
     * @pre No hay precondiciones específicas.
     * @param puntuacion Puntuación a comprobar
     * @return true si existe la puntuación
     * @post Se devuelve true si la puntuación especificada existe en la lista de puntuaciones, false en caso contrario.
     */
    public boolean contienePuntuacion(int puntuacion) {
        return puntuaciones.contains(puntuacion);
    }
} 