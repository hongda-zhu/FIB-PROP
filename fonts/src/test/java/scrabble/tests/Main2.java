package scrabble.tests;

import scrabble.domain.controllers.subcontrollers.ControladorJuego;
import scrabble.domain.models.Jugador;
import scrabble.domain.models.JugadorHumano;
import scrabble.domain.models.JugadorIA;
import scrabble.domain.models.JugadorIA.Dificultad;
import scrabble.helpers.Tuple;

import java.util.Map;
import java.util.Scanner;

public class Main2 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Crear instancia del controlador
        ControladorJuego juego = new ControladorJuego();

        String rutaFichas = "src/resources/alpha.txt"; // Ruta del archivo de fichas
        String rutaAlphabet = "src/resources/words.txt"; // Ruta del archivo de fichas

        // Inicializar el juego
        juego.anadirLenguaje("Esp", rutaFichas, rutaAlphabet);
        juego.setLenguaje("Esp");
        juego.iniciarJuego("Esp");

        JugadorIA jugadorIA = new JugadorIA("Jugador IA", Dificultad.FACIL);
        JugadorHumano jugadorHumano = new JugadorHumano("Jugador 1", "Humano-1");

        Jugador[] jugadores = {jugadorHumano, jugadorIA};


        for (Jugador jugador : jugadores) {
            if (juego.getCantidadFichas() < 7) {
                System.out.println("No hay suficientes fichas en la bolsa para continuar el juego.");
                break;
            }
            Map<String, Integer> rack = juego.cogerFichas(7);
            jugador.inicializarRack(rack);
        }

        Dificultad dificultad = null;

        for (Jugador jugador : jugadores) {
            if (jugador instanceof JugadorIA) {
                dificultad = ((JugadorIA) jugador).getNivelDificultad();
            }
        }

        // Bucle de juego hasta que termine
        while (!juego.isJuegoTerminado()) {
            for (Jugador jugador : jugadores) {
                Tuple<Map<String, Integer>, Integer> result = juego.realizarTurno(jugador.getNombre(), jugador.getRack(), jugador instanceof JugadorIA, dificultad);

                if (result == null) {
                    System.out.println("El jugador " + jugador.getNombre() + " paso la jugada.");
                    jugador.addSkipTrack();
                } else {
                    jugador.inicializarRack(result.x);
                    jugador.addPuntuacion(result.y);
                    
                    Map<String, Integer> nuevasFicha = juego.cogerFichas(7 - jugador.getCantidadFichas());
                    
                    if (nuevasFicha == null) {
                        juego.finalizarJuego();
                        
                    } else {
                        for (Map.Entry<String, Integer> entry : nuevasFicha.entrySet()) {
                            String letra = entry.getKey();
                            int cantidad = entry.getValue();
                            for (int i = 0; i < cantidad; i++) {
                                jugador.agregarFicha(letra);
                            }   
                        }
                    }
                }
            }

            boolean allskiped = true;

            for (Jugador jugador : jugadores) {
                if (jugador.getSkipTrack() < 3) {
                    allskiped = false;
                    break;
                }
            }
            if (allskiped) {
                System.out.println("Los jugadores han pasado mas de 2 veces consecutivas. El juego ha terminado.");
                juego.finalizarJuego();
            }
        }

        // Mostrar estadísticas finales
        System.out.println("Estadísticas finales:");
        for (Jugador jugador : jugadores) {
            System.out.println("Jugador: " + jugador.getNombre());
            System.out.println("Puntuación: " + jugador.getPuntuacion());
            System.out.println("-----------------------------");
        }        
        System.out.println("Gracias por jugar :)");
    }
}

