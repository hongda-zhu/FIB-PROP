package scrabble.domain.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import scrabble.helpers.Triple;

/**
 * Clase que implementa un Grafo Acíclico Dirigido para Palabras (DAWG, Directed Acyclic Word Graph).
 * Esta estructura es eficiente para almacenar y buscar palabras en un diccionario.
 */
public class Dawg {

    private final DawgNode root;
    private Map<DawgNode, DawgNode> minimizedNodes = new HashMap<>();
    private Stack<Triple<DawgNode, String, DawgNode>> uncheckedNodes = new Stack<>();
    private String previousWord = "";

    /**
     * Constructor por defecto. Inicializa un DAWG vacío con un nodo raíz.
     * 
     * @pre No hay precondiciones específicas.
     * @post Se crea una nueva instancia de DAWG con un nodo raíz vacío.
     */
    public Dawg() {
        root = new DawgNode();
    }


    
    private int commonPrefix(String word)
    {
        for (int commonPrefix = 0; commonPrefix < Math.min(word.length(), previousWord.length()); commonPrefix++)
        {
            if (word.charAt(commonPrefix) != previousWord.charAt(commonPrefix))
            {
                return commonPrefix;
            }
        }

        return 0;
    }


    private void minimize(int downTo)
    {
        for (int i = uncheckedNodes.size() - 1; i > downTo - 1; i--)
        {
            Triple<DawgNode, String, DawgNode> unNode = uncheckedNodes.pop();
            DawgNode parent = unNode.x;
            String letter = unNode.y;
            DawgNode child = unNode.z;

            if (minimizedNodes.containsKey(child)) {
                DawgNode newChild = minimizedNodes.get(child);
                parent.switchEdge(letter, newChild);
            } else {
                minimizedNodes.put(child, child);
            }
        }
    }

    public void finish() {
        minimize(0);
        minimizedNodes.clear();
        uncheckedNodes.clear();
        previousWord = "";
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
        // if (word.isEmpty()) {
        //     return;
        // }
        
        // DawgNode current = root;
        // for (char c : word.toCharArray()) {
        //     if (current.getEdge(String.valueOf(c)) == null) {
        //         current.addEdge(String.valueOf(c), new DawgNode());
        //     }
        //     current = current.getEdge(String.valueOf(c));
        // }
        // current.setFinal(true);

        int commonPrefix = commonPrefix(word);
        minimize(commonPrefix);

        DawgNode current = uncheckedNodes.isEmpty() ? root : uncheckedNodes.peek().z;

        for (int i = commonPrefix; i < word.length(); i++)
        {
            String letter = String.valueOf(word.charAt(i));
            DawgNode newNode = new DawgNode();
            current.addEdge(letter, newNode);
            uncheckedNodes.push(new Triple<>(current, letter, newNode));
            current = newNode;
        }
        current.setFinal(true);
        previousWord = word;
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

    /**
     * Obtiene todas las palabras almacenadas en el DAWG.
     * 
     * @pre No hay precondiciones específicas.
     * @return Lista de todas las palabras almacenadas en el DAWG
     * @post Se devuelve una lista (posiblemente vacía) con todas las palabras almacenadas en el DAWG.
     */
    public List<String> getAllWords() {
        List<String> result = new ArrayList<>();
        collectWords(root, "", result);
        return result;
    }

    /**
     * Método auxiliar recursivo para recolectar todas las palabras del DAWG.
     * 
     * @pre node no debe ser null, prefix y result no deben ser null.
     * @param node Nodo actual en la recursión
     * @param prefix Prefijo acumulado hasta el nodo actual
     * @param result Lista donde se almacenan las palabras encontradas
     * @post La lista result se actualiza con todas las palabras encontradas a partir del nodo actual.
     */
    private void collectWords(DawgNode node, String prefix, List<String> result) {
        if (node.isFinal()) {
            result.add(prefix);
        }
        
        Set<String> edges = node.getAllEdges();
        for (String edge : edges) {
            DawgNode nextNode = node.getEdge(edge);
            collectWords(nextNode, prefix + edge, result);
        }
    }

}