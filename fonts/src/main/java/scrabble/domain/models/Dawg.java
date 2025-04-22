package scrabble.domain.models;

import java.util.Set;

/**
 * Clase que implementa un Grafo Acíclico Dirigido para Palabras (DAWG, Directed Acyclic Word Graph).
 * Esta estructura es eficiente para almacenar y buscar palabras en un diccionario.
 */
public class Dawg {

    private final DawgNode root;

    /**
     * Constructor por defecto. Inicializa un DAWG vacío con un nodo raíz.
     * 
     * @pre No hay precondiciones específicas.
     * @post Se crea una nueva instancia de DAWG con un nodo raíz vacío.
     */
    public Dawg() {
        root = new DawgNode();
    }

    /**
     * Inserta una palabra en el DAWG.
     * 
     * @pre La palabra no debe ser null.
     * @param word Palabra a insertar
     * @post Si la palabra no está vacía, se inserta en el DAWG, creando los nodos necesarios.
     *       Si la palabra está vacía, no se realiza ninguna acción.
     * @throws NullPointerException si word es null
     */
    public void insert(String word) {
        if (word.isEmpty()) {
            return;
        }
        
        DawgNode current = root;
        for (char c : word.toCharArray()) {
            if (current.getEdge(String.valueOf(c)) == null) {
                current.addEdge(String.valueOf(c), new DawgNode());
            }
            current = current.getEdge(String.valueOf(c));
        }
        current.setFinal(true);
    }

    /**
     * Busca una palabra en el DAWG.
     * 
     * @pre La palabra no debe ser null.
     * @param word Palabra a buscar
     * @return true si la palabra existe en el DAWG, false en caso contrario
     * @post Se devuelve true si la palabra existe en el DAWG (es decir, si se puede seguir un camino
     *       desde la raíz hasta un nodo final siguiendo las letras de la palabra).
     *       Se devuelve false en caso contrario.
     * @throws NullPointerException si word es null
     */
    public boolean search(String word) {
        DawgNode current = root;
        for (char c : word.toCharArray()) {
            if (current.getEdge(String.valueOf(c)) == null) {
                return false;
            }
            current = current.getEdge(String.valueOf(c));
        }
        return current.isFinal();
    }

    /**
     * Obtiene el nodo raíz del DAWG.
     * 
     * @pre No hay precondiciones específicas.
     * @return Nodo raíz
     * @post Se devuelve el nodo raíz del DAWG.
     */
    public DawgNode getRoot() {
        return root;
    }

    /**
     * Obtiene el nodo correspondiente al final de una secuencia de caracteres.
     * 
     * @pre La palabra no debe ser null.
     * @param word Secuencia de caracteres a seguir
     * @return El nodo al final de la secuencia, o null si no existe tal secuencia
     * @post Si la palabra está vacía, se devuelve el nodo raíz.
     *       Si se puede seguir un camino desde la raíz siguiendo las letras de la palabra,
     *       se devuelve el nodo final de ese camino.
     *       Si no existe tal camino, se devuelve null.
     * @throws NullPointerException si word es null
     */
    public DawgNode getNode(String word) {
        if (word.isEmpty()) {
            return root;
        }
        
        DawgNode current = root;
        for (char c : word.toCharArray()) {
            if (current.getEdge(String.valueOf(c)) == null) {
                return null;
            }
            current = current.getEdge(String.valueOf(c));
        }
        return current;
    }


    /**
     * Obtiene los bordes disponibles desde el nodo actual en función de la palabra parcial proporcionada.
     *
     * @pre La palabra parcial no debe ser null.
     * @param partialword La palabra parcial utilizada para localizar el nodo actual en el DAWG
     * @return Un conjunto de cadenas que representan los bordes disponibles desde el nodo actual,
     *         o null si no se encuentra un nodo correspondiente
     * @post Si existe un nodo correspondiente a la palabra parcial, se devuelve un conjunto
     *       con todas las etiquetas de aristas salientes de ese nodo.
     *       Si no existe tal nodo, se devuelve null.
     * @throws NullPointerException si partialword es null
     */
    public Set<String> getAvailableEdges(String partialword) {
        DawgNode currentNode = getNode(partialword);

        return currentNode != null ? currentNode.getAllEdges() : null;
    }

    /**
     * Verifica si una palabra parcial es final en el DAWG (Directed Acyclic Word Graph).
     *
     * @pre La palabra parcial no debe ser null.
     * @param partialword La palabra parcial que se desea verificar
     * @return true si el nodo correspondiente a la palabra parcial existe y es final,
     *         false en caso contrario
     * @post Se devuelve true si existe un nodo correspondiente a la palabra parcial y ese nodo
     *       está marcado como final. Se devuelve false en caso contrario.
     * @throws NullPointerException si partialword es null
     */
    public boolean isFinal(String partialword) {
        DawgNode currentNode = getNode(partialword);
        return currentNode != null && currentNode.isFinal();
    }


}