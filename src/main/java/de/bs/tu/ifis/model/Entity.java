package de.bs.tu.ifis.model;

import de.bs.tu.ifis.model.graph.Node;

import java.util.LinkedList;
import java.util.List;

/**
 * Entity as an extension of the node class
 * @author Hermann Kroll
 */
public class Entity extends Node {
    private final String name;
    private final List<Predicate> predicates = new LinkedList<Predicate>();

    public Entity(final String name){
        this.name = name;
    }


    public void addPredicate(final Predicate predicate){
        this.predicates.add(predicate);
        this.addConnectedEdge(predicate);
    }

    public String getName() {
        return name;
    }

    public List<Predicate> getPredicates() {
        return predicates;
    }

    @Override
    public String toString() {
        return "Entity{" + "name='" + name + '\'' + '}';
    }

    public String toStringWithNeighbourhood(){
        final StringBuffer buffer = new StringBuffer();
        buffer.append("Entity{" + "name='" + name + '\'' + '}');

        for(final Predicate pre : predicates){
            buffer.append('\n');
            buffer.append("\t\t\t\t");
            buffer.append(pre);
        }

        return buffer.toString();
    }


}
