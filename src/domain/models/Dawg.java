package domain.models;

public class Dawg {

    private final DawgNode root;

    public Dawg() {
        root = new DawgNode();
    }

    public void insert(String word) {
        DawgNode current = root;
        for (char c : word.toCharArray()) {
            if (current.getEdge(String.valueOf(c)) == null) {
                current.addEdge(String.valueOf(c), new DawgNode());
            }
            current = current.getEdge(String.valueOf(c));
        }
        current.setFinal(true);
    }

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

    public DawgNode getRoot() {
        return root;
    }

    public DawgNode getNode(String word) {
        DawgNode current = root;
        for (char c : word.toCharArray()) {
            if (current.getEdge(String.valueOf(c)) == null) {
                return null;
            }
            current = current.getEdge(String.valueOf(c));
        }
        return current;
    }
}
