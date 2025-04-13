package domain.controllers.subcontrollers.managers;

import domain.helpers.Triple;
import domain.helpers.Tuple;
import domain.models.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class GestorJugada {



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


    public GestorJugada(Tablero tablero) {
        this.tablero = tablero;
        this.lastCrossCheck = null;
        this.direction = null;
        this.diccionario = null;
    }

    public void anadirLenguaje(String nombre, String rutaArchivoAlpha, String rutaArchivoWords) {
        if (this.diccionario == null) {
            this.diccionario = new Diccionario();
        }
        this.diccionario.addDawg(nombre, rutaArchivoWords);
        this.diccionario.addAlphabet(nombre, rutaArchivoAlpha);
    }

    public void setLenguaje(String nombre) {
        this.dawg = this.diccionario.getDawg(nombre);
        this.alphabet = this.diccionario.getAlphabet(nombre);
    }

    public Map<String, Integer> getBag(String name) {
        return this.diccionario.getBag(name);
    }
 
    public Tuple<Integer, Integer> before (Tuple<Integer, Integer> pos) {
        return direction == Direction.HORIZONTAL? new Tuple<>(pos.x, pos.y - 1): new Tuple<>(pos.x - 1, pos.y);
    } 

    public Tuple<Integer, Integer> after (Tuple<Integer, Integer> pos) {
        return direction == Direction.HORIZONTAL? new Tuple<>(pos.x, pos.y + 1): new Tuple<>(pos.x + 1, pos.y);
    }

    public Tuple<Integer, Integer> before_cross (Tuple<Integer, Integer> pos) {
        return direction == Direction.HORIZONTAL? new Tuple<>(pos.x - 1, pos.y): new Tuple<>(pos.x, pos.y - 1);
    }

    public Tuple<Integer, Integer> after_cross (Tuple<Integer, Integer> pos) {
        return direction == Direction.HORIZONTAL? new Tuple<>(pos.x + 1, pos.y): new Tuple<>(pos.x, pos.y + 1);
    }

    public Set<Tuple<Integer, Integer>> find_anchors() {
        Set<Tuple<Integer, Integer>> anchors = new HashSet<>();
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
        return anchors;
    }

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

    public Set<Triple<String,Tuple<Integer, Integer>, Direction>> searchAllMoves(Map<String, Integer> rack) {
        Set<Triple<String,Tuple<Integer, Integer>, Direction>> answers = new HashSet<>();
        Set<Tuple<Integer, Integer>> anchors = find_anchors();

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


    public Map<String, Integer> makeMove(Triple<String,Tuple<Integer, Integer>, Direction> move, Map<String, Integer> rack) {
        String word = move.x;
        Tuple<Integer, Integer> pos = move.y;
        Direction dir = move.z;

        Map<String, Integer> newRack = new HashMap<>(rack);

        for (int i = word.length() - 1; i >= 0; i--) {
            String letter = String.valueOf(word.charAt(i));
            this.tablero.setTile(pos, String.valueOf(letter));
            if (dir == Direction.HORIZONTAL) {
                pos = new Tuple<Integer, Integer>(pos.x, pos.y - 1); 
            } else {
                pos = new Tuple<Integer, Integer>(pos.x - 1, pos.y);
            }

            if (newRack.containsKey(letter)) {
                if (newRack.get(letter) == 1) {
                    newRack.remove(letter);
                } else {
                    newRack.put(letter, newRack.get(letter) - 1);
                }
            }
        }

        return newRack;

    }


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


    public boolean isValidMove (Triple<String,Tuple<Integer, Integer>, Direction> move, Map<String, Integer> rack) {
        Set<Triple<String,Tuple<Integer, Integer>, Direction>> possibleWords = searchAllMoves(rack);
        return possibleWords.contains(move);
    }

    public boolean isValidFirstMove(Triple<String, Tuple<Integer, Integer>, Direction> move, Map<String, Integer> rack) {
        String word = move.x;
        Tuple<Integer, Integer> pos = move.y;
        Direction dir = move.z;

        // Check if the word fits within the board boundaries
        for (int i = word.length() - 1; i >= 0; i--) {
            if (!tablero.validPosition(pos)) {
                return false;
            }
            pos = dir == Direction.HORIZONTAL ? new Tuple<>(pos.x, pos.y - 1) : new Tuple<>(pos.x - 1, pos.y);
        }

        // Reset position to the starting point
        pos = move.y;

        // Check if the word is placed on the center tile
        boolean centerTileCovered = false;
        for (int i = word.length() - 1; i >= 0; i--) {
            if (pos.equals(tablero.getCenter())) {
                centerTileCovered = true;
            }
            pos = dir == Direction.HORIZONTAL ? new Tuple<>(pos.x, pos.y - 1) : new Tuple<>(pos.x - 1, pos.y);
        }

        if (!centerTileCovered) {
            return false;
        }

        // Check if the word can be formed using the rack
        Map<String, Integer> tempRack = new HashMap<>(rack);
        for (char c : word.toCharArray()) {
            String letter = String.valueOf(c).toUpperCase();
            if (tempRack.containsKey(letter)) {
                if (tempRack.get(letter) == 1) {
                    tempRack.remove(letter);
                } else {
                    tempRack.put(letter, tempRack.get(letter) - 1);
                }
            } else if (tempRack.containsKey("#")) { // Use blank tile
                if (tempRack.get("#") == 1) {
                    tempRack.remove("#");
                } else {
                    tempRack.put("#", tempRack.get("#") - 1);
                }
            } else {
                return false;
            }
        }

        return true;
    }

    public void mostrarTablero () {
        System.out.println(this.tablero.toString());
    }

    public Triple<String, Tuple<Integer, Integer>, Direction> jugarTurno(boolean isFirst) {
        Scanner scanner = new Scanner(System.in);
        String palabra = "";
        // Leer la palabra
        if (isFirst) {
            System.out.println("Es tu primer turno, debes colocar una palabra en el centro del tablero.");
        } else {
            System.out.print("Introduce la palabra a colocar (o 'p' para pasar): ");
        }

        System.out.println("Coloca una palabra en el tablero.");
        palabra = scanner.nextLine();
        
        
        // Si el usuario escribe 'p', retornar null
        if (!isFirst && palabra.equals("p")) {
            return null;
        }
        
        
        // Leer la posición de la última letra (coordenada X e Y)
        int x = -1;
        int y = -1;
        boolean coordenadasValidas = false;
        
        while (!coordenadasValidas) {
            System.out.print("Introduce la posición de la última letra (X Y): ");
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
            System.out.print("Introduce la dirección (HORIZONTAL (H) o VERTICAL(V)): ");
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
