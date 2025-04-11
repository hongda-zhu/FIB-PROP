package domain.models;

public class Dawg {

    private final DawgNode root;

    public Dawg() {
        root = new DawgNode();
    }

    public void insert(String word) {
        DawgNode current = root;
        for (char c : word.toCharArray()) {
            if (current.getEdge(c) == null) {
                current.addEdge(c, new DawgNode());
            }
            current = current.getEdge(c);
        }
        current.setFinal(true);
    }

    public boolean search(String word) {
        DawgNode current = root;
        for (char c : word.toCharArray()) {
            if (current.getEdge(c) == null) {
                return false;
            }
            current = current.getEdge(c);
        }
        return current.isFinal();
    }

    public DawgNode getRoot() {
        return root;
    }

    public DawgNode getNode(String word) {
        DawgNode current = root;
        for (char c : word.toCharArray()) {
            if (current.getEdge(c) == null) {
                return null;
            }
            current = current.getEdge(c);
        }
        return current;
    }
}
