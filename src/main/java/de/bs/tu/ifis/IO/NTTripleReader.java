package de.bs.tu.ifis.IO;

import de.bs.tu.ifis.model.Entity;
import de.bs.tu.ifis.model.Literal;
import de.bs.tu.ifis.model.Predicate;
import de.bs.tu.ifis.model.graph.Graph;
import de.bs.tu.ifis.model.graph.Node;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class NTTripleReader {
    private Graph graph;
    private final HashMap<String, Entity> entityIndex = new HashMap<>();
    private final LinkedList<Predicate> triples = new LinkedList<>();




    public LinkedList<Predicate> readPredicatesFromNTFile(final String ntFilename) throws IOException {
        entityIndex.clear();
        triples.clear();

        BufferedReader brPred = new BufferedReader(new FileReader(new File(ntFilename)));
        String linePred;
        while ((linePred = brPred.readLine()) != null) {
            //Everything before closing .
            final String[] tripleSplit = linePred.split("> ");
            final String ent = tripleSplit[0].replace("<" , "");
            final String pred = tripleSplit[1].replace("<", ""); // no <,> inside predicate name
            String object = tripleSplit[2];
            //Is object entity or literal?
            Node objNode = null;
            if (object.startsWith("<http://")) {
                String objEnt = object.replace("<", "");
                objNode = entityIndex.get(objEnt); // Search entity in index
                if(objNode == null) {
                    objNode = new Entity(objEnt);// no <,> inside entity name
                    entityIndex.put(objEnt, (Entity)objNode);
                }
            } else {
                //Type is availbe
                if(object.contains("\"^^<")){
                    object = object + ">";
                }
                objNode = new Literal(object.replace(" ." , ""));
            }

            Entity entity =  entityIndex.get(ent);
            if(entity == null){
                entity = new Entity(ent);
                entityIndex.put(ent, entity);
            }
            final Predicate predicate = new Predicate(pred, entity, objNode);
            entity.addPredicate(predicate);
            triples.add(predicate);
        }
        graph = new Graph(entityIndex);

        return triples;
    }

    public Graph getGraph() {
        return graph;
    }

    public HashMap<String, Entity> getEntityIndex() {
        return entityIndex;
    }

    public LinkedList<Predicate> getTriples() {
        return triples;
    }
}
