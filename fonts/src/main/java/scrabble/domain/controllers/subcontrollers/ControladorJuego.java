package scrabble.domain.controllers.subcontrollers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import scrabble.domain.models.Bolsa;
import scrabble.domain.models.Tablero;
import scrabble.domain.persistences.implementaciones.RepositorioPartidaImpl;
import scrabble.domain.persistences.interfaces.RepositorioPartida;
import scrabble.excepciones.ExceptionPersistenciaFallida;
import scrabble.helpers.Triple;
import scrabble.helpers.Tuple;
import scrabble.helpers.Dificultad;
import scrabble.helpers.Direction;

/**
 * Controlador principal para la gestión completa de partidas de Scrabble.
 * 
 * Esta clase centraliza toda la lógica de juego, incluyendo la gestión del tablero,
 * validación de movimientos, cálculo de puntuaciones, manejo de turnos y persistencia
 * del estado de la partida. Coordina las interacciones entre el tablero, la bolsa de fichas,
 * los jugadores y el diccionario para implementar las reglas completas del Scrabble.
 * 
 * Funcionalidades principales:
 * - Inicialización y configuración de partidas
 * - Validación de movimientos según las reglas del juego
 * - Cálculo automático de puntuaciones con multiplicadores
 * - Gestión de turnos para jugadores humanos e IA
 * - Búsqueda algorítmica de movimientos válidos
 * - Persistencia y carga del estado de partidas
 * - Detección automática de fin de juego
 * 
 * Implementa Serializable para permitir el guardado y carga completa del estado
 * de la partida, incluyendo el tablero, bolsa, jugadores y configuración.
 * 
 * @version 2.0
 * @since 1.0
 */

