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
 * Esta estructura es eficiente para almacenar y buscar palabras en un diccionario, optimizando
 * tanto el uso de memoria como la velocidad de búsqueda. El DAWG permite compartir sufijos
 * comunes entre palabras, reduciendo significativamente el espacio requerido comparado con
 * un trie tradicional.
 * 
 * La implementación utiliza un algoritmo de minimización incremental que construye el DAWG
 * de manera eficiente mientras se insertan las palabras. Es especialmente útil para
 * diccionarios grandes donde muchas palabras comparten terminaciones comunes.
 * 
 * 
 * @version 1.0
 * @since 1.0
 */
public class Dawg {

    private final DawgNode root;
    private Map<DawgNode, DawgNode> minimizedNodes = new HashMap<>();
    private Stack<Triple<DawgNode, String, DawgNode>> uncheckedNodes = new Stack<>();
    private String previousWord = "";

    /**
     * Constructor por defecto. Inicializa un DAWG vacío con un nodo raíz.
     * Crea las estructuras de datos necesarias para la construcción incremental
     * del DAWG, incluyendo el mapa de nodos minimizados y la pila de nodos
     * no verificados para el proceso de minimización.
     *
     * @pre No hay precondiciones específicas.
     * @post Se crea una nueva instancia de DAWG con un nodo raíz vacío.
     */
    public Dawg() {
        root = new DawgNode();
    }



