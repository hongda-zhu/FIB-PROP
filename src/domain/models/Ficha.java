package domain.models;

public class Ficha {

    private char letra;
    private int valor;

    public Ficha(char letra, int valor) {
        this.letra = letra;
        this.valor = valor;
    }

    public char getLetra() { return letra; }
    public int getValor() { return valor; }

}
