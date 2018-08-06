package de.bs.tu.ifis.model.graph;

import de.bs.tu.ifis.model.Entity;

import java.util.HashMap;

/**
 * Implementation of an graph
 * uses an index for all of its nodes
 * @author Hermann Kroll
 */
public class Graph {
    private final HashMap<String, Entity> nodeIndex;


    public Graph(){
        this.nodeIndex = new HashMap<>();
    }

    public Graph(final HashMap<String, Entity> nodeIndex){
        this.nodeIndex = nodeIndex;
    }


    public void addNode(final Entity entity){
        //Only if not contained
        if(!nodeIndex.containsKey(entity.getName())){
            nodeIndex.put(entity.getName(), entity);
        }
    }



    public Entity findEntity(final String entityName){
        return nodeIndex.get(entityName);
    }

}
