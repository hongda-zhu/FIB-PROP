package domain.testing;

import domain.controllers.subcontrollers.ControladorJuego;
import domain.controllers.subcontrollers.managers.GestorJugada;
import domain.controllers.subcontrollers.managers.GestorJugada.Direction;
import domain.models.Dawg;
import domain.models.JugadorHumano;
import domain.models.JugadorIA;
import domain.models.Tablero;
import domain.models.Triple;
import domain.models.Tuple;
import domain.models.Jugador;

import java.util.Map;
import java.util.Set;

public class Main2 {
    public static void main(String[] args) {
        // Crear los jugadores
        Jugador jugadorHumano1 = new JugadorHumano("Jugador 1", null);
        Jugador jugadorIA = new JugadorIA("IA 1", null);

        // Crear el controlador de juego con dos jugadores
        ControladorJuego controladorJuego = new ControladorJuego(2);
        
        // Inicializar jugadores
        controladorJuego.agregarJugador(jugadorHumano1, 0);
        controladorJuego.agregarJugador(jugadorIA, 1);
        
        // Inicializar la Dawg con algunas palabras válidas
        controladorJuego.inicializarDawg();

        // Iniciar el juego
        controladorJuego.iniciarJuego();
    }
}
