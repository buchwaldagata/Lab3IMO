package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GreedyCycleAlgorithm {
    public Long runAlgorithm(int firstVertex, int[][] coordinateList, Long[][] distanceMatrix) {
        List<Integer> unassignedVertices = new ArrayList<>();
        for(int i = 0; i < distanceMatrix.length; i++) {
            unassignedVertices.add(i);
        }

        // key - visit number
        // value - vertex index
        Map<Integer, Integer> firstCycle = new HashMap<>();
        Map<Integer, Integer> secondCycle = new HashMap<>();

        // assign first vertex to first cycle
        addVertexToCycle(unassignedVertices, firstCycle, firstVertex, 0);

        // find max distance vertex
        Long maxDistance = 0L;
        int maxDistanceVertex = firstVertex;
        for (int j = 0; j < distanceMatrix[firstVertex].length; j++) {
            if (distanceMatrix[firstVertex][j] > maxDistance){
                maxDistance = distanceMatrix[firstVertex][j];
                maxDistanceVertex = j;
            }
        }

        // assign max distance vertex to second cycle
        addVertexToCycle(unassignedVertices, secondCycle, maxDistanceVertex, 0);

        // add second and last vertices
        addSecondAndLastVertices(unassignedVertices, distanceMatrix, firstCycle);
        addSecondAndLastVertices(unassignedVertices, distanceMatrix, secondCycle);

        // add rest vertices
        while (true){
            if(unassignedVertices.isEmpty()) {
                break;
            }
            findAndAddBestVertex(unassignedVertices, distanceMatrix, firstCycle);

            if(unassignedVertices.isEmpty()) {
                break;
            }
            findAndAddBestVertex(unassignedVertices, distanceMatrix, secondCycle);
        }

        // save cycles to files
        GreedyCycleAlgorithmRunner.saveCycle(coordinateList, firstCycle, "Greedy Cycle first " + firstVertex);
        GreedyCycleAlgorithmRunner.saveCycle(coordinateList, secondCycle, "Greedy Cycle second " + firstVertex);
        return GreedyCycleAlgorithmRunner.getLengthFromCycle(firstCycle, distanceMatrix) + GreedyCycleAlgorithmRunner.getLengthFromCycle(secondCycle,distanceMatrix);
    }

    private static void addVertexToCycle(List<Integer> unassignedVertices, Map<Integer, Integer> cycle,
                                         int vertex, int numberInCycle) {
        cycle.put(numberInCycle, vertex);
        unassignedVertices.removeIf(i -> i.equals(vertex));
    }

    private static void addSecondAndLastVertices(List<Integer> unassignedVertices, Long[][] distanceMatrix,
                                                 Map<Integer, Integer> cycle) {
        int secondVertexFirstCycle = findSecondVertex(unassignedVertices, distanceMatrix, cycle.get(0));
        addVertexToCycle(unassignedVertices, cycle, secondVertexFirstCycle, 1);
        cycle.put(2, cycle.get(0));
    }

    private static int findSecondVertex(List<Integer> unassignedVertices, Long[][] distanceMatrix, int vertex) {
        Long minDistance = -1L;
        int minDistanceVertex = -1;
        for(int i=0; i<distanceMatrix[vertex].length; i++) {
            if(unassignedVertices.contains(i)) {
                Long distanceToVertex = distanceMatrix[vertex][i];
                if(distanceToVertex<minDistance || minDistance==-1) {
                    minDistance = distanceToVertex;
                    minDistanceVertex = i;
                }
            }
        }
        return minDistanceVertex;
    }

    private static void findAndAddBestVertex(List<Integer> unassignedVertices, Long[][] distanceMatrix,
                                             Map<Integer, Integer> cycle) {
        int resultNewVertex = -1;
        int resultNewVertexNumberInCycle = -1;
        long resultTotalCost = -1L;
        for(int i=0; i<cycle.keySet().size()-1; i++) {
            int fromVertex = cycle.get(i);
            int toVertex = cycle.get(i+1);
            Long oldCost = distanceMatrix[fromVertex][toVertex];
            for(int newVertex: unassignedVertices) {
                Long firstCost = distanceMatrix[fromVertex][newVertex];
                Long secondCost = distanceMatrix[newVertex][toVertex];
                Long newCost = firstCost + secondCost;
                long totalCost = newCost - oldCost;

                if(totalCost<resultTotalCost || resultTotalCost==-1) {
                    resultNewVertexNumberInCycle = i+1;
                    resultNewVertex = newVertex;
                    resultTotalCost = totalCost;
                }
            }
        }

        for(int i=cycle.keySet().size()-1; i>=resultNewVertexNumberInCycle; i--) {
            cycle.put(i+1, cycle.get(i));
        }
        addVertexToCycle(unassignedVertices, cycle, resultNewVertex, resultNewVertexNumberInCycle);
    }
}