package domain.models;

public class Ficha {

    private String letra;
    private int valor;

    public Ficha(String letra, int valor) {
        this.letra = letra;
        this.valor = valor;
    }

    public String getLetra() { return letra; }
    public int getValor() { return valor; }

}
