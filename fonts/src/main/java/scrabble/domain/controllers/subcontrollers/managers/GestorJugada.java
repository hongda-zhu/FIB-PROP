package scrabble.domain.controllers.subcontrollers.managers;

import java.io.Serializable;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.List;

import scrabble.domain.models.Dawg;
import scrabble.domain.models.DawgNode;
import scrabble.domain.models.Diccionario;
import scrabble.domain.models.Tablero;
import scrabble.helpers.Triple;
import scrabble.helpers.Tuple;
import scrabble.helpers.BooleanWrapper;

/**
 * Clase GestorJugada
 * Esta clase se encarga de gestionar las jugadas en el juego de Scrabble.
 * Permite buscar movimientos válidos, calcular puntos y realizar jugadas en el tablero.
 */

public class GestorJugada implements Serializable {

    public enum Direction {
        HORIZONTAL,
        VERTICAL
    }

    private Tablero tablero;
    private Diccionario diccionario;
    private Map<Tuple<Integer, Integer>, Set<String>> lastCrossCheck;
    private Direction direction;
    private Map<String, Integer> alphabet;
    private Dawg dawg;
    private String idiomaActual;

    public GestorJugada(Tablero tablero) {
        this.tablero = tablero;
        this.lastCrossCheck = null;
        this.direction = null;
        this.diccionario = null;
        this.idiomaActual = null;
    }
    
    /**
     * Establece el diccionario a utilizar por el gestor de jugada.
     * @param diccionario Diccionario a utilizar
     */
    public void setDiccionario(Diccionario diccionario) {
        this.diccionario = diccionario;
    }
    
    /**
     * Devuelve la lista de nombres de los diccionarios disponibles actualmente en el sistema.
     * @return Lista de nombres de diccionarios disponibles.
     */
    public List<String> getDiccionariosDisponibles() {
        if (this.diccionario == null) {
            return List.of();
        }
        return this.diccionario.getDiccionariosDisponibles();
    }

    /**
     * Crea un tablero de tamaño NxN.
     * @param N Tamaño del tablero
     */
    public void creaTableroNxN(int N) {
        this.tablero = new Tablero(N);
    }

    /**
     * Método para seleccionar un lenguaje existente en el diccionario.
     * @param nombre Nombre del lenguaje a seleccionar.
     */
    public void setLenguaje(String nombre) {
        if (this.diccionario == null) {
            throw new IllegalStateException("No se ha establecido ningún diccionario");
        }
        
        this.idiomaActual = nombre;
        this.dawg = this.diccionario.getDawg(nombre);
        this.alphabet = this.diccionario.getAlphabet(nombre);
    }

    /**
     * Método para obtener la bolsa de letras del idioma actual
     * @param name Nombre del lenguaje.
     * @return Mapa que representa la bolsa de letras.
     */
    public Map<String, Integer> getBag(String name) {
        if (this.diccionario == null) {
            throw new IllegalStateException("No se ha establecido ningún diccionario");
        }
        return this.diccionario.getBag(name);
    }

    /**
     * Método before
     * @param pos Posición actual.
     * @return Nueva posición antes de la actual.
     */
    public Tuple<Integer, Integer> before(Tuple<Integer, Integer> pos) {
        return direction == Direction.HORIZONTAL ? new Tuple<>(pos.x, pos.y - 1) : new Tuple<>(pos.x - 1, pos.y);
    } 

    /**
     * Método after
     * @param pos Posición actual.
     * @return Nueva posición después de la actual.
     */
    public Tuple<Integer, Integer> after(Tuple<Integer, Integer> pos) {
        return direction == Direction.HORIZONTAL ? new Tuple<>(pos.x, pos.y + 1) : new Tuple<>(pos.x + 1, pos.y);
    }

    /**
     * Método before_cross
     * @param pos Posición actual.
     * @return Nueva posición antes de la actual en la dirección girada.
     */
    public Tuple<Integer, Integer> before_cross(Tuple<Integer, Integer> pos) {
        return direction == Direction.HORIZONTAL ? new Tuple<>(pos.x - 1, pos.y) : new Tuple<>(pos.x, pos.y - 1);
    }

