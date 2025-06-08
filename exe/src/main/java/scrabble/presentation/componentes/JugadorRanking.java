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
     * @param nombre Nombre del jugador
     * @param pt Puntos totales acumulados por el jugador
     * @param pm Puntuación máxima alcanzada en una partida
     * @param pmed Puntuación promedio por partida
     * @param partidas Número total de partidas jugadas
     * @param victorias Número total de victorias
     * @pre nombre != null && !nombre.trim().isEmpty() && pt >= 0 && pm >= 0 && 
     *      pmed >= 0 && partidas >= 0 && victorias >= 0 && victorias <= partidas
     * @post Jugador creado con todas las propiedades inicializadas correctamente
     */
    public JugadorRanking(String nombre, int pt, int pm, double pmed, int partidas, int victorias) {
        this.nombre = new SimpleStringProperty(nombre);
        this.puntosTotales = new SimpleIntegerProperty(pt);
        this.puntosMaximos = new SimpleIntegerProperty(pm);
        this.puntosMedias = new SimpleDoubleProperty(pmed);
        this.partidas = new SimpleIntegerProperty(partidas);
        this.victorias = new SimpleIntegerProperty(victorias);
    }

    /**
     * Obtiene el nombre del jugador.
     * @return Nombre del jugador
     * @post Resultado no nulo
     */
    public String getNombre() { return nombre.get(); }

    /**
     * Obtiene los puntos totales acumulados por el jugador.
     * @return Puntos totales del jugador
     * @post Resultado >= 0
     */
    public int getPuntosTotales() { return puntosTotales.get(); }

    /**
     * Obtiene la puntuación máxima alcanzada en una partida.
     * @return Puntos máximos obtenidos en una sola partida
     * @post Resultado >= 0
     */
    public int getPuntosMaximos() { return puntosMaximos.get(); }

    /**
     * Obtiene la puntuación promedio por partida del jugador.
     * @return Puntos promedio por partida
     * @post Resultado >= 0
     */
    public double  getPuntosMedias() { return puntosMedias.get(); }
    /**
     * Obtiene el número total de partidas jugadas.
     * @return Cantidad de partidas jugadas
     * @post Resultado >= 0
     */
    public int getPartidas() { return partidas.get(); }

    /**
     * Obtiene el número total de victorias del jugador.
     * @return Cantidad de victorias obtenidas
     * @post Resultado >= 0 && Resultado <= getPartidas()
     */
    public int getVictorias() { return victorias.get(); }

}
