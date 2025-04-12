package domain.models;

import java.util.HashMap;
import java.util.Map;

import domain.controllers.subcontrollers.managers.GestorJugada.Direction;

public class Tablero {
    private String[][] tablero;
    private Bonus[][] bonus;
    public Map<Character, Integer> alphabetPoint;
    {
        alphabetPoint = new HashMap<>();
        alphabetPoint.put('a', 1);
        alphabetPoint.put('b', 2);
        alphabetPoint.put('c', 3);
        alphabetPoint.put('d', 4);
        alphabetPoint.put('e', 5);
        alphabetPoint.put('f', 6);
        alphabetPoint.put('g', 7);
        alphabetPoint.put('h', 8);
        alphabetPoint.put('i', 9);
        alphabetPoint.put('j', 10);
        alphabetPoint.put('k', 11);
        alphabetPoint.put('l', 12);
        alphabetPoint.put('m', 13);
        alphabetPoint.put('n', 14);
        alphabetPoint.put('o', 15);
        alphabetPoint.put('p', 16);
        alphabetPoint.put('q', 17);
        alphabetPoint.put('r', 18);
        alphabetPoint.put('s', 19);
        alphabetPoint.put('t', 20);
        alphabetPoint.put('u', 21);
        alphabetPoint.put('v', 22);
        alphabetPoint.put('w', 23);
        alphabetPoint.put('x', 24);
        alphabetPoint.put('y', 25);
        alphabetPoint.put('z', 26);
    }
    private int N;


    public enum Bonus {
        N, TW, TL, DW, DL, X
    }

    public Tablero() {
        this(15);
    }

