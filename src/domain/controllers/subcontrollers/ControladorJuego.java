package domain.controllers.subcontrollers;

import domain.controllers.subcontrollers.managers.GestorJugada;
import domain.controllers.subcontrollers.managers.GestorJugada.Direction;
import domain.models.Bolsa;
import domain.models.Dawg;
import domain.models.Tablero;
import domain.models.Triple;
import domain.models.Tuple;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class ControladorJuego {

    private GestorJugada gestorJugada;
    private Bolsa bolsa;
    private boolean juegoTerminado = false;


    public ControladorJuego() {
        Map<String, Integer> alphabet = new HashMap<>();

        this.gestorJugada = new GestorJugada(new Tablero(), new Dawg(), alphabet) ;
        this.bolsa = null;
    }

    public void inicializarDawgDesdeEntrada() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Introduce palabras separadas por enter. Escribe 'FIN' para terminar:");
        
        List<String> palabras = new ArrayList<>();
        while (true) {
            String palabra = scanner.nextLine();
            if (palabra.equalsIgnoreCase("FIN")) {
                break;
            }
            palabras.add(palabra);
        }
        
        this.gestorJugada.inicializarDawg(palabras);
    }

    public void iniciarJuego(String rutaArchivo) {
        bolsa = new Bolsa();
        bolsa.llenarBolsa(rutaArchivo);

        // for (Jugador jugador : this.jugadores) {
        //     if (bolsa.getCantidadFichas() < 7) {
        //         System.out.println("No hay suficientes fichas en la bolsa para continuar el juego.");
        //         break;
        //     }
        //     if (jugador instanceof JugadorHumano){
        //         ((JugadorHumano) jugador).robarFichas(bolsa, 7);
        //     } else if (jugador instanceof JugadorIA) {
        //         ((JugadorIA) jugador).robarFichas(bolsa, 7);
        //     }
        //     else {
        //         System.out.println("El Jugador no es un jugador válido.");
        //     }
        // }

        // Lo hace el dominio

    }

    private void limpiarConsola() {
        for (int i = 0; i < 25; i++) {
            System.out.println();  // Imprimir 50 líneas vacías
        }
    }

    private int primerTurno(String nombreJugador, Map<String, Integer> rack) {
        // El primer turno es especial, ya que el jugador puede colocar una palabra en el tablero
        // y no puede pasar su turno.
        limpiarConsola();

        this.gestorJugada.mostrarTablero();

        Triple<String,Tuple<Integer, Integer>, Direction> move = this.gestorJugada.jugarTurno();

        while (!this.gestorJugada.isValidMove(move, rack)) {
            this.gestorJugada.makeMove(move);
            return this.gestorJugada.calculateMovePoints(move);
        }

        System.out.println("La jugada no es válida. Intenta nuevamente.");
        return primerTurno(nombreJugador, rack); // Verifica si la jugada es válida
    }

    private void realizarTurno(String nombreJugador, Map<String, Integer> rack,  boolean isIA) {
        limpiarConsola();

        this.gestorJugada.mostrarTablero();
        realizarAccion(nombreJugador, rack, isIA);
    }

    private int realizarAccion(String nombreJugador, Map<String, Integer> rack,  boolean isIA) {
        // El jugador puede colocar palabras, pasar o intercambiar fichas.
        if (!isIA) {
            // El jugador humano puede ingresar una palabra o hacer una acción
            // Aquí implementas la lógica para que el jugador humano haga su jugada.
            System.out.println(nombreJugador + "Es tu turno, coloca una palabra o skip");

            Triple<String,Tuple<Integer, Integer>, Direction> move = this.gestorJugada.jugarTurno();

            if (move == null) {
                // Si el jugador decide pasar su turno, se puede implementar aquí
                System.out.println(nombreJugador + " ha decidido pasar su turno.");
                return -1;
            } else {   
                if (this.gestorJugada.isValidMove(move, rack)){
                    this.gestorJugada.makeMove(move);
                    return this.gestorJugada.calculateMovePoints(move);
                } else {
                    System.out.println("La jugada no es válida. Intenta nuevamente.");
                    return realizarAccion(nombreJugador, rack, isIA);// Verifica si la jugada es válida
                }
            }
        } else {
            Set<Triple<String,Tuple<Integer, Integer>, Direction>> move = this.gestorJugada.searchAllMoves(rack);
            if (move == null || move.isEmpty()) {
                System.out.println("El jugador IA no puede hacer una jugada.");
                return -1;
            } else {
                Triple<String,Tuple<Integer, Integer>, Direction> bestMove = null;
                int bestMovePoints = 0;
                for (Triple<String,Tuple<Integer, Integer>, Direction> m : move) {
                    int currentMovePoints = this.gestorJugada.calculateMovePoints(m);
                    if (bestMove == null || currentMovePoints > bestMovePoints) {
                        bestMove = m;
                        bestMovePoints = currentMovePoints;
                    }
                }
                System.out.println("El jugador IA está haciendo su jugada.");
                this.gestorJugada.makeMove(bestMove);
                return bestMovePoints;
            }
        
        }
    }

    private boolean verificarFinJuego() {
        // Verifica si el juego ha terminado (por ejemplo, si la bolsa está vacía o si un jugador ha usado todas sus fichas)
        return this.bolsa.getCantidadFichas() == 0;
    }



    public void finalizarJuego() {
        System.out.println("El juego ha terminado.");
        juegoTerminado = true;
    }

    public void reiniciarJuego() {
        Map<String, Integer> alphabet = new HashMap<>();

        this.gestorJugada = new GestorJugada(new Tablero(), new Dawg(), alphabet);
        this.bolsa = null;
    }

    public boolean isJuegoTerminado() {
        return juegoTerminado;
    }
}