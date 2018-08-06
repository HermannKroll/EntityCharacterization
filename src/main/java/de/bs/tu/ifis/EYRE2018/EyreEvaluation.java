package de.bs.tu.ifis.EYRE2018;

import de.bs.tu.ifis.Config;
import de.bs.tu.ifis.IO.*;
import de.bs.tu.ifis.analysis.GraphAnalysis;
import de.bs.tu.ifis.database.VirtuosoDB;
import de.bs.tu.ifis.evaluation.Evaluation;
import de.bs.tu.ifis.evaluation.EvaluationStrategy;
import de.bs.tu.ifis.model.Entity;
import de.bs.tu.ifis.model.Predicate;
import de.bs.tu.ifis.model.graph.Graph;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class EyreEvaluation {
    private final Logger logger = LogManager.getLogger();

    private List<EyreEntity> entityList = null;
    private final List<Entity> dbpediaEntites = new LinkedList<>();
    private final List<Entity> lmdbEntites = new LinkedList<>();


    private VirtuosoDB virtuosoDB;
    private Graph ontologyDBpedia;
    private GraphAnalysis analysisDBpedia;
    private GraphAnalysis analysisLMDB;

    private void loadEntites(){
        logger.info("Read entity list from " + Config.EYRE_ENTITY_LIST);
        final EyreEntityListReader entityListReader = new EyreEntityListReader();
        entityList = entityListReader.readEntityListFromFile(Config.EYRE_ENTITY_LIST, true);

        // Sort entites in dbpedia and lmdb
        for(final EyreEntity entity : entityList){
            if (entity.getGraph().contains("dbpedia")) {
                dbpediaEntites.add(entity);
            }
            if(entity.getGraph().contains("linked")){
                lmdbEntites.add(entity);
            }

        }

    }

    private Graph readOnotology(final String filename){
        logger.info("Read ontology from file: " + filename);
        NTTripleReader reader = new NTTripleReader();
        try {
            reader.readPredicatesFromNTFile(filename);
            logger.info("Ontology loaded!");
            return reader.getGraph();
        } catch (IOException ex){
            logger.error("Error while reading ontology file because:" + ex );
            ex.printStackTrace();
        }
        return null;
    }


    private void performAnalysis(){
        analysisDBpedia = new GraphAnalysis(virtuosoDB, "<http://dbpedia.org>");
        analysisLMDB = new GraphAnalysis(virtuosoDB, "<http://linkedmdb.org#>");

        if(Config.CACHE_ANALYSIS_RECREATE){
            logger.info("Analysis Cache should be used...");
            File cacheDir = new File(Config.CACHE_DIRECTORY);
            if(!cacheDir.exists()){
                logger.info("Create cache directory...");
                cacheDir.mkdir();
            }


            this.ontologyDBpedia = readOnotology(Config.ONTOLOGY_DBPEDIA_FILE);
            analysisDBpedia.enableOnotologyAnalysis(ontologyDBpedia, ontologyDBpedia.findEntity(Config.ONTOLOGY_DBPEDIA_ROOT_ENTITY));
            logger.info("Performing full analysis for dbpedia entites...");
            analysisDBpedia.performFullAnalysis(dbpediaEntites);
            logger.info("Performing full analysis for lmdb entites...");
            analysisLMDB.performFullAnalysis(lmdbEntites);
            logger.info("Analysis finished!");

            logger.info("Serializing analysis caches...");
            GraphAnalysisCacheIO.serializeGraphAnalysisCache(analysisDBpedia.getCache(), Config.CACHE_ANALYSIS_DBPEDIA);
            GraphAnalysisCacheIO.serializeGraphAnalysisCache(analysisLMDB.getCache(), Config.CACHE_ANALYSIS_LMDB);
            logger.info("Analysis saved!");
        }

        if(!Config.CACHE_ANALYSIS_RECREATE && Config.CACHE_ANALYSIS_ENABLED){
            logger.info("Load analysis from cache...");
            analysisDBpedia.setCache(GraphAnalysisCacheIO.deserializeGraphAnalysisCache(Config.CACHE_ANALYSIS_DBPEDIA));
            analysisLMDB.setCache(GraphAnalysisCacheIO.deserializeGraphAnalysisCache(Config.CACHE_ANALYSIS_LMDB));
            logger.info("Analysis loaded!");
        }
    }


    private void printEntityAndPredicateAnalysis(){
        int i = 0;
        for (final Entity e : entityList) {
            logger.info(e);
            logger.info(e.toStringWithNeighbourhood());
            EyreEntity eyreEntity = (EyreEntity) e;
            if (eyreEntity.getGraph().contains("dbpedia")) {
                logger.info(analysisDBpedia.computeEntityWithAnalysis(e));
            }
            if(eyreEntity.getGraph().contains("linked")){
                logger.info(analysisLMDB.computeEntityWithAnalysis(e));
            }

            if( i == 5)
                break;
            i += 1;
        }

    }



    private void performEvaluation(){
        //Used Strategiy?
        EvaluationStrategy evaluationStrategy = Config.EYRE_EVALUATION_STRATEGY;


        logger.info("Start with evaluation...");
        // Evaluation
        final Evaluation evaluationDBpedia = new Evaluation(analysisDBpedia);


        logger.info("Perfom dbpedia evaluation...");
        int eid = 1;
        for(final Entity e: dbpediaEntites) {
            evaluationDBpedia.computePredicateScores(e.getPredicates());

            final List<Predicate> rankedPredicates = evaluationStrategy.rankPredicate(e.getPredicates(), evaluationDBpedia.getScoredPredicatesForMetric());
            final String pathRoot = Config.EYRE_RESULT_PATH + "dbpedia/";
            if(Config.EYRE_WRITE_RESULTS)
                writeResultsForEntity(pathRoot, eid, rankedPredicates, evaluationDBpedia);
            eid += 1;
        }

        final Evaluation evaluationlmdb = new Evaluation(analysisLMDB);
        logger.info("Perfom lmdb evaluation...");

        for(final Entity e: lmdbEntites) {
            evaluationlmdb.computePredicateScores(e.getPredicates());
            final List<Predicate> rankedPredicates = evaluationStrategy.rankPredicate(e.getPredicates(), evaluationlmdb.getScoredPredicatesForMetric());
            final String pathRoot = Config.EYRE_RESULT_PATH + "lmdb/";
            if(Config.EYRE_WRITE_RESULTS)
                writeResultsForEntity(pathRoot, eid,  rankedPredicates, evaluationlmdb);
            eid += 1;
        }
    }

    private void writeResultsForEntity(final String pathRoot, final int eid, List<Predicate> bestRankedPredicates, final Evaluation evaluation){
        try {
            File file = new File(pathRoot + eid + "/");
            file.mkdirs();



            if(Config.EYRE_WRITE_RESULTS_WITH_SCORES){
                String file_rank = pathRoot + eid + "/" + eid + "_rank.csv";
                String file_rank_5 = pathRoot + eid + "/" + eid + "_top5.csv";
                String file_rank_10 = pathRoot + eid + "/" + eid + "_top10.csv";

                EvaluationWriter.writeEvaluation(file_rank, bestRankedPredicates, evaluation.getScoredPredicatesForMetric());
                EvaluationWriter.writeEvaluation(file_rank_5, bestRankedPredicates.subList(0, 5), evaluation.getScoredPredicatesForMetric());
                EvaluationWriter.writeEvaluation(file_rank_10, bestRankedPredicates.subList(0, 10), evaluation.getScoredPredicatesForMetric());
            } else {
                String file_rank = pathRoot + eid + "/" + eid + "_rank.nt";
                String file_rank_5 = pathRoot + eid + "/" + eid + "_top5.nt";
                String file_rank_10 = pathRoot + eid + "/" + eid + "_top10.nt";

                NTTripleWriter.writePredicatesToFile(file_rank, bestRankedPredicates);
                NTTripleWriter.writePredicatesToFile(file_rank_5, bestRankedPredicates.subList(0, 5));
                NTTripleWriter.writePredicatesToFile(file_rank_10, bestRankedPredicates.subList(0, 10));
            }
        } catch (IOException ex){
            logger.error("Error while writing results for entity with id: "+ eid + " because: " + ex);
            ex.printStackTrace();
        }


    }


    public void performExperiments(){
        logger.info("Initialize EYRE 2018 Experiments...");
        loadEntites();


        logger.info("There are " + dbpediaEntites.size() + " dbpedia entites and " + lmdbEntites.size() + " lmdb entites!");
        try {
            if(Config.CONNECT_TO_DATABASE) {
                virtuosoDB = new VirtuosoDB(Config.DATABASE_SERVER_ADRESS, Config.DATABASE_SERVER_PORT);
                virtuosoDB.connect();
            }

            performAnalysis();

            //Optional:
            //printEntityAndPredicateAnalysis();


            performEvaluation();

            if(Config.CONNECT_TO_DATABASE) {
                virtuosoDB.close();
            }
        }catch (SQLException ex){
            logger.error("Database error: " + ex);
            ex.printStackTrace();
        }

    }


}
