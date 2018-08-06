package de.bs.tu.ifis.model;

import de.bs.tu.ifis.model.graph.Edge;
import de.bs.tu.ifis.model.graph.Node;


/**
 * Predicate is an extension of an edge
 * @author Hermann Kroll
 */
public class Predicate extends Edge {
    private final String name;

    public Predicate(final String name, final Node from, final Node to){
        super(from, to);
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public Entity getEntityFrom() {
        if(super.getFrom() instanceof Entity)
            return (Entity)super.getFrom();
        return null;
    }

    public Entity getEntityTo(){
        if(super.getTo() instanceof Entity)
            return (Entity)super.getTo();

        return null;
    }

    @Override
    public String toString() {
        return "Predicate{" +
                from + " --- " +
                name + " ---> " +
                to +
                '}';
    }
}
