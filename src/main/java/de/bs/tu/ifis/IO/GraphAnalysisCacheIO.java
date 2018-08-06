package de.bs.tu.ifis.IO;

import de.bs.tu.ifis.analysis.model.GraphAnalysisCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;

public class GraphAnalysisCacheIO {
    private static Logger logger = LogManager.getLogger();

    public static void serializeGraphAnalysisCache(final GraphAnalysisCache cache, final String filename){
        try {

            JAXBContext ctx = JAXBContext.newInstance(GraphAnalysisCache.class);

            Marshaller m = ctx.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(filename));
            m.marshal(cache, bout);
            bout.close();
        } catch (Exception ex){
            logger.error("Error while serializing graph analysis cache: " + filename + " because: " + ex);
            ex.printStackTrace();
        }
    }


    public static GraphAnalysisCache deserializeGraphAnalysisCache(final String filename){
        try {
            JAXBContext readCtx = JAXBContext.newInstance(GraphAnalysisCache.class);
            Unmarshaller um = readCtx.createUnmarshaller();

            GraphAnalysisCache cache = (GraphAnalysisCache) um.unmarshal(
                    new BufferedInputStream(new FileInputStream(filename)));
            return cache;
        } catch (Exception ex){
            logger.error("Error while deserializing graph analysis cache: " + filename + " because: " + ex);
            ex.printStackTrace();
        }
        return null;
    }


}
