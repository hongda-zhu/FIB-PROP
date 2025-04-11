package domain.controllers.subcontrollers.managers;

import domain.models.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GestorJugada {
    private Tablero tablero;
    private Dawg dawg;

    public GestorJugada(Tablero tablero, Dawg dawg) {
        this.tablero = tablero;
        this.dawg = dawg;
    }

    public Tuple<Integer, Integer> left (Tuple<Integer, Integer> pos) {
        return new Tuple<>(pos.x, pos.y - 1);
    } 

    public Tuple<Integer, Integer> right (Tuple<Integer, Integer> pos) {
        return new Tuple<>(pos.x, pos.y + 1);
    }

    public Tuple<Integer, Integer> up (Tuple<Integer, Integer> pos) {
        return new Tuple<>(pos.x - 1, pos.y);
    }

    public Tuple<Integer, Integer> down (Tuple<Integer, Integer> pos) {
        return new Tuple<>(pos.x + 1, pos.y);
    }

    public Set<Tuple<Integer, Integer>> find_anchors() {
        Set<Tuple<Integer, Integer>> anchors = new HashSet<>();
        for (int i = 0; i < tablero.getSize(); i++) {
            for (int j = 0; j < tablero.getSize(); j++) {
                Tuple<Integer, Integer> pos = new Tuple<>(i,j);
                if (tablero.isEmpty(pos)) {
                    if (tablero.isFilled(up(pos)) || tablero.isFilled(down(pos)) || tablero.isFilled(left(pos)) || tablero.isFilled(right(pos))) {
                        anchors.add(pos);
                    }
                }
            }
        }
        return anchors;
    }

    public Set<Tuple<String,Tuple<Integer, Integer>>> allWordsLeft(String partialWord, Map<Character, Integer> rack, DawgNode currenNode, Tuple<Integer, Integer> nextPos, int limit) {
        Set<Tuple<String,Tuple<Integer, Integer>>> words = new HashSet<>();
        words.addAll(allWords(partialWord, rack, currenNode, nextPos));

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
                    words.addAll(allWordsLeft(newPartialWord, newRack, nextNode, nextPos, limit - 1));
                }
            }
        }
        return words;
    }

    public Set<Tuple<String,Tuple<Integer, Integer>>> allWords(String partialWord, Map<Character, Integer> rack, DawgNode currenNode, Tuple<Integer, Integer> nextPos) {
        Set<Tuple<String,Tuple<Integer, Integer>>> words = new HashSet<>();

        if(!this.tablero.isFilled(nextPos) && currenNode.isFinal()) {
            words.add(new Tuple<>(partialWord, left(nextPos)));
        }
        if (this.tablero.validPosition(nextPos)){
            if (this.tablero.isEmpty(nextPos)) {

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
                        words.addAll(allWords(newPartialWord, newRack, nextNode, right(nextPos)));
                    }
                }
            } else {
                char c = this.tablero.getTile(nextPos).charAt(0);
                if (currenNode.getAllEdges().contains(c)) {
                    String newPartialWord = partialWord + c;
                    DawgNode nextNode = currenNode.getEdge(c);
                    words.addAll(allWords(newPartialWord, rack, nextNode, right(nextPos)));
                }
            }
        }
        return words;
    }

    public Set<Tuple<String,Tuple<Integer, Integer>>> allAnswers(Map<Character, Integer> rack) {
        Set<Tuple<String,Tuple<Integer, Integer>>> answers = new HashSet<>();
        Set<Tuple<Integer, Integer>> anchors = find_anchors();
        
        for (Tuple<Integer, Integer> pos : anchors) {

            if (this.tablero.isFilled(left(pos))) {

                String partial_word = "";
                Tuple<Integer, Integer> left_pos = left(pos);
                
                while(tablero.isFilled(left_pos)) {
                    partial_word = tablero.getTile(left_pos) + partial_word;
                    left_pos = left(left_pos);
                }
                
                DawgNode currentNode = dawg.getNode(partial_word);
                if (currentNode != null) {
                    answers.addAll(this.allWords(partial_word, rack, currentNode, pos));
                }
            } else {

                Tuple<Integer, Integer> left_pos = left(pos);
                int limit = 0;
                while(tablero.isEmpty(left_pos)) {
                    limit += 1;
                    left_pos = left(left_pos);
                }

                answers.addAll(allWordsLeft("", rack, dawg.getRoot(), pos, limit));

            }
            
        }
        return answers;
    }
 
}
