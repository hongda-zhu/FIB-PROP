package domain.models;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bolsa {
    private List<Ficha> fichas;

    public void llenarBolsa() {
        fichas = new ArrayList<>();
    
        agregarFichas('a', 12, 1);
        agregarFichas('e', 12, 1);
        agregarFichas('o', 9, 1);
        agregarFichas('s', 6, 1);
        agregarFichas('i', 6, 1);
        agregarFichas('u', 6, 1);
        agregarFichas('n', 5, 1);
        agregarFichas('l', 4, 1);
        agregarFichas('r', 5, 1);
        agregarFichas('t', 4, 1);
        agregarFichas('c', 4, 3);
        agregarFichas('d', 5, 2);
        agregarFichas('m', 3, 3);
        agregarFichas('b', 2, 3);
        agregarFichas('p', 2, 3);
        agregarFichas('h', 2, 4);
        agregarFichas('g', 2, 2);
        agregarFichas('y', 1, 4);
        agregarFichas('q', 1, 5);
        agregarFichas('j', 1, 8);
        agregarFichas('ñ', 1, 8);
        agregarFichas('x', 1, 8);
        agregarFichas('z', 1, 10);

    
        Collections.shuffle(fichas);
    }
    
    private void agregarFichas(char letra, int cantidad, int valor) {
        for (int i = 0; i < cantidad; i++) {
            fichas.add(new Ficha(letra, valor));
        }
    }
    

    public Ficha sacarFicha() {
        if (fichas.isEmpty()) return null;
        return fichas.remove(0);
    }

    public int getCantidadFichas() {
        return fichas.size();
    }
}

