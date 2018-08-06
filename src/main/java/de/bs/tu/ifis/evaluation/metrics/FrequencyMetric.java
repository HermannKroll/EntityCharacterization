package de.bs.tu.ifis.evaluation.metrics;

import de.bs.tu.ifis.Const;
import de.bs.tu.ifis.analysis.GraphAnalysis;
import de.bs.tu.ifis.analysis.model.EntityAnalysis;
import de.bs.tu.ifis.analysis.model.PredicateAnalysis;
import de.bs.tu.ifis.evaluation.Metric;
import de.bs.tu.ifis.model.Predicate;

public class FrequencyMetric extends Metric {
	
	//counts occurences of a predicate in the whole database
	@Override
	public double computeScore(Predicate predicate, EntityAnalysis entityFromA, PredicateAnalysis predicateA, EntityAnalysis entityToA, GraphAnalysis analysis) {
		return analysis.countPredicateTypeOccurences(predicate.getName()) / (double)Const.DBPEDIA_PREDICATE_SIZE;
    }

	@Override
	public String getMetricName() {
		return "Frequency";
	}
}
