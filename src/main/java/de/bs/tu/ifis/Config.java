package de.bs.tu.ifis;

import de.bs.tu.ifis.evaluation.EvaluationStrategy;
import de.bs.tu.ifis.evaluation.strategies.BAFRECStrategy;

public class Config {
    //Result path where the output for the ESBM benchmark should be saved
    public static final String EYRE_RESULT_PATH = "result/";
    //Path to the EYRE entity list (all subdirectories needs to be there too)
    public static final String EYRE_ENTITY_LIST = "data/EYRE2018/elist.txt";

    //Setting the ontology file
    public static final String ONTOLOGY_DBPEDIA_FILE = "data/dbpedia_2016_ontology.nt";
    //Setting the root element of the ontology
    public static final String ONTOLOGY_DBPEDIA_ROOT_ENTITY = "http://www.w3.org/2002/07/owl#Thing";




    public static final String DATABASE_SERVER_ADRESS = "";
    public static final int DATABASE_SERVER_PORT = 0;
    public static final boolean CONNECT_TO_DATABASE = false;


    public static final boolean CACHE_ANALYSIS_ENABLED = true;
    public static final boolean CACHE_ANALYSIS_RECREATE = false;
    public static final String CACHE_DIRECTORY =  "cache";
    public static final String CACHE_ANALYSIS_DBPEDIA =  CACHE_DIRECTORY + "/" + "cacheDBpedia.xml";
    public static final String CACHE_ANALYSIS_LMDB = CACHE_DIRECTORY + "/" + "cacheLMDB.xml";



    public static final EvaluationStrategy EYRE_EVALUATION_STRATEGY = new BAFRECStrategy();
    public static final Boolean EYRE_WRITE_RESULTS = true;
    public static final Boolean EYRE_WRITE_RESULTS_WITH_SCORES = false;

}
