package domain.controllers.subcontrollers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import domain.controllers.subcontrollers.managers.GestorJugada;
import domain.controllers.subcontrollers.managers.GestorJugada.Direction;
import domain.models.Bolsa;
import domain.models.Dawg;
import domain.models.JugadorHumano;
import domain.models.JugadorIA;
import domain.models.Tablero;
import domain.models.Triple;
import domain.models.Tuple;
import domain.models.Jugador;

public class ControladorJuego {

    private GestorJugada gestorJugada;
    private Jugador[] jugadores;
    private int numeroJugadores;
    private Bolsa bolsa;
    private boolean juegoTerminado = false;
    private int turnoActual = 0;


    public ControladorJuego(int N) {
        this.numeroJugadores = N;
        this.jugadores = new Jugador[this.numeroJugadores]; // Assuming a two-player game
        Map<Character, Integer> alphabet = new HashMap<>();

        int i = 0;
        for (char c = 'a'; c <= 'z'; c++) {
            alphabet.put(c, ++i);
        }

        this.gestorJugada = new GestorJugada(new Tablero(), new Dawg(), alphabet);
        this.bolsa = null;
    }

    public void inicializarDawg() {
        this.gestorJugada.dawg.insert("or");
        this.gestorJugada.dawg.insert("floor");
        this.gestorJugada.dawg.insert("for");
        this.gestorJugada.dawg.insert("fall");
        this.gestorJugada.dawg.insert("future");
        this.gestorJugada.dawg.insert("off");
        this.gestorJugada.dawg.insert("effect");
        this.gestorJugada.dawg.insert("hola");
    }

    public void agregarJugador(Jugador jugador, int index) {
        if (index >= 0 && index < numeroJugadores) {
            this.jugadores[index] = jugador;
        } else {
            System.out.println("Índice de jugador no válido.");
        }
    }

    public void eliminarJugador(int index) {
        if (index >= 0 && index < numeroJugadores) {
            this.jugadores[index] = null;
        } else {
            System.out.println("Índice de jugador no válido.");
        }
    }

    public void iniciarJuego() {
        bolsa = new Bolsa();
        bolsa.llenarBolsa();

        for (Jugador jugador : this.jugadores) {
            if (bolsa.getCantidadFichas() < 7) {
                System.out.println("No hay suficientes fichas en la bolsa para continuar el juego.");
                break;
            }
            if (jugador instanceof JugadorHumano){
                ((JugadorHumano) jugador).robarFichas(bolsa, 7);
            } else if (jugador instanceof JugadorIA) {
                ((JugadorIA) jugador).robarFichas(bolsa, 7);
            }
            else {
                System.out.println("El Jugador no es un jugador válido.");
            }
        }
        comenzarTurnos();
    }

    private void limpiarConsola() {
        for (int i = 0; i < 50; i++) {
            System.out.println();  // Imprimir 50 líneas vacías
        }
    }

    private void comenzarTurnos() {
        while (!juegoTerminado) {
            limpiarConsola();
            Jugador jugadorActual = jugadores[turnoActual];
            System.out.println("Turno de: " + jugadorActual.getNombre());

            this.gestorJugada.mostrarTablero();
            // El jugador puede realizar una acción (por ejemplo, colocar palabra, pasar, etc.)
            realizarAccion(jugadorActual);

            // Verificar si el juego ha terminado
            if (verificarFinJuego()) {
                finalizarJuego();
            }

            // Pasar al siguiente turno
            turnoActual = (turnoActual + 1) % jugadores.length;
        }
    }

    private void realizarAccion(Jugador jugador) {
        // El jugador puede colocar palabras, pasar o intercambiar fichas.
        if (jugador instanceof JugadorHumano) {
            // El jugador humano puede ingresar una palabra o hacer una acción
            // Aquí implementas la lógica para que el jugador humano haga su jugada.
            System.out.println("Es tu turno, coloca una palabra o skip");

            Triple<String,Tuple<Integer, Integer>, Direction> move = ((JugadorHumano) jugador).jugarTurno();

            if (move == null) {
                // Si el jugador decide pasar su turno, se puede implementar aquí
                System.out.println("El jugador ha decidido pasar su turno.");
                ((JugadorHumano)jugador).addSkipTrack();
            } else {   
                if (this.gestorJugada.isValidMove(move, ((JugadorHumano)jugador).getFichas())){
                    this.gestorJugada.calculateMovePoints(move);
                    this.gestorJugada.makeMove(move);
                    ((JugadorHumano)jugador).setSkipTrack(0);
                } else realizarAccion(jugador);// Verifica si la jugada es válida
            }
        } else if (jugador instanceof JugadorIA) {
            Set<Triple<String,Tuple<Integer, Integer>, Direction>> move = this.gestorJugada.searchAllMoves(((JugadorIA)jugador).getFichas());
            if (move == null || move.isEmpty()) {
                System.out.println("El jugador IA no puede hacer una jugada.");
                ((JugadorIA)jugador).addSkipTrack();
                return;
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
                this.gestorJugada.makeMove(bestMove);
                ((JugadorIA)jugador).addPuntaje(bestMovePoints);
                System.out.println("El jugador IA está haciendo su jugada.");
                ((JugadorIA)jugador).setSkipTrack(0);
            }
          
        }
    }

    private boolean verificarFinJuego() {
        // El juego termina cuando no hay más fichas en la bolsa y los jugadores ya no pueden jugar
        if (bolsa.getCantidadFichas() == 0) {
            // Verificar si todos los jugadores han usado todas sus fichas o no pueden jugar
            for (Jugador jugador : jugadores) {
                if (jugador instanceof JugadorHumano) {
                    if (((JugadorHumano)jugador).getFichas().size() > 0) {
                        return false; // Al menos un jugador tiene fichas restantes
                    }
                } else if (jugador instanceof JugadorIA) {
                    if(((JugadorIA)jugador).getFichas().size() > 0) {
                        return false; // Al menos un jugador IA tiene fichas restantes
                    }
                }
            }
            return true; 
        } else {
            // Verificar si todos los jugadores han pasado su turno consecutivamente
            for (Jugador jugador : jugadores) {
                if (jugador instanceof JugadorHumano) {
                    if (((JugadorHumano)jugador).getSkipTrack() < 2) {
                        return false; // Al menos un jugador no ha pasado su turno
                    }
                } else if (jugador instanceof JugadorIA) {
                    if(((JugadorIA)jugador).getSkipTrack() < 2) {
                        return false; // Al menos un jugador IA no ha pasado su turno
                    }
                }
            }
            return true;
        }
    }

    private void mostrarPuntajes() {
        System.out.println("Puntajes finales:");
        for (Jugador jugador : jugadores) {
            if (jugador instanceof JugadorHumano) {
                System.out.println(jugador.getNombre() + ": " + ((JugadorHumano)jugador).getPuntaje());
            } else if (jugador instanceof JugadorIA) {
                System.out.println("IA " + jugador.getNombre() + ": " + ((JugadorIA)jugador).getPuntaje());
            }
        }
    }

    public void finalizarJuego() {
        System.out.println("El juego ha terminado.");
        mostrarPuntajes();
        juegoTerminado = true;
    }

    public void reiniciarJuego() {
        this.jugadores = new Jugador[this.numeroJugadores]; // Assuming a two-player game
        Map<Character, Integer> alphabet = new HashMap<>();

        int i = 0;
        for (char c = 'a'; c <= 'z'; c++) {
            alphabet.put(c, ++i);
        }

        this.gestorJugada = new GestorJugada(new Tablero(), new Dawg(), alphabet);
        this.bolsa = null;
    }
}