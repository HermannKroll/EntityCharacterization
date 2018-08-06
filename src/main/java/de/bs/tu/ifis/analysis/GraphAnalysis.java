package de.bs.tu.ifis.analysis;

import de.bs.tu.ifis.analysis.model.EntityAnalysis;
import de.bs.tu.ifis.analysis.model.GraphAnalysisCache;
import de.bs.tu.ifis.analysis.model.PredicateAnalysis;
import de.bs.tu.ifis.database.VirtuosoDB;
import de.bs.tu.ifis.model.Entity;
import de.bs.tu.ifis.model.Literal;
import de.bs.tu.ifis.model.Predicate;
import de.bs.tu.ifis.model.graph.Graph;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;

public class GraphAnalysis {
    private final Logger logger = LogManager.getLogger();
    private final VirtuosoDB virtuosoDB;
    private final String graph;

    private GraphAnalysisCache cache = new GraphAnalysisCache();

    private Graph ontology = null;
    private Entity ontologyRoot = null;
    private HashMap<String, Double> pagerankScoreIndex = null;

    public GraphAnalysis(final VirtuosoDB virtuosoDB, final String graph){
        this.virtuosoDB = virtuosoDB;
        this.graph = graph;
    }

    

    public void clearCache(){
        cache.clearCaches();
    }




    /**
     * computes the amount for each predicate of the entity (predicate name is only used once)
     * uses caching if predicate statistics are already queried
     * @param entity entity which predicates should be analyzed
     * @return a hashmap containing the predicate names with a amount for each predicate
     */
    public HashMap<String, Long> countPredicateTypesAmountsForEntity(final Entity entity){
        final HashMap<String, Long> predicateTypes = new HashMap<>();
        for(final Predicate predicate : entity.getPredicates()){
            //If occurrences are already cached -> use it
            PredicateAnalysis predicationAnalysis = cache.predicateAnalysisIndex.get(predicate.getName());
            if(predicationAnalysis == null){
                predicationAnalysis = new PredicateAnalysis();
                cache.predicateAnalysisIndex.put(predicate.getName(), predicationAnalysis);
            }

            if(predicationAnalysis.getAmountInDatabase() != null){
                predicateTypes.put(predicate.getName(), predicationAnalysis.getAmountInDatabase());
            }
            else {
                //Fetch from database
                long amount = virtuosoDB.countPredicateTypeInDatabase(predicate.getName(), graph);
                predicateTypes.put(predicate.getName(), amount);

                // Add to cache
                predicationAnalysis.setAmountInDatabase(amount);

            }
        }
        return predicateTypes;
    }


    /**
     * computes the occurrences of a specific predicate type
     * @param predicate predicate
     * @return the amount
     */
    public long countPredicateTypeOccurences(final String predicate){
        final PredicateAnalysis predicationAnalysis =  cache.findOrCreatePredicateAnalysis(predicate);

        //Test if amount is cached
        Long amount = predicationAnalysis.getAmountInDatabase();
        if(amount != null){
            return amount;
        }
        //Not cached - query it
        amount = virtuosoDB.countPredicateTypeInDatabase(predicate, graph);
        //Cache it
        predicationAnalysis.setAmountInDatabase(amount);
        return amount;
    }

    /**
     * computes the amount of incoming predicates for an entity
     * uses caching
     * @param entity entity
     * @return the amount
     */
    public long countEntityIncomingPredicates(final String entity){
        final EntityAnalysis entityAnalysis = cache.findOrCreateEntityAnalysis(entity);

        //Test if amount is cached
        Long amount = entityAnalysis.getIncomingPredicates();
        if(amount != null){
            return amount;
        }
        //Not cached - query it
        amount = virtuosoDB.countEntityIncomingPredicates(entity, graph);
        //Cache it
        entityAnalysis.setIncomingPredicates(amount);
        return amount;
    }

    /**
     * computes the amount of incoming predicates for a literal
     * uses caching
     * @param literal literal
     * @return the amount
     */
    public long countLiteralIncomingPredicates(final String literal){
        final EntityAnalysis entityAnalysis = cache.findOrCreateEntityAnalysis(literal);

        //Test if amount is cached
        Long amount = entityAnalysis.getIncomingPredicates();
        if(amount != null){
            return amount;
        }
        //Not cached - query it
        amount = virtuosoDB.countLiteralIncomingPredicates(literal, graph);
        //Cache it
        entityAnalysis.setIncomingPredicates(amount);
        return amount;
    }

    /**
     * computes the amount of outgoing predicates for an entity
     * uses caching
     * @param entity entity
     * @return the amount
     */
    public long countEntityOutgoingPredicates(final String entity){
        final EntityAnalysis entityAnalysis = cache.findOrCreateEntityAnalysis(entity);

        //Test if amount is cached
        Long amount = entityAnalysis.getOutgoingPredicates();
        if(amount != null){
            return amount;
        }
        //Not cached - query it
        amount = virtuosoDB.countEntityOutgoingPredicates(entity, graph);
        //Cache it
        entityAnalysis.setOutgoingPredicates(amount);
        return amount;
    }


    /**
     * Count the amount of incoming and outgoing edges for the neighbourhood entities to which this
     * entity is connected to
     * @param entity source entity which neighbourhood entites should be analyzed
     */
    public void countPredicatesForNeighbourhoodEntities(final Entity entity){
        for(final Predicate pred : entity.getPredicates()){
            if(pred.getTo() instanceof Entity){
                final Entity to = (Entity)pred.getTo();
                countEntityIncomingPredicates(to.getName());
                countEntityOutgoingPredicates(to.getName());
            }
        }


    }