    /**
     * Método after_cross
     * @param pos Posición actual.
     * @return Nueva posición después de la actual en la dirección girada.
     */
    public Tuple<Integer, Integer> after_cross(Tuple<Integer, Integer> pos) {
        return direction == Direction.HORIZONTAL ? new Tuple<>(pos.x + 1, pos.y) : new Tuple<>(pos.x, pos.y + 1);
    }

    /*
     * Método para encontrar los anclajes en el tablero.
     * @return Conjunto de posiciones de anclajes.
     */

    public Set<Tuple<Integer, Integer>> find_anchors(boolean juegoIniciado) {
        Set<Tuple<Integer, Integer>> anchors = new HashSet<>();
        if (juegoIniciado) {
            for (int i = 0; i < tablero.getSize(); i++) {
                for (int j = 0; j < tablero.getSize(); j++) {
                    Tuple<Integer, Integer> pos = new Tuple<>(i,j);
                    if (tablero.isEmpty(pos)) {
                        if (tablero.isFilled(before_cross(pos)) || tablero.isFilled(after_cross(pos)) || tablero.isFilled(before(pos)) || tablero.isFilled(after(pos))) {
                            anchors.add(pos);
                        }
                    }
                }
            }
        } else anchors.add(this.tablero.getCenter());
        System.out.println("Anchors encontrados: " + anchors);
        return anchors;
    }

    /*
     * Método para extender la palabra hacia la izquierda.
     * @param partialWord Palabra parcial.
     * @param rack Mapa de letras disponibles.
     * @param currenNode Nodo actual en el DAWG.
     * @param nextPos Posición siguiente.
     * @param limit Límite de letras a usar.
     * @return Conjunto de palabras extendidas.
     */

    public Set<Triple<String,Tuple<Integer, Integer>, Direction>> extendLeft(String partialWord, Map<String, Integer> rack, DawgNode currenNode, Tuple<Integer, Integer> nextPos, int limit) {
        Set<Triple<String,Tuple<Integer, Integer>, Direction>> words = new HashSet<>();
        words.addAll(extendRight(partialWord, rack, currenNode, nextPos, false));

        if (limit > 0){
            for (String c : currenNode.getAllEdges()) {
                if (rack.containsKey(c) ||rack.containsKey("#")) {
                    String newPartialWord = partialWord + c;
                    DawgNode nextNode = currenNode.getEdge(c);
                    Map<String, Integer> newRack = new HashMap<>(rack);

                    if (!rack.containsKey(c)) c = "#";

                    if (newRack.get(c) == 1) {
                        newRack.remove(c);
                    } else {
                        newRack.put(c, newRack.get(c) - 1);
                    }
                    words.addAll(extendLeft(newPartialWord, newRack, nextNode, nextPos, limit - 1));
                }
            }
        }
        return words;
    }

    /*
     * Método para extender la palabra hacia la derecha.
     * @param partialWord Palabra parcial.
     * @param rack Mapa de letras disponibles.
     * @param currenNode Nodo actual en el DAWG.
     * @param nextPos Posición siguiente.
     * @param archorFilled Indica si el anclaje está lleno.
     * @return Conjunto de palabras extendidas.
     */

    public Set<Triple<String,Tuple<Integer, Integer>, Direction>> extendRight(String partialWord, Map<String, Integer> rack, DawgNode currenNode, Tuple<Integer, Integer> nextPos, boolean archorFilled) {
        Set<Triple<String,Tuple<Integer, Integer>, Direction>> words = new HashSet<>();

        if(!this.tablero.isFilled(nextPos) && currenNode.isFinal() && archorFilled) {
            words.add(new Triple<>(partialWord, before(nextPos), this.direction));
        }
        if (this.tablero.validPosition(nextPos)){
            if (this.tablero.isEmpty(nextPos)) {

                for (String c : currenNode.getAllEdges()) {
                    Set<String> allowedChars = this.lastCrossCheck.get(nextPos);
                    if ((rack.containsKey(c) || rack.containsKey("#")) && allowedChars != null && allowedChars.contains(c)) {
                        String newPartialWord = partialWord + c;
                        DawgNode nextNode = currenNode.getEdge(c);
                        Map<String, Integer> newRack = new HashMap<>(rack);

                        if (!rack.containsKey(c)) c = "#";
                        if (newRack.get(c) == 1) {
                            newRack.remove(c);
                        } else {
                            newRack.put(c, newRack.get(c) - 1);
                        }
                        words.addAll(extendRight(newPartialWord, newRack, nextNode, after(nextPos), true));
                    }
                }
            } else {
                String c = String.valueOf(this.tablero.getTile(nextPos));
                if (currenNode.getAllEdges().contains(String.valueOf(c))) {
                    String newPartialWord = partialWord + c;
                    DawgNode nextNode = currenNode.getEdge(c);
                    words.addAll(extendRight(newPartialWord, rack, nextNode, after(nextPos), true));
                }
            }
        }
        return words;
    }

