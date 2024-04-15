package org.example;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
//        GreedyCycleAlgorithmRunner.run();

        Instance kroA200 = new Instance("src/main/resources/kroA200.tsp");
        Instance kroB200 = new Instance("src/main/resources/kroB200.tsp");

        long startTime = System.nanoTime();

        List<HillClimbingCandidates> solutions = new ArrayList<HillClimbingCandidates>();

        for (int i = 0; i < 100; i++) {
            RandomStart startingCycles = new RandomStart();
            solutions.add(new HillClimbingCandidates(kroB200, startingCycles, 20));
        }


        long endTime   = System.nanoTime();
        long totalTime = (endTime - startTime)/1000000;
        System.out.println("Time: " + totalTime);
        int best = 0;
        double cost = 999999999;
        for (int i = 0; i < solutions.size(); i++) {
            if (solutions.get(i).bestCyclesLength < cost) {
                best = i;
                cost = solutions.get(i).bestCyclesLength;
            }
        }

        List<Double> criteriumValues=new ArrayList<>();
        for (HillClimbingCandidates solution: solutions) {
            criteriumValues.add(solution.getSolutionValue());
        }
        System.out.println("Max: " + criteriumValues.stream().max(Double::compareTo).get());
        System.out.println("Min: " + criteriumValues.stream().min(Double::compareTo).get());
        if(criteriumValues.stream().reduce(Double::sum).isPresent()){
            System.out.println("Avg: " + criteriumValues.stream().reduce(Double::sum).get()/Double.parseDouble(String.valueOf(criteriumValues.size())));
        }

        solutions.get(best).solutionToCsv("best.csv",kroB200);



    }
}