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
     */
    public Dawg() {
        root = new DawgNode();
    }

    /**
     * Inserta una palabra en el DAWG.
     * 
     * @param word Palabra a insertar
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
     * @param word Palabra a buscar
     * @return true si la palabra existe en el DAWG, false en caso contrario
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
     * @return Nodo raíz
     */
    public DawgNode getRoot() {
        return root;
    }

    /**
     * Obtiene el nodo correspondiente al final de una secuencia de caracteres.
     * 
     * @param word Secuencia de caracteres a seguir
     * @return El nodo al final de la secuencia, o null si no existe tal secuencia
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
     * @param partialword La palabra parcial utilizada para localizar el nodo actual en el DAWG (Directed Acyclic Word Graph).
     * @return Un conjunto de cadenas que representan los bordes disponibles desde el nodo actual,
     *         o null si no se encuentra un nodo correspondiente.
     */
    public Set<String> getAvailableEdges(String partialword) {
        DawgNode currentNode = getNode(partialword);

        return currentNode != null ? currentNode.getAllEdges() : null;
    }

    /**
     * Verifica si una palabra parcial es final en el DAWG (Directed Acyclic Word Graph).
     *
     * @param partialword La palabra parcial que se desea verificar.
     * @return {true} si el nodo correspondiente a la palabra parcial existe y es final,
     *         {false} en caso contrario.
     */
    public boolean isFinal(String partialword) {
        DawgNode currentNode = getNode(partialword);
        return currentNode != null && currentNode.isFinal();
    }


}