    /*
     * Método para realizar una verificación cruzada en el tablero.
     * @return Mapa de posiciones y conjuntos de palabras posibles.
     */

    public Map<Tuple<Integer, Integer>, Set<String>> crossCheck() {
        Map<Tuple<Integer, Integer>, Set<String>> words = new HashMap<>();
        for (int i = 0; i < tablero.getSize(); i++) {
            for (int j = 0; j < tablero.getSize(); j++) {
                Tuple<Integer, Integer> pos = new Tuple<>(i,j);
                if (tablero.isEmpty(pos)) {
                    String beforePart = "";
                    String afterPart = "";
                    
                    Tuple<Integer, Integer> up_pos = pos;
                    while(this.tablero.isFilled(before_cross(up_pos))) {
                        beforePart = tablero.getTile(before_cross(up_pos)) + beforePart ;
                        up_pos = before_cross(up_pos);
                    }

                    Tuple<Integer, Integer> down_pos = pos;
                    while(this.tablero.isFilled(after_cross(down_pos))) {
                        afterPart = afterPart + tablero.getTile(after_cross(down_pos));
                        down_pos = after_cross(down_pos);
                    }
                    Set<String> set = new HashSet<>();
                    if (beforePart.length() == 0 && afterPart.length() == 0) {
                        Collections.addAll(set, alphabet.keySet().toArray(new String[0]));
                    } else {
                        for (String c : alphabet.keySet()) {
                            String candidateWord = beforePart + c + afterPart;
                            if (dawg.search(candidateWord)) {
                                set.add(c);
                            }
                        }
                    }
                    words.put(pos, set);
                }
            }
        }
        return words;
    }

    /*
     * Método para buscar todos los movimientos posibles en el tablero.
     * @param rack Mapa de letras disponibles.
     * @return Conjunto de movimientos posibles.
     */

    public Set<Triple<String,Tuple<Integer, Integer>, Direction>> searchAllMoves(Map<String, Integer> rack, boolean isFirst) {
        Set<Triple<String,Tuple<Integer, Integer>, Direction>> answers = new HashSet<>();
        Set<Tuple<Integer, Integer>> anchors = find_anchors(isFirst);

        for (Direction dir : Direction.values()) {

            this.direction = dir;

            this.lastCrossCheck = crossCheck();
            
            for (Tuple<Integer, Integer> pos : anchors) {
                
                if (this.tablero.isFilled(before(pos))) {
                    
                    String partial_word = "";
                    Tuple<Integer, Integer> before_pos = pos;
                    
                while(tablero.isFilled(before(before_pos))) {
                    partial_word = tablero.getTile(before(before_pos)) + partial_word;
                    before_pos = before(before_pos);
                }
                
                DawgNode currentNode = dawg.getNode(partial_word);
                if (currentNode != null) {
                    answers.addAll(this.extendRight(partial_word, rack, currentNode, pos, false));
                }
                } else {

                    Tuple<Integer, Integer> before_pos = pos;
                    int limit = 0;
                    while(tablero.isEmpty(before(before_pos)) && !anchors.contains(before(before_pos))) {
                        limit += 1;
                        before_pos = before(before_pos);
                    }
                    
                    answers.addAll(extendLeft("", rack, dawg.getRoot(), pos, limit));
                    
                }
                
            }
        }
        return answers;
    }

    /*
     * Método para realizar un movimiento en el tablero.
     * @param move Movimiento a realizar.
     * @param rack Mapa de letras disponibles.
     * @return Mapa actualizado de letras disponibles.
     */

