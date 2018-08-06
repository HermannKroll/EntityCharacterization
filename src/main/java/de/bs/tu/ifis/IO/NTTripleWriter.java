package de.bs.tu.ifis.IO;

import de.bs.tu.ifis.model.Entity;
import de.bs.tu.ifis.model.Literal;
import de.bs.tu.ifis.model.Predicate;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class NTTripleWriter {


    /**
     * Writes a list of triples into the .nt triple format
     * @param filename path to the new file
     * @param predicates list of predicates
     * @throws IOException if an io error occurrs
     */
    public static void writePredicatesToFile(final String filename, final List<Predicate> predicates) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename));
        for(final Predicate pred: predicates){
            final StringBuffer buffer = new StringBuffer();
            buffer.append("<" + ((Entity)pred.getFrom()).getName() + "> ");
            buffer.append("<" + pred.getName() + "> ");
            if(pred.getTo() instanceof Entity){
                buffer.append("<" + ((Entity)pred.getTo()).getName() + ">");
            } else{
                buffer.append(((Literal)pred.getTo()).getValue());
            }
            buffer.append(" .\n");

            bufferedWriter.write(buffer.toString());
        }
        bufferedWriter.close();
    }
}