public class ControladorJuego implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idPartida = -1; // Identificador de la partida actual
    private transient ControladorDiccionario controladorDiccionario;
    private Tablero tablero;
    private Bolsa bolsa;

    private Direction direction;
    private boolean juegoTerminado;
    private boolean juegoIniciado;
    private Map<Tuple<Integer, Integer>, Set<String>> lastCrossCheck;
    private String nombreDiccionario;
    private Map<String, Integer> jugadores;
    private static RepositorioPartida repositorioPartida;
    

    private Set<String> alfabeto = Set.of(
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", 
        "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    );

    /**
     * Constructor por defecto para la clase ControladorJuego.
     * 
     * @throws ExceptionPersistenciaFallida si ocurre un error al inicializar el repositorio de partidas
     * @pre No hay precondiciones específicas.
     * @post Se inicializa una nueva instancia de ControladorJuego con valores por defecto:
     *       - tablero, lastCrossCheck y direction son null
     *       - juegoIniciado y juegoTerminado son false
     *       - bolsa es null
     *       - idPartida es -1
     *       - Se obtiene la instancia del ControladorDiccionario
     */
    public ControladorJuego() throws ExceptionPersistenciaFallida {
        this.tablero = null;
        this.lastCrossCheck = null;
        this.direction = null;
        this.juegoIniciado = false;
        this.juegoTerminado = false;
        this.bolsa = null;
        this.controladorDiccionario = ControladorDiccionario.getInstance();
        repositorioPartida = new RepositorioPartidaImpl();
    }

    /**
     * Obtiene el nombre del diccionario de la partida actual.
     * 
     * @return El nombre del diccionario empleado en la partida actual.
     */
    public String getNombreDiccionario() {
        return this.nombreDiccionario;
    }

    /**
     * Obtiene el tamaño del tablero de la partida actual.
     * 
     * @return El tamaño del tablero (número entero que representa las dimensiones N×N).
     */
    public int getSize() {
        return this.tablero.getSize();
    }    

    
    /**
     * Inicializa el juego configurando el tablero, diccionario, idioma y bolsa de fichas.
     *
     * @param N                Tamaño del tablero (N x N).
     * @param jugadores        Mapa de nombres de los jugadores con sus puntuaciones iniciales.
     * @param nombreDiccionario Nombre del diccionario que se utilizará para el juego.
     * @throws ExceptionPersistenciaFallida si ocurre un error al generar el ID de partida
     * @pre El diccionario con nombreDiccionario debe existir en el sistema.
     * @post Se inicializa el juego con un tablero de tamaño N×N, se asocia un diccionario
     *       y se crea la bolsa de fichas basada en la configuración del diccionario.
     * @throws NullPointerException Si alguno de los parámetros es null.
     * @throws IllegalArgumentException Si N es menor que 1 o si el mapa de jugadores está vacío.
     */
    public void inicializarJuego(int N, Map<String, Integer> jugadores, String nombreDiccionario) throws ExceptionPersistenciaFallida {
        this.tablero = new Tablero(N);
        this.nombreDiccionario = nombreDiccionario;
        this.jugadores = jugadores;
        this.juegoIniciado = false;
        this.juegoTerminado = false;
        this.idPartida = repositorioPartida.generarNuevoId();
        
        Map<String, Integer> fichas = controladorDiccionario.getFichas(nombreDiccionario);
        this.bolsa = new Bolsa();
        this.bolsa.llenarBolsa(fichas);
    }

    
    /**
     * Obtiene el estado actual del tablero.
     * 
     * @return Mapa de las posiciones ocupadas con sus respectivas letras
     * @pre El tablero debe estar inicializado.
     * @post Se devuelve el estado actual del tablero sin modificarlo.
     */
    public Map<Tuple<Integer, Integer>, String> getEstadoTablero() {
        return tablero.getEstadoTablero();
    } 

    /**
     * Obtiene una cantidad específica de fichas de la bolsa.
     * 
     * @pre La cantidad debe ser un valor positivo.
     * @param cantidad La cantidad de fichas a recuperar de la bolsa.
     * @return Un mapa donde las claves son los identificadores de las fichas (String) y los valores 
     *         son las cantidades de cada ficha (Integer). Devuelve null si no hay suficientes fichas 
     *         en la bolsa.
     * @post Si hay suficientes fichas en la bolsa, se devuelve un mapa con las fichas extraídas.
     *       Si no hay suficientes fichas, se devuelve null.
     * @throws IllegalArgumentException Si cantidad es menor que 1.
     * @throws NullPointerException Si la bolsa no ha sido inicializada.
     */
    public Map<String, Integer> cogerFichas(int cantidad) {
        Map<String, Integer> fichas = new HashMap<>();

        for (int i = 0; i < cantidad; i++) {
            String ficha = this.bolsa.sacarFicha();
            if (ficha != null) {
                fichas.put(ficha, fichas.getOrDefault(ficha, 0) + 1);
            } else return null; // No hay suficientes fichas en la bolsa
        }
        return fichas;
    }

    /**
     * Agrega fichas a la bolsa según el mapa proporcionado.
     *
     * @pre El mapa de fichas no debe ser null y la bolsa debe estar inicializada.
     * @param fichas Un mapa donde las claves son las representaciones de las fichas
     *               (como cadenas de texto) y los valores son la cantidad de cada ficha
     *               que se debe agregar a la bolsa.
     * @post Las fichas especificadas son añadidas a la bolsa.
     * @throws NullPointerException Si el parámetro fichas es null o si la bolsa no ha sido inicializada.
     */
    public void meterFichas(Map<String, Integer> fichas) {
        for (Map.Entry<String, Integer> entry : fichas.entrySet()) {
            String ficha = entry.getKey();
            int cantidad = entry.getValue();
            for (int i = 0; i < cantidad; i++) {
                this.bolsa.agregarFichas(ficha, cantidad);
            }
        }
    }


    /**
     * Obtiene la cantidad de fichas restantes en la bolsa.
     * 
     * @pre La bolsa debe estar inicializada.
     * @return El número de fichas disponibles en la bolsa.
     * @post Se devuelve un entero no negativo que representa la cantidad de fichas en la bolsa.
     * @throws NullPointerException Si la bolsa no ha sido inicializada.
     */
    public int getCantidadFichas() {
        // Verifica si el juego ha terminado (por ejemplo, si la bolsa está vacía o si un jugador ha usado todas sus fichas)
        return this.bolsa.getCantidadFichas();
    }
    

    /**
     * Calcula la posición anterior a la dada, según la dirección actual del juego.
     *
     * @pre La posición proporcionada no debe ser null y la dirección del juego debe estar establecida.
     * @param pos Posición actual.
     * @return Nueva posición antes de la actual.
     * @post Se devuelve una nueva tupla que representa la posición anterior a la proporcionada,
     *       según la dirección actual (horizontal o vertical).
     * @throws NullPointerException Si el parámetro pos es null o si la dirección no está establecida.
     */
    public Tuple<Integer, Integer> before(Tuple<Integer, Integer> pos) {
        return direction == Direction.HORIZONTAL ? new Tuple<>(pos.x, pos.y - 1) : new Tuple<>(pos.x - 1, pos.y);
    } 

    /**
     * Calcula la posición siguiente a la dada, según la dirección actual del juego.
     *
     * @pre La posición proporcionada no debe ser null y la dirección del juego debe estar establecida.
     * @param pos Posición actual.
     * @return Nueva posición después de la actual.
     * @post Se devuelve una nueva tupla que representa la posición siguiente a la proporcionada,
     *       según la dirección actual (horizontal o vertical).
     * @throws NullPointerException Si el parámetro pos es null o si la dirección no está establecida.
     */
    public Tuple<Integer, Integer> after(Tuple<Integer, Integer> pos) {
        return direction == Direction.HORIZONTAL ? new Tuple<>(pos.x, pos.y + 1) : new Tuple<>(pos.x + 1, pos.y);
    }

    /**
     * Calcula la posición anterior a la dada en la dirección cruzada a la actual.
     *
     * @pre La posición proporcionada no debe ser null y la dirección del juego debe estar establecida.
     * @param pos Posición actual.
     * @return Nueva posición antes de la actual en la dirección girada.
     * @post Se devuelve una nueva tupla que representa la posición anterior en dirección cruzada
     *       (si la dirección actual es horizontal, se mueve verticalmente hacia arriba, y viceversa).
     * @throws NullPointerException Si el parámetro pos es null o si la dirección no está establecida.
     */
    public Tuple<Integer, Integer> before_cross(Tuple<Integer, Integer> pos) {
        return direction == Direction.HORIZONTAL ? new Tuple<>(pos.x - 1, pos.y) : new Tuple<>(pos.x, pos.y - 1);
    }

    /**
     * Calcula la posición siguiente a la dada en la dirección cruzada a la actual.
     *
     * @pre La posición proporcionada no debe ser null y la dirección del juego debe estar establecida.
     * @param pos Posición actual.
     * @return Nueva posición después de la actual en la dirección girada.
     * @post Se devuelve una nueva tupla que representa la posición siguiente en dirección cruzada
     *       (si la dirección actual es horizontal, se mueve verticalmente hacia abajo, y viceversa).
     * @throws NullPointerException Si el parámetro pos es null o si la dirección no está establecida.
     */
    public Tuple<Integer, Integer> after_cross(Tuple<Integer, Integer> pos) {
        return direction == Direction.HORIZONTAL ? new Tuple<>(pos.x + 1, pos.y) : new Tuple<>(pos.x, pos.y + 1);
    }

    /*
     * Método para encontrar los anclajes en el tablero.
     * @return Conjunto de posiciones de anclajes.
     */

    /**
     * Encuentra las posiciones de anclaje en el tablero.
     * Los anclajes son posiciones vacías adyacentes a casillas ocupadas, 
     * o el centro del tablero si es el primer turno.
     *
     * @pre El tablero debe estar inicializado.
     * @param juegoIniciado Indica si el juego ya ha comenzado.
     * @return Conjunto de posiciones (tuplas) que representan los anclajes en el tablero.
     * @post Se devuelve un conjunto no vacío con las posiciones de anclaje.
     *       Si el juego no ha iniciado, solo se devuelve el centro del tablero.
     * @throws NullPointerException Si el tablero no ha sido inicializado.
     */
    public Set<Tuple<Integer, Integer>> find_anchors(boolean juegoIniciado) {
        Set<Tuple<Integer, Integer>> anchors = new HashSet<>();
        if (juegoIniciado) {
            for (int i = 0; i < tablero.getSize(); i++) {
                for (int j = 0; j < tablero.getSize(); j++) {
                    Tuple<Integer, Integer> pos = new Tuple<>(i,j);
                    if (tablero.isEmpty(pos)) {
                        if (tablero.isFilled(before_cross(pos)) || tablero.isFilled(after_cross(pos)) || tablero.isFilled(before(pos)) || tablero.isFilled(after(pos))) {
                            anchors.add(pos);
                        }
                    }
                }
            }
        } else anchors.add(this.tablero.getCenter());
        return anchors;
    }

    /**
     * Extiende una palabra hacia la izquierda añadiendo letras disponibles del atril.
     * Método auxiliar para la búsqueda de movimientos válidos.
     * 
     * @param partialWord Palabra parcial que se está formando.
     * @param rack Mapa de letras disponibles en el atril del jugador.
     * @param nextPos Posición siguiente a explorar en el tablero.
     * @param limit Límite máximo de letras que se pueden añadir a la izquierda.
     * @return Conjunto de movimientos válidos encontrados.
     */

    /**
     * Extiende una palabra hacia la izquierda y luego explora posibles extensiones hacia la derecha.
     * Parte de una búsqueda recursiva para encontrar jugadas válidas.
     *
     * @pre El diccionario debe estar inicializado y las estructuras de datos (rack, etc.) deben ser válidas.
     * @param partialWord Palabra parcial que se está formando.
     * @param rack Mapa de letras disponibles en el atril del jugador.
     * @param nextPos Posición siguiente a explorar en el tablero.
     * @param limit Límite máximo de letras que se pueden añadir a la izquierda.
     * @return Conjunto de tripletas (palabra, posición, dirección) que representan movimientos válidos.
     * @post Se devuelve un conjunto (posiblemente vacío) de jugadas válidas.
     * @throws NullPointerException Si alguno de los parámetros es null.
     */
    public Set<Triple<String,Tuple<Integer, Integer>, Direction>> extendLeft(String partialWord, Map<String, Integer> rack, Tuple<Integer, Integer> nextPos, int limit) {
        Set<Triple<String,Tuple<Integer, Integer>, Direction>> words = new HashSet<>();
        words.addAll(extendRight(partialWord, rack, nextPos, false));

        if (limit > 0){
            for (String c : this.controladorDiccionario.getAvailableEdges(nombreDiccionario, partialWord)) {
                if (rack.containsKey(c) ||rack.containsKey("#")) {
                    String newPartialWord = partialWord + c;
                    Map<String, Integer> newRack = new HashMap<>(rack);

                    if (!rack.containsKey(c)) c = "#";

                    if (newRack.get(c) == 1) {
                        newRack.remove(c);
                    } else {
                        newRack.put(c, newRack.get(c) - 1);
                    }
                    words.addAll(extendLeft(newPartialWord, newRack, nextPos, limit - 1));
                }
            }
        }
        return words;
    }

    /**
     * Extiende una palabra hacia la derecha añadiendo letras disponibles del atril.
     * Método auxiliar para la búsqueda de movimientos válidos.
     * 
     * @param partialWord Palabra parcial que se está formando.
     * @param rack Mapa de letras disponibles en el atril del jugador.
     * @param nextPos Posición siguiente a explorar en el tablero.
     * @param archorFilled Indica si la posición de anclaje ya está ocupada.
     * @return Conjunto de movimientos válidos encontrados.
     */

    /**
     * Extiende una palabra hacia la derecha.
     * Parte de una búsqueda recursiva para encontrar jugadas válidas.
     *
     * @pre El diccionario y el tablero deben estar inicializados y las estructuras de datos (rack, lastCrossCheck) deben ser válidas.
     * @param partialWord Palabra parcial que se está formando.
     * @param rack Mapa de letras disponibles en el atril del jugador.
     * @param nextPos Posición siguiente a explorar en el tablero.
     * @param archorFilled Indica si la posición de anclaje ya está ocupada.
     * @return Conjunto de tripletas (palabra, posición, dirección) que representan movimientos válidos.
     * @post Se devuelve un conjunto (posiblemente vacío) de jugadas válidas.
     * @throws NullPointerException Si alguno de los parámetros es null.
     */
    public Set<Triple<String,Tuple<Integer, Integer>, Direction>> extendRight(String partialWord, Map<String, Integer> rack, Tuple<Integer, Integer> nextPos, boolean archorFilled) {
        Set<Triple<String,Tuple<Integer, Integer>, Direction>> words = new HashSet<>();

        if(!this.tablero.isFilled(nextPos) && this.controladorDiccionario.isFinal(nombreDiccionario, partialWord) && archorFilled) {
            words.add(new Triple<>(partialWord, before(nextPos), this.direction));
        }
        if (this.tablero.validPosition(nextPos)){
            if (this.tablero.isEmpty(nextPos)) {

                for (String c : this.controladorDiccionario.getAvailableEdges(nombreDiccionario, partialWord)) {
                    Set<String> allowedChars = this.lastCrossCheck.get(nextPos);
                    if ((rack.containsKey(c) || rack.containsKey("#")) && allowedChars != null && allowedChars.contains(c)) {
                        String newPartialWord = partialWord + c;
                        Map<String, Integer> newRack = new HashMap<>(rack);

                        if (!rack.containsKey(c)) c = "#";
                        if (newRack.get(c) == 1) {
                            newRack.remove(c);
                        } else {
                            newRack.put(c, newRack.get(c) - 1);
                        }
                        words.addAll(extendRight(newPartialWord, newRack, after(nextPos), true));
                    }
                }
            } else {
                String c = String.valueOf(this.tablero.getTile(nextPos));
                if (this.controladorDiccionario.getAvailableEdges(nombreDiccionario, partialWord).contains(c)) {
                    String newPartialWord = partialWord + c;
                    
                    words.addAll(extendRight(newPartialWord, rack, after(nextPos), true));
                }
            }
        }
        return words;
    }

    /**
     * Realiza una verificación cruzada en el tablero para determinar letras válidas.
     * Analiza cada posición vacía para determinar qué letras pueden formar palabras válidas
     * en la dirección perpendicular a la jugada principal.
     * 
     * @return Mapa que asocia posiciones con conjuntos de letras válidas en esa posición.
     */

    /**
     * Realiza una verificación cruzada en el tablero para determinar qué letras son válidas en cada posición.
     * Este método analiza cada posición vacía y determina qué letras pueden colocarse
     * para formar palabras válidas en la dirección perpendicular a la jugada principal.
     *
     * @pre El tablero y el diccionario deben estar inicializados.
     * @return Mapa que asocia posiciones (tuplas) con conjuntos de caracteres válidos en esa posición.
     * @post Se devuelve un mapa donde las claves son posiciones en el tablero y los valores
     *       son conjuntos de letras que pueden colocarse en esa posición para formar palabras válidas.
     * @throws NullPointerException Si el tablero o el diccionario no han sido inicializados.
     */
    public Map<Tuple<Integer, Integer>, Set<String>> crossCheck() {
        Map<Tuple<Integer, Integer>, Set<String>> words = new HashMap<>();
        for (int i = 0; i < tablero.getSize(); i++) {
            for (int j = 0; j < tablero.getSize(); j++) {
                Tuple<Integer, Integer> pos = new Tuple<>(i,j);
                if (tablero.isEmpty(pos)) {
                    String beforePart = "";
                    String afterPart = "";
                    
                    Tuple<Integer, Integer> up_pos = pos;
                    while(this.tablero.isFilled(before_cross(up_pos))) {
                        beforePart = tablero.getTile(before_cross(up_pos)) + beforePart ;
                        up_pos = before_cross(up_pos);
                    }

                    Tuple<Integer, Integer> down_pos = pos;
                    while(this.tablero.isFilled(after_cross(down_pos))) {
                        afterPart = afterPart + tablero.getTile(after_cross(down_pos));
                        down_pos = after_cross(down_pos);
                    }
                    Set<String> set = new HashSet<>();
                    if (beforePart.length() == 0 && afterPart.length() == 0) {
                        set.addAll(alfabeto);
                    } else {
                        for (String c : alfabeto) {
                            String candidateWord = beforePart + c + afterPart;
                            if (this.controladorDiccionario.existePalabra(nombreDiccionario, candidateWord)) {
                                set.add(c);
                            }
                        }
                    }
                    words.put(pos, set);
                }
            }
        }
        return words;
    }

    /**
     * Busca todos los movimientos posibles en el tablero con las fichas disponibles.
     * Método central para la lógica del juego que utiliza los métodos auxiliares
     * find_anchors, extendLeft/Right y crossCheck.
     * 
     * @param rack Mapa de letras disponibles en el atril del jugador.
     * @param juegoIniciado Indica si el juego ya ha comenzado.
     * @return Conjunto de movimientos válidos posibles.
     */

    /**
     * Busca todos los movimientos posibles en el tablero dadas las fichas disponibles.
     * Este método es central para la lógica del juego y usa los métodos auxiliares
     * find_anchors, extendLeft/Right y crossCheck.
     *
     * @pre El tablero, diccionario y dirección deben estar inicializados.
     * @param rack Mapa de letras disponibles en el atril del jugador.
     * @param juegoIniciado Indica si el juego ya ha comenzado.
     * @return Conjunto de tripletas (palabra, posición, dirección) que representan movimientos válidos.
     * @post Se devuelve un conjunto (posiblemente vacío) de todos los movimientos válidos posibles.
     * @throws NullPointerException Si el rack es null o si el tablero o diccionario no están inicializados.
     */
    public Set<Triple<String,Tuple<Integer, Integer>, Direction>> searchAllMoves(Map<String, Integer> rack, boolean juegoIniciado) {
        Set<Triple<String,Tuple<Integer, Integer>, Direction>> answers = new HashSet<>();
        Set<Tuple<Integer, Integer>> anchors = find_anchors(juegoIniciado);

        for (Direction dir : Direction.values()) {

            this.direction = dir;

            this.lastCrossCheck = crossCheck();
            
            for (Tuple<Integer, Integer> pos : anchors) {
                
                if (this.tablero.isFilled(before(pos))) {
                    
                    String partial_word = "";
                    Tuple<Integer, Integer> before_pos = pos;
                    
                while(tablero.isFilled(before(before_pos))) {
                    partial_word = tablero.getTile(before(before_pos)) + partial_word;
                    before_pos = before(before_pos);
                }
                
                if (this.controladorDiccionario.nodeExists(nombreDiccionario, partial_word)) {
                    answers.addAll(this.extendRight(partial_word, rack, pos, false));
                }
                } else {

                    Tuple<Integer, Integer> before_pos = pos;
                    int limit = 0;
                    while(tablero.isEmpty(before(before_pos)) && !anchors.contains(before(before_pos))) {
                        limit += 1;
                        before_pos = before(before_pos);
                    }
                    
                    answers.addAll(extendLeft("", rack, pos, limit)); //areglable
                    
                }
                
            }
        }
        return answers;
    }

    /**
     * Realiza un movimiento en el tablero colocando las letras correspondientes.
     * Actualiza el tablero con las letras del movimiento y modifica el atril del jugador.
     * 
     * @param move Movimiento a realizar (palabra, posición, dirección).
     * @param rack Mapa de letras disponibles en el atril del jugador.
     * @return Mapa actualizado con las letras restantes después del movimiento.
     */

    /**
     * Realiza un movimiento en el tablero, colocando las letras correspondientes
     * y actualizando el atril del jugador.
     *
     * @pre El movimiento debe ser válido y el tablero debe estar inicializado.
     * @param move Tripleta (palabra, posición, dirección) que representa el movimiento a realizar.
     * @param rack Mapa de letras disponibles en el atril del jugador.
     * @return Mapa actualizado con las letras disponibles después de realizar el movimiento.
     * @post Las letras del movimiento se colocan en el tablero y se devuelve el rack actualizado.
     * @throws NullPointerException Si alguno de los parámetros es null.
     * @throws IllegalArgumentException Si el movimiento intenta colocar letras fuera del tablero.
     */
    public Map<String, Integer> makeMove(Triple<String,Tuple<Integer, Integer>, Direction> move, Map<String, Integer> rack) {
        String word = move.x.toUpperCase();
        Tuple<Integer, Integer> pos = move.y;
        Direction dir = move.z;

        Map<String, Integer> newRack = new HashMap<>(rack);

        for (int i = word.length() - 1; i >= 0; i--) {
            String letter = String.valueOf(word.charAt(i)).toUpperCase();
            
            if ((newRack.containsKey(letter) || newRack.containsKey("#")) && this.tablero.isEmpty(pos)) {
                String letraParaEliminar = letter;
                if (!newRack.containsKey(letter)) letraParaEliminar = "#";

                if (newRack.get(letraParaEliminar) == 1) {
                    newRack.remove(letraParaEliminar);
                } else {
                    newRack.put(letraParaEliminar, newRack.get(letraParaEliminar) - 1);
                }
            }
            this.tablero.setTile(pos, String.valueOf(letter));
            if (dir == Direction.HORIZONTAL) {
                pos = new Tuple<Integer, Integer>(pos.x, pos.y - 1); 
            } else {
                pos = new Tuple<Integer, Integer>(pos.x - 1, pos.y);
            }
        }
        return newRack;
    }

    /**
     * Calcula los puntos obtenidos por un movimiento específico.
     * Tiene en cuenta los multiplicadores de letra y palabra del tablero.
     * 
     * @param move Movimiento a evaluar (palabra, posición, dirección).
     * @return Puntos totales obtenidos por el movimiento.
     */

    /**
     * Calcula los puntos obtenidos por un movimiento específico.
     * Tiene en cuenta los multiplicadores de letra y palabra del tablero.
     *
     * @pre El movimiento debe ser válido y el tablero y diccionario deben estar inicializados.
     * @param move Tripleta (palabra, posición, dirección) que representa el movimiento a evaluar.
     * @return Puntos obtenidos por el movimiento.
     * @post Se devuelve un entero no negativo que representa la puntuación del movimiento.
     * @throws NullPointerException Si el parámetro move es null o si el tablero no está inicializado.
     */
    public int calculateMovePoints(Triple<String,Tuple<Integer, Integer>, Direction> move) {

        int points = 0;
        int doubleTimes = 0;
        int tripleTimes = 0;
        
        String word = move.x;
        Tuple<Integer, Integer> pos = move.y;
        Direction dir = move.z;

        for (int i = word.length() - 1; i >= 0; i--) {
            int letterPoint = controladorDiccionario.getPuntaje(nombreDiccionario, String.valueOf(word.charAt(i)));
            if (this.tablero.isFilled(new Tuple<>(pos.x, pos.y))) {
                points += letterPoint;
            } else {
                switch (this.tablero.getBonus(pos)) {
                    case TW:
                        points += letterPoint;
                        tripleTimes++;
                        break;
                    case TL:
                        points += letterPoint * 3;
                        break;
                    case DW:
                        points += letterPoint;
                        doubleTimes++;
                        break;
                    case DL:
                        points += letterPoint * 2;
                        break;
                    case X:
                        points += letterPoint * 2;
                        break;
                    default:
                        points += letterPoint;
                        break;
                }
            }
            pos = dir == Direction.HORIZONTAL? new Tuple<>(pos.x, pos.y - 1): new Tuple<>(pos.x - 1, pos.y);
        }
        return points * (int) Math.pow(2, doubleTimes) * (int) Math.pow(3, tripleTimes); 
    }

    /**
     * Verifica si un movimiento es válido según las reglas del juego.
     * Utiliza el método searchAllMoves para determinar si el movimiento está
     * entre los movimientos válidos posibles.
     * 
     * @param move Movimiento a evaluar (palabra, posición, dirección).
     * @param rack Mapa de letras disponibles en el atril del jugador.
     * @return true si el movimiento es válido, false en caso contrario.
     */

    /**
     * Verifica si un movimiento es válido según las reglas del juego.
     * Utiliza el método searchAllMoves para determinar si el movimiento está
     * entre los movimientos válidos posibles.
     *
     * @pre El tablero y el diccionario deben estar inicializados.
     * @param move Tripleta (palabra, posición, dirección) que representa el movimiento a evaluar.
     * @param rack Mapa de letras disponibles en el atril del jugador.
     * @return true si el movimiento es válido, false en caso contrario.
     * @post Se devuelve un valor booleano que indica si el movimiento es válido
     *       según las reglas del juego y el estado actual del tablero.
     * @throws NullPointerException Si alguno de los parámetros es null.
     */
    public boolean isValidMove (Triple<String,Tuple<Integer, Integer>, Direction> move, Map<String, Integer> rack) {
        Set<Triple<String,Tuple<Integer, Integer>, Direction>> possibleWords = searchAllMoves(rack, true);
        return possibleWords.contains(move);
    }

    /**
     * Verifica si un movimiento es válido específicamente para el primer turno del juego.
     * En el primer turno, la palabra debe pasar por el centro del tablero y ser válida
     * según el diccionario.
     * 
     * @param move Movimiento a evaluar (palabra, posición, dirección).
     * @param rack Mapa de letras disponibles en el atril del jugador.
     * @return true si el movimiento es válido para el primer turno, false en caso contrario.
     */

     /**
      * Verifica si un movimiento es válido específicamente para el primer turno del juego.
      * En el primer turno, la palabra debe pasar por el centro del tablero.
      *
      * @pre El tablero y el diccionario deben estar inicializados.
      * @param move Tripleta (palabra, posición, dirección) que representa el movimiento a evaluar.
      * @param rack Mapa de letras disponibles en el atril del jugador.
      * @return true si el movimiento es válido para el primer turno, false en caso contrario.
      * @post Se devuelve un valor booleano que indica si el movimiento es válido
      *       para el primer turno del juego.
      * @throws NullPointerException Si alguno de los parámetros es null.
      */
      public boolean isValidFirstMove(Triple<String, Tuple<Integer, Integer>, Direction> move, Map<String, Integer> rack) {
        if (move == null || rack == null) {
            throw new NullPointerException("Move o rack es null");
        }
    
        String word = move.x;
        Tuple<Integer, Integer> pos = move.y;
        Direction dir = move.z;
    
    
        // Check if the word fits within the board boundaries
        Tuple<Integer, Integer> currentPos = new Tuple<>(pos.x, pos.y);
        for (int i = word.length() - 1; i >= 0; i--) {
            if (!tablero.validPosition(currentPos)) {
                return false;
            }
            currentPos = dir == Direction.HORIZONTAL
                ? new Tuple<>(currentPos.x, currentPos.y - 1)
                : new Tuple<>(currentPos.x - 1, currentPos.y);
        }
    
    
        // Reset position to the starting point
        currentPos = new Tuple<>(pos.x, pos.y);
    
        // Check if the word is placed on the center tile
        boolean centerTileCovered = false;
        for (int i = word.length() - 1; i >= 0; i--) {
            if (currentPos.equals(tablero.getCenter())) {
                centerTileCovered = true;
            }
            currentPos = dir == Direction.HORIZONTAL
                ? new Tuple<>(currentPos.x, currentPos.y - 1)
                : new Tuple<>(currentPos.x - 1, currentPos.y);
        }
    
        if (!centerTileCovered) {
            return false;
        }
    
        // Check if the word can be formed using the rack
        Map<String, Integer> tempRack = new HashMap<>(rack);
    
        for (int i = 0; i < word.length(); i++) {
            String letter = String.valueOf(word.charAt(i)).toUpperCase();
            String diletter = "";
            if (i + 1 < word.length()) {
                diletter = letter + String.valueOf(word.charAt(i + 1)).toUpperCase();
            }
    
            if (tempRack.containsKey(letter) || tempRack.containsKey(diletter)) {
                String key = tempRack.containsKey(diletter) ? diletter : letter;
                if (tempRack.get(key) == 1) {
                    tempRack.remove(key);
                } else {
                    tempRack.put(key, tempRack.get(key) - 1);
                }
            } else if (tempRack.containsKey("#")) { // Use blank tile
                if (tempRack.get("#") == 1) {
                    tempRack.remove("#");
                } else {
                    tempRack.put("#", tempRack.get("#") - 1);
                }
            } else {
                return false;
            }
        }
    
        boolean found = this.controladorDiccionario.existePalabra(nombreDiccionario, word);
        return found;
    }
    
    

    /**
     * Realiza una acción en el juego, ya sea por un jugador humano o por la IA.
     * Para jugadores humanos, ejecuta el movimiento proporcionado.
     * Para la IA, busca y ejecuta el mejor movimiento posible según la dificultad.
     * 
     * @param move El movimiento a realizar (palabra, posición, dirección).
     * @param nombreJugador El nombre del jugador que está realizando la acción.
     * @param rack El atril del jugador con las letras disponibles para jugar.
     * @param isIA Indica si el jugador es una IA o un jugador humano.
     * @param dificultad La dificultad de la IA (si aplica).
     * @param isFirst Indica si es el primer turno de la partida.
     * @return Tupla con el nuevo atril del jugador y los puntos obtenidos, o null si no se puede realizar la acción.
     */

     private Tuple<Map<String, Integer>, Integer> realizarAccion(Triple<String,Tuple<Integer, Integer>, Direction> move, String nombreJugador, Map<String, Integer> rack, boolean isIA, Dificultad dificultad, boolean isFirst) {
        if (!isIA) { 
            this.juegoIniciado = true;
            return new Tuple<Map<String,Integer>,Integer>(this.makeMove(move, rack), this.calculateMovePoints(move));
        } else {
            Set<Triple<String,Tuple<Integer, Integer>, Direction>> moves = this.searchAllMoves(rack, this.juegoIniciado);
            if (moves == null || moves.isEmpty()) {
                return null;
            } else {
                Triple<String,Tuple<Integer, Integer>, Direction> bestMove = null;
                int bestMovePoints = 0;
                for (Triple<String,Tuple<Integer, Integer>, Direction> m : moves) {
                    int currentMovePoints = this.calculateMovePoints(m);
                    if (bestMove == null || currentMovePoints > bestMovePoints) {
                        bestMove = m;
                        bestMovePoints = currentMovePoints;
                    }
                    if (dificultad == Dificultad.FACIL) {
                        if (currentMovePoints > 0) {
                            break; // Si la dificultad es fácil, no es necesario buscar más
                        }
                    }
                }
                this.juegoIniciado = true;
                move.setFromTriple(bestMove);
                return new Tuple<Map<String,Integer>,Integer>(this.makeMove(bestMove, rack), bestMovePoints);
            }
        
        }
    }
    /**
     * Realiza un turno en el juego procesando la acción del jugador o de la IA.
     * Método público que delega en realizarAccion para ejecutar la lógica del turno.
     * 
     * @param move El movimiento a realizar (puede ser null para la IA).
     * @param nombreJugador El nombre del jugador que está realizando el turno.
     * @param rack Mapa de letras disponibles en el atril del jugador.
     * @param isIA Indica si el jugador es una IA o un jugador humano.
     * @param dificultad La dificultad de la IA (si aplica).
     * @return Tupla con el nuevo atril del jugador y los puntos obtenidos, o null si no se puede realizar la acción.
     */

    /**
     * Realiza un turno en el juego procesando la acción del jugador o de la IA.
     * 
     * @pre El juego debe estar inicializado con tablero y diccionario válidos.
     * @param move El movimiento a realizar (puede ser null para la IA).
     * @param nombreJugador El nombre del jugador que está realizando el turno.
     * @param rack Mapa de letras disponibles en el atril del jugador.
     * @param isIA Indica si el jugador es una IA o un jugador humano.
     * @param dificultad La dificultad de la IA (si aplica).
     * @return Una tupla que contiene el nuevo rack del jugador y los puntos obtenidos,
     *         o null si no se pudo realizar ninguna acción.
     * @post Si la acción es válida, se actualiza el estado del juego, se modifica el tablero
     *       y se devuelve el rack actualizado junto con los puntos obtenidos.
     * @throws NullPointerException Si alguno de los parámetros esenciales es null.
     */
    public Tuple<Map<String, Integer>, Integer> realizarTurno(Triple<String,Tuple<Integer, Integer>, Direction> move, String nombreJugador, Map<String, Integer> rack,  boolean isIA, Dificultad dificultad) {
        return realizarAccion(move, nombreJugador, rack, isIA, dificultad, juegoIniciado);
    }

    /**
     * Finaliza el juego marcándolo como terminado.
     * Este método se llama cuando el juego debe terminar por cualquier razón
     * (victoria de un jugador, bolsa vacía, etc.).
     */

     /**
      * Finaliza el juego marcándolo como terminado.
      * Este método se llama cuando el juego debe terminar por cualquier razón.
      *
      * @pre No hay precondiciones específicas.
      * @post El estado del juego se marca como terminado (juegoTerminado = true).
      */
     public void finalizarJuego() {
        juegoTerminado = true;
    }

    /**
     * Reinicia el estado del juego para comenzar una nueva partida.
     * Restablece los flags de estado pero mantiene la configuración del tablero y jugadores.
     */

    /**
     * Reinicia el estado del juego para comenzar una nueva partida.
     * Restablece los flags de estado pero mantiene la configuración (tablero, jugadores, etc.).
     *
     * @pre No hay precondiciones específicas.
     * @post El juego se marca como no iniciado y no terminado.
     */
    public void reiniciarJuego() {
        
        juegoTerminado = false;
        juegoIniciado = false;
    }

    /**
     * Comprueba si la partida ha sido marcada como terminada.
     *
     * @pre No hay precondiciones específicas.
     * @return {@code true} si la partida ha terminado, {@code false} en caso contrario.
     * @post Se devuelve el estado actual de la variable juegoTerminado sin modificarla.
     */
    public boolean isJuegoTerminado() {
        // Asume que existe una variable de instancia boolean juegoTerminado
        return juegoTerminado;
    }

    /**
     * Comprueba si la partida ha sido marcada como iniciada.
     *
     * @pre No hay precondiciones específicas.
     * @return {@code true} si la partida ha iniciado, {@code false} en caso contrario.
     * @post Se devuelve el estado actual de la variable juegoIniciado sin modificarla.
     */
    public boolean isJuegoIniciado() {
        // Asume que existe una variable de instancia boolean juegoIniciado
        return juegoIniciado;
    }

    /**
     * Actualiza las puntuaciones de un jugador específico.
     * 
     * @param nombre El nombre del jugador cuya puntuación se va a actualizar.
     * @param puntuacion Los puntos a añadir a la puntuación actual del jugador.
     * @pre El jugador debe existir en el mapa de jugadores.
     * @post La puntuación del jugador se incrementa con el valor especificado.
     */
    public void actualizarPuntuaciones(String nombre, int puntuacion) {
        this.jugadores.put(nombre, this.jugadores.get(nombre) + puntuacion);
    }

    /**
     * Genera una cadena de texto que representa el estado actual de la partida,
     * incluyendo el tablero y la cantidad de fichas restantes.
     *
     * @pre El tablero y la bolsa deben estar inicializados.
     * @param nombreJugador El nombre del jugador actual (o relevante) para mostrar en el estado.
     * @return Un {@code String} formateado con el estado de la partida.
     * @post Se devuelve una representación textual del estado de la partida sin modificar el estado del juego.
     * @throws NullPointerException si {@code tablero} o {@code bolsa} son null.
     */
    public String mostrarStatusPartida(String nombreJugador) {
        StringBuilder sb = new StringBuilder();
        sb.append("=====================================\n");
        sb.append("          ESTADO DE LA PARTIDA         \n"); 
        sb.append("=====================================\n");
        sb.append("Jugador: ").append(nombreJugador).append("\n\n");
        sb.append("Tablero:\n").append(tablero.toString()).append("\n\n");
        sb.append("Fichas restantes en la bolsa: ").append(bolsa.getCantidadFichas()).append("\n"); // Asume que este método existe en Bolsa
        return sb.toString();
    }


    /**
     * Guarda el estado completo del juego actual usando el repositorio de partidas.
     * Utiliza el ID de partida actual para identificar el archivo de guardado.
     *
     * @pre No hay precondiciones específicas, pero se recomienda que el juego esté en un estado válido.
     * @return {@code true} si el guardado fue exitoso, {@code false} en caso contrario.
     * @throws ExceptionPersistenciaFallida si ocurre un error durante el proceso de guardado.
     * @post Si la operación es exitosa, el estado del juego se guarda en el repositorio.
     */
    public boolean guardar() throws ExceptionPersistenciaFallida {
        return repositorioPartida.guardar(this.idPartida, this);
    }
    
    /**
     * Carga el estado del juego desde el repositorio de partidas,
     * sobrescribiendo el estado del objeto actual.
     *
     * @pre Debe existir una partida guardada con el ID especificado.
     * @param idPartida El ID de la partida a cargar.
     * @throws ExceptionPersistenciaFallida si ocurre un error al cargar la partida o si no se encuentra.
     * @post Si la partida existe, el estado del objeto actual se actualiza con el estado guardado.
     */
    public void cargarDesdeArchivo(int idPartida) throws ExceptionPersistenciaFallida {
    
        try {
            ControladorJuego loadedGame = repositorioPartida.cargar(idPartida);
            if (loadedGame != null) {
                this.tablero = loadedGame.tablero;
                this.bolsa = loadedGame.bolsa;
                this.direction = loadedGame.direction;
                this.juegoTerminado = loadedGame.juegoTerminado;
                this.juegoIniciado = loadedGame.juegoIniciado;
                this.lastCrossCheck = loadedGame.lastCrossCheck;
                this.nombreDiccionario = loadedGame.nombreDiccionario;
                this.alfabeto = loadedGame.alfabeto;
                this.jugadores = loadedGame.jugadores;
                this.idPartida = loadedGame.idPartida;
            } else {
                throw new ExceptionPersistenciaFallida("Partida no encontrada con ID: " + idPartida);
            }
        } catch (ExceptionPersistenciaFallida e) {
            throw e;
        }
    }

    /**
     * Obtiene el nombre del diccionario asociado a una partida específica.
     *
     * @param idPartida el identificador único de la partida.
     * @return el nombre del diccionario utilizado en la partida, o {@code null} si no se encuentra la partida.
     * @throws ExceptionPersistenciaFallida si ocurre un error al intentar cargar la partida desde el repositorio.
     */
    public static String obtenerDiccionarioPartida(int idPartida) throws ExceptionPersistenciaFallida {
        ControladorJuego loadedGame = repositorioPartida.cargar(idPartida);
        if (loadedGame != null) return loadedGame.nombreDiccionario;
        return null;
    }
    
    /**
     * Obtiene el número de jugadores asociado a una partida específica.
     *
     * @param idPartida el identificador único de la partida.
     * @return la cantidad de jugadores en la partida, o -1 si no se encuentra la partida.
     * @throws ExceptionPersistenciaFallida si ocurre un error al intentar cargar la partida desde el repositorio.
     */
    public static int getNumJugadoresPartida(int idPartida) throws ExceptionPersistenciaFallida {
        ControladorJuego loadedGame = repositorioPartida.cargar(idPartida);
        if (loadedGame != null) return loadedGame.jugadores.size();
        return -1;
    }

    /**
     * Lista todos los IDs de partidas guardadas disponibles en el repositorio.
     *
     * @pre No hay precondiciones específicas.
     * @return Una {@code List<Integer>} con los IDs de las partidas guardadas.
     * La lista estará vacía si no existen partidas guardadas.
     * @throws ExceptionPersistenciaFallida si ocurre un error al acceder al repositorio.
     * @post Se devuelve una lista con los IDs de las partidas guardadas sin modificar el estado del sistema.
     */
    public static List<Integer> listarArchivosGuardados() throws ExceptionPersistenciaFallida {

        try {
            return repositorioPartida.listarTodas();
        } catch (ExceptionPersistenciaFallida e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * Intenta eliminar una partida guardada especificada por su ID del repositorio.
     *
     * @pre No hay precondiciones específicas.
     * @param idPartida El ID de la partida a eliminar.
     * @return {@code true} si la partida existía y fue eliminada con éxito,
     * {@code false} en caso contrario (ej., la partida no existía o hubo un error).
     * @throws ExceptionPersistenciaFallida si ocurre un error al acceder al repositorio.
     * @post Si la partida existía, se elimina del repositorio de partidas guardadas.
     */
    public static boolean eliminarArchivoGuardado(int idPartida) throws ExceptionPersistenciaFallida {
        try {
            boolean success = repositorioPartida.eliminar(idPartida);
            return success;
        } catch (ExceptionPersistenciaFallida e) {
            return false;
        }

    }

    /**
     * Obtiene el mapa de jugadores asociados a una partida guardada específica.
     *
     * @pre No hay precondiciones específicas.
     * @param idPartida El ID de la partida de la que se quieren obtener los jugadores.
     * @return Un {@code Map<String, Integer>} con los nombres de los jugadores y sus puntuaciones.
     * Devuelve un mapa vacío si el archivo no existe o si la partida no existe.
     * @throws ExceptionPersistenciaFallida si ocurre un error al cargar la partida desde el repositorio.
     * @post Se devuelve un mapa con los jugadores y sus puntuaciones sin modificar el estado del sistema.
     */
    public static Map<String, Integer> getJugadoresPorId(int idPartida) throws ExceptionPersistenciaFallida {

        try {
            ControladorJuego controlador = repositorioPartida.cargar(idPartida);
            if (controlador != null) {
            return controlador.getJugadoresActuales();
            } else {
            return new HashMap<String, Integer>();
            }
        } catch (ExceptionPersistenciaFallida e) {
            return new HashMap<String, Integer>();
        }
        
    }
    
    /**
     * Obtiene el listado (Map) de nombres de los jugadores actualmente registrados en la partida.
     *
     * @pre No hay precondiciones específicas, pero se espera que el juego haya sido inicializado.
     * @return Un {@code Map<String, Integer>} que contiene los nombres únicos de los jugadores y sus respectivos puntuaciones.
     * Puede devolver un mapa vacío si no hay jugadores.
     * @post Se devuelve una copia del mapa de jugadores sin modificar el estado del juego.
     */
    public Map<String, Integer> getJugadoresActuales() {
        return jugadores;
    }

    /**
     * Obtiene el identificador único de la partida actual.
     * 
     * @return El ID de la partida actual, o -1 si no se ha asignado un ID.
     * @pre No hay precondiciones específicas.
     * @post Se devuelve el ID de la partida sin modificar el estado del juego.
     */
    public int getIdPartida() {
        return idPartida;
    }
 }
