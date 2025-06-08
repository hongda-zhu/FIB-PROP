package scrabble.domain.models;

import java.util.*;
import scrabble.helpers.Triple;

/**
 * Clase que implementa un Grafo Acíclico Dirigido para Palabras (DAWG, Directed Acyclic Word Graph).
 * Esta estructura es eficiente para almacenar y buscar palabras en un diccionario,
 * compartiendo sufijos comunes para optimizar memoria.
 * 
 * Esta versión permite letras multicaracter como "CH", "LL" o "RR", usando un alfabeto definido por el usuario.
 * No requiere que el alfabeto esté ordenado, ya que se realiza una búsqueda de coincidencia máxima.
 *
 * @version 2.0
 * @since 1.0
 */
public class Dawg {

    private final DawgNode root;
    private final Set<String> alfabeto;
    private Map<DawgNode, DawgNode> minimizedNodes = new HashMap<>();
    private Stack<Triple<DawgNode, String, DawgNode>> uncheckedNodes = new Stack<>();
    private List<String> previousTokens = new ArrayList<>();



    /**
     * Constructor sin parámetros que inicializa el DAWG con un alfabeto vacío.
     * El DAWG no podrá almacenar palabras hasta que se defina un alfabeto válido.
     */
    public Dawg() {
        this.root = new DawgNode();
        this.alfabeto = new HashSet<>();
    }

    /**
     * Constructor que inicializa el DAWG con un alfabeto definido por el usuario.
     * El alfabeto puede contener letras multicaracter como "CH", "LL", etc.
     *
     * @param alfabeto Conjunto de letras válidas (símbolos) para las palabras del DAWG.
     * @pre El alfabeto no debe ser null ni vacío, y debe contener solo símbolos válidos.
     * @post Se crea un DAWG vacío con nodo raíz y alfabeto personalizado.
     */
    public Dawg(Set<String> alfabeto) {
        this.root = new DawgNode();
        this.alfabeto = alfabeto;
    }

    /**
     * Divide una palabra en letras válidas según el alfabeto definido.
     * Prioriza coincidencias más largas en caso de ambigüedad (e.g., "CH" sobre "C").
     *
     * @param word Palabra a tokenizar.
     * @return Lista de letras (símbolos) correspondientes.
     * @throws IllegalArgumentException si algún fragmento no pertenece al alfabeto.
     */
    private List<String> tokenize(String word) {
        List<String> tokens = new ArrayList<>();
        int i = 0;
        while (i < word.length()) {
            String match = null;
            int maxLength = 0;
            for (String symbol : alfabeto) {
                if (i + symbol.length() <= word.length() && word.startsWith(symbol, i)) {
                    if (symbol.length() > maxLength) {
                        match = symbol;
                        maxLength = symbol.length();
                    }
                }
            }
            if (match == null) {
                throw new IllegalArgumentException("Símbolo no reconocido en el alfabeto: " + word.substring(i));
            }
            tokens.add(match);
            i += match.length();
        }
        return tokens;
    }

    /**
     * Calcula el prefijo común entre la palabra actual y la anterior insertada.
     *
     * @param currentTokens Lista de símbolos de la palabra actual.
     * @return Longitud del prefijo común.
     */
    private int commonPrefix(List<String> currentTokens) {
        int commonPrefix = 0;
        while (commonPrefix < currentTokens.size() && commonPrefix < previousTokens.size()) {
            if (!currentTokens.get(commonPrefix).equals(previousTokens.get(commonPrefix))) break;
            commonPrefix++;
        }
        return commonPrefix;
    }