    public Tablero (int N) {
        this.tablero = new String[N][N];
        this.N = N;
        this.bonus = this.N == 15? inicializarTablero15x15(): new Bonus[N][N];
        
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                this.tablero[i][j] = " ";
                if (N != 15) this.bonus[i][j] = Bonus.N;
            }
        }
    }

    private Bonus[][] inicializarTablero15x15() {
        Bonus[][] bonusMatrix = new Bonus[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                bonusMatrix[i][j] = Bonus.N;
            }
        }   
        //starting center
		bonusMatrix[7][7] = Bonus.N;
		//triple word
		bonusMatrix[0][0] = Bonus.TW;
		bonusMatrix[7][0] = Bonus.TW;
		bonusMatrix[14][0] = Bonus.TW;
		bonusMatrix[0][7] = Bonus.TW;
		bonusMatrix[0][14] = Bonus.TW;
		bonusMatrix[7][14] = Bonus.TW;
		bonusMatrix[14][7] = Bonus.TW;
		bonusMatrix[14][14] = Bonus.TW;
		//triple letter
		bonusMatrix[5][1] = Bonus.TL;
		bonusMatrix[9][1] = Bonus.TL;
		bonusMatrix[1][5] = Bonus.TL;
		bonusMatrix[5][5] = Bonus.TL;
		bonusMatrix[9][5] = Bonus.TL;
		bonusMatrix[13][5] = Bonus.TL;;
		bonusMatrix[1][9] = Bonus.TL;
		bonusMatrix[5][9] = Bonus.TL;
		bonusMatrix[9][9] = Bonus.TL;
		bonusMatrix[13][9] = Bonus.TL;;
		bonusMatrix[5][13] = Bonus.TL;;
		bonusMatrix[9][13] = Bonus.TL;;
		//double word
		bonusMatrix[1][1] = Bonus.DW;
		bonusMatrix[2][2] = Bonus.DW;
		bonusMatrix[3][3] = Bonus.DW;
		bonusMatrix[4][4] = Bonus.DW;
		bonusMatrix[4][10] = Bonus.DW;;
		bonusMatrix[3][11] = Bonus.DW;;
		bonusMatrix[2][12] = Bonus.DW;;
		bonusMatrix[1][13] = Bonus.DW;;
		bonusMatrix[13][1] = Bonus.DW;;
		bonusMatrix[12][2] = Bonus.DW;;
		bonusMatrix[11][3] = Bonus.DW;;
		bonusMatrix[10][4] = Bonus.DW;;
		bonusMatrix[10][10] = Bonus.DW;;
		bonusMatrix[11][11] = Bonus.DW;;
		bonusMatrix[12][12] = Bonus.DW;;
		bonusMatrix[13][13] = Bonus.DW;;
		//double letters
		bonusMatrix[3][0] = Bonus.DL;
		bonusMatrix[11][0] = Bonus.DL;;
		bonusMatrix[0][3] = Bonus.DL;
		bonusMatrix[6][2] = Bonus.DL;
		bonusMatrix[7][3] = Bonus.DL;
		bonusMatrix[8][2] = Bonus.DL;
		bonusMatrix[14][3] = Bonus.DL;;
		bonusMatrix[2][6] = Bonus.DL;
		bonusMatrix[6][6] = Bonus.DL;
		bonusMatrix[8][6] = Bonus.DL;
		bonusMatrix[12][6] = Bonus.DL;;
		bonusMatrix[3][7] = Bonus.DL;
		bonusMatrix[11][7] = Bonus.DL;;
		bonusMatrix[2][8] = Bonus.DL;
		bonusMatrix[6][8] = Bonus.DL;
		bonusMatrix[8][8] = Bonus.DL;
		bonusMatrix[12][8] = Bonus.DL;;
		bonusMatrix[0][11] = Bonus.DL;;
		bonusMatrix[7][11] = Bonus.DL;;
		bonusMatrix[14][11] = Bonus.DL;;
		bonusMatrix[6][12] = Bonus.DL;;
		bonusMatrix[8][12] = Bonus.DL;;
		bonusMatrix[3][14] = Bonus.DL;;
		bonusMatrix[6][12] = Bonus.DL;;
		bonusMatrix[11][14] = Bonus.DL;;

        return bonusMatrix;
    }

    public Tablero(Tablero tablero2) {
        this.N = tablero2.getSize();
        this.tablero = new String[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                this.tablero[i][j] = tablero2.getTile(new Tuple<>(i, j));
            }
        }
    }

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
    

    public String getTile(Tuple<Integer, Integer> pos) {
        return this.tablero[pos.x][pos.y];
    }

    public void setTile(Tuple<Integer, Integer> pos, String letra) {
        this.tablero[pos.x][pos.y] = letra;
    }

    public boolean validPosition(Tuple<Integer, Integer> pos) {
        return (pos.x >= 0 && pos.x < N && pos.y >= 0 && pos.y < N);
    }

    public boolean isEmpty(Tuple<Integer, Integer> pos) {
        return validPosition(pos) && this.getTile(pos).equals(" ");
    }

    public boolean isFilled(Tuple<Integer, Integer> pos) {
        return  validPosition(pos) && !this.getTile(pos).equals(" ");
    }

    public int getSize() {
        return this.N;
    }

    public int makeMove(Tuple<Integer, Integer> lastPos, String word, Direction direction) {
        int points = 0;
        int doubleTimes = 0;
        int tripleTimes = 0;
        if (validPosition(lastPos)) {
            for (int i = word.length() - 1; i >= 0; i--) {
                setTile(lastPos, String.valueOf(word.charAt(i)));
                if (direction == Direction.HORIZONTAL) {
                    lastPos = new Tuple<Integer, Integer>(lastPos.x, lastPos.y - 1); // Move to the next position in the word
                } else {
                    lastPos = new Tuple<Integer, Integer>(lastPos.x - 1, lastPos.y);
                }
                int letterPoint = alphabetPoint.get(String.valueOf(word.charAt(i)).toLowerCase().charAt(0));

                switch (bonus[lastPos.x][lastPos.y]) {
                    case TW:
                        points += letterPoint;
                        tripleTimes++;
                        break;
                    case TL:
                        points += letterPoint * 3;
                        break;
                    case DW:
                        points += letterPoint;
                        doubleTimes++;
                        break;
                    case DL:
                        points += letterPoint * 2;
                        break;
                    case X:
                        points += letterPoint * 2;
                        break;
                    default:
                        points += letterPoint;
                }
            }
            return points * (int) Math.pow(2, doubleTimes) * (int) Math.pow(3, tripleTimes); 
        } else {
            throw new IllegalArgumentException("Posición inválida en el tablero.");
        }
    }


}
