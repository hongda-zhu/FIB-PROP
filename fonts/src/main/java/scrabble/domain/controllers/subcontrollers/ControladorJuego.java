package scrabble.domain.controllers.subcontrollers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import scrabble.domain.controllers.subcontrollers.managers.GestorJugada;
import scrabble.domain.controllers.subcontrollers.managers.GestorJugada.Direction;
import scrabble.domain.models.Bolsa;
import scrabble.domain.models.JugadorIA.Dificultad;
import scrabble.domain.models.Tablero;
import scrabble.helpers.Triple;
import scrabble.helpers.Tuple;

/**
 * ControladorJuego es la clase encargada de gestionar el flujo del juego de Scrabble.
 * Esta clase se encarga de iniciar el juego, gestionar los turnos de los jugadores,
 * validar las jugadas y mantener el estado del juego.
 */

public class ControladorJuego {

    private GestorJugada gestorJugada;
    private Bolsa bolsa;
    private boolean juegoTerminado = false;
    private boolean juegoIniciado = false;


    public ControladorJuego() {
        this.gestorJugada = new GestorJugada(new Tablero()) ;
        this.bolsa = null;
    }

    /*
     * Método para iniciar el juego.
     * Este método inicializa la bolsa de fichas y carga el diccionario correspondiente al idioma seleccionado.
     * @param languaje El idioma del juego 
     */

    public void iniciarJuego(String languaje) {
        bolsa = new Bolsa();
        bolsa.llenarBolsa(this.gestorJugada.getBag(languaje));
    }

    /*
     * Método para limpiar la consola.
     * Este método imprime una serie de líneas vacías para simular la limpieza de la consola.
     */

    private void limpiarConsola() {
        for (int i = 0; i < 25; i++) {
            System.out.println();  // Imprimir 50 líneas vacías
        }
    }

    /*
     * Método para añadir un nuevo idioma al juego.
     * Este método permite añadir un nuevo idioma al juego, especificando el nombre del idioma,
     * la ruta del archivo con juegos de letras y la ruta del diccionario.
     * @param nombre El nombre del idioma a añadir
     * @param rutaArchivoAlpha La ruta del archivo con juegos de letras correspondiente al idioma
     * @param rutaArchivoWords La ruta del diccionario correspondiente al idioma
     */

    public void anadirLenguaje(String nombre, String rutaArchivoAlpha, String rutaArchivoWords) {
        this.gestorJugada.anadirLenguaje(nombre, rutaArchivoAlpha, rutaArchivoWords);
    }

    /*
     * Método para establecer el idioma del juego.
     * Este método permite establecer el idioma del juego, especificando el nombre del idioma.
     * @param nombre El nombre del idioma a establecer
     */

    public void setLenguaje(String nombre) {
        this.gestorJugada.setLenguaje(nombre);
    }
    
    /* 
     * Método para hacer el primer turno del jugador.
     * Este método permite al jugador realizar su primer movimiento en el juego.
     * @param nombreJugador El nombre del jugador que está realizando el movimiento
     * @param rack El rack del jugador, que contiene las letras disponibles para jugar
     * @return Un objeto Tuple que contiene el nuevo rack del jugador y los puntos obtenidos por la jugada
     */

    private Tuple<Map<String, Integer>, Integer> primerTurno(String nombreJugador, Map<String, Integer> rack) {

        Triple<String,Tuple<Integer, Integer>, Direction> move = this.gestorJugada.jugarTurno(true);

        if (move == null) {
            // Si el jugador decide pasar su turno, se puede implementar aquí
            System.out.println(nombreJugador + "no puedes pasar de turno, eres el primero en jugar.");
            return null;
        }

        if (move != null && this.gestorJugada.isValidFirstMove(move, rack)) {
            this.juegoIniciado = true;
            return new Tuple<Map<String, Integer>, Integer>(this.gestorJugada.makeMove(move, rack), this.gestorJugada.calculateMovePoints(move));
        } else {
            System.out.println("La jugada no es válida. Intenta nuevamente.");
            return primerTurno(nombreJugador, rack); // Verifica si la jugada es válida
        }
    }

    /*
     * Método para realizar un turno en el juego.
     * Este método permite al jugador realizar su turno, ya sea humano o IA.
     * @param nombreJugador El nombre del jugador que está realizando el movimiento
     * @param rack El rack del jugador, que contiene las letras disponibles para jugar
     * @param isIA Indica si el jugador es una IA o un jugador humano
     * @param dificultad La dificultad de la IA (si aplica)
     * @return Un objeto Tuple que contiene el nuevo rack del jugador y los puntos obtenidos por la jugada
     */

