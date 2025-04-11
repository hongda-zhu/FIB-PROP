package domain.models;

public class Casilla {
    private String tipoCasilla;
    private String[] tipo = {"TRIPLE_PALABRA", "DOBLE_PALABRA", "TRIPLE_LETRA", "DOBLE_LETRA", "NORMAL", "CENTRO"};
    // private Ficha ficha;

    public Casilla() {
        tipoCasilla = tipo[4];
        // ficha = null;
    }

    public String getTipoCasilla() {
        return tipoCasilla;
    }

    public void setTipoCasilla(String tipoCasilla) {
        this.tipoCasilla = tipoCasilla;
    }

    // public Ficha getFicha() {
    //     return ficha;
    // }

    // public void setFicha(Ficha ficha) {
    //     this.ficha = ficha;
    // }

    public void listarTipoCasilla() {
        for (int i = 0; i < tipo.length; i++) {
            System.out.println(tipo[i]);
        }
    }

    // public boolean estaVacia() {
    //     return ficha == null;
    // }

    // public void ponerCasilla(Ficha ficha) {
    //     this.ficha = ficha;
    // }

    // public void quitarCasilla() {
    //     ficha = null;
    // }
}
