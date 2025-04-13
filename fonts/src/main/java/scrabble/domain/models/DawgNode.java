package scrabble.domain.models;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class DawgNode {
    private Map<String, DawgNode> edges;
    private boolean isFinal;

    public DawgNode() {
        this.edges = new HashMap<>();
        this.isFinal = false;
    }

    public DawgNode getEdge(String c) {
        return edges.get(c);
    }

    public Set<String> getAllEdges() {
        return edges.keySet();
    }

    public void addEdge(String c, DawgNode node) {
        edges.put(c, node);
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }
}