    public Map<String, Integer> makeMove(Triple<String,Tuple<Integer, Integer>, Direction> move, Map<String, Integer> rack) {
        String word = move.x.toUpperCase();
        Tuple<Integer, Integer> pos = move.y;
        Direction dir = move.z;

        Map<String, Integer> newRack = new HashMap<>(rack);

        for (int i = word.length() - 1; i >= 0; i--) {
            String letter = String.valueOf(word.charAt(i)).toUpperCase();
            
            if (newRack.containsKey(letter) && this.tablero.isEmpty(pos)) {
                if (newRack.get(letter) == 1) {
                    newRack.remove(letter);
                } else {
                    newRack.put(letter, newRack.get(letter) - 1);
                }
            }
            this.tablero.setTile(pos, String.valueOf(letter));
            if (dir == Direction.HORIZONTAL) {
                pos = new Tuple<Integer, Integer>(pos.x, pos.y - 1); 
            } else {
                pos = new Tuple<Integer, Integer>(pos.x - 1, pos.y);
            }
        }
        return newRack;

    }

    /*
     * Método para calcular los puntos de un movimiento.
     * @param move Movimiento a evaluar.
     * @return Puntos obtenidos por el movimiento.
     */

    public int calculateMovePoints(Triple<String,Tuple<Integer, Integer>, Direction> move) {

        int points = 0;
        int doubleTimes = 0;
        int tripleTimes = 0;
        
        String word = move.x;
        Tuple<Integer, Integer> pos = move.y;
        Direction dir = move.z;

        for (int i = word.length() - 1; i >= 0; i--) {
            int letterPoint = alphabet.get(String.valueOf(word.charAt(i)));
            if (this.tablero.isFilled(new Tuple<>(pos.x, pos.y))) {
                points += letterPoint;
            } else {
                switch (this.tablero.getBonus(pos)) {
                    case TW:
                        points += letterPoint;
                        tripleTimes++;
                        break;
                    case TL:
                        points += letterPoint * 3;
                        break;
                    case DW:
                        points += letterPoint;
                        doubleTimes++;
                        break;
                    case DL:
                        points += letterPoint * 2;
                        break;
                    case X:
                        points += letterPoint * 2;
                        break;
                    default:
                        points += letterPoint;
                        break;
                }
            }
            pos = dir == Direction.HORIZONTAL? new Tuple<>(pos.x, pos.y - 1): new Tuple<>(pos.x - 1, pos.y);
        }
        return points * (int) Math.pow(2, doubleTimes) * (int) Math.pow(3, tripleTimes); 
    }

    /*
     * Método para verificar si un movimiento es válido.
     * @param move Movimiento a evaluar.
     * @param rack Mapa de letras disponibles.
     * @return true si el movimiento es válido, false en caso contrario.
     */

    public boolean isValidMove (Triple<String,Tuple<Integer, Integer>, Direction> move, Map<String, Integer> rack) {
        Set<Triple<String,Tuple<Integer, Integer>, Direction>> possibleWords = searchAllMoves(rack, true);
        return possibleWords.contains(move);
    }

    /*
     * Método para verificar si es un movimiento válido en el primer turno.
     * @param move Movimiento a evaluar.
     * @param rack Mapa de letras disponibles.
     * @return true si el movimiento es válido, false en caso contrario.
     */

