package de.bs.tu.ifis.EYRE2018;

import de.bs.tu.ifis.model.Entity;
import de.bs.tu.ifis.model.Literal;
import de.bs.tu.ifis.model.graph.Node;
import de.bs.tu.ifis.model.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class EyreEntityListReader {
    private Logger logger = LogManager.getLogger();

    public List<EyreEntity> readEntityListFromFile(final String filename, final boolean includePredicates){
        final List<EyreEntity> entityList = new LinkedList<>();
        logger.debug("Read EYRE entity list from file: " + filename);
        try {
            try (BufferedReader br = new BufferedReader(new FileReader(new File(filename)))) {
                String line;
                boolean firstLine = true;
                while ((line = br.readLine()) != null) {
                    //Skip first line
                    if(firstLine){
                        firstLine = false;
                        continue;
                    }

                    //Split line by \t
                    String[] columns = line.split("\t");
                    long eid = Long.valueOf(columns[0]);
                    int tripeNum = Integer.valueOf(columns[4]);

                    final EyreEntity entity = new EyreEntity(eid, columns[1], columns[2], tripeNum);
                    entityList.add(entity);

                    if(includePredicates){
                        // Filename for predicates
                        // exctract directory first
                        String predicateFile = filename.substring(0, filename.lastIndexOf("/")+1);
                        if(entity.getGraph().contains("dbpedia")){
                            predicateFile += "dbpedia/";
                        }
                        else if(entity.getGraph().contains("linkedmdb")){
                            predicateFile += "lmdb/";
                        } else{
                            logger.error("Unknown graph for entity: " + entity);
                            continue;
                        }
                        predicateFile += eid + "/" + eid + "_desc.nt";
                        logger.debug("Load predicates for entity: " + entity + " from file: " + predicateFile);
                        try (BufferedReader brPred = new BufferedReader(new FileReader(new File(predicateFile)))) {
                            String linePred;
                            while ((linePred = brPred.readLine()) != null) {
                                final String[] tripleSplit = linePred.split("> ");
                                final String pred = tripleSplit[1].replace("<", ""); // no <,> inside predicate name
                                String object = tripleSplit[2];
                                //Is object entity or literal?
                                Node node = null;
                                if(object.startsWith("<http://")){
                                    node = new Entity(object.replace("<", ""));// no <,> inside entity name
                                } else {
                                    //Remove " ."
                                    //Type is availbe
                                    if(object.contains("\"^^<")){
                                        object = object + ">";
                                    }
                                    node = new Literal(object.replace(" ." , ""));
                                }
                                final Predicate predicate = new Predicate(pred, entity, node);
                                entity.addPredicate(predicate);

                            }
                        } catch (IOException ex2){
                            logger.error("Error while reading predicates for entity: " + entity + " from filename: " + predicateFile);
                            ex2.printStackTrace();
                        }
                    }

                }
            }
            logger.debug(entityList.size() + " entities read from file " + filename);
            return entityList;
        } catch (IOException ex){
            logger.error("Error while reading entity list from file: " + filename + " because " + ex);
            ex.printStackTrace();
        }

        return null;
    }

}