    public Tuple<Map<String, Integer>, Integer> realizarTurno(String nombreJugador, Map<String, Integer> rack,  boolean isIA, Dificultad dificultad) {
        limpiarConsola();
        this.gestorJugada.mostrarTablero();

        System.out.println("Tu rack actual es: " + rack);
        return !juegoIniciado? primerTurno(nombreJugador, rack) : realizarAccion(nombreJugador, rack, isIA, dificultad);
    }

    /*
     * Método para realizar una acción en el juego.
     * Este método permite al jugador realizar una acción, ya sea colocar palabras, pasar o intercambiar fichas.
     * @param nombreJugador El nombre del jugador que está realizando la acción
     * @param rack El rack del jugador, que contiene las letras disponibles para jugar
     * @param isIA Indica si el jugador es una IA o un jugador humano
     * @param dificultad La dificultad de la IA (si aplica)
     * @return Un objeto Tuple que contiene el nuevo rack del jugador y los puntos obtenidos por la jugada
     */

    private Tuple<Map<String, Integer>, Integer> realizarAccion(String nombreJugador, Map<String, Integer> rack, boolean isIA, Dificultad dificultad) {
        // El jugador puede colocar palabras, pasar o intercambiar fichas.
        if (!isIA) {
            // El jugador humano puede ingresar una palabra o hacer una acción
            // Aquí implementas la lógica para que el jugador humano haga su jugada.
            System.out.println(nombreJugador + "Es tu turno, coloca una palabra o skip");

            Triple<String,Tuple<Integer, Integer>, Direction> move = this.gestorJugada.jugarTurno(false);

            if (move == null) {
                // Si el jugador decide pasar su turno, se puede implementar aquí
                System.out.println(nombreJugador + " ha decidido pasar su turno.");
                return null;
            } else {   
                if (this.gestorJugada.isValidMove(move, rack)){
                    return new Tuple<Map<String,Integer>,Integer>(this.gestorJugada.makeMove(move, rack), this.gestorJugada.calculateMovePoints(move));
                } else {
                    System.out.println("La jugada no es válida. Intenta nuevamente.");
                    return realizarAccion(nombreJugador, rack, isIA, dificultad);// Verifica si la jugada es válida
                }
            }
        } else {
            Set<Triple<String,Tuple<Integer, Integer>, Direction>> move = this.gestorJugada.searchAllMoves(rack);
            if (move == null || move.isEmpty()) {
                System.out.println("El jugador IA no puede hacer una jugada.");
                return null;
            } else {
                Triple<String,Tuple<Integer, Integer>, Direction> bestMove = null;
                int bestMovePoints = 0;
                for (Triple<String,Tuple<Integer, Integer>, Direction> m : move) {
                    int currentMovePoints = this.gestorJugada.calculateMovePoints(m);
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
                System.out.println("El jugador IA está haciendo su jugada.");
                return new Tuple<Map<String,Integer>,Integer>(this.gestorJugada.makeMove(bestMove, rack), bestMovePoints);
            }
        
        }
    }

    /*
     * Método para obtener la cantidad de fichas restantes en la bolsa.
     * Este método devuelve la cantidad de fichas restantes en la bolsa.
     * @return La cantidad de fichas restantes en la bolsa
     */

    public int getCantidadFichas() {
        // Verifica si el juego ha terminado (por ejemplo, si la bolsa está vacía o si un jugador ha usado todas sus fichas)
        return this.bolsa.getCantidadFichas();
    }

    /*
     * Método para finalizar el juego.
     * Este método se llama cuando el juego ha terminado, ya sea porque un jugador ha ganado o porque no hay más fichas en la bolsa.
     * En este caso, se muestra un mensaje indicando que el juego ha terminado.
     */

    public void finalizarJuego() {
        System.out.println("El juego ha terminado.");
        juegoTerminado = true;
    }

    /*
     * Método para reiniciar el juego.
     * Este método reinicia el juego, restableciendo el estado del juego y la bolsa de fichas.
     */

    public void reiniciarJuego() {
        this.gestorJugada = new GestorJugada(new Tablero());
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

    /*
     * Método para coger fichas de la bolsa.
     * Este método permite al jugador coger una cantidad específica de fichas de la bolsa.
     * @param cantidad La cantidad de fichas a coger
     * @return Un mapa que contiene las fichas cogidas y su cantidad
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
}