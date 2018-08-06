package de.bs.tu.ifis;


import de.bs.tu.ifis.EYRE2018.EyreEvaluation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class EYREExperiments {
    private static Logger logger = LogManager.getLogger();


    public static void main(String[] args){
        long millis = System.currentTimeMillis();
        new EyreEvaluation().performExperiments();
        System.out.println("Algorithm takes: " + (System.currentTimeMillis() - millis) + " ms");
    }
}
