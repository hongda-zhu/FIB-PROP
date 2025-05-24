package scrabble.presentation.componentes;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Clase que representa un jugador en el ranking.
 * Contiene información sobre el nombre, puntos totales, puntos máximos,
 * puntos medias, partidas jugadas y victorias.
 */

public class JugadorRanking {
    private final SimpleStringProperty nombre;
    private final SimpleIntegerProperty puntosTotales;
    private final SimpleIntegerProperty puntosMaximos;
    private final SimpleDoubleProperty puntosMedias;
    private final SimpleIntegerProperty partidas;
    private final SimpleIntegerProperty victorias;

    /**
     * Constructor de la clase JugadorRanking.
     *
     * @param nombre      Nombre del jugador.
     * @param pt          Puntos totales del jugador.
     * @param pm          Puntos máximos del jugador.
     * @param pmed        Puntos medias del jugador.
     * @param partidas     Partidas jugadas por el jugador.
     * @param victorias    Victorias del jugador.
     */
    
    public JugadorRanking(String nombre, int pt, int pm, double pmed, int partidas, int victorias) {
        this.nombre = new SimpleStringProperty(nombre);
        this.puntosTotales = new SimpleIntegerProperty(pt);
        this.puntosMaximos = new SimpleIntegerProperty(pm);
        this.puntosMedias = new SimpleDoubleProperty(pmed);
        this.partidas = new SimpleIntegerProperty(partidas);
        this.victorias = new SimpleIntegerProperty(victorias);
    }

    // Getters
    public String getNombre() { return nombre.get(); }
    public int getPuntosTotales() { return puntosTotales.get(); }
    public int getPuntosMaximos() { return puntosMaximos.get(); }
    public double  getPuntosMedias() { return puntosMedias.get(); }
    public int getPartidas() { return partidas.get(); }
    public int getVictorias() { return victorias.get(); }
}
