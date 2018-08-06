package de.bs.tu.ifis.analysis;

import de.bs.tu.ifis.model.graph.Edge;
import de.bs.tu.ifis.model.graph.Node;

import java.util.*;

public class GraphTools {




    private static Node getLowestDistanceNode(final Set<Node> unsettledNodes, final HashMap<Node, Long> nodeDistances) {
        Node lowestDistanceNode = null;
        Long lowestDistance = Long.MAX_VALUE;
        for (final Node node: unsettledNodes) {
            Long nodeDistance = nodeDistances.get(node);
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }

    private static void calculateMinimumDistance(final Node sourceNode, final Node evaluationNode, final HashMap<Node, Long> nodeDistances,
                                                 final HashMap<Node, LinkedList<Node>> shortestPathToNode, final boolean constructPath) {
        final Long sourceDistance = nodeDistances.get(sourceNode);
        final Long evaluationDistance =  nodeDistances.get(evaluationNode);
        // Not discovered yet or  Disocvered but new distance is smaller?
        if(evaluationDistance == null || sourceDistance + 1 < evaluationDistance){
            nodeDistances.put(evaluationNode, sourceDistance + 1);

            if(constructPath) {
                //Get a copy of shorted path from source
                LinkedList<Node> shortestPath = new LinkedList<>(shortestPathToNode.get(sourceNode));
                shortestPath.add(evaluationNode);
                shortestPathToNode.put(evaluationNode, shortestPath);
            }
        }
    }

    private static Object calculateShortestPathBetweenHelper(final Node start, final Node end, final boolean constructPath) {
        final HashMap<Node, Long> nodeDistances = new HashMap<>();
        nodeDistances.put(start, 0L);
        final HashMap<Node, LinkedList<Node>> shortestPathToNode = new HashMap<>();
        final Set<Node> visitedNodes = new HashSet<>();
        final Set<Node> toDisoverNodes = new HashSet<>();

        if(constructPath){
            LinkedList<Node> pathToStart = new LinkedList<>();
            pathToStart.add(start);
            shortestPathToNode.put(start, pathToStart);
        }

        toDisoverNodes.add(start);
        while (!toDisoverNodes.isEmpty()) {
           final Node currentNode = getLowestDistanceNode(toDisoverNodes, nodeDistances);
            toDisoverNodes.remove(currentNode);

            for(final Edge cE : currentNode.getConnectedEdges()){
                final Node adjacentNode = cE.getTo();
                if (!visitedNodes.contains(adjacentNode)) {
                    calculateMinimumDistance(currentNode, adjacentNode , nodeDistances, shortestPathToNode, constructPath);
                    toDisoverNodes.add(adjacentNode);
                }
            }
            visitedNodes.add(currentNode);
        }
        // Return the whole path
        if(constructPath){
            return shortestPathToNode.get(end);
        }
        // Return only the distance
        else {
            return nodeDistances.get(end);
        }

    }


    public static long calculateShortestDistanceBetween(final Node start, final Node end){
        Long distance = (Long) calculateShortestPathBetweenHelper(start, end, false);
        if(distance == null){
            return -1;
        } else {
            return distance;
        }
    }

    public static LinkedList<Node> calculateShortestPathBetween(final Node start,final Node end){
        return (LinkedList)calculateShortestPathBetweenHelper(start, end, true);
    }







    public static long subtreeSize(final Node root){
        final Stack<Node> nodesToDiscover = new Stack<>();
        final HashSet<Node> nodesVisited = new HashSet<>();
        long subtreeSize = 0;

        nodesToDiscover.add(root);
        nodesVisited.add(root);
        while(!nodesToDiscover.empty()){
            final Node current = nodesToDiscover.pop();

            for(final Edge cE : current.getConnectedEdges()){
                if(!nodesVisited.contains(cE.getTo())){
                    nodesToDiscover.add(cE.getTo());
                    subtreeSize += 1;
                }
            }
        }
        return subtreeSize;
    }
}
