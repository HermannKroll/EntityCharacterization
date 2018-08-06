package de.bs.tu.ifis.evaluation.metrics;

import de.bs.tu.ifis.analysis.GraphAnalysis;
import de.bs.tu.ifis.analysis.model.EntityAnalysis;
import de.bs.tu.ifis.analysis.model.PredicateAnalysis;
import de.bs.tu.ifis.evaluation.Metric;
import de.bs.tu.ifis.model.Predicate;

public class OntologyDepthMetric extends Metric {

    @Override
    public double computeScore(Predicate predicate, EntityAnalysis entityFromA, PredicateAnalysis predicateA, EntityAnalysis entityToA, GraphAnalysis analysis) {
        if(entityToA != null && entityToA.getOntologyDepth() != null && entityToA.getOntologyDepth() > 0)
            return entityToA.getOntologyDepth();
        return 0;
    }

    @Override
    public String getMetricName() {
        return "Ontology Depth";
    }
}
