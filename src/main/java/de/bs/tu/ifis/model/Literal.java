package de.bs.tu.ifis.model;

import de.bs.tu.ifis.model.graph.Node;

/**
 * Literal is a extension of a graph node
 * @author Hermann Kroll
 */
public class Literal extends Node {
    private final String value;


    public Literal(final String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Literal{" +
                "value='" + value + '\'' +
                '}';
    }
}