    private int commonPrefix(String word)
    {
        int commonPrefix;
        for (commonPrefix = 0; commonPrefix < Math.min(word.length(), previousWord.length()); commonPrefix++)
        {
            if (word.charAt(commonPrefix) != previousWord.charAt(commonPrefix))
            {
                return commonPrefix;
            }
        }

        return commonPrefix;
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

    /**
     * Finaliza la construcción del DAWG.
     * Completa el proceso de minimización para todos los nodos restantes
     * y limpia las estructuras de datos temporales utilizadas durante la construcción.
     * Este método debe ser llamado después de insertar todas las palabras
     * para garantizar que el DAWG esté completamente optimizado.
     * 
     * @pre No hay precondiciones específicas.
     * @post El DAWG queda completamente minimizado y las estructuras temporales se limpian.
     */
    public void finish() {
        minimize(0);
        minimizedNodes.clear();
        uncheckedNodes.clear();
        previousWord = "";
    }

    /**
     * Inserta una palabra en el DAWG.
     * Añade una nueva palabra al DAWG utilizando el algoritmo de construcción incremental.
     * La palabra se inserta carácter por carácter, creando los nodos necesarios y
     * aplicando minimización cuando es posible para optimizar la estructura.
     * Las palabras deben insertarse en orden lexicográfico para que la minimización
     * funcione correctamente.
     *
     * @param word Palabra a insertar en el DAWG
     * @pre La palabra no debe ser null.
     * @post Si la palabra no está vacía, se inserta en el DAWG, creando los nodos necesarios.
     * Si la palabra está vacía, no se realiza ninguna acción.
     * @throws NullPointerException si word es null
     */
    public void insert(String word) {
        // Add null check as per documentation comment
        if (word == null || word.isEmpty()) {
            throw new NullPointerException("Cannot insert null word.");
        }

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
     * Verifica si una palabra específica existe en el DAWG siguiendo el camino
     * desde la raíz hasta un nodo final. La búsqueda es eficiente con complejidad
     * O(m) donde m es la longitud de la palabra.
     *
     * @param word Palabra a buscar en el DAWG
     * @return true si la palabra existe en el DAWG, false en caso contrario
     * @pre La palabra no debe ser null.
     * @post Se devuelve true si la palabra existe en el DAWG (es decir, si se puede seguir un camino
     * desde la raíz hasta un nodo final siguiendo las letras de la palabra).
     * Se devuelve false en caso contrario.
     * @throws NullPointerException si word es null
     */
    public boolean search(String word) {
        // Add null check as per documentation comment
        if (word == null) {
            throw new NullPointerException("Cannot search for null word.");
        }
        DawgNode current = root;
        for (char c : word.toCharArray()) {
            // Assuming DawgNode.getEdge returns null if edge does not exist
            DawgNode nextNode = current.getEdge(String.valueOf(c));
            if (nextNode == null) {
                return false;
            }
            current = nextNode;
        }
        return current.isFinal();
    }

    /**
     * Obtiene el nodo raíz del DAWG.
     * Proporciona acceso al nodo raíz desde el cual se pueden realizar
     * traversals y búsquedas en la estructura del DAWG.
     *
     * @return Nodo raíz del DAWG
     * @pre No hay precondiciones específicas.
     * @post Se devuelve el nodo raíz del DAWG.
     */
    public DawgNode getRoot() {
        return root;
    }

    /**
     * Obtiene el nodo correspondiente al final de una secuencia de caracteres.
     * Sigue el camino desde la raíz siguiendo los caracteres de la palabra
     * y devuelve el nodo final alcanzado. Útil para verificar prefijos
     * y obtener información sobre palabras parciales.
     *
     * @param word Secuencia de caracteres a seguir desde la raíz
     * @return El nodo al final de la secuencia, o null si no existe tal secuencia
     * @pre La palabra no debe ser null.
     * @post Si la palabra está vacía, se devuelve el nodo raíz.
     * Si se puede seguir un camino desde la raíz siguiendo las letras de la palabra,
     * se devuelve el nodo final de ese camino.
     * Si no existe tal camino, se devuelve null.
     * @throws NullPointerException si word es null
     */
    public DawgNode getNode(String word) {
         // Add null check as per documentation comment
        if (word == null) {
            throw new NullPointerException("Cannot get node for null word.");
        }
        if (word.isEmpty()) {
            return root;
        }

        DawgNode current = root;
        for (char c : word.toCharArray()) {
             // Assuming DawgNode.getEdge returns null if edge does not exist
            DawgNode nextNode = current.getEdge(String.valueOf(c));
            if (nextNode == null) {
                return null;
            }
            current = nextNode;
        }
        return current;
    }


    /**
     * Obtiene los bordes disponibles desde el nodo actual en función de la palabra parcial proporcionada.
     * Encuentra el nodo correspondiente a la palabra parcial y devuelve todas las
     * aristas salientes disponibles desde ese nodo. Útil para implementar
     * autocompletado y validación de prefijos en tiempo real.
     *
     * @param partialword La palabra parcial utilizada para localizar el nodo actual en el DAWG
     * @return Un conjunto de cadenas que representan los bordes disponibles desde el nodo actual,
     * o null si no se encuentra un nodo correspondiente
     * @pre La palabra parcial no debe ser null.
     * @post Si existe un nodo correspondiente a la palabra parcial, se devuelve un conjunto
     * con todas las etiquetas de aristas salientes de ese nodo.
     * Si no existe tal nodo, se devuelve null.
     * @throws NullPointerException si partialword es null
     */
    public Set<String> getAvailableEdges(String partialword) {
         // Add null check as per documentation comment
        if (partialword == null) {
            throw new NullPointerException("Cannot get available edges for null word.");
        }
        DawgNode currentNode = getNode(partialword);

        return currentNode != null ? currentNode.getAllEdges() : null;
    }

    /**
     * Verifica si una palabra parcial es final en el DAWG (Directed Acyclic Word Graph).
     * Determina si la palabra parcial corresponde a una palabra completa válida
     * en el diccionario, es decir, si el nodo alcanzado está marcado como final.
     * Útil para validar palabras durante la construcción de jugadas.
     *
     * @param partialword La palabra parcial que se desea verificar
     * @return true si el nodo correspondiente a la palabra parcial existe y es final,
     * false en caso contrario
     * @pre La palabra parcial no debe ser null.
     * @post Se devuelve true si existe un nodo correspondiente a la palabra parcial y ese nodo
     * está marcado como final. Se devuelve false en caso contrario.
     * @throws NullPointerException si partialword es null
     */
    public boolean isFinal(String partialword) {
         // Add null check as per documentation comment
        if (partialword == null) {
            throw new NullPointerException("Cannot check if null word is final.");
        }
        DawgNode currentNode = getNode(partialword);
        return currentNode != null && currentNode.isFinal();
    }

    /**
     * Obtiene todas las palabras almacenadas en el DAWG.
     * Realiza un recorrido completo del DAWG para extraer todas las palabras
     * válidas almacenadas en la estructura. Útil para debugging, exportación
     * de diccionarios y análisis de contenido.
     *
     * @return Lista de todas las palabras almacenadas en el DAWG
     * @pre No hay precondiciones específicas.
     * @post Se devuelve una lista (posiblemente vacía) con todas las palabras almacenadas en el DAWG.
     */
    public List<String> getAllWords() {
        List<String> result = new ArrayList<>();
        collectWords(root, "", result);
        return result;
    }

    /**
     * Método auxiliar recursivo para recolectar todas las palabras del DAWG.
     * Realiza un recorrido en profundidad del DAWG para encontrar todas las
     * palabras válidas, construyendo cada palabra carácter por carácter
     * mientras navega por la estructura.
     *
     * @param node Nodo actual en la recursión
     * @param prefix Prefijo acumulado hasta el nodo actual
     * @param result Lista donde se almacenan las palabras encontradas
     * @pre node no debe ser null, prefix y result no deben ser null.
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