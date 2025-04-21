package scrabble.domain.controllers.subcontrollers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import scrabble.domain.models.Bolsa;
import scrabble.domain.models.Tablero;
import scrabble.helpers.Triple;
import scrabble.helpers.Tuple;
import scrabble.helpers.BooleanWrapper;
import scrabble.helpers.Dificultad;

/**
 * Clase GestorJugada
 * Esta clase se encarga de gestionar las jugadas en el juego de Scrabble.
 * Permite buscar movimientos válidos, calcular puntos y realizar jugadas en el tablero.
 */

public class ControladorJuego implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Direction {
        HORIZONTAL,
        VERTICAL
    }

    private transient ControladorDiccionario controladorDiccionario;
    private Tablero tablero;
    private Bolsa bolsa;

    private Direction direction;
    private boolean juegoTerminado;
    private boolean juegoIniciado;
    private Map<Tuple<Integer, Integer>, Set<String>> lastCrossCheck;
    private String nombreDiccionario;
    private Set<String> jugadores;

    private Set<String> alfabeto = Set.of(
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", 
        "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    );

    public ControladorJuego() {
        this.tablero = null;
        this.lastCrossCheck = null;
        this.direction = null;
        this.juegoIniciado = false;
        this.juegoTerminado = false;
        this.bolsa = null;
        this.controladorDiccionario = ControladorDiccionario.getInstance();
    }


    /**
     * Inicializa el juego configurando el tablero, diccionario, idioma y bolsa de fichas.
     *
     * @param N                Tamaño del tablero (N x N).
     * @param jugadores        Conjunto de nombres de los jugadores que participan en el juego.
     * @param nombreDiccionario Nombre del diccionario que se utilizará para el juego.
     */
    public void inicializarJuego(int N, Set<String> jugadores, String nombreDiccionario) {
        this.tablero = new Tablero(N);
        this.nombreDiccionario = nombreDiccionario;
        this.jugadores = jugadores;
        
        Map<String, Integer> fichas = controladorDiccionario.getFichas(nombreDiccionario);
        this.bolsa = new Bolsa();
        this.bolsa.llenarBolsa(fichas);
    }

    
    /**
     * Obtiene una cantidad específica de fichas de la bolsa.
     * 
     * Este método intenta recuperar la cantidad especificada de fichas de la bolsa.
     * Si no hay suficientes fichas disponibles, el método devolverá null.
     * De lo contrario, devuelve un mapa que contiene las fichas y sus respectivas cantidades.
     * 
     * @param cantidad La cantidad de fichas a recuperar de la bolsa.
     * @return Un mapa donde las claves son los identificadores de las fichas (String) y los valores 
     *         son las cantidades de cada ficha (Integer). Devuelve null si no hay suficientes fichas 
     *         en la bolsa.
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
     * @param fichas Un mapa donde las claves son las representaciones de las fichas
     *               (como cadenas de texto) y los valores son la cantidad de cada ficha
     *               que se debe agregar a la bolsa.
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
     * @return El número de fichas disponibles en la bolsa.
     */
    public int getCantidadFichas() {
        // Verifica si el juego ha terminado (por ejemplo, si la bolsa está vacía o si un jugador ha usado todas sus fichas)
        return this.bolsa.getCantidadFichas();
    }
    

    /**
     * Método before
     * @param pos Posición actual.
     * @return Nueva posición antes de la actual.
     */
    public Tuple<Integer, Integer> before(Tuple<Integer, Integer> pos) {
        return direction == Direction.HORIZONTAL ? new Tuple<>(pos.x, pos.y - 1) : new Tuple<>(pos.x - 1, pos.y);
    } 

    /**
     * Método after
     * @param pos Posición actual.
     * @return Nueva posición después de la actual.
     */
    public Tuple<Integer, Integer> after(Tuple<Integer, Integer> pos) {
        return direction == Direction.HORIZONTAL ? new Tuple<>(pos.x, pos.y + 1) : new Tuple<>(pos.x + 1, pos.y);
    }

    /**
     * Método before_cross
     * @param pos Posición actual.
     * @return Nueva posición antes de la actual en la dirección girada.
     */
    public Tuple<Integer, Integer> before_cross(Tuple<Integer, Integer> pos) {
        return direction == Direction.HORIZONTAL ? new Tuple<>(pos.x - 1, pos.y) : new Tuple<>(pos.x, pos.y - 1);
    }

    /**
     * Método after_cross
     * @param pos Posición actual.
     * @return Nueva posición después de la actual en la dirección girada.
     */
    public Tuple<Integer, Integer> after_cross(Tuple<Integer, Integer> pos) {
        return direction == Direction.HORIZONTAL ? new Tuple<>(pos.x + 1, pos.y) : new Tuple<>(pos.x, pos.y + 1);
    }

    /*
     * Método para encontrar los anclajes en el tablero.
     * @return Conjunto de posiciones de anclajes.
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

    /*
     * Método para extender la palabra hacia la izquierda.
     * @param partialWord Palabra parcial.
     * @param rack Mapa de letras disponibles.
     * @param currenNode Nodo actual en el DAWG.
     * @param nextPos Posición siguiente.
     * @param limit Límite de letras a usar.
     * @return Conjunto de palabras extendidas.
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

    /*
     * Método para extender la palabra hacia la derecha.
     * @param partialWord Palabra parcial.
     * @param rack Mapa de letras disponibles.
     * @param currenNode Nodo actual en el DAWG.
     * @param nextPos Posición siguiente.
     * @param archorFilled Indica si el anclaje está lleno.
     * @return Conjunto de palabras extendidas.
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

    /*
     * Método para realizar una verificación cruzada en el tablero.
     * @return Mapa de posiciones y conjuntos de palabras posibles.
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

    /*
     * Método para buscar todos los movimientos posibles en el tablero.
     * @param rack Mapa de letras disponibles.
     * @return Conjunto de movimientos posibles.
     */

    public Set<Triple<String,Tuple<Integer, Integer>, Direction>> searchAllMoves(Map<String, Integer> rack, boolean isFirst) {
        Set<Triple<String,Tuple<Integer, Integer>, Direction>> answers = new HashSet<>();
        Set<Tuple<Integer, Integer>> anchors = find_anchors(isFirst);

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

    /*
     * Método para realizar un movimiento en el tablero.
     * @param move Movimiento a realizar.
     * @param rack Mapa de letras disponibles.
     * @return Mapa actualizado de letras disponibles.
     */

    public Map<String, Integer> makeMove(Triple<String,Tuple<Integer, Integer>, Direction> move, Map<String, Integer> rack) {
        String word = move.x.toUpperCase();
        Tuple<Integer, Integer> pos = move.y;
        Direction dir = move.z;

        Map<String, Integer> newRack = new HashMap<>(rack);

        for (int i = word.length() - 1; i >= 0; i--) {
            String letter = String.valueOf(word.charAt(i)).toUpperCase();
            
            if (newRack.containsKey(letter) && this.tablero.isEmpty(pos)) {
                if (newRack.get(letter) == 1) {
                    newRack.remove(letter);
                } else {
                    newRack.put(letter, newRack.get(letter) - 1);
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

    /*
     * Método para calcular los puntos de un movimiento.
     * @param move Movimiento a evaluar.
     * @return Puntos obtenidos por el movimiento.
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

    /*
     * Método para verificar si un movimiento es válido.
     * @param move Movimiento a evaluar.
     * @param rack Mapa de letras disponibles.
     * @return true si el movimiento es válido, false en caso contrario.
     */

    public boolean isValidMove (Triple<String,Tuple<Integer, Integer>, Direction> move, Map<String, Integer> rack) {
        Set<Triple<String,Tuple<Integer, Integer>, Direction>> possibleWords = searchAllMoves(rack, true);
        return possibleWords.contains(move);
    }

    /*
     * Método para verificar si es un movimiento válido en el primer turno.
     * @param move Movimiento a evaluar.
     * @param rack Mapa de letras disponibles.
     * @return true si el movimiento es válido, false en caso contrario.
     */

     public boolean isValidFirstMove(Triple<String, Tuple<Integer, Integer>, Direction> move, Map<String, Integer> rack) {
        String word = move.x;
        Tuple<Integer, Integer> pos = move.y;
        Direction dir = move.z;
    
    
        // Check if the word fits within the board boundaries
        for (int i = word.length() - 1; i >= 0; i--) {
            if (!tablero.validPosition(pos)) {
                return false;
            }
            pos = dir == Direction.HORIZONTAL ? new Tuple<>(pos.x, pos.y - 1) : new Tuple<>(pos.x - 1, pos.y);
        }
    
        // Reset position to the starting point
        pos = move.y;
    
        // Check if the word is placed on the center tile
        boolean centerTileCovered = false;
        for (int i = word.length() - 1; i >= 0; i--) {
            if (pos.equals(tablero.getCenter())) {
                centerTileCovered = true;
            }
            pos = dir == Direction.HORIZONTAL ? new Tuple<>(pos.x, pos.y - 1) : new Tuple<>(pos.x - 1, pos.y);
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
                diletter = String.valueOf(word.charAt(i + 1)).toUpperCase();
            }
    
            if (tempRack.containsKey(letter) || tempRack.containsKey(diletter)) {
                String key = tempRack.containsKey(letter) ? letter : diletter;
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
    
    /*
     * Método para mostrar el tablero en la consola.
     * Este método imprime el tablero en la consola.
     */



    /*
     * Método para realizar un turno de juego.
     * @param isFirst Indica si es el primer turno del jugador.
     * @return Triple que contiene la palabra, la posición y la dirección.
     */

    /*
     * Método para realizar una acción en el juego.
     * Este método permite al jugador realizar una acción, ya sea colocar palabras, pasar o intercambiar fichas.
     * @param move El movimiento a realizar, que incluye la palabra, posición y dirección
     * @param nombreJugador El nombre del jugador que está realizando la acción
     * @param rack El rack del jugador, que contiene las letras disponibles para jugar
     * @param isIA Indica si el jugador es una IA o un jugador humano
     * @param dificultad La dificultad de la IA (si aplica)
     * @param isFirst Indica si es el primer turno de la partida
     * @param pausado Flag para indicar si la partida se pausa
     * @return Un objeto Tuple que contiene el nuevo rack del jugador y los puntos obtenidos por la jugada
     */

     private Tuple<Map<String, Integer>, Integer> realizarAccion(Triple<String,Tuple<Integer, Integer>, Direction> move, String nombreJugador, Map<String, Integer> rack, boolean isIA, Dificultad dificultad, boolean isFirst) {
        if (!isIA) {
            if (move == null) {
                return null;
            } else {   
                return new Tuple<Map<String,Integer>,Integer>(this.makeMove(move, rack), this.calculateMovePoints(move));
            }
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
                return new Tuple<Map<String,Integer>,Integer>(this.makeMove(bestMove, rack), bestMovePoints);
            }
        
        }
    }
    /*
     * Método para realizar un turno en el juego.
     * Este método permite al jugador realizar su turno, ya sea humano o IA.
     * @param nombreJugador El nombre del jugador que está realizando el movimiento
     * @param rack El rack del jugador, que contiene las letras disponibles para jugar
     * @param isIA Indica si el jugador es una IA o un jugador humano
     * @param dificultad La dificultad de la IA (si aplica)
     * @param pausado Flag para indicar si la partida se pausa
     * @return Un objeto Tuple que contiene el nuevo rack del jugador y los puntos obtenidos por la jugada
     */

    public Tuple<Map<String, Integer>, Integer> realizarTurno(Triple<String,Tuple<Integer, Integer>, Direction> move, String nombreJugador, Map<String, Integer> rack,  boolean isIA, Dificultad dificultad) {
        return realizarAccion(move, nombreJugador, rack, isIA, dificultad, juegoIniciado);
    }

        /*
     * Método para finalizar el juego.
     * Este método se llama cuando el juego ha terminado, ya sea porque un jugador ha ganado o porque no hay más fichas en la bolsa.
     * En este caso, se muestra un mensaje indicando que el juego ha terminado.
     */

     public void finalizarJuego() {
        juegoTerminado = true;
    }

    /*
     * Método para reiniciar el juego.
     * Este método reinicia el juego, restableciendo el estado del juego y la bolsa de fichas.
     */

    public void reiniciarJuego() {
        
        juegoTerminado = false;
        juegoIniciado = false;
    }

    /*
     * Método para obtener el estado del juego.
     * Este método devuelve un booleano que indica si el juego ha terminado o no.
     * @return true si el juego ha terminado, false en caso contrario
     */

    public boolean isJuegoTerminado() {
        return juegoTerminado;
    }

    public boolean isJuegoIniciado() {
        return juegoIniciado;
    }

    public String mostrarStatusPartida(String nombreJugador) {
        StringBuilder sb = new StringBuilder();
        sb.append("=====================================\n");
        sb.append("          ESTADO DE LA PARTIDA       \n");
        sb.append("=====================================\n");
        sb.append("Jugador: ").append(nombreJugador).append("\n\n");
        sb.append("Tablero:\n").append(tablero.toString()).append("\n\n");
        sb.append("Fichas restantes en la bolsa: ").append(bolsa.getCantidadFichas()).append("\n");
        return sb.toString();
    }


        // Guarda este objeto en un archivo .dat
    public void guardar(String nombreArchivo) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nombreArchivo))) {
            oos.writeObject(this);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo: " + nombreArchivo, e);
        }
    }

    public void cargarDesdeArchivo(String nombreArchivo) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(nombreArchivo))) {
            ControladorJuego cargado = (ControladorJuego) ois.readObject();
    
            // Sobrescribir campos
            this.tablero = cargado.tablero;
            this.bolsa = cargado.bolsa;
            this.direction = cargado.direction;
            this.juegoTerminado = cargado.juegoTerminado;
            this.juegoIniciado = cargado.juegoIniciado;
            this.lastCrossCheck = cargado.lastCrossCheck;
            this.nombreDiccionario = cargado.nombreDiccionario;
            this.alfabeto = cargado.alfabeto;
            this.jugadores = cargado.jugadores;
    
            // No se copia controladorDiccionario porque es transient
            System.out.println("Datos cargados desde " + nombreArchivo);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error al cargar el archivo: " + nombreArchivo, e);
        }
    }
    

    // Lista todos los archivos .dat en el directorio actual
    public List<String> listarArchivosGuardados() {
        File dir = new File("./partidas/");
        File[] archivos = dir.listFiles((d, name) -> name.endsWith(".dat"));
        List<String> lista = new ArrayList<>();
        if (archivos != null) {
            for (File archivo : archivos) {
                lista.add(archivo.getName());
            }
        }
        return lista;
    }

    // Elimina un archivo .dat
    public boolean eliminarArchivoGuardado(String nombreArchivo) {
        File archivo = new File(nombreArchivo);
        if (archivo.exists()) {
            return archivo.delete();
        }
        return false;
    }


    public Set<String> getJugadoresActuales() {
        return jugadores;
    }
 }
