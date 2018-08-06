package de.bs.tu.ifis.evaluation.metrics;

import de.bs.tu.ifis.analysis.GraphAnalysis;
import de.bs.tu.ifis.analysis.model.EntityAnalysis;
import de.bs.tu.ifis.analysis.model.PredicateAnalysis;
import de.bs.tu.ifis.evaluation.Metric;
import de.bs.tu.ifis.model.Predicate;
import de.bs.tu.ifis.model.graph.Edge;

public class ExclusivityMetric extends Metric{

	//Checks for amount of predicates with same label as pred around incident nodes
	public double computeExc(Predicate pred) {
		int occurrence = 0;
		for (Predicate adjPred : pred.getEntityFrom().getPredicates()) {
			if (adjPred.getName().equals(pred)) {
				occurrence++;
			}
		}
		if (pred.getEntityTo() != null) {
			for (Predicate adjPred : pred.getEntityTo().getPredicates()) {
				if (adjPred.getName().equals(pred)) {
					occurrence++;
				}
			}
		}
		if (occurrence == 0) {
			return 1.0;
		}
		return 1.0 / occurrence;
	}

	@Override
	public double computeScore(Predicate predicate, EntityAnalysis entityFromA, PredicateAnalysis predicateA, EntityAnalysis entityToA, GraphAnalysis analysis) {
		return computeExc(predicate); //Placeholder
	}

	@Override
	public String getMetricName() {
		return "Exclusivity";
	}
}
