package scrabble.helpers;
import java.io.Serializable;
/**
    * Enumeración que define los tipos de bonificación disponibles en el tablero.
    * N: Normal (sin bonificación)
    * TW: Triple Word (triplica puntos de la palabra)
    * TL: Triple Letter (triplica puntos de la letra)
    * DW: Double Word (duplica puntos de la palabra)
    * DL: Double Letter (duplica puntos de la letra)
    * X: Casilla especial (duplica puntos de la letra)
    */
public enum Bonus implements Serializable{

    N, TW, TL, DW, DL, X;
        private static final long serialVersionUID = 1L;
}