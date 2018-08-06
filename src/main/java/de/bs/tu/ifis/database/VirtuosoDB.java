package de.bs.tu.ifis.database;

import de.bs.tu.ifis.model.Entity;
import de.bs.tu.ifis.model.Literal;
import de.bs.tu.ifis.model.graph.Node;
import de.bs.tu.ifis.model.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

/**
 * offers a connection to a virtuoso server
 * @author Hermann Kroll
 */
public class VirtuosoDB {
    private Logger logger = LogManager.getLogger();

    private final String username;
    private final String password;
    private final String host;
    private final int port;
    private Connection connection;


    public VirtuosoDB(final String host, final int port, final String username, final String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public VirtuosoDB(final String host, final int port) {
        this.host = host;
        this.port = port;
        this.username = "dba";
        this.password = "dba";
    }


    public void connect() throws SQLException {
        logger.info("Trying to connect to database " +  "jdbc:virtuoso://" + host + ":" + port + "...");
        this.connection = DriverManager.getConnection("jdbc:virtuoso://" + host + ":" + port +"/charset=UTF-8", username, password);
        logger.info("Connection ethablished!");
    }

    public void close() throws SQLException {
        if(!this.connection.isClosed()){
            logger.info("Trying to close connection!");
            connection.close();
        }
        logger.info("Connection closed!");
    }


    /**
     * Queries and entity with his whole direct connected neighbourhood from virtuose
     * @param entity Entity like in virtuoso e.g. http://dbpedia.org/...
     * @param graph graph to fetch from (FROM Clause)
     * @return The entity with its connected neighbourhood
     */
    public Entity queryEntityNeighbourhood(final String entity, String graph){
        ResultSet rs = this.executeQuery("SPARQL SELECT ?p ?o " +
                "FROM " + graph + " " +
                "WHERE { "+ entity+" ?p ?o}");

        if(rs == null){
            logger.error("ResultSet is empty! Can't build neighbourhood");
            return null;
        }

        Entity start = new Entity(entity);
        try{
            while(rs.next()){
                String predicateName = rs.getString(1);
                String objectString = rs.getString(2);
                Node end = null;
                if (objectString.startsWith("http:"))
                    end = new Entity(objectString);
                else
                    end = new Literal(objectString);


                Predicate predicate = new Predicate(predicateName, start, end);
                start.addPredicate(predicate);
            }
            return start;
        } catch (SQLException ex){
            logger.error("SQL Exception while building neighbourhood: " + ex);
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * extracts a long from the result set with error catching
     * @param rs ResultSet
     * @return the count result or -1 if an error occured
     */
    private long extractLongFromCountResultSet(final ResultSet rs){
        // Exctract amount
        try {
            if(rs.next()) {
                long amount = rs.getLong(1);
                return amount;
            } else{
                logger.error("empty result set while couting");
            }
        } catch (SQLException ex){
            logger.error("SQL Exception while extracting long from counting because: " + ex);
            ex.printStackTrace();
        }
        return -1;
    }

    /**
     * Returns the amount of predicates with a specific type
     * @param predicate predicate type
     * @param graph graph to query on
     * @return the amount or -1 if an error occured
     */
    public long countPredicateTypeInDatabase(final String predicate, final String graph){
        ResultSet rs = this.executeQuery("SPARQL SELECT COUNT(*) " +
                                                "FROM " + graph + " " +
                                                "WHERE { ?s <" + predicate + "> ?o }");

        if(rs == null){
            logger.error("Cannot fetch amount of predicates for " + predicate );
            return -1;
        }
        return extractLongFromCountResultSet(rs);
    }




    /**
     * counts the amount of incoming predicates of a edge
     * @param entity the entity to count for
     * @param graph graph to query on
     * @return the amount of incoming predicates or -1 of an error occured
     */
    public long countEntityIncomingPredicates(final String entity, final String graph){
        ResultSet rs = this.executeQuery("SPARQL SELECT COUNT(*) " +
                "FROM " + graph + " " +
                "WHERE {  ?s ?p  <" + entity + ">  }");

        if(rs == null){
            logger.error("Cannot fetch amount of incoming predicates for entity " + entity );
            return -1;
        }

        return extractLongFromCountResultSet(rs);
    }


    /**
     * counts the amount of incoming predicates of a edge
     * @param literal the literal to count for
     * @param graph graph to query on
     * @return the amount of incoming predicates or -1 of an error occured
     */
    public long countLiteralIncomingPredicates(final String literal, final String graph){
        ResultSet rs = this.executeQuery("SPARQL SELECT COUNT(*) " +
                "FROM " + graph + " " +
                "WHERE {  ?s ?p "+ literal + " }");

        if(rs == null){
            logger.error("Cannot fetch amount of incoming predicates for entity " + literal );
            return -1;
        }

        return extractLongFromCountResultSet(rs);
    }


    /**
     * counts the amount of outgoing predicates of a edge
     * @param entity the entity to count for
     * @param graph graph to query on
     * @return the amount of outgoing predicates or -1 of an error occured
     */
    public long countEntityOutgoingPredicates(final String entity, final String graph){
        ResultSet rs = this.executeQuery("SPARQL SELECT COUNT(*) " +
                "FROM " + graph + " " +
                "WHERE {  <" + entity + "> ?p ?o }");

        if(rs == null){
            logger.error("Cannot fetch amount of outgoing predicates for entity " + entity );
            return -1;
        }

        return extractLongFromCountResultSet(rs);
    }


    /**
     * executes a sql query on the connection
     * @param sql query
     * @return resultset or null if an error occured
     */
    public ResultSet executeQuery(final String sql){
        try{
            long start = System.currentTimeMillis();
            Statement stmt = connection.createStatement();
            ResultSet set =  stmt.executeQuery(sql);

            long time = (System.currentTimeMillis() - start);
            logger.debug("Query [" + time + "ms]:  " + sql);
            return set;
        } catch (SQLException e) {
            logger.error("Query: " + sql + " konnte nicht ausfgeführt werden: " + e.getMessage());
        }
        return null;
    }


}
