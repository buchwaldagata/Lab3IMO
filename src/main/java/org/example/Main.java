package org.example;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {

//        Instance kroA200 = new Instance("src/main/resources/kroA200.tsp");
        Instance kroA200 = new Instance("src/main/resources/kroA200.tsp");

        long startTime = System.nanoTime();

        List<SearchMemory> solutions = new ArrayList<>();
//        List<HillClimbing> solutions = new ArrayList<>();

      for (int i = 0; i < 100; i++) {
        RandomStart startingCycles = new RandomStart();
//        solutions.add(new HillClimbing(kroA200, startingCycles));
          solutions.add(new SearchMemory(kroA200, startingCycles));
        }


        long endTime   = System.nanoTime();
        long totalTime = (endTime - startTime)/1000000;
        System.out.println(totalTime);
        int best = 0;
        double cost = 999999999;
        for (int i = 0; i < solutions.size(); i++) {
            if (solutions.get(i).bestCyclesLength < cost) {
                best = i;
                cost = solutions.get(i).bestCyclesLength;
            }
        }

        List<Double> critterValues=new ArrayList<>();
        for (SearchMemory solution: solutions) {
            critterValues.add(solution.getSolutionValue());
        }
        System.out.println(critterValues.stream().max(Double::compareTo));
        System.out.println(critterValues.stream().min(Double::compareTo));
        if(critterValues.stream().reduce(Double::sum).isPresent()){
            System.out.println(critterValues.stream().reduce(Double::sum).get()/Double.parseDouble(String.valueOf(critterValues.size())));
        }

        solutions.get(best).solutionToCsv("best.csv",kroA200);



    }
}