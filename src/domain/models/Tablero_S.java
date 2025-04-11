package domain.models;

public class Tablero_S {
    private Casilla[][] tablero;
    private int filas;
    private int columnas;

    public Tablero_S() {
        this.filas = 15;
        this.columnas = 15;
        tablero = new Casilla[filas][columnas];
        inicializarTablero();
    }

    public void inicializarTablero() {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                tablero[i][j] = new Casilla();
            }
        }
        // Asignar tipos de casillas
        tablero[0][0].setTipoCasilla("TRIPLE_PALABRA");
        tablero[0] [columnas - 1].setTipoCasilla("TRIPLE_PALABRA");
        tablero[filas - 1][0].setTipoCasilla("TRIPLE_PALABRA");
        tablero[filas - 1][columnas - 1].setTipoCasilla("TRIPLE_PALABRA");
        tablero[filas / 2][0].setTipoCasilla("TRIPLE_PALABRA");
        tablero[filas / 2][columnas - 1].setTipoCasilla("TRIPLE_PALABRA");
        tablero[0][columnas / 2].setTipoCasilla("TRIPLE_PALABRA");
        tablero[filas - 1][columnas / 2].setTipoCasilla("TRIPLE_PALABRA");
        tablero[filas / 2][columnas / 2].setTipoCasilla("CENTRO");
        tablero[1][5].setTipoCasilla("TRIPLE_LETRA");
        tablero[1][9].setTipoCasilla("TRIPLE_LETRA");
        tablero[5][5].setTipoCasilla("TRIPLE_LETRA");
        tablero[5][9].setTipoCasilla("TRIPLE_LETRA");
        tablero[5][1].setTipoCasilla("TRIPLE_LETRA");
        tablero[5][13].setTipoCasilla("TRIPLE_LETRA");
        tablero[9][5].setTipoCasilla("TRIPLE_LETRA");
        tablero[9][9].setTipoCasilla("TRIPLE_LETRA");
        tablero[9][1].setTipoCasilla("TRIPLE_LETRA");
        tablero[9][13].setTipoCasilla("TRIPLE_LETRA");
        tablero[13][5].setTipoCasilla("TRIPLE_LETRA");
        tablero[13][9].setTipoCasilla("TRIPLE_LETRA");
        tablero[0][3].setTipoCasilla("DOBLE_LETRA");
        tablero[0][11].setTipoCasilla("DOBLE_LETRA");
        tablero[2][6].setTipoCasilla("DOBLE_LETRA");
        tablero[2][8].setTipoCasilla("DOBLE_LETRA");
        tablero[3][7].setTipoCasilla("DOBLE_LETRA");
        tablero[7][3].setTipoCasilla("DOBLE_LETRA");
        tablero[8][2].setTipoCasilla("DOBLE_LETRA");
        tablero[6][2].setTipoCasilla("DOBLE_LETRA");
        tablero[11][0].setTipoCasilla("DOBLE_LETRA");
        tablero[3][0].setTipoCasilla("DOBLE_LETRA");
        tablero[14][3].setTipoCasilla("DOBLE_LETRA");
        tablero[14][11].setTipoCasilla("DOBLE_LETRA");
        tablero[12][6].setTipoCasilla("DOBLE_LETRA");
        tablero[12][8].setTipoCasilla("DOBLE_LETRA");
        tablero[11][7].setTipoCasilla("DOBLE_LETRA");
        tablero[11][14].setTipoCasilla("DOBLE_LETRA");
        tablero[3][14].setTipoCasilla("DOBLE_LETRA");
        tablero[7][11].setTipoCasilla("DOBLE_LETRA");
        tablero[8][12].setTipoCasilla("DOBLE_LETRA");
        tablero[6][12].setTipoCasilla("DOBLE_LETRA");
        tablero[6][6].setTipoCasilla("DOBLE_LETRA");
        tablero[6][8].setTipoCasilla("DOBLE_LETRA");
        tablero[8][6].setTipoCasilla("DOBLE_LETRA");
        tablero[8][8].setTipoCasilla("DOBLE_LETRA");

        for (int i = 0; i < filas; i++) {
            if (tablero[i][i].getTipoCasilla().equals("NORMAL")) {
                   tablero[i][i].setTipoCasilla("DOBLE_PALABRA");
            }
        }
        for (int i = filas - 1; i >= 0; i--) {
            if (tablero[i][i].getTipoCasilla().equals("NORMAL")) {
                tablero[i][i].setTipoCasilla("DOBLE_PALABRA");
            }
        }

    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }

    public Casilla getCasilla(int fila, int columna) {
        return tablero[fila][columna];
    }

    public void setCasilla(int fila, int columna, Casilla casilla) {
        tablero[fila][columna] = casilla;
    }

    public void mostrarTablero() {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                switch (tablero[i][j].getTipoCasilla()) {
                    case "NORMAL":
                        System.out.print("[  ]");
                        break;
                    case "DOBLE_PALABRA":
                        System.out.print("[DW]");
                        break;
                    case "TRIPLE_PALABRA":
                        System.out.print("[TW]");
                        break;
                    case "DOBLE_LETRA":
                        System.out.print("[DL]");
                        break;
                    case "TRIPLE_LETRA":
                        System.out.print("[TL]");
                        break;
                    case "CENTRO":
                        System.out.print("[ C]");
                        break;
                    default:
                        System.out.print("[  ]");
                        break;
                }
            }
            System.out.println();
        }
    }
}