package scrabble.domain.models;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Clase que representa la bolsa de fichas del juego Scrabble.
 * Contiene todas las fichas disponibles y permite sacarlas aleatoriamente.
 */
public class Bolsa implements Serializable{
    private List<String> fichas;

    /**
     * Llena la bolsa con fichas según las frecuencias especificadas.
     * 
     * @param bolsa Mapa con las frecuencias de cada letra
     */
    public void llenarBolsa(Map<String, Integer> bolsa) {
        fichas = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : bolsa.entrySet()) {
            String caracter = entry.getKey();
            int frecuencia = entry.getValue();
            agregarFichas(caracter, frecuencia);                     
        }
        Collections.shuffle(fichas);
    }
    
    /**
     * Método auxiliar para agregar múltiples fichas de la misma letra.
     * 
     * @param letra Letra a agregar
     * @param cantidad Cantidad de fichas de esa letra
     */
    public void agregarFichas(String letra, int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            fichas.add(letra);
        }
    }
    
    /**
     * Saca una ficha aleatoria de la bolsa.
     * 
     * @return La ficha sacada, o null si la bolsa está vacía
     */
    public String sacarFicha() {
        if (fichas.isEmpty()) return null;
        return fichas.remove(0);
    }

    /**
     * Obtiene la cantidad de fichas que quedan en la bolsa.
     * 
     * @return Número de fichas restantes
     */
    public int getCantidadFichas() {
        return fichas.size();
    }

}