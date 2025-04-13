package domain.models;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Bolsa {
    private List<String> fichas;

    public void llenarBolsa(Map<String, Integer> bolsa) {
        fichas = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : bolsa.entrySet()) {
            String caracter = entry.getKey();
            int frecuencia = entry.getValue();
            agregarFichas(caracter, frecuencia);                     
        }
        Collections.shuffle(fichas);
    }
        
    private void agregarFichas(String letra, int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            fichas.add(letra);
        }
    }
    
    public String sacarFicha() {
        if (fichas.isEmpty()) return null;
        return fichas.remove(0);
    }

    public int getCantidadFichas() {
        return fichas.size();
    }
}

