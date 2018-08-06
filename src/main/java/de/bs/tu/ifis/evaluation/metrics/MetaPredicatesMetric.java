package de.bs.tu.ifis.evaluation.metrics;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import de.bs.tu.ifis.analysis.GraphAnalysis;
import de.bs.tu.ifis.analysis.model.EntityAnalysis;
import de.bs.tu.ifis.analysis.model.PredicateAnalysis;
import de.bs.tu.ifis.evaluation.Metric;
import de.bs.tu.ifis.model.Predicate;

public class MetaPredicatesMetric extends Metric {
	private final List<String> metaList = new LinkedList<>();


	public MetaPredicatesMetric(){
		metaList.add("http://www.w3.org/1999/02/22-rdf-syntax");
		metaList.add("http://www.w3.org/2000/01/rdf-schema");

		//metaList.add("http://dbpedia.org/ontology/*");
	}


	public double isMeta(Predicate pred) {
		for(final String metaRule: metaList){
			if(pred.getName().startsWith(metaRule)){
				return 1.0;
			}
		}
		return 0.0;
	}
	
	@Override
	public double computeScore(Predicate predicate, EntityAnalysis entityFromA, PredicateAnalysis predicateA, EntityAnalysis entityToA, GraphAnalysis analysis) {
		return isMeta(predicate);
    }

	@Override
	public String getMetricName() {
		return "MetaPredicates";
	}
}
