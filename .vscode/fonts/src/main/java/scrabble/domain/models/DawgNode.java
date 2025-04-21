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
     */
    public DawgNode() {
        this.edges = new HashMap<>();
        this.isFinal = false;
    }

    /**
     * Obtiene el nodo conectado mediante una arista con la etiqueta dada.
     * 
     * @param c Etiqueta de la arista (normalmente un carácter)
     * @return El nodo conectado o null si no existe tal arista
     */
    public DawgNode getEdge(String c) {
        return edges.get(c);
    }

    /**
     * Obtiene el conjunto de todas las etiquetas de aristas salientes de este nodo.
     * 
     * @return Conjunto de etiquetas de aristas
     */
    public Set<String> getAllEdges() {
        return edges.keySet();
    }

    /**
     * Añade una arista saliente desde este nodo hacia otro.
     * 
     * @param c Etiqueta de la arista (normalmente un carácter)
     * @param node Nodo destino de la arista
     */
    public void addEdge(String c, DawgNode node) {
        edges.put(c, node);
    }

    /**
     * Verifica si este nodo es final (indica el final de una palabra válida).
     * 
     * @return true si es un nodo final, false en caso contrario
     */
    public boolean isFinal() {
        return isFinal;
    }

    /**
     * Establece si este nodo es final o no.
     * 
     * @param isFinal true para marcar como nodo final, false en caso contrario
     */
    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }
}