package de.bs.tu.ifis.evaluation.strategies;

import de.bs.tu.ifis.evaluation.EvaluationStrategy;
import de.bs.tu.ifis.evaluation.metrics.*;
import de.bs.tu.ifis.model.Predicate;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;

import java.util.*;
import java.util.stream.Collectors;

/**
 * BAFREC implementation
 * ranks all predicates with the introduced BAFREC strategy
 * 1. categorize predicates into meta and data information
 * 2. rank both categorizes
 * 3. pick with weighted round robin
 * @author Hermann Kroll
 */
public class BAFRECStrategy extends EvaluationStrategy {
    private static final int META_DATA_RATIO = 3;

    private HashMap<Class, HashMap<Predicate, Double>> scoredPredicatesForMetric = null;
    private List<Predicate> predicates = null;


    private List<Predicate> metaPredicates = new LinkedList<>();
    private HashMap<String, List<Predicate>> metaPredicatesGroupByPredicate = new HashMap<>();
    private List<Predicate> dataPredicates = new LinkedList<>();
    private HashMap<String, List<Predicate>> dataPredicatesGroupByPredicate = new HashMap<>();

    private List<Predicate> metaPredicatesRanked = new LinkedList<>();
    private List<Predicate> dataPredicatesRanked = new LinkedList<>();

    private static ILexicalDatabase db = new NictWordNet();
    public BAFRECStrategy(){
        WS4JConfiguration.getInstance().setMFS(true);
    }


    /**
     * resets all internal data structures
     */
    private void clearData(){
        metaPredicates.clear();
        metaPredicatesGroupByPredicate.clear();
        dataPredicates.clear();
        dataPredicatesGroupByPredicate.clear();

        metaPredicatesRanked.clear();
        dataPredicatesRanked.clear();
    }

    /**
     * adds an predicate regarding its group to the specific list or creates a new list, if the group does not exists
     * @param p predicate
     * @param map map to add
     */
    private void addPredicateToHashmapList(final Predicate p, final HashMap<String, List<Predicate>> map){
        List<Predicate> predicates = map.get(p.getName());
        if(predicates == null){
            predicates = new LinkedList<>();
            predicates.add(p);
            map.put(p.getName(), predicates);
        } else{
            predicates.add(p);
        }
    }

    /**
     * categorize the predicates into meta and data information
     */
    private void categorizePredicates(){
        for(final Predicate pred: predicates){
            final double metascore = scoredPredicatesForMetric.get(MetaPredicatesMetric.class).get(pred);
            //is meta edge
            if(metascore > 0.8){
                metaPredicates.add(pred);
                addPredicateToHashmapList(pred, metaPredicatesGroupByPredicate);
            }
            //no meta edge
            else{
                dataPredicates.add(pred);
                addPredicateToHashmapList(pred, dataPredicatesGroupByPredicate);
            }
        }
    }


