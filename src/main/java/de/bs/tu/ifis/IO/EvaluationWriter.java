package de.bs.tu.ifis.IO;

import de.bs.tu.ifis.evaluation.Metric;
import de.bs.tu.ifis.model.Entity;
import de.bs.tu.ifis.model.Literal;
import de.bs.tu.ifis.model.Predicate;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

public class EvaluationWriter {



    public static void writeEvaluation(final String filename, final List<Predicate> predicates, final HashMap<Class, HashMap<Predicate, Double>> scoredMap) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename));

        //Header Line
        bufferedWriter.write("Predicate");
        for(final Class metric: scoredMap.keySet()) {
            //Retrieve metric name
            String metricName = "";
            try {
                Constructor<?> cons = metric.getConstructor();
                Object object = cons.newInstance(null);
                metricName = ((Metric)object).getMetricName();
            } catch (Exception e) {
                e.printStackTrace();
            }

            bufferedWriter.write('\t' + metricName);
        }
        bufferedWriter.write("\n");

        for(final Predicate pred: predicates){
            final StringBuffer buffer = new StringBuffer();
            buffer.append("\"");
            buffer.append("<" + ((Entity)pred.getFrom()).getName() + "> ");
            buffer.append("<" + pred.getName() + "> ");
            if(pred.getTo() instanceof Entity){
                buffer.append("<" + ((Entity)pred.getTo()).getName() + ">");
            } else{
                buffer.append(((Literal)pred.getTo()).getValue());
            }
            buffer.append("\"");

            for(final Class metric: scoredMap.keySet()){
                final HashMap<Predicate, Double> scoresForMetric = scoredMap.get(metric);
                Double score = scoresForMetric.get(pred);
                buffer.append('\t');
                buffer.append(""+score);
            }

            buffer.append("\n");

            bufferedWriter.write(buffer.toString());
        }
        bufferedWriter.close();
    }
}