    /**
     * Realiza el proceso de minimización desde un punto dado.
     * Compara nodos hijos y reutiliza nodos ya minimizados si son equivalentes.
     *
     * @param downTo Índice hasta el cual se realiza la minimización.
     */
    private void minimize(int downTo) {
        for (int i = uncheckedNodes.size() - 1; i >= downTo; i--) {
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
     * Aplica minimización completa y limpia estructuras temporales.
     */
    public void finish() {
        minimize(0);
        minimizedNodes.clear();
        uncheckedNodes.clear();
        previousTokens = new ArrayList<>();
    }

    /**
     * Inserta una palabra en el DAWG. Requiere que las palabras se inserten en orden lexicográfico.
     *
     * @param word Palabra a insertar.
     * @throws NullPointerException si la palabra es null o vacía.
     * @throws IllegalArgumentException si contiene símbolos no válidos.
     */
    public void insert(String word) {
        if (word == null || word.isEmpty()) {
            throw new NullPointerException("No se puede insertar una palabra nula o vacía.");
        }

        List<String> currentTokens = tokenize(word);
        int common = commonPrefix(currentTokens);
        minimize(common);

        DawgNode current = uncheckedNodes.isEmpty() ? root : uncheckedNodes.peek().z;

        for (int i = common; i < currentTokens.size(); i++) {
            String letter = currentTokens.get(i);
            DawgNode newNode = new DawgNode();
            current.addEdge(letter, newNode);
            uncheckedNodes.push(new Triple<>(current, letter, newNode));
            current = newNode;
        }

        current.setFinal(true);
        previousTokens = currentTokens;
    }

    /**
     * Verifica si una palabra existe en el DAWG.
     *
     * @param word Palabra a buscar.
     * @return true si la palabra existe, false en caso contrario.
     * @throws NullPointerException si la palabra es null.
     */
    public boolean search(String word) {
        if (word == null) {
            throw new NullPointerException("No se puede buscar una palabra nula.");
        }

        List<String> tokens = tokenize(word);
        DawgNode current = root;
        for (String letter : tokens) {
            current = current.getEdge(letter);
            if (current == null) return false;
        }
        return current.isFinal();
    }

    /**
     * Retorna el nodo raíz del DAWG.
     *
     * @return Nodo raíz.
     */
    public DawgNode getRoot() {
        return root;
    }

    /**
     * Obtiene el nodo al final del camino que representa la palabra.
     *
     * @param word Palabra a buscar.
     * @return Nodo final o null si el camino no existe.
     * @throws NullPointerException si la palabra es null.
     */
    public DawgNode getNode(String word) {
        if (word == null) {
            throw new NullPointerException("No se puede obtener nodo para una palabra nula.");
        }

        if (word.isEmpty()) return root;

        List<String> tokens = tokenize(word);
        DawgNode current = root;
        for (String letter : tokens) {
            current = current.getEdge(letter);
            if (current == null) return null;
        }
        return current;
    }

    /**
     * Retorna los bordes (símbolos posibles) desde el nodo correspondiente a la palabra parcial.
     *
     * @param partialword Palabra parcial.
     * @return Conjunto de símbolos disponibles o null si el nodo no existe.
     * @throws NullPointerException si la palabra parcial es null.
     */
    public Set<String> getAvailableEdges(String partialword) {
        if (partialword == null) {
            throw new NullPointerException("No se puede obtener bordes de palabra nula.");
        }
        DawgNode node = getNode(partialword);
        return node != null ? node.getAllEdges() : null;
    }

    /**
     * Verifica si la palabra parcial es una palabra completa válida (nodo final).
     *
     * @param partialword Palabra parcial.
     * @return true si el nodo existe y es final, false si no.
     * @throws NullPointerException si la palabra parcial es null.
     */
    public boolean isFinal(String partialword) {
        if (partialword == null) {
            throw new NullPointerException("No se puede verificar si palabra nula es final.");
        }
        DawgNode node = getNode(partialword);
        return node != null && node.isFinal();
    }

    /**
     * Retorna todas las palabras almacenadas en el DAWG.
     *
     * @return Lista con todas las palabras del diccionario.
     */
    public List<String> getAllWords() {
        List<String> result = new ArrayList<>();
        collectWords(root, "", result);
        return result;
    }

    /**
     * Método auxiliar recursivo para recolectar todas las palabras.
     *
     * @param node Nodo actual.
     * @param prefix Prefijo acumulado.
     * @param result Lista donde se almacenan las palabras encontradas.
     */
    private void collectWords(DawgNode node, String prefix, List<String> result) {
        if (node.isFinal()) {
            result.add(prefix);
        }

        for (String edge : node.getAllEdges()) {
            DawgNode next = node.getEdge(edge);
            collectWords(next, prefix + edge, result);
        }
    }
}
