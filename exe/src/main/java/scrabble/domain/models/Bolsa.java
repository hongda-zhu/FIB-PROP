package scrabble.domain.models;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Clase que representa la bolsa de fichas del juego Scrabble.
 * Contiene todas las fichas disponibles y permite sacarlas aleatoriamente.
 * La bolsa se inicializa con una distribución específica de letras según
 * las reglas del juego y mantiene el estado de las fichas restantes durante
 * la partida. Implementa Serializable para permitir la persistencia de partidas.
 * 
 * 
 * @version 1.0
 * @since 1.0
 */
public class Bolsa implements Serializable{
    private static final long serialVersionUID = 1L;
    private List<String> fichas;


    /**
     * Constructor por defecto. Inicializa la bolsa como una lista vacía.
     * La bolsa debe ser llenada posteriormente usando el método llenarBolsa()
     * con la distribución de fichas apropiada para el idioma del juego.
     * 
     * @pre No hay precondiciones específicas.
     * @post Se crea una nueva instancia de Bolsa con una lista de fichas vacía.
     */
    public Bolsa() {
        fichas = new ArrayList<>();
    }

    /**
     * Llena la bolsa con fichas según las frecuencias especificadas.
     * Inicializa la bolsa con la distribución de letras proporcionada en el mapa,
     * donde cada entrada representa una letra y su frecuencia de aparición.
     * Las fichas se mezclan aleatoriamente para garantizar una distribución
     * impredecible durante el juego.
     * 
     * @param bolsa Mapa con las frecuencias de cada letra (letra -> cantidad)
     * @pre El mapa bolsa no debe ser null.
     * @post La bolsa se inicializa con las fichas especificadas en el mapa,
     *       respetando las frecuencias indicadas. Las fichas se mezclan aleatoriamente.
     * @throws NullPointerException si el mapa bolsa es null
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
     * Añade la cantidad especificada de fichas de una letra determinada
     * a la bolsa. Este método es utilizado internamente por llenarBolsa()
     * para construir la distribución inicial de fichas.
     * 
     * @param letra Letra a agregar a la bolsa
     * @param cantidad Cantidad de fichas de esa letra a añadir
     * @pre La letra no debe ser null y la cantidad debe ser no negativa.
     * @post Se añaden a la bolsa 'cantidad' fichas de la letra especificada.
     * @throws NullPointerException si letra es null
     */
    public void agregarFichas(String letra, int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            fichas.add(letra);
        }
    }
    
    /**
     * Saca una ficha aleatoria de la bolsa.
     * Extrae la primera ficha de la lista (que está en posición aleatoria
     * debido a la mezcla previa) y la elimina de la bolsa. Este método
     * simula el acto de sacar una ficha al azar de la bolsa física del juego.
     * 
     * @return La ficha sacada, o null si la bolsa está vacía
     * @pre No hay precondiciones específicas.
     * @post Si la bolsa no está vacía, se extrae y devuelve la primera ficha (que está en una posición aleatoria 
     *       debido a la mezcla previa). Si la bolsa está vacía, se devuelve null.
     */
    public String sacarFicha() {
        if (fichas.isEmpty()) return null;
        return fichas.remove(0);
    }

    /**
     * Obtiene la cantidad de fichas que quedan en la bolsa.
     * Proporciona información sobre el estado actual de la bolsa,
     * útil para determinar cuándo se acerca el final del juego
     * o para mostrar información al jugador.
     * 
     * @return Número de fichas restantes en la bolsa
     * @pre No hay precondiciones específicas.
     * @post Se devuelve un entero no negativo que representa el número de fichas restantes en la bolsa.
     */
    public int getCantidadFichas() {
        return fichas.size();
    }

}