    public long computeOntologyDepth(final String entity){
        //Is in cache?
        final EntityAnalysis entityAnalysis = cache.findOrCreateEntityAnalysis(entity);
        if(entityAnalysis.getOntologyDepth() != null){
            return entityAnalysis.getOntologyDepth();
        }
        //Ontology is set?
        if(ontology != null) {
           //Find entity node in ontology with same name
            final Entity onE1 = ontology.findEntity(entity);
            if(onE1 != null){
                //Compute Path between ontology root and node
                long pathLength = GraphTools.calculateShortestDistanceBetween(ontologyRoot, onE1);
                entityAnalysis.setOntologyDepth(pathLength);
                return pathLength;
            }
        }
        return -1;
    }


    public long computeOntologySubtreeSize(final String entity){
        //Is in cache?
        final EntityAnalysis entityAnalysis = cache.findOrCreateEntityAnalysis(entity);
        if(entityAnalysis.getSubtreeSize() != null){
            return entityAnalysis.getSubtreeSize();
        }
        //Ontology is set?
        if(ontology != null) {
            //Find entity node in ontology with same name
            final Entity onE1 = ontology.findEntity(entity);
            if(onE1 != null){
                //Compute Path between ontology root and node
                long pathLength = GraphTools.subtreeSize(onE1);
                entityAnalysis.setSubtreeSize(pathLength);
                return pathLength;
            }
        }
        return -1;
    }

    /**
     * computes the PageRank Score for an entty
     * uses cache
     * @param entity entity
     * @return PageRank Score
     */
    public double computePagerankScore(final String entity){
        //Is in cache?
        final EntityAnalysis entityAnalysis = cache.findOrCreateEntityAnalysis(entity);
        if(entityAnalysis.getPagerankScore() != null){
            return entityAnalysis.getPagerankScore();
        }
        //Ontology is set?
        if(pagerankScoreIndex != null) {
            Double score = pagerankScoreIndex.get(entity);
            if(score == null){
                return -1;
            }

            entityAnalysis.setPagerankScore(score);
            return score;
        }
        return -1;
    }

    /**
     * performs a full analysis for the entites
     * @param entityList given entites
     */
    public void performFullAnalysis(final List<Entity> entityList){
        for(final Entity ent : entityList){
            //Entity analysis
            this.countPredicateTypeOccurences(ent.getName());
            this.countEntityIncomingPredicates(ent.getName());
            this.countEntityOutgoingPredicates(ent.getName());

            this.computeOntologyDepth(ent.getName());
            this.computeOntologySubtreeSize(ent.getName());
            //this.computePagerankScore(ent.getName());

            for(final Predicate pre : ent.getPredicates()){
                //Pred analysis
                this.countPredicateTypeOccurences(pre.getName());

                if(pre.getTo() instanceof Entity) {
                    final Entity to = (Entity) pre.getTo();
                    //Entity "to" analysis
                    this.countEntityIncomingPredicates(to.getName());
                    this.countEntityOutgoingPredicates(to.getName());

                    this.computeOntologyDepth(to.getName());
                    this.computeOntologySubtreeSize(to.getName());
                    //this.computePagerankScore(to.getName());
                }
                if(pre.getTo() instanceof Literal){
                    final Literal lit = (Literal)pre.getTo();
                    this.countLiteralIncomingPredicates(lit.getValue());
                }
            }
        }
    }

    public void enableOnotologyAnalysis(final Graph ontology, final Entity ontologyRoot){
        this.ontology = ontology;
        this.ontologyRoot = ontologyRoot;
    }

    public void enablePagerankScoreAnalysis(final HashMap<String, Double> pagerankScoreIndex){
        this.pagerankScoreIndex = pagerankScoreIndex;
    }

    public String computeEntityWithAnalysis(final Entity entity){
        final StringBuffer buffer = new StringBuffer();
        //buffer.append("Entity{" + "name='" + entity.getName() + '\'' + '}');
        buffer.append(entity.toString());
        for(final Predicate pre : entity.getPredicates()){
            buffer.append('\n');
            buffer.append("\t\t\t\t");
            buffer.append(pre.getName() + " [amount=" + countPredicateTypeOccurences(pre.getName()) + "] --->  ");

            if(pre.getTo() instanceof Entity){
                final Entity to = (Entity)pre.getTo();
                long incomingEdgesForE = this.countEntityIncomingPredicates(to.getName());
                long outgoingEdgesForE = this.countEntityOutgoingPredicates(to.getName());
                long ontologyDepth = this.computeOntologyDepth(to.getName());
                long ontologySubtreeSize = this.computeOntologySubtreeSize(to.getName());

                buffer.append(to.getName() + " [incPre = " + incomingEdgesForE + " , outPre=" + outgoingEdgesForE);
                if(ontologyDepth != -1) {
                    buffer.append(", ontDepth="+ ontologyDepth + ", onSubtreeSize=" + ontologySubtreeSize);
                }
                double pagerank = this.computePagerankScore(to.getName());
                if(pagerank > 0.0){
                    buffer.append(", pagerank=" + String.format("%1.2f", pagerank));
                }

                buffer.append("]");

            } else {
                buffer.append(pre.getTo());
            }
        }

        return buffer.toString();
    }


    public GraphAnalysisCache getCache() {
        return cache;
    }

    public void setCache(GraphAnalysisCache cache) {
        this.cache = cache;
    }
}
