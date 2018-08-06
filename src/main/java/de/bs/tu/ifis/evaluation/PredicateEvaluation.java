package de.bs.tu.ifis.evaluation;

import de.bs.tu.ifis.model.Predicate;

public class PredicateEvaluation implements Comparable<PredicateEvaluation>{
    private final Predicate predicate;
    private final double score;

    public PredicateEvaluation(final Predicate predicate, final double score){
        this.predicate = predicate;
        this.score = score;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public double getScore() {
        return score;
    }

    @Override
    public int compareTo(PredicateEvaluation o) {
        return Double.compare(o.score, this.score);
    }
}
