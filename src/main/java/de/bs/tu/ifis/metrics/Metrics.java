package de.bs.tu.ifis.metrics;

import java.util.HashMap;
import java.util.Map;

import de.bs.tu.ifis.model.*;
import de.bs.tu.ifis.model.graph.*;

public class Metrics {

	private static HashMap<Node, Double> prHash;
	
	public static void pageRank(Entity e, int ingoingEdges) {
		double dampingFactor = 0.85;
		double newValue;
		initPR(e, dampingFactor);
		for (Map.Entry<Node, Double> entry : prHash.entrySet()) {
			newValue = (1 - dampingFactor) + dampingFactor * getPRSum(e) / ingoingEdges;
			entry.setValue(newValue);
		}
	}
	
	public static double getPRSum(Entity e) {
		double sum = 0;
		for (Predicate pred : e.getPredicates()) {
			if (pred.getTo().equals(e)) {
				sum += prHash.get(pred.getFrom());
			}
		}
		return sum;
	}
	
	public static void initPR(Entity e, double dampingFactor) {
		double value = 1 - dampingFactor;
		prHash.put(e, value);
		for (Predicate pred : e.getPredicates()) {
			if (pred.getTo().equals(e)) {
				prHash.put(pred.getTo(), value);
			}
		}
	}
	
	public HashMap<Node, Double> getPRHash() {
		return prHash;
	}
}
