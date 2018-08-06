package de.bs.tu.ifis.analysis.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;

@XmlRootElement
public class GraphAnalysisCache {
    @XmlElement(name="entites")
    public HashMap<String, EntityAnalysis> entityAnalysisIndex = new HashMap<>();
    @XmlElement(name="predications")
    public HashMap<String, PredicateAnalysis> predicateAnalysisIndex = new HashMap<>();

    public void clearCaches(){
        entityAnalysisIndex.clear();
        predicateAnalysisIndex.clear();
    }


    public EntityAnalysis findOrCreateEntityAnalysis(final String entity){
        EntityAnalysis entityAnalysis = entityAnalysisIndex.get(entity);
        if(entityAnalysis == null){
            entityAnalysis = new EntityAnalysis();
            entityAnalysisIndex.put(entity, entityAnalysis);
        }
        return entityAnalysis;
    }

    public PredicateAnalysis findOrCreatePredicateAnalysis(final String predicate){
        PredicateAnalysis predicateAnalysis = predicateAnalysisIndex.get(predicate);
        if(predicateAnalysis == null){
            predicateAnalysis = new PredicateAnalysis();
            predicateAnalysisIndex.put(predicate, predicateAnalysis);
        }
        return predicateAnalysis;
    }


}
