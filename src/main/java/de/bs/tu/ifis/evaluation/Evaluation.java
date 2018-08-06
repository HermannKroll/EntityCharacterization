package de.bs.tu.ifis.evaluation;

import de.bs.tu.ifis.analysis.GraphAnalysis;
import de.bs.tu.ifis.analysis.model.EntityAnalysis;
import de.bs.tu.ifis.analysis.model.PredicateAnalysis;
import de.bs.tu.ifis.evaluation.metrics.*;
import de.bs.tu.ifis.model.Entity;
import de.bs.tu.ifis.model.Predicate;

import java.util.*;

/**
 * Easy evaluation with multiple metrics on predicates
 * @author Hermann Kroll
 */
public class Evaluation {
    private final List<Metric> metrics = new LinkedList<>();
    private final GraphAnalysis analysis;
    private HashMap<Class, HashMap<Predicate, Double>> scoredPredicatesForMetric = new HashMap<>();



    public Evaluation(final GraphAnalysis analysis){
        this.analysis = analysis;

        metrics.add(new FreqRarMetric());
        metrics.add(new FrequencyMetric());
        metrics.add(new MetaPredicatesMetric());
        metrics.add(new PopularityMetric());
        metrics.add(new RarityMetric());
        metrics.add(new OntologyDepthMetric());
    }


    /**
     * adds a new metric for the evaluation
     * @param m metric to add
     */
    public void addMetricForEvaluation(final Metric m){
        metrics.add(m);
    }

    /**
     * computes for each added metric the score in form of a treemap for the predicates
     * @param predicates for which the scores should be computed
     */
    public void computePredicateScores(final List<Predicate> predicates) {
        //Clear all computed scores
        scoredPredicatesForMetric.clear();

        //Compute a treemap for each metric
        for(final Metric m : metrics) {
            final HashMap<Predicate, Double> predicateScores = new HashMap<>();
            for (final Predicate p : predicates) {
                final EntityAnalysis fromA = analysis.getCache().entityAnalysisIndex.get(((Entity)p.getFrom()).getName());
                final PredicateAnalysis predA = analysis.getCache().predicateAnalysisIndex.get(p.getName());
                EntityAnalysis toA = null;
                if(p.getTo() instanceof Entity) {
                    toA = analysis.getCache().entityAnalysisIndex.get(((Entity) p.getTo()).getName());
                }

                predicateScores.put(p, m.computeScore(p,fromA,predA,toA,analysis));
            }
            scoredPredicatesForMetric.put(m.getClass(), predicateScores);
        }
    }


    /**
     * gets the scores for the precicates for a choosen metric type
     * @param metricClass class of the metric
     * @return TreeMap of the computed scores for the predicates with the metric
     */
    public HashMap<Predicate, Double> getPredicateScoreMapForMetric(final Class metricClass){
        return scoredPredicatesForMetric.get(metricClass);
    }


    public HashMap<Class, HashMap<Predicate, Double>> getScoredPredicatesForMetric() {
        return scoredPredicatesForMetric;
    }

    public List<Predicate> getPredicatesSortedByScore(final Class metricClass){
        final HashMap<Predicate, Double> sortedScoredPredicates = this.getPredicateScoreMapForMetric(metricClass);
        final List<PredicateEvaluation> bestEntries = new LinkedList<>();
        for(final Map.Entry<Predicate, Double> e : sortedScoredPredicates.entrySet()){
            bestEntries.add(new PredicateEvaluation(e.getKey(), e.getValue()));
        }
        //Sort List
        Collections.sort(bestEntries);

        final List<Predicate> bestPredicates = new LinkedList<>();
        for(final PredicateEvaluation pe: bestEntries){
            bestPredicates.add(pe.getPredicate());
        }
        return bestPredicates;
    }

}
