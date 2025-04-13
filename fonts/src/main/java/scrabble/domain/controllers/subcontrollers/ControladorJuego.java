package domain.controllers.subcontrollers;

import domain.controllers.subcontrollers.managers.GestorJugada;
import domain.controllers.subcontrollers.managers.GestorJugada.Direction;
import domain.helpers.Triple;
import domain.helpers.Tuple;
import domain.models.Bolsa;
import domain.models.Dawg;
import domain.models.Diccionario;
import domain.models.Tablero;
import domain.models.JugadorIA.Dificultad;

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
    private boolean juegoIniciado = false;


    public ControladorJuego() {
        this.gestorJugada = new GestorJugada(new Tablero()) ;
        this.bolsa = null;
    }

    public void iniciarJuego(String languaje) {
        bolsa = new Bolsa();
        bolsa.llenarBolsa(this.gestorJugada.getBag(languaje));
    }

    private void limpiarConsola() {
        for (int i = 0; i < 25; i++) {
            System.out.println();  // Imprimir 50 líneas vacías
        }
    }

    public void anadirLenguaje(String nombre, String rutaArchivoAlpha, String rutaArchivoWords) {
        this.gestorJugada.anadirLenguaje(nombre, rutaArchivoAlpha, rutaArchivoWords);
    }

    public void setLenguaje(String nombre) {
        this.gestorJugada.setLenguaje(nombre);
    }

    

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

    public Tuple<Map<String, Integer>, Integer> realizarTurno(String nombreJugador, Map<String, Integer> rack,  boolean isIA, Dificultad dificultad) {
        limpiarConsola();
        this.gestorJugada.mostrarTablero();

        System.out.println("Tu rack actual es: " + rack);
        return !juegoIniciado? primerTurno(nombreJugador, rack) : realizarAccion(nombreJugador, rack, isIA, dificultad);
    }

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

    public int getCantidadFichas() {
        // Verifica si el juego ha terminado (por ejemplo, si la bolsa está vacía o si un jugador ha usado todas sus fichas)
        return this.bolsa.getCantidadFichas();
    }

    public void finalizarJuego() {
        System.out.println("El juego ha terminado.");
        juegoTerminado = true;
    }

    public void reiniciarJuego() {
        this.gestorJugada = new GestorJugada(new Tablero());
        juegoTerminado = false;
        juegoIniciado = false;
    }

    public boolean isJuegoTerminado() {
        return juegoTerminado;
    }

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