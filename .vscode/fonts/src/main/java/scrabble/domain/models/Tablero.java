package scrabble.domain.models;

import scrabble.domain.controllers.subcontrollers.ControladorJuego.Direction;
import scrabble.helpers.Tuple;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase que representa el tablero de juego de Scrabble.
 * Gestiona la disposición de fichas, bonificaciones especiales y cálculo de puntuaciones.
 * Permite crear tableros de diferentes tamaños, siendo el estándar de 15x15.
 */
public class Tablero implements Serializable{
    private static final long serialVersionUID = 1L;
    /** Matriz que almacena las letras colocadas en el tablero */
    private String[][] tablero;
    
    /** Matriz que almacena los bonus de cada casilla */
    private Bonus[][] bonus;
    
    /** Mapa que asocia cada letra con su valor en puntos */
    private Map<Character, Integer> alphabetPoint;
    
    /** Tamaño del tablero (NxN) */
    private int N;

    /**
     * Inicializa el mapa de puntos para cada letra del alfabeto.
     * Por defecto, asigna puntos incrementales (a=1, b=2, etc).
     */
    private void inicializarPuntosAlfabeto() {
        alphabetPoint = new HashMap<>();
        String letras = "abcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < letras.length(); i++) {
            alphabetPoint.put(letras.charAt(i), i + 1);
        }
    }

    /**
     * Enumeración que define los tipos de bonificación disponibles en el tablero.
     * N: Normal (sin bonificación)
     * TW: Triple Word (triplica puntos de la palabra)
     * TL: Triple Letter (triplica puntos de la letra)
     * DW: Double Word (duplica puntos de la palabra)
     * DL: Double Letter (duplica puntos de la letra)
     * X: Casilla especial (duplica puntos de la letra)
     */
    public enum Bonus {
        N, TW, TL, DW, DL, X
    }

    /**
     * Constructor por defecto. Crea un tablero de 15x15.
     */
    public Tablero() {
        this(15);
    }

    /**
     * Constructor que inicializa un tablero de tamaño NxN.
     * 
     * @param N Tamaño del tablero
     */
    public Tablero (int N) {
        this.tablero = new String[N][N];
        this.N = N;
        inicializarPuntosAlfabeto();
        this.bonus = this.N == 15 ? inicializarTablero15x15() : new Bonus[N][N];
        
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                this.tablero[i][j] = " ";
                if (N != 15) this.bonus[i][j] = Bonus.N;
            }
        }
    }

    /**
     * Inicializa la matriz de bonificaciones para un tablero estándar de 15x15.
     * Configura las posiciones de bonificaciones especiales según las reglas de Scrabble.
     * 
     * @return Matriz de bonificaciones configurada
     */
    private Bonus[][] inicializarTablero15x15() {
        Bonus[][] bonusMatrix = new Bonus[N][N];
        // Inicializar todo con N (Normal)
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                bonusMatrix[i][j] = Bonus.N;
            }
        }   
        
        // Triple Word (TW)
        List<Tuple<Integer, Integer>> twPositions = Arrays.asList(
            new Tuple<>(0, 0), new Tuple<>(7, 0), new Tuple<>(14, 0),
            new Tuple<>(0, 7), new Tuple<>(0, 14), new Tuple<>(7, 14),
            new Tuple<>(14, 7), new Tuple<>(14, 14)
        );
        
        // Triple Letter (TL)
        List<Tuple<Integer, Integer>> tlPositions = Arrays.asList(
            new Tuple<>(5, 1), new Tuple<>(9, 1), new Tuple<>(1, 5),
            new Tuple<>(5, 5), new Tuple<>(9, 5), new Tuple<>(13, 5),
            new Tuple<>(1, 9), new Tuple<>(5, 9), new Tuple<>(9, 9),
            new Tuple<>(13, 9), new Tuple<>(5, 13), new Tuple<>(9, 13)
        );
        
        // Double Word (DW)
        List<Tuple<Integer, Integer>> dwPositions = Arrays.asList(
            new Tuple<>(1, 1), new Tuple<>(2, 2), new Tuple<>(3, 3),
            new Tuple<>(4, 4), new Tuple<>(4, 10), new Tuple<>(3, 11),
            new Tuple<>(2, 12), new Tuple<>(1, 13), new Tuple<>(13, 1),
            new Tuple<>(12, 2), new Tuple<>(11, 3), new Tuple<>(10, 4),
            new Tuple<>(10, 10), new Tuple<>(11, 11), new Tuple<>(12, 12),
            new Tuple<>(13, 13)
        );
        
        // Double Letter (DL)
        List<Tuple<Integer, Integer>> dlPositions = Arrays.asList(
            new Tuple<>(3, 0), new Tuple<>(11, 0), new Tuple<>(0, 3),
            new Tuple<>(6, 2), new Tuple<>(7, 3), new Tuple<>(8, 2),
            new Tuple<>(14, 3), new Tuple<>(2, 6), new Tuple<>(6, 6),
            new Tuple<>(8, 6), new Tuple<>(12, 6), new Tuple<>(3, 7),
            new Tuple<>(11, 7), new Tuple<>(2, 8), new Tuple<>(6, 8),
            new Tuple<>(8, 8), new Tuple<>(12, 8), new Tuple<>(0, 11),
            new Tuple<>(7, 11), new Tuple<>(14, 11), new Tuple<>(6, 12),
            new Tuple<>(8, 12), new Tuple<>(3, 14), new Tuple<>(11, 14)
        );
        
        // Asignar casillas especiales
        for (Tuple<Integer, Integer> pos : twPositions) {
            bonusMatrix[pos.x][pos.y] = Bonus.TW;
        }
        
        for (Tuple<Integer, Integer> pos : tlPositions) {
            bonusMatrix[pos.x][pos.y] = Bonus.TL;
        }
        
        for (Tuple<Integer, Integer> pos : dwPositions) {
            bonusMatrix[pos.x][pos.y] = Bonus.DW;
        }
        
        for (Tuple<Integer, Integer> pos : dlPositions) {
            bonusMatrix[pos.x][pos.y] = Bonus.DL;
        }
        
        // Centro del tablero
        bonusMatrix[7][7] = Bonus.X;
        
        return bonusMatrix;
    }

    /**
     * Constructor de copia.
     * Crea un nuevo tablero con el mismo contenido y bonificaciones que el tablero pasado como parámetro.
     * 
     * @param tablero2 Tablero a copiar
     */
    public Tablero(Tablero tablero2) {
        this.N = tablero2.getSize();
        this.tablero = new String[N][N];
        this.bonus = new Bonus[N][N];
        inicializarPuntosAlfabeto();
        
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                this.tablero[i][j] = tablero2.getTile(new Tuple<>(i, j));
                this.bonus[i][j] = tablero2.getBonus(new Tuple<>(i, j));
            }
        }
    }

    /**
     * Genera una representación en texto del tablero actual.
     * Incluye índices de filas y columnas, separadores y el contenido de cada casilla.
     * 
     * @return Representación en texto del tablero
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        // Encabezado de columnas
        sb.append("   ");
        for (int j = 0; j < N; j++) {
            sb.append(String.format(" %2d ", j)); // Formato de columna
        }
        sb.append("\n");
    
        // Separador de columnas
        sb.append("   ");
        for (int j = 0; j < N; j++) {
            sb.append("----"); // Separador
        }
        sb.append("\n");
    
        // Filas con índices
        for (int i = 0; i < N; i++) {
            sb.append(String.format("%2d |", i)); // Índice de fila
            for (String casilla : tablero[i]) {
                sb.append(String.format(" %s |", casilla)); // Formato de casilla
            }
            sb.append("\n");
    
            // Separador entre filas
            sb.append("   ");
            for (int j = 0; j < N; j++) {
                sb.append("----"); // Separador
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    
    /**
     * Obtiene la ficha en una posición específica del tablero.
     * 
     * @param pos Posición (x,y) a consultar
     * @return Letra en esa posición o espacio si está vacía
     */
    public String getTile(Tuple<Integer, Integer> pos) {
        return this.tablero[pos.x][pos.y];
    }

    /**
     * Coloca una ficha en una posición específica del tablero.
     * 
     * @param pos Posición (x,y) donde colocar la ficha
     * @param letra Letra a colocar
     */
    public void setTile(Tuple<Integer, Integer> pos, String letra) {
        this.tablero[pos.x][pos.y] = letra;
    }

    /**
     * Verifica si una posición es válida dentro del tablero.
     * 
     * @param pos Posición (x,y) a verificar
     * @return true si la posición está dentro de los límites del tablero, false en caso contrario
     */
    public boolean validPosition(Tuple<Integer, Integer> pos) {
        return (pos.x >= 0 && pos.x < N && pos.y >= 0 && pos.y < N);
    }

    /**
     * Verifica si una posición del tablero está vacía.
     * 
     * @param pos Posición (x,y) a verificar
     * @return true si la posición está vacía, false si contiene una ficha
     */
    public boolean isEmpty(Tuple<Integer, Integer> pos) {
        return validPosition(pos) && this.getTile(pos).equals(" ");
    }

    /**
     * Verifica si una posición del tablero está ocupada.
     * 
     * @param pos Posición (x,y) a verificar
     * @return true si la posición contiene una ficha, false si está vacía
     */
    public boolean isFilled(Tuple<Integer, Integer> pos) {
        return  validPosition(pos) && !this.getTile(pos).equals(" ");
    }

    /**
     * Obtiene el tamaño del tablero.
     * 
     * @return Tamaño N del tablero (NxN)
     */
    public int getSize() {
        return this.N;
    }

    /**
     * Obtiene la posición central del tablero.
     * 
     * @return Tupla con las coordenadas del centro del tablero
     */
    public Tuple<Integer, Integer> getCenter() {
        return new Tuple<>(N/2, N/2);
    }

    /**
     * Calcula el siguiente punto en una dirección dada.
     * Utilizado para recorrer posiciones en el tablero durante jugadas.
     * 
     * @param pos Posición actual
     * @param direction Dirección del movimiento
     * @return Nueva posición
     */
    private Tuple<Integer, Integer> calcularSiguientePosicion(Tuple<Integer, Integer> pos, Direction direction) {
        if (direction == Direction.HORIZONTAL) {
            return new Tuple<>(pos.x, pos.y - 1);
        } else {
            return new Tuple<>(pos.x - 1, pos.y);
        }
    }

    /**
     * Calcula los puntos para una letra en una posición específica.
     * Tiene en cuenta las bonificaciones de la casilla.
     * 
     * @param letra Letra a colocar
     * @param pos Posición en el tablero
     * @return Par con los puntos base y multiplicadores (para palabras)
     */
    private Tuple<Integer, Integer> calcularPuntosLetra(char letra, Tuple<Integer, Integer> pos) {
        int puntos = alphabetPoint.get(Character.toLowerCase(letra));
        int multiplicadorPalabra = 1;
        
        switch (bonus[pos.x][pos.y]) {
            case TW:
                multiplicadorPalabra = 3;
                break;
            case TL:
                puntos *= 3;
                break;
            case DW:
                multiplicadorPalabra = 2;
                break;
            case DL:
                puntos *= 2;
                break;
            case X:
                puntos *= 2;
                break;
            default:
                // Sin modificación
        }
        
        return new Tuple<>(puntos, multiplicadorPalabra);
    }

    /**
     * Realiza un movimiento en el tablero.
     * Coloca una palabra completa y calcula los puntos obtenidos.
     *
     * @param lastPos Posición final de la palabra
     * @param word Palabra a colocar
     * @param direction Dirección (horizontal o vertical)
     * @return Puntos obtenidos por el movimiento
     */
    public int makeMove(Tuple<Integer, Integer> lastPos, String word, Direction direction) {
        if (!validPosition(lastPos)) {
            throw new IllegalArgumentException ("Posición inválida en el tablero.");
        }
        
        int puntosTotales = 0;
        int multiplicadorPalabraTotal = 1;
        Tuple<Integer, Integer> posActual = lastPos;
        
        for (int i = word.length() - 1; i >= 0; i--) {
            char letra = word.charAt(i);
            setTile(posActual, String.valueOf(letra));
            
            Tuple<Integer, Integer> puntuacion = calcularPuntosLetra(letra, posActual);
            puntosTotales += puntuacion.x;
            multiplicadorPalabraTotal *= puntuacion.y;
            
            posActual = calcularSiguientePosicion(posActual, direction);
        }
        
        return puntosTotales * multiplicadorPalabraTotal;
    }

    /**
     * Obtiene el bonus en una posición del tablero.
     * 
     * @param pos Posición del tablero
     * @return Tipo de bonus en esa posición
     */
    public Bonus getBonus(Tuple<Integer, Integer> pos) {
        if (validPosition(pos)) {
            return bonus[pos.x][pos.y];
        }
        return null;
    }
    
    /**
     * Obtiene el valor en puntos de una letra.
     * 
     * @param letra Letra a consultar
     * @return Valor en puntos
     */
    public int getPointValue(char letra) {
        return alphabetPoint.getOrDefault(Character.toLowerCase(letra), 0);
    }

}
