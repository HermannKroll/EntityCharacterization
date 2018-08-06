# EntityCharacterization
This repositoy contains our introduced BAFREC (BAlancing Frequency and Rarity in Entity Characterization) algorithm. 

## Getting Started
We can't deliver all necessary dependencies, because some are missing inside maven. This dependencies have to be downloaded manually and setup like described below. 


## Dependecies
# ws4j
1. Download ws4j-1.0.1.jar from https://code.google.com/archive/p/ws4j/ 
2. Make sure its named "ws4j-1.0.1.jar"
3. Copy the file into lib/ws4j/ws4j/1.0.1/ws4j-1.0.1.jar 


# jawjaw
1. Download jawjaw-1.0.2.jar from https://code.google.com/archive/p/jawjaw/
2. Make sure its named "jawjaw-1.0.2.jar"
3. Copy the file into lib/jawjaw/jawjaw/jawjaw-1.0.2.jar



# Virtuoso JDBC 
(you don't need to setup a database containing the data)
1. Extract the Virtuoso JDBC driver like explained in
http://docs.openlinksw.com/virtuoso/virtuosodriverpackaging/
2. Copy the jar inside lib/jdbc/virtjdbc/4.2/virtjdbc-4.2.jar 


## Cached database statistics
To prohibit setting up a DBpedia and LinkedMDB database, we deliver two cache files which contain all necessary statistics. The Virtuoso JDBC driver needs to be included, but no database connection is necessary. The connection is disabled as default.


## Configuration
You can configure this software using the 
src/main/java/de/bs/tu/ifis/Config.java class.

You need to setup the following:
1. location of the ESBM benchmark data 
2. location to save the results
