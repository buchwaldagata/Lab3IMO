package org.example;

import javafx.util.Pair;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class HillClimbingCandidates {
    List<List<Integer>> solution;
    List<Integer> cycleA;
    List<Integer> cycleB;

    List<List<Integer>> distanceMatrix;
    double cycleALength= 0;
    double cycleBLength= 0;

    double bestCyclesLength = 0;
    double currentCyclesLength = 0;

    final int nearestVerticesNumber;
    List<List<Integer>> nearestVertices;

    HillClimbingCandidates(Instance instance, RandomStart startingCycles, int nearestVerticesNumber){
        solution=new ArrayList<>();
        Pair<List<Integer>,List<Integer>> cycles = startingCycles.getCycles();
        cycleA = cycles.getKey();
        cycleB = cycles.getValue();
        distanceMatrix = instance.getDistanceMatrix();
        cycleALength = calcCycleLength(distanceMatrix, cycleA);
        cycleBLength = calcCycleLength(distanceMatrix, cycleB);
        bestCyclesLength = cycleALength + cycleBLength;
        this.nearestVerticesNumber = nearestVerticesNumber;
        nearestVertices = findNearestVertices(nearestVerticesNumber);
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

        findSolutions(solutionsEdgeCycleA, solutionsOutside, cycleA, cycleB);

        findSolutions(solutionsEdgeCycleB, solutionsOutside, cycleB, cycleA);

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

    private void findSolutions(TreeMap<Double, List<Integer>> solutionsEdgeCycle,
                               TreeMap<Double, Pair<List<Integer>, List<Integer>>> solutionsOutside,
                               List<Integer> cycle1,
                               List<Integer> cycle2) {
        for (int i = 0; i < cycle1.size() ; i++) {
            for (int vertex: nearestVertices.get(i)) {
                if(cycle1.contains(vertex)) {
                    int indexSmaller = i;
                    int indexBigger = cycle1.indexOf(vertex);
                    if(indexBigger<indexSmaller) {
                        int tmp = indexSmaller;
                        indexSmaller = indexBigger;
                        indexBigger = tmp;
                    }
                    Pair<Double, List<Integer>> x = getSolutionEdge(cycle1, indexSmaller, indexBigger);
                    solutionsEdgeCycle.put(x.getKey(),x.getValue());
                } else {
                    int vertexIndex = cycle2.indexOf(vertex);
                    Pair<Pair<Double,Double>,Pair<List<Integer>,List<Integer>>> y = getSolutionOutside(cycle1, cycle2, i, vertexIndex);
                    solutionsOutside.put(y.getKey().getKey()+y.getKey().getValue() , y.getValue());
                }
            }
        }
    }

    private List<List<Integer>> findNearestVertices(int nearestVerticesNumber) {
        List<List<Integer>> nearestVertices = new ArrayList<>();
        for (List<Integer> vertices: distanceMatrix) {
            List<Pair<Integer, Integer>> verticesWithIndexes = new ArrayList<>();
            for(int i=0; i<vertices.size(); i++) {
                verticesWithIndexes.add(new Pair<>(i, vertices.get(i)));
            }
            Collections.sort(verticesWithIndexes, Comparator.comparing(Pair::getValue));
            List<Integer> nearestVerticesForVertex = new ArrayList<>();
            for(int i=1; i<1+nearestVerticesNumber; i++) {
                nearestVerticesForVertex.add(verticesWithIndexes.get(i).getKey());
            }
            nearestVertices.add(nearestVerticesForVertex);
        }
        return nearestVertices;
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