     public boolean isValidFirstMove(Triple<String, Tuple<Integer, Integer>, Direction> move, Map<String, Integer> rack) {
        String word = move.x;
        Tuple<Integer, Integer> pos = move.y;
        Direction dir = move.z;
    
        System.out.println("Validando palabra: " + word + " desde posición: " + pos + " en dirección: " + dir);
    
        // Check if the word fits within the board boundaries
        for (int i = word.length() - 1; i >= 0; i--) {
            System.out.println("Verificando posición válida: " + pos);
            if (!tablero.validPosition(pos)) {
                System.out.println("Posición inválida: " + pos);
                return false;
            }
            pos = dir == Direction.HORIZONTAL ? new Tuple<>(pos.x, pos.y - 1) : new Tuple<>(pos.x - 1, pos.y);
        }
    
        // Reset position to the starting point
        pos = move.y;
    
        // Check if the word is placed on the center tile
        boolean centerTileCovered = false;
        for (int i = word.length() - 1; i >= 0; i--) {
            System.out.println("Comprobando si cubre el centro: " + pos);
            if (pos.equals(tablero.getCenter())) {
                System.out.println("¡Casilla central cubierta!");
                centerTileCovered = true;
            }
            pos = dir == Direction.HORIZONTAL ? new Tuple<>(pos.x, pos.y - 1) : new Tuple<>(pos.x - 1, pos.y);
        }
    
        if (!centerTileCovered) {
            System.out.println("La palabra no pasa por el centro.");
            return false;
        }
    
        // Check if the word can be formed using the rack
        Map<String, Integer> tempRack = new HashMap<>(rack);
        for (int i = 0; i < word.length(); i++) {
            String letter = String.valueOf(word.charAt(i)).toUpperCase();
            String diletter = "";
            if (i + 1 < word.length()) {
                diletter = String.valueOf(word.charAt(i + 1)).toUpperCase();
            }
            System.out.println("Verificando letra: " + letter + " (o " + diletter + ") en rack: " + tempRack);
    
            if (tempRack.containsKey(letter) || tempRack.containsKey(diletter)) {
                String key = tempRack.containsKey(letter) ? letter : diletter;
                System.out.println("Usando ficha: " + key);
                if (tempRack.get(key) == 1) {
                    tempRack.remove(key);
                } else {
                    tempRack.put(key, tempRack.get(key) - 1);
                }
            } else if (tempRack.containsKey("#")) { // Use blank tile
                System.out.println("Usando comodín para letra: " + letter);
                if (tempRack.get("#") == 1) {
                    tempRack.remove("#");
                } else {
                    tempRack.put("#", tempRack.get("#") - 1);
                }
            } else {
                System.out.println("Letra no disponible en el rack: " + letter);
                return false;
            }
        }
    
        boolean found = this.dawg.search(word);
        System.out.println("¿Palabra encontrada en diccionario? " + found);
        return found;
    }
    
    /*
     * Método para mostrar el tablero en la consola.
     * Este método imprime el tablero en la consola.
     */

    public void mostrarTablero () {
        System.out.println(this.tablero.toString());
    }

    /*
     * Método para realizar un turno de juego.
     * @param isFirst Indica si es el primer turno del jugador.
     * @return Triple que contiene la palabra, la posición y la dirección.
     */

    public Triple<String, Tuple<Integer, Integer>, Direction> jugarTurno(BooleanWrapper pausado) {
        Scanner scanner = new Scanner(System.in);
        String palabra = "";
        // Leer la palabra

        System.out.print("Introduce la palabra a colocar (o 'p' para pasar): ");
        

        System.out.println("Coloca una palabra en el tablero.");
        palabra = scanner.nextLine().toUpperCase();
        
        if (palabra.equals("X")) {
            pausado.value = true;
            return null;
        }
        
        // Si el usuario escribe 'p', retornar null
        if (palabra.equals("p")) {
            return null;
        }
        
        
        // Leer la posición de la última letra (coordenada X e Y)
        int x = -1;
        int y = -1;
        boolean coordenadasValidas = false;
        
        while (!coordenadasValidas) {
            System.out.print("Introduce las coordenadas (a b (eje vertical/ eje horizontal)) de la última letra de tu palabra: ");
            try {
                x = scanner.nextInt();
                y = scanner.nextInt();
                scanner.nextLine(); // Consume the remaining newline
                coordenadasValidas = true;
            } catch (Exception e) {
                scanner.nextLine(); // Clear the invalid input
                System.out.println("Formato incorrecto. Debes introducir dos números separados por un espacio.");
            }
        }
        
        // Leer la dirección
        String dir;
        boolean direccionValida = false;
        
        while (!direccionValida) {
            System.out.print("Introduce la orientación de tu palabra 'H' (horizontal) o 'V' (vertical): ");
            dir = scanner.nextLine().toUpperCase();
            
            if (dir.equals("H") || dir.equals("V")) {
                direccionValida = true;
                Direction direction = dir.equals("H") ? Direction.HORIZONTAL : Direction.VERTICAL;
                
                // Devolver la respuesta en formato Triple
                return new Triple<>(palabra, new Tuple<>(x, y), direction);
            } else {
                System.out.println("Dirección no válida. Debe ser HORIZONTAL o VERTICAL.");
            }
        }
        // Este return nunca debería alcanzarse, pero es necesario para la compilación
        return null;
    }
 }
