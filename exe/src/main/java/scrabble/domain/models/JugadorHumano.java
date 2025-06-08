package scrabble.domain.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase que representa a un jugador humano en el sistema.
 * 
 * Esta clase extiende la funcionalidad base de Jugador añadiendo características específicas
 * para jugadores humanos, incluyendo el estado de participación en partidas y el seguimiento
 * de la partida actual. Los jugadores humanos persisten en el sistema y mantienen sus
 * estadísticas entre sesiones de juego.
 * 
 * 
 * @version 2.0
 * @since 1.0
 */
public class JugadorHumano extends Jugador {
    private static final long serialVersionUID = 1L;
    private boolean enPartida;
    private String nombrePartidaActual; // Nombre de la partida en la que está participando

    /**
     * Constructor de la clase JugadorHumano.
     * Crea un nuevo jugador humano con el nombre especificado, inicializando
     * su estado como no participante en ninguna partida.
     * 
     * @pre El nombre no debe ser null.
     *      El nombre debe ser único en el sistema.
     * @param nombre Nombre del jugador, sirve como identificador único
     * @post Se crea una nueva instancia de JugadorHumano con el nombre especificado,
     *       marcado como no en partida y con un rack vacío inicializado.
     * @throws NullPointerException si el nombre es null
     */
    public JugadorHumano(String nombre) {
        super(nombre);
        this.enPartida = false;
        this.nombrePartidaActual = ""; // Inicialmente sin partida asignada
        setRackInterno(new HashMap<>()); // Inicialmente sin fichas en el rack
    }
    
    /**
     * Establece si el jugador está actualmente en una partida.
     * 
     * @pre No hay precondiciones específicas.
     * @param enPartida true si está en partida, false en caso contrario
     * @post Si enPartida es true, el jugador se marca como actualmente en una partida.
     *       Si enPartida es false, el jugador se marca como no en partida y se limpia 
     *       el nombre de la partida actual.
     */
    public void setEnPartida(boolean enPartida) {
        this.enPartida = enPartida;
        
        // Si ya no está en partida, limpiamos el nombre de la partida
        if (!enPartida) {
            this.nombrePartidaActual = "";
        }
    }
    
    /**
     * Verifica si el jugador está actualmente en una partida.
     * 
     * @pre No hay precondiciones específicas.
     * @return true si está en partida, false en caso contrario
     * @post Se devuelve el estado actual de participación en partida del jugador.
     */
    public boolean isEnPartida() {
        return enPartida;
    }

    /**
     * Obtiene el nombre de la partida en la que está participando el jugador.
     * 
     * @pre No hay precondiciones específicas.
     * @return Nombre de la partida actual o cadena vacía si no está en partida
     * @post Se devuelve el nombre de la partida actual o cadena vacía si no está en partida.
     */
    public String getNombrePartidaActual() {
        return nombrePartidaActual;
    }

    /**
     * Establece el nombre de la partida en la que está participando el jugador.
     * 
     * @pre El nombre de la partida no debe ser null.
     * @param nombrePartida Nombre de la partida
     * @post El nombre de la partida en la que participa el jugador se actualiza al valor especificado.
     * @throws NullPointerException si el nombre de la partida es null
     */
    public void setNombrePartidaActual(String nombrePartida) {
        this.nombrePartidaActual = nombrePartida;
    }

    /**
     * Indica si el jugador está controlado por la IA.
     * 
     * @pre No hay precondiciones específicas.
     * @return false, ya que este jugador es humano
     * @post Siempre se devuelve false porque un jugador humano nunca está controlado por la IA.
     */
    @Override
    public boolean esIA() {
        return false;
    }

    /**
     * Obtiene las fichas del jugador.
     * 
     * @pre No hay precondiciones específicas.
     * @return Mapa con las fichas del jugador
     * @post Se devuelve un mapa con las fichas del jugador actualmente en su rack.
     */
    public Map<String, Integer> getFichas() {
        return getRackInterno();
    }

    /**
     * Devuelve una representación en forma de String del jugador.
     * 
     * @pre No hay precondiciones específicas.
     * @return String con el nombre del jugador
     * @post Se devuelve una cadena con el formato "JugadorHumano: [nombre][enPartida][nombrePartidaActual]".
     */
    @Override
    public String toString() {
        return "JugadorHumano{" +
               "nombre='" + getNombreInterno() + '\'' +
               ", enPartida=" + enPartida +
               ", nombrePartidaActual='" + nombrePartidaActual + '\'' +
               '}';
    }
}