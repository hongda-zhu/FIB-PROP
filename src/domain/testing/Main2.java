package domain.testing;

import domain.controllers.subcontrollers.managers.GestorJugada;
import domain.controllers.subcontrollers.managers.GestorJugada.Direction;
import domain.models.Dawg;
import domain.models.Tablero;
import domain.models.Triple;
import domain.models.Tuple;
import java.util.Map;
import java.util.Set;

public class Main2 {
    public static void main(String[] args) {
        Dawg dawg = new Dawg();
        dawg.insert("or");
        dawg.insert("floor");
        dawg.insert("for");
        dawg.insert("fall");
        dawg.insert("future");
        dawg.insert("off");
        dawg.insert("effect");


        Tablero tablero = new Tablero(7);
        tablero.setTile(new Tuple<>(1, 1), "o");
        tablero.setTile(new Tuple<>(2, 1), "f");
        // tablero.setTile(new Tuple<>(2, 5), "r");
        System.out.print(tablero.toString());

        GestorJugada gestor = new GestorJugada(tablero, dawg);

        Map<Character, Integer> rack = Map.of(
            'e', 2,
            'f', 2,
            'c', 1,
            't', 1,
            'r', 1,
            'o', 2,
            'l', 1
        );

        Set<Triple<String,Tuple<Integer, Integer>, Direction>> result = gestor.allAnswers(rack);

        System.out.println("Possible words:");
        for (Triple<String,Tuple<Integer, Integer>, Direction> word : result) {
            System.out.println(word.x);
            Tablero tempTablero = new Tablero(tablero); // Create a copy of the original board
            Tuple<Integer, Integer> pos = word.y;
            for (int i = word.x.length() - 1; i >= 0; i--) {
                tempTablero.setTile(pos, String.valueOf(word.x.charAt(i)));
                if (word.z == Direction.HORIZONTAL) {
                    pos = new Tuple<Integer, Integer>(pos.x, pos.y - 1); // Move to the next position in the word
                } else {
                    pos = new Tuple<Integer, Integer>(pos.x - 1, pos.y);
                }
            }
            System.out.print(tempTablero.toString() + "\n" + "----------------\n");
        }
        
    }
}
