package de.bs.tu.ifis.model.graph;

/**
 * Implementation of an graph edge
 * @author Hermann Kroll
 */
public class Edge {
    protected final Node from;
    protected final Node to;

    public Edge(final Node from, final Node to){
        this.from = from;
        this.to = to;
    }

    public Node getFrom() {
        return from;
    }

    public Node getTo() {
        return to;
    }
}
