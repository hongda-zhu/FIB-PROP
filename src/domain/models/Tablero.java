package domain.models;

public class Tablero {
    private String[][] tablero;
    private int N;

    public Tablero (int N) {
        this.tablero = new String[N][N];
        this.N = N;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                this.tablero[i][j] = "_";
            }
        }
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
        for (String[] fila : tablero) {
            for (String casilla : fila) {
                sb.append(casilla).append(" ");
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
        return validPosition(pos) && this.getTile(pos).equals("_");
    }

    public boolean isFilled(Tuple<Integer, Integer> pos) {
        return  validPosition(pos) && !this.getTile(pos).equals("_");
    }

    public int getSize() {
        return this.N;
    }
}
