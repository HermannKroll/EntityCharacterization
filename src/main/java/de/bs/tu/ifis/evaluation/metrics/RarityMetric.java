package de.bs.tu.ifis.evaluation.metrics;

import de.bs.tu.ifis.analysis.GraphAnalysis;
import de.bs.tu.ifis.analysis.model.EntityAnalysis;
import de.bs.tu.ifis.analysis.model.PredicateAnalysis;
import de.bs.tu.ifis.evaluation.Metric;
import de.bs.tu.ifis.model.Entity;
import de.bs.tu.ifis.model.Predicate;

public class RarityMetric extends Metric {
	
	//counts ingoing edges of an entity
	@Override
	public double computeScore(Predicate predicate, EntityAnalysis entityFromA, PredicateAnalysis predicateA, EntityAnalysis entityToA, GraphAnalysis analysis) {
		if(entityToA == null)
			return 0.0;
		if(entityToA.getIncomingPredicates() == null || entityToA.getIncomingPredicates() == 0)
			return 0.0;

		return 1.0 / (double)entityToA.getIncomingPredicates();
    }

	@Override
	public String getMetricName() {
		return "Rarity";
	}
}
