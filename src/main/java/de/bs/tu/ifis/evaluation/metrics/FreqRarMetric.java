package de.bs.tu.ifis.evaluation.metrics;

import de.bs.tu.ifis.analysis.GraphAnalysis;
import de.bs.tu.ifis.analysis.model.EntityAnalysis;
import de.bs.tu.ifis.analysis.model.PredicateAnalysis;
import de.bs.tu.ifis.evaluation.Metric;
import de.bs.tu.ifis.model.Predicate;

public class FreqRarMetric extends Metric {
	
	//computes product of the FrequencyMetric and RarityMetric metrics
	@Override
	public double computeScore(Predicate predicate, EntityAnalysis entityFromA, PredicateAnalysis predicateA, EntityAnalysis entityToA, GraphAnalysis analysis) {
		FrequencyMetric freq = new FrequencyMetric();
		RarityMetric rar = new RarityMetric();
		return freq.computeScore(predicate, entityFromA, predicateA, entityToA, analysis) * rar.computeScore(predicate, entityFromA, predicateA, entityToA, analysis);
    }


	@Override
	public String getMetricName() {
		return "FreqRar";
	}
}
