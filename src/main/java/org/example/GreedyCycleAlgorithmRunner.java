package org.example;

import java.io.*;
import java.util.Map;

public class GreedyCycleAlgorithmRunner {
    public static void run() {
        CoordinateList coordinateList = new CoordinateList("src/main/resources/kroB200.tsp");
        int[][] intCoordinateList = coordinateList.intCoordinateList;
        DistanceMatrix distanceMatrix = new DistanceMatrix(intCoordinateList);
        Long[][] distanceMatrix2 = distanceMatrix.distanceMatrix;

        long startTime = System.nanoTime();
        GreedyCycleAlgorithm greedyCycleAlgorithm = new GreedyCycleAlgorithm();
        Long maxGreedyCycle = (long) -1;
        Long minGreedyCycle = 1000000L;
        int minIndexGreedyCycle = -1;
        int totalLengthGreedyCycle = 0;
        for (int i = 0 ; i < 100; i++) {
            Long lenGreedyCycle = greedyCycleAlgorithm.runAlgorithm(i*2, intCoordinateList,distanceMatrix2);
            if(lenGreedyCycle > maxGreedyCycle){
                maxGreedyCycle = lenGreedyCycle;
            }
            if (lenGreedyCycle < minGreedyCycle){
                minGreedyCycle = lenGreedyCycle;
                minIndexGreedyCycle = i;
            }
            totalLengthGreedyCycle+= lenGreedyCycle;
        }
        long endTime   = System.nanoTime();
        long totalTime = (endTime - startTime)/1000000;
        System.out.println("Time: " + totalTime);

        int avgGreedyCycle = totalLengthGreedyCycle/100;

        System.out.println("\nGreedy Cycle");
        System.out.println("Maximum " + maxGreedyCycle);
        System.out.println("Minimum " + minGreedyCycle + " for " + minIndexGreedyCycle);
        System.out.println("Average " + avgGreedyCycle);



    }

    static void saveToFile(BufferedWriter bufferedWriter, int firstVertex, int x, int y, String nameOfFile){
        try {
            writePointToCsv(bufferedWriter, firstVertex, x, y);
//            System.out.println("Liczby zostały zapisane do pliku " + nameOfFile);

        } catch (IOException e) {
            System.err.println("Wystąpił błąd podczas zapisywania do pliku: " + e.getMessage());
        }
    }

    private static void writePointToCsv(BufferedWriter bufferedWriter, int vertex, int x, int y) throws IOException {
        bufferedWriter.write(vertex + "," + x + "," + y + "\n");
    }
    public static Long getLengthFromCycle(Map<Integer, Integer> cycle, Long[][] distanceMatrix) {
        Long len = 0L;
        for(int i=0; i<cycle.keySet().size()-1; i++) {
            int fromVertex = cycle.get(i);
            int toVertex = cycle.get(i+1);
            len += distanceMatrix[fromVertex][toVertex];
        }
        return len;
    }

    static void saveCycle(int[][] coordinateList, Map<Integer, Integer> cycle, String filename) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(filename);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        for(int i: cycle.keySet()) {
            int vertex = cycle.get(i);
            for (int[] coordinateRow: coordinateList) {
                if(vertex==coordinateRow[0]) {
                    GreedyCycleAlgorithmRunner.saveToFile(bufferedWriter, coordinateRow[0], coordinateRow[1], coordinateRow[2], filename);
                }
            }
        }
        try {
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}