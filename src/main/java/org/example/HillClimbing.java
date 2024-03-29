package org.example;

import javafx.util.Pair;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


public class HillClimbing {

    List<List<Integer>> solution;
    List<Integer> cycleA;
    List<Integer> cycleB;

    List<List<Integer>> distanceMatrix;
    double cycleALength= 0;
    double cycleBLength= 0;

    double bestCyclesLength = 0;
    double currentCyclesLength = 0;


    HillClimbing(Instance instance, RandomStart startingCycles){
        solution=new ArrayList<>();
        Pair<List<Integer>,List<Integer>> cycles = startingCycles.getCycles();
        cycleA = cycles.getKey();
        cycleB = cycles.getValue();
        distanceMatrix = instance.getDistanceMatrix();
        cycleALength = calcCycleLength(distanceMatrix, cycleA);
        cycleBLength = calcCycleLength(distanceMatrix, cycleB);
        bestCyclesLength = cycleALength + cycleBLength;
        solve();

    }

    private double calcCycleLength(List<List<Integer>> distanceMatrix, List<Integer> solution){
        double length = 0;
        for(int i= 0; i<solution.size()-1; i++){
            length += distanceMatrix.get(solution.get(i)).get(solution.get(i+1));
        }
        length += distanceMatrix.get(solution.get(solution.size() - 1)).get(solution.get(0));
        return length;
    }

    private List<Integer> swapVertexOutside(List<Integer> cycleModified, List<Integer> cycleRef, int indexModify, int indexRef){
        List<Integer> alternativeCycle = new ArrayList<>(cycleModified);
        Integer newVertex = cycleRef.get(indexRef);
        alternativeCycle.set(indexModify, newVertex);
        return alternativeCycle;
    }

    private Pair<Pair<Double,Double>,Pair<List<Integer>,List<Integer>>> getSolutionOutside(List<Integer> cycleOne, List<Integer> cycleTwo, int indexOne, int indexTwo){
        List<Integer> newCycleOne = swapVertexOutside(cycleOne, cycleTwo, indexOne, indexTwo);
        double distanceOne = calcCycleLength(distanceMatrix, newCycleOne);
        List<Integer> newCycleTwo = swapVertexOutside(cycleTwo, cycleOne, indexTwo, indexOne);
        double distanceTwo = calcCycleLength(distanceMatrix, newCycleTwo);
        Pair<Pair<Double,Double>,Pair<List<Integer>,List<Integer>>> result = new Pair<>(new Pair<>(distanceOne,distanceTwo), new Pair<>(newCycleOne,newCycleTwo));
        return result;
    }

    private Pair<Double,List<Integer>> getSolutionEdge(List<Integer> cycle, int indexOne, int indexTwo){
        List<Integer> fragment = new ArrayList<>(cycle.subList(indexOne, indexTwo+1));
        Collections.reverse(fragment);
        List<Integer> newCycle = new ArrayList<>();
        newCycle.addAll(cycle.subList(0,indexOne));
        newCycle.addAll(fragment);
        newCycle.addAll(cycle.subList(indexTwo+1,cycle.size()));
        double distance = calcCycleLength(distanceMatrix, newCycle);
        return new Pair<>(distance, newCycle);
    }

    private void getAllSolutionsEdge() {
        TreeMap<Double, List<Integer>> solutionsEdgeCycleA = new TreeMap<>();
        TreeMap<Double, List<Integer>> solutionsEdgeCycleB = new TreeMap<>();
        TreeMap<Double,Pair<List<Integer>,List<Integer>>> solutionsOutside = new TreeMap<>();


        for (int i = 0; i < cycleA.size() ; i++) {
            for (int j = i+1; j < cycleA.size() - 1; j++) {

                Pair<Double, List<Integer>> x = getSolutionEdge(cycleA, i, j);
                solutionsEdgeCycleA.put(x.getKey(),x.getValue());
                Pair<Double, List<Integer>> y = getSolutionEdge(cycleB, i, j);
                solutionsEdgeCycleB.put(y.getKey(),y.getValue());
            }
        }
        for (int i = 0; i < cycleA.size() ; i++) {
            for (int j = 0; j < cycleA.size() ; j++) {
                Pair<Pair<Double,Double>,Pair<List<Integer>,List<Integer>>> y = getSolutionOutside(cycleA, cycleB, i, j);
                solutionsOutside.put(y.getKey().getKey()+y.getKey().getValue() , y.getValue());
            }
        }

        Map.Entry<Double, List<Integer>> bestEdgeCycleA = solutionsEdgeCycleA.firstEntry();
        Map.Entry<Double, List<Integer>> bestEdgeCycleB = solutionsEdgeCycleB.firstEntry();
        double bestEdge = bestEdgeCycleA.getKey() + bestEdgeCycleB.getKey();

        Map.Entry<Double, Pair<List<Integer>,List<Integer>>> bestOutsideCycles = solutionsOutside.firstEntry();
        double bestOutside = bestOutsideCycles.getKey();

        if(bestEdge <= bestOutside) {
            currentCyclesLength = bestEdge;
            cycleA = bestEdgeCycleA.getValue();
            cycleB = bestEdgeCycleB.getValue();
        } else {
            currentCyclesLength = bestOutside;
            cycleA = bestOutsideCycles.getValue().getKey();
            cycleB = bestOutsideCycles.getValue().getValue();
        }
    }

    public void solve(){
        while(true){
            getAllSolutionsEdge();
            if(currentCyclesLength<bestCyclesLength){
                bestCyclesLength = currentCyclesLength;
            }else{
                break;
            }
        }
    }
    public double getSolutionValue(){
        return bestCyclesLength;
    }
    public void solutionToCsv(String path,Instance instance) throws IOException {
        FileWriter fileWriter = new FileWriter(path);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.print("cycle,x,y\n");
        for (Integer a : cycleA) {
            printWriter.printf("%s,%d,%d\n","a", instance.coordinates.get(a).getKey(), instance.coordinates.get(a).getValue());
        }
        for (Integer a : cycleB) {
            printWriter.printf("%s,%d,%d\n","b", instance.coordinates.get(a).getKey(), instance.coordinates.get(a).getValue());
        }
        printWriter.close();
    }
}