    /**
     * ranks the meta information
     * 1. first all facts are grouped by their predicate
     * 2. groups are ranked by frequency
     * 3. select for each group the predicate with largest ontology depth or rarity
     * 4. add selected facts into meta result
     * 5. go to step 1 untill all facts are selected
     */
    private void rankMetaPredicates(){
        //Repeat until every meta predicate is ranked
        while(metaPredicatesRanked.size() != metaPredicates.size()) {
            final List<Predicate> bestEntries = new LinkedList<>();
            final HashMap<Predicate, Double> predScores = new HashMap<>();
            for (final String key : metaPredicatesGroupByPredicate.keySet()) {
                for (final Predicate p : metaPredicatesGroupByPredicate.get(key)) {
                    if(metaPredicatesRanked.contains(p))
                        continue;

                    double ontDepth = scoredPredicatesForMetric.get(OntologyDepthMetric.class).get(p);
                    //double ontSize = scoredPredicatesForMetric.get(OntologySubtreeSizeMetric.class).get(p);

                    if (ontDepth >= 1.0)
                        predScores.put(p, ontDepth);
                    else {
                        double rarity = scoredPredicatesForMetric.get(RarityMetric.class).get(p);
                        predScores.put(p, rarity);
                    }
                }
                List<Map.Entry<Predicate, Double>> ranked = predScores.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.toList());
                if(ranked.size() > 0) {
                    final Map.Entry<Predicate, Double> best = ranked.get(0);
                    bestEntries.add(best.getKey());
                }
            }
            predScores.clear();
            for(final Predicate pred : bestEntries) {
                double frequency = scoredPredicatesForMetric.get(FrequencyMetric.class).get(pred);
                predScores.put(pred, frequency);
            }
            List<Map.Entry<Predicate, Double>> ranked = predScores.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.toList());
            for (final Map.Entry<Predicate, Double> entry : ranked) {
                metaPredicatesRanked.add(entry.getKey());
            }
        }
    }


    /**
     * tokenize a combined predicate into its words
     * splits at capital letters
     * @param predicate predicate to split
     * @return
     */
    private String[] tokenizeWords(final String predicate){
        return predicate.split("(?=\\p{Lu})");
    }


    /**
     * implementation of the introduced wordnet similarity based on ws4j
     * @param p1 first predicate
     * @param p2 second predicate
     * @return a similarity of the predicate concept between 0 and 1
     */
    private double simWordnet(final Predicate p1, final Predicate p2){
        final String[] split1 = p1.getName().split("/");
        final String[] split2 = p2.getName().split("/");

        final String word1 = split1[split1.length-1];
        final String word2 = split2[split2.length-1];

        if(word1.equals(word2)){
            return 1.0;
        }

        // if words are concatenated like "broadcast area"
        final String[] words1 = tokenizeWords(word1);
        final String[] words2 = tokenizeWords(word2);
        // compute average word sim, if multiple words are contained
        if(words1.length > 1 || words2.length > 1){
            double summedSim = 0.0;
            int count = 0;
            for(final String w1: words1){
                for(final String w2: words2){
                    final String w1small = w1.toLowerCase();
                    final String w2small = w2.toLowerCase();
                    if(w1small.equals(w2small))
                        summedSim += 1.0;
                    else
                        summedSim += new WuPalmer(db).calcRelatednessOfWords(w1small, w2small);
                    count++;
                }
            }
            summedSim = summedSim / count;
            return summedSim;
        }

        // single words -> single sim
        double s = new WuPalmer(db).calcRelatednessOfWords(word1, word2);

        return s;
    }

    /**
     * scores a single data predicate with the popularity metric
     * @param p a predicate to score
     * @return a score
     */
    private double scoreDataPredicate(final Predicate p){
        return scoredPredicatesForMetric.get(PopularityMetric.class).get(p);
    }


    /**
     *
     */
    private void rankDataPredicates(){
        if(dataPredicates.size() == 0)
            return;

        final HashMap<Predicate, HashMap<Predicate, Double>> simWordnetBetweenPredicates = new HashMap<>();
        for(final Predicate p1: dataPredicates){
            simWordnetBetweenPredicates.put(p1, new HashMap<>());
            for(final Predicate p2: dataPredicates){
                double simWordnet = simWordnet(p1,p2);
                simWordnetBetweenPredicates.get(p1).put(p2, simWordnet);
            }
        }

        final HashMap<Predicate, Double> predScores = new HashMap<>();
        Predicate bestPredicate = null;
        double score = -1.0;
        for(final Predicate p1: dataPredicates){
            double newScore = scoreDataPredicate(p1);
            predScores.put(p1, newScore);
            if(newScore > score){
                score = newScore;
                bestPredicate = p1;
            }
        }
        dataPredicatesRanked.add(bestPredicate);

        final List<Predicate> notSelected = new LinkedList<>(dataPredicates);
        notSelected.remove(bestPredicate);
        while(!notSelected.isEmpty()){
            for(Predicate current : notSelected) {
                double wordNetSum = 0;
                int count = 0;
                for(final Predicate p1 : dataPredicatesRanked){
                    wordNetSum += simWordnetBetweenPredicates.get(current).get(p1);
                    count++;
                }
                wordNetSum = wordNetSum / count;

                double wordnetInverse = (1.0 - wordNetSum);
                double scoreWithWordnet =  wordnetInverse * predScores.get(current);
                predScores.put(current, scoreWithWordnet);
            }

            score = -1.0;
            bestPredicate = null;
            for(final Predicate p1: notSelected){
                double newScore = predScores.get(p1);
                if(newScore > score){
                    score = newScore;
                    bestPredicate = p1;
                }
            }
            //scores are all ~0 for the not selected facts -> choose different metric
            if(score < 0.01){
                for(Predicate current : notSelected) {
                    predScores.put(current, scoredPredicatesForMetric.get(FreqRarMetric.class).get(current));
                }
            }

            score = -1.0;
            bestPredicate = null;
            for(final Predicate p1: notSelected){
                double newScore = predScores.get(p1);
                if(newScore > score){
                    score = newScore;
                    bestPredicate = p1;
                }
            }

            notSelected.remove(bestPredicate);
            dataPredicatesRanked.add(bestPredicate);
        }

    }

    /**
     * ranks all predicates with the introduced BAFREC strategy
     * 1. categorize predicates into meta and data information
     * 2. rank both categorizes
     * 3. pick with weighted round robin
     * @param predicates given set of predicates to rank
     * @param scoredPredicatesForMetric hashmap in which all predicates should be scored via the necessary metrics
     * @return
     */
    @Override
    public List<Predicate> rankPredicate(List<Predicate> predicates, HashMap<Class, HashMap<Predicate, Double>> scoredPredicatesForMetric) {
        //Reset all internal datastructres
        clearData();
        this.predicates = predicates;
        this.scoredPredicatesForMetric = scoredPredicatesForMetric;

        //First sort the predicates into categories
        categorizePredicates();

        //Rank meta & data predicates
        rankMetaPredicates();
        rankDataPredicates();

        //Result set
        final List<Predicate> bestPredicates = new LinkedList<>();
        // Do weighted round robin pick
        long resultSize = predicates.size();
        while(bestPredicates.size() != resultSize){
            //First pick a meta information
            if(metaPredicatesRanked.size() > 0) {
                bestPredicates.add(metaPredicatesRanked.get(0));
                metaPredicatesRanked.remove(0);


                if(bestPredicates.size() == resultSize)
                    break;
            }
            //Next pick the amount of meta facts given by the parameter
            for(int i = 0; i < META_DATA_RATIO; i++) {
                if(dataPredicatesRanked.size() > 0) {
                    bestPredicates.add(dataPredicatesRanked.get(0));
                    dataPredicatesRanked.remove(0);

                    if(bestPredicates.size() == resultSize)
                        break;
                }
            }
        }

        return bestPredicates;
    }
}
