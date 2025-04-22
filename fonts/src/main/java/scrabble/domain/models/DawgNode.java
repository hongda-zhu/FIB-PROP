package scrabble.domain.models;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Clase que representa un nodo en el grafo acíclico dirigido para palabras (DAWG).
 * Cada nodo puede tener múltiples aristas salientes hacia otros nodos, y puede ser
 * un nodo final que indica el fin de una palabra válida.
 */
public class DawgNode {
    private Map<String, DawgNode> edges;
    private boolean isFinal;

    /**
     * Constructor por defecto. Inicializa un nodo sin aristas y no final.
     * 
     * @pre No hay precondiciones específicas.
     * @post Se crea un nuevo nodo con un mapa vacío de aristas y marcado como no final.
     */
    public DawgNode() {
        this.edges = new HashMap<>();
        this.isFinal = false;
    }

    /**
     * Obtiene el nodo conectado mediante una arista con la etiqueta dada.
     * 
     * @pre No hay precondiciones específicas fuertes, aunque c no debería ser null.
     * @param c Etiqueta de la arista (normalmente un carácter)
     * @return El nodo conectado o null si no existe tal arista
     * @post Si existe una arista con la etiqueta dada, se devuelve el nodo conectado.
     *       Si no existe tal arista, se devuelve null.
     * @throws NullPointerException si c es null
     */
    public DawgNode getEdge(String c) {
        return edges.get(c);
    }

    /**
     * Obtiene el conjunto de todas las etiquetas de aristas salientes de este nodo.
     * 
     * @pre No hay precondiciones específicas.
     * @return Conjunto de etiquetas de aristas
     * @post Se devuelve un conjunto (posiblemente vacío) con todas las etiquetas de aristas
     *       salientes de este nodo.
     */
    public Set<String> getAllEdges() {
        return edges.keySet();
    }

    /**
     * Añade una arista saliente desde este nodo hacia otro.
     * 
     * @pre c y node no deben ser null.
     * @param c Etiqueta de la arista (normalmente un carácter)
     * @param node Nodo destino de la arista
     * @post Se añade una nueva arista desde este nodo hacia el nodo especificado,
     *       con la etiqueta dada. Si ya existía una arista con la misma etiqueta,
     *       se reemplaza con la nueva.
     * @throws NullPointerException si c o node son null
     */
    public void addEdge(String c, DawgNode node) {
        edges.put(c, node);
    }

    /**
     * Verifica si este nodo es final (indica el final de una palabra válida).
     * 
     * @pre No hay precondiciones específicas.
     * @return true si es un nodo final, false en caso contrario
     * @post Se devuelve el estado actual del nodo: true si es final, false si no lo es.
     */
    public boolean isFinal() {
        return isFinal;
    }

    /**
     * Establece si este nodo es final o no.
     * 
     * @pre No hay precondiciones específicas.
     * @param isFinal true para marcar como nodo final, false en caso contrario
     * @post El estado del nodo se actualiza al valor especificado.
     */
    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }
}