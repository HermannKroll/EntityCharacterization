package de.bs.tu.ifis.model.graph;

import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of an graph node
 * @author Hermann Kroll
 */
public class Node {
    protected final List<Edge> connectedEdges = new LinkedList<>();


    public void addConnectedEdge(final Edge edge){
        this.connectedEdges.add(edge);
    }

    public List<Edge> getConnectedEdges() {
        return connectedEdges;
    }
}
