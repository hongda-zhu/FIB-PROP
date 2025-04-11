package domain.models;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class DawgNode {
    private Map<Character, DawgNode> edges;
    private boolean isFinal;

    public DawgNode() {
        this.edges = new HashMap<>();
        this.isFinal = false;
    }

    public DawgNode getEdge(char c) {
        return edges.get(c);
    }

    public Set<Character> getAllEdges() {
        return edges.keySet();
    }

    public void addEdge(char c, DawgNode node) {
        edges.put(c, node);
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }
}
