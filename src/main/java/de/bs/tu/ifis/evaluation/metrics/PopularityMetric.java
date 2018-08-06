package de.bs.tu.ifis.evaluation.metrics;

import de.bs.tu.ifis.analysis.GraphAnalysis;
import de.bs.tu.ifis.analysis.model.EntityAnalysis;
import de.bs.tu.ifis.analysis.model.PredicateAnalysis;
import de.bs.tu.ifis.evaluation.Metric;
import de.bs.tu.ifis.model.Entity;
import de.bs.tu.ifis.model.Predicate;

public class PopularityMetric extends Metric {
	

	@Override
	public double computeScore(Predicate predicate, EntityAnalysis entityFromA, PredicateAnalysis predicateA, EntityAnalysis entityToA, GraphAnalysis analysis) {
		if (entityToA != null && entityToA.getIncomingPredicates() != null && entityToA.getIncomingPredicates() > 0) {
			return Math.log(entityToA.getIncomingPredicates());
		}


		return 0.0;
    }

	@Override
	public String getMetricName() {
		return "Popularity";
	}
}
