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
    private Map<String, Integer> rack; 
    
    /**
     * Contador de turnos que el jugador ha pasado (skip).
     * Se utiliza para controlar el fin de la partida cuando todos los jugadores pasan turnos consecutivamente.
     */
    private int skipTrack;
    
    /**
     * Nombre del jugador, utilizado como identificador único
     */
    private String nombre;
    
    /**
     * Constructor para la clase Jugador
     * 
     * @pre El nombre no debe ser null y debe ser único en el sistema.
     * @param nombre Nombre del jugador, sirve como identificador único
     * @post Se crea una nueva instancia de Jugador con el nombre especificado,
     *       el contador de skips inicializado a 0 y el rack a null.
     * @throws NullPointerException si el nombre es null
     */
    public Jugador(String nombre) {
        this.nombre = nombre;
        this.skipTrack = 0;
        this.rack = null; 
    }
    
    /**
     * Obtiene el nombre del jugador.
     * 
     * @pre No hay precondiciones específicas.
     * @return Nombre del jugador
     * @post Se devuelve el nombre del jugador.
     */
    public String getNombre() {
        return nombre;
    }
    
    /**
     * Inicializa el atril de fichas del jugador.
     * 
     * @pre El rack no debe ser null.
     * @param rack Mapa inicial de fichas (letra -> cantidad)
     * @post El atril de fichas del jugador se inicializa con el mapa especificado.
     * @throws NullPointerException si rack es null
     */
    public void inicializarRack(Map<String, Integer> rack) {
        this.rack = rack;
    }

    /**
     * Obtiene el atril de fichas actual del jugador.
     * 
     * @pre No hay precondiciones específicas.
     * @return Mapa con las fichas disponibles
     * @post Se devuelve el mapa actual de fichas del jugador, que puede ser null
     *       si el rack no ha sido inicializado.
     */
    public Map<String, Integer> getRack() {
        return rack;
    }

    /**
     * Extrae una ficha del atril del jugador.
     * Si es la última ficha de ese tipo, la elimina completamente del atril.
     * 
     * @pre El rack debe estar inicializado y la ficha no debe ser null.
     * @param ficha Letra a extraer
     * @return Tupla con la ficha extraída y la cantidad restante, o null si la ficha no existe
     * @post Si la ficha existe en el rack, se decrementa su cantidad o se elimina si es la última,
     *       y se devuelve una tupla con la ficha y la cantidad restante.
     *       Si la ficha no existe en el rack, se devuelve null.
     * @throws NullPointerException si ficha es null o si el rack no ha sido inicializado
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
     * @pre El rack debe estar inicializado y la ficha no debe ser null.
     * @param ficha Letra a agregar
     * @post La ficha se añade al rack del jugador. Si ya existía, se incrementa su cantidad.
     * @throws NullPointerException si ficha es null o si el rack no ha sido inicializado
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
     * @pre El rack debe estar inicializado.
     * @return Número total de fichas disponibles
     * @post Se devuelve un entero no negativo que representa el número total de fichas
     *       en el rack del jugador.
     * @throws NullPointerException si el rack no ha sido inicializado
     */
    public int getCantidadFichas() {
        return rack.values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * Incrementa el contador de turnos pasados.
     * Este contador se utiliza para determinar cuándo finaliza la partida.
     * 
     * @pre No hay precondiciones específicas.
     * @post El contador de turnos pasados se incrementa en 1.
     */
    public void addSkipTrack() {
        this.skipTrack += 1;
    }

    /**
     * Establece el contador de turnos pasados a un valor específico.
     * 
     * @pre El skipTrack debe ser no negativo.
     * @param skipTrack Nuevo valor para el contador de turnos pasados
     * @post El contador de turnos pasados se actualiza al valor especificado.
     * @throws IllegalArgumentException si skipTrack es negativo
     */
    public void setSkipTrack(int skipTrack) {
        this.skipTrack = skipTrack;
    }

    /**
     * Obtiene el contador actual de turnos pasados.
     * 
     * @pre No hay precondiciones específicas.
     * @return Número de turnos pasados consecutivamente
     * @post Se devuelve un entero no negativo que representa el número de turnos
     *       consecutivos que el jugador ha pasado.
     */
    public int getSkipTrack() {
        return skipTrack;
    }

    /**
     * Verifica si el jugador es una IA.
     * Cada clase hija debe implementar este método.
     * 
     * @pre No hay precondiciones específicas.
     * @return true si es una IA, false si es un jugador humano
     * @post Se devuelve un valor booleano indicando si el jugador es controlado por IA.
     */
    public abstract boolean esIA();

    /**
     * Limpia el contador de turnos pasados, estableciéndolo a 0.
     * 
     * @pre No hay precondiciones específicas.
     * @post El contador de turnos pasados se establece a 0.
     */
    public void clearSkipTrack() {
        setSkipTrack(0);
    }

    /**
     * Obtiene el nombre para uso interno en subclases.
     * 
     * @pre No hay precondiciones específicas.
     * @return Nombre del jugador
     * @post Se devuelve el nombre del jugador.
     */
    protected String getNombreInterno() {
        return nombre;
    }

    /**
     * Obtiene el rack para uso interno en subclases.
     * 
     * @pre No hay precondiciones específicas.
     * @return Mapa con las fichas del jugador
     * @post Se devuelve el rack actual del jugador, que puede ser null si no ha sido inicializado.
     */
    protected Map<String, Integer> getRackInterno() {
        return rack;
    }

    /**
     * Establece el rack para uso interno en subclases.
     * 
     * @pre rack no debe ser null.
     * @param rack Mapa de fichas para asignar al jugador
     * @post El rack del jugador se actualiza al valor especificado.
     * @throws NullPointerException si rack es null
     */
    protected void setRackInterno(Map<String, Integer> rack) {
        if (rack == null) {
            throw new NullPointerException("El rack no puede ser null");
        }
        this.rack = rack;
    }

    /**
     * Obtiene el skipTrack para uso interno en subclases.
     * 
     * @pre No hay precondiciones específicas.
     * @return Contador de turnos omitidos
     * @post Se devuelve el contador actual de turnos omitidos.
     */
    protected int getSkipTrackInterno() {
        return skipTrack;
    }
}