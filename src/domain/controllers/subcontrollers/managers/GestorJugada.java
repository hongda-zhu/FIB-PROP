package domain.controllers.subcontrollers.managers;

import domain.models.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GestorJugada {

    public enum Direction {
        HORIZONTAL,
        VERTICAL
    }

    private Tablero tablero;
    private Dawg dawg;
    private final Character[] alphabet = {'a', 'b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
    private Map<Tuple<Integer, Integer>, Set<Character>> lastCrossCheck = new HashMap<>();
    private Direction direction = null;


    public GestorJugada(Tablero tablero, Dawg dawg) {
        this.tablero = tablero;
        this.dawg = dawg;
        this.lastCrossCheck = null;
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

    public Set<Triple<String,Tuple<Integer, Integer>, Direction>> allWordsbefore(String partialWord, Map<Character, Integer> rack, DawgNode currenNode, Tuple<Integer, Integer> nextPos, int limit) {
        Set<Triple<String,Tuple<Integer, Integer>, Direction>> words = new HashSet<>();
        words.addAll(allWords(partialWord, rack, currenNode, nextPos, false));

        if (limit > 0){
            for (Character c : currenNode.getAllEdges()) {
                if (rack.containsKey(c)) {
                    String newPartialWord = partialWord + c;
                    DawgNode nextNode = currenNode.getEdge(c);
                    Map<Character, Integer> newRack = new HashMap<>(rack);
                    if (newRack.get(c) == 1) {
                        newRack.remove(c);
                    } else {
                        newRack.put(c, newRack.get(c) - 1);
                    }
                    words.addAll(allWordsbefore(newPartialWord, newRack, nextNode, nextPos, limit - 1));
                }
            }
        }
        return words;
    }

    public Set<Triple<String,Tuple<Integer, Integer>, Direction>> allWords(String partialWord, Map<Character, Integer> rack, DawgNode currenNode, Tuple<Integer, Integer> nextPos, boolean archorFilled) {
        Set<Triple<String,Tuple<Integer, Integer>, Direction>> words = new HashSet<>();

        if(!this.tablero.isFilled(nextPos) && currenNode.isFinal() && archorFilled) {
            words.add(new Triple<>(partialWord, before(nextPos), this.direction));
        }
        if (this.tablero.validPosition(nextPos)){
            if (this.tablero.isEmpty(nextPos)) {

                for (Character c : currenNode.getAllEdges()) {
                    Set<Character> allowedChars = this.lastCrossCheck.get(nextPos);
                    if (rack.containsKey(c) && allowedChars != null && allowedChars.contains(c)) {
                        String newPartialWord = partialWord + c;
                        DawgNode nextNode = currenNode.getEdge(c);
                        Map<Character, Integer> newRack = new HashMap<>(rack);
                        if (newRack.get(c) == 1) {
                            newRack.remove(c);
                        } else {
                            newRack.put(c, newRack.get(c) - 1);
                        }
                        words.addAll(allWords(newPartialWord, newRack, nextNode, after(nextPos), true));
                    }
                }
            } else {
                char c = this.tablero.getTile(nextPos).charAt(0);
                if (currenNode.getAllEdges().contains(c)) {
                    String newPartialWord = partialWord + c;
                    DawgNode nextNode = currenNode.getEdge(c);
                    words.addAll(allWords(newPartialWord, rack, nextNode, after(nextPos), true));
                }
            }
        }
        return words;
    }


    public Map<Tuple<Integer, Integer>, Set<Character>> crossCheck() {
        Map<Tuple<Integer, Integer>, Set<Character>> words = new HashMap<>();
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
                    Set<Character> set = new HashSet<>();
                    if (beforePart.length() == 0 && afterPart.length() == 0) {
                        Collections.addAll(set, alphabet);
                    } else {
                        for (Character c : alphabet) {
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

    public Set<Triple<String,Tuple<Integer, Integer>, Direction>> allAnswers(Map<Character, Integer> rack) {
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
                    answers.addAll(this.allWords(partial_word, rack, currentNode, pos, false));
                }
                } else {

                    Tuple<Integer, Integer> before_pos = pos;
                    int limit = 0;
                    while(tablero.isEmpty(before(before_pos)) && !anchors.contains(before(before_pos))) {
                        limit += 1;
                        before_pos = before(before_pos);
                    }
                    
                    answers.addAll(allWordsbefore("", rack, dawg.getRoot(), pos, limit));
                    
                }
                
            }
        }
        return answers;
    }
    
}
