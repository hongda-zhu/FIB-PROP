package domain.models;

public class Partida  {
    private int id;
    private int id_global = 1;
    // private Jugador[] jugadores;
    private Tablero_S tablero;
    
    public Partida(/*Jugador[] jugadores,*/ Tablero_S tablero) {
        this.id = id_global++;
        // this.jugadores = jugadores;
        this.tablero = tablero;
    }

    public int getId() {
        return id;
    }

    // public Jugador[] getJugadores() {
    //     return jugadores;
    // }

    public Tablero_S getTablero() {
        return tablero;
    }
}