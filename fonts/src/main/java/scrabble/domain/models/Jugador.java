package scrabble.domain.models;

import java.io.Serializable;
import java.util.Map;

import scrabble.helpers.Tuple;

/**
 * Clase abstracta que define las operaciones y atributos básicos comunes a todos los jugadores.
 * Implementa la interfaz Serializable para permitir la persistencia de las partidas.
 */
public abstract class Jugador implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Mapa que representa el atril de fichas del jugador.
     * Las claves son las letras y los valores son la cantidad de cada letra.
     */
    protected Map<String, Integer> rack; 
    
    /**
     * Contador de turnos que el jugador ha pasado (skip).
     * Se utiliza para controlar el fin de la partida cuando todos los jugadores pasan turnos consecutivamente.
     */
    protected int skipTrack;
    
    /**
     * Nombre del jugador, utilizado como identificador único
     */
    protected String nombre;
    
    /**
     * Puntuación del jugador
     */
    protected int puntuacion;
    
    /**
     * Constructor para la clase Jugador
     * 
     * @param nombre Nombre del jugador, sirve como identificador único
     */
    public Jugador(String nombre) {
        this.nombre = nombre;
        this.puntuacion = 0;
        this.skipTrack = 0;
        this.rack = null; 
    }
    
    /**
     * Obtiene el nombre del jugador.
     * 
     * @return Nombre del jugador
     */
    public String getNombre() {
        return nombre;
    }
    
    /**
     * Establece la puntuación del jugador.
     * 
     * @param puntuacion Nueva puntuación
     */
    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }
    
    /**
     * Obtiene la puntuación actual del jugador.
     * 
     * @return Puntuación actual
     */
    public int getPuntuacion() {
        return puntuacion;
    }

    /**
     * Incrementa la puntuación del jugador con el valor especificado.
     * 
     * @param puntuacion Puntos a agregar a la puntuación actual
     */
    public void addPuntuacion(int puntuacion) {
        this.puntuacion += puntuacion;
    }

    /**
     * Inicializa el atril de fichas del jugador.
     * 
     * @param rack Mapa inicial de fichas (letra -> cantidad)
     */
    public void inicializarRack(Map<String, Integer> rack) {
        this.rack = rack;
    }

    /**
     * Obtiene el atril de fichas actual del jugador.
     * 
     * @return Mapa con las fichas disponibles
     */
    public Map<String, Integer> getRack() {
        return rack;
    }

    /**
     * Extrae una ficha del atril del jugador.
     * Si es la última ficha de ese tipo, la elimina completamente del atril.
     * 
     * @param ficha Letra a extraer
     * @return Tupla con la ficha extraída y la cantidad restante, o null si la ficha no existe
     */
    public Tuple<String, Integer> sacarFicha(String ficha) {
        if (rack.containsKey(ficha)) {
            int cantidad = rack.get(ficha);
            if (cantidad > 1) {
                rack.put(ficha, cantidad - 1);
            } else {
                rack.remove(ficha);
            }
            return new Tuple<>(ficha, cantidad - 1);
        }
        return null; 
    }

    /**
     * Agrega una ficha al atril del jugador.
     * Si ya existe la ficha, incrementa su cantidad.
     * 
     * @param ficha Letra a agregar
     */
    public void agregarFicha(String ficha) {
        if (rack.containsKey(ficha)) {
            int cantidad = rack.get(ficha);
            rack.put(ficha, cantidad + 1);
        } else {
            rack.put(ficha, 1);
        }
    }

    /**
     * Calcula la cantidad total de fichas en el atril del jugador.
     * 
     * @return Número total de fichas disponibles
     */
    public int getCantidadFichas() {
        return rack.values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * Incrementa el contador de turnos pasados.
     * Este contador se utiliza para determinar cuándo finaliza la partida.
     */
    public void addSkipTrack() {
        this.skipTrack += 1;
    }

    /**
     * Establece el contador de turnos pasados a un valor específico.
     * 
     * @param skipTrack Nuevo valor para el contador de turnos pasados
     */
    public void setSkipTrack(int skipTrack) {
        this.skipTrack = skipTrack;
    }

    /**
     * Obtiene el contador actual de turnos pasados.
     * 
     * @return Número de turnos pasados consecutivamente
     */
    public int getSkipTrack() {
        return skipTrack;
    }

    /**
     * Verifica si el jugador es una IA.
     * Cada clase hija debe implementar este método.
     * 
     * @return true si es una IA, false si es un jugador humano
     */
    public abstract boolean esIA();

    public void clearSkipTrack() {
        setSkipTrack(0);
    }
}