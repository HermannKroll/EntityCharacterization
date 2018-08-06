package de.bs.tu.ifis.evaluation;

import de.bs.tu.ifis.analysis.GraphAnalysis;
import de.bs.tu.ifis.analysis.model.EntityAnalysis;
import de.bs.tu.ifis.analysis.model.PredicateAnalysis;
import de.bs.tu.ifis.model.Predicate;

/**
 * abstract class for a Metric to compute scores on predicates
 * @author Hermann Kroll
 */
public abstract class Metric {

    /**
     * Computes a score between 0..1 for a predicate
     * @param predicate choosen predicate
     * @param entityFromA entity analysis for from entity
     * @param predicateA predicate analysis for the predicate
     * @param entityToA entity analysis for to entity
     * @param analysis graph analysis for the graph in which the predicate is contained
     * @return a score between 0 and 1
     */
    public abstract double computeScore(final Predicate predicate, final EntityAnalysis entityFromA,
                                        final PredicateAnalysis predicateA, final EntityAnalysis entityToA,
                                        GraphAnalysis analysis);




    public abstract String getMetricName();
}
