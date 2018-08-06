package de.bs.tu.ifis.analysis;

import de.bs.tu.ifis.model.graph.Edge;
import de.bs.tu.ifis.model.graph.Node;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

// http://www.baeldung.com/java-dijkstra
public class GraphToolsTest {


    @Test
    public void testShortestPathDistance(){
        Node nodeA = new Node();
        Node nodeB = new Node();
        Node nodeC = new Node();
        Node nodeD = new Node();
        Node nodeE = new Node();
        Node nodeF = new Node();

        nodeA.addConnectedEdge(new Edge(nodeA, nodeB));
        nodeA.addConnectedEdge(new Edge(nodeA, nodeC));


        nodeB.addConnectedEdge(new Edge(nodeB, nodeD));
        nodeB.addConnectedEdge(new Edge(nodeB, nodeF));

        nodeC.addConnectedEdge(new Edge(nodeC, nodeE));

        nodeD.addConnectedEdge(new Edge(nodeD, nodeE));
        nodeD.addConnectedEdge(new Edge(nodeD, nodeF));

        nodeF.addConnectedEdge(new Edge(nodeF, nodeE));


        long distanceAB = GraphTools.calculateShortestDistanceBetween(nodeA, nodeB);
        long distanceAD = GraphTools.calculateShortestDistanceBetween(nodeA, nodeD);
        long distanceAE = GraphTools.calculateShortestDistanceBetween(nodeA, nodeE);
        long distanceAC = GraphTools.calculateShortestDistanceBetween(nodeA, nodeC);
        long distanceBE = GraphTools.calculateShortestDistanceBetween(nodeB, nodeE);
        long distanceAF = GraphTools.calculateShortestDistanceBetween(nodeA, nodeF);



        assertEquals(1, distanceAB);
        assertEquals(2, distanceAD);
        assertEquals(2, distanceAE);
        assertEquals(1, distanceAC);
        assertEquals(2, distanceBE);
        assertEquals(2, distanceAF);

        //No connection exists
        long distanceBC = GraphTools.calculateShortestDistanceBetween(nodeB, nodeC);
        long distanceBA = GraphTools.calculateShortestDistanceBetween(nodeB, nodeA);
        assertEquals(-1, distanceBC);
        assertEquals(-1, distanceBA);


        //Create connection between A und E
        nodeE.addConnectedEdge(new Edge(nodeE, nodeA));
        long distanceEA = GraphTools.calculateShortestDistanceBetween(nodeE, nodeA);
        assertEquals(1, distanceEA);


        //Distance to self
        long distanceAA = GraphTools.calculateShortestDistanceBetween(nodeA, nodeA);
        assertEquals(0, distanceAA);


    }


    @Test
    public void testShortestPath(){
        Node nodeA = new Node();
        Node nodeB = new Node();
        Node nodeC = new Node();
        Node nodeD = new Node();
        Node nodeE = new Node();
        Node nodeF = new Node();

        nodeA.addConnectedEdge(new Edge(nodeA, nodeB));
        nodeA.addConnectedEdge(new Edge(nodeA, nodeC));


        nodeB.addConnectedEdge(new Edge(nodeB, nodeD));
        nodeB.addConnectedEdge(new Edge(nodeB, nodeF));

        nodeC.addConnectedEdge(new Edge(nodeC, nodeE));

        nodeD.addConnectedEdge(new Edge(nodeD, nodeE));
        nodeD.addConnectedEdge(new Edge(nodeD, nodeF));

        nodeF.addConnectedEdge(new Edge(nodeF, nodeE));


        LinkedList distanceAB = GraphTools.calculateShortestPathBetween(nodeA, nodeB);
        LinkedList distanceAD = GraphTools.calculateShortestPathBetween(nodeA, nodeD);
        LinkedList distanceAE = GraphTools.calculateShortestPathBetween(nodeA, nodeE);
        LinkedList distanceAC = GraphTools.calculateShortestPathBetween(nodeA, nodeC);
        LinkedList distanceBE = GraphTools.calculateShortestPathBetween(nodeB, nodeE);
        LinkedList distanceAF = GraphTools.calculateShortestPathBetween(nodeA, nodeF);



        assertEquals(2, distanceAB.size());
        assertEquals(true, distanceAB.contains(nodeA));
        assertEquals(true, distanceAB.contains(nodeB));


        assertEquals(3, distanceAD.size());
        assertEquals(true, distanceAD.contains(nodeA));
        assertEquals(true, distanceAD.contains(nodeB));
        assertEquals(true, distanceAD.contains(nodeD));


        assertEquals(3, distanceAE.size());
        assertEquals(true, distanceAE.contains(nodeA));
        assertEquals(true, distanceAE.contains(nodeC));
        assertEquals(true, distanceAE.contains(nodeE));


        assertEquals(2, distanceAC.size());
        assertEquals(true, distanceAC.contains(nodeA));
        assertEquals(true, distanceAC.contains(nodeC));


        assertEquals(3, distanceBE.size());

        assertEquals(3, distanceAF.size());
        assertEquals(true, distanceAF.contains(nodeA));
        assertEquals(true, distanceAF.contains(nodeB));
        assertEquals(true, distanceAF.contains(nodeF));

        //No connection exists
        LinkedList distanceBC = GraphTools.calculateShortestPathBetween(nodeB, nodeC);
        LinkedList distanceBA = GraphTools.calculateShortestPathBetween(nodeB, nodeA);
        assertEquals(null, distanceBC);
        assertEquals(null, distanceBA);


        //Create connection between A und E
        nodeE.addConnectedEdge(new Edge(nodeE, nodeA));
        LinkedList distanceEA = GraphTools.calculateShortestPathBetween(nodeE, nodeA);
        assertEquals(2, distanceEA.size());
        assertEquals(true, distanceEA.contains(nodeE));
        assertEquals(true, distanceEA.contains(nodeA));


        //Distance to self
        LinkedList distanceAA = GraphTools.calculateShortestPathBetween(nodeA, nodeA);
        assertEquals(1, distanceAA.size());
        assertEquals(true, distanceAA.contains(nodeA));


    }
}
