package de.bs.tu.ifis.evaluation;

import de.bs.tu.ifis.model.Predicate;

import java.util.HashMap;
import java.util.List;

public abstract class EvaluationStrategy {



    public abstract List<Predicate> rankPredicate(final List<Predicate> predicates, final HashMap<Class, HashMap<Predicate, Double>> scoredPredicatesForMetric);
}
