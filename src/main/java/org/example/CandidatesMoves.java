package org.example;

import java.util.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import static java.lang.Math.abs;

public class CandidatesMoves {
    List<List<Integer>> cycles;
    List<List<Integer>> distanceMatrix;
    int swappedEdgeCycleA = 0;
    int swappedEdgeCycleB = 1;
    int swappedVertex = 2;
    int cycleAsize;
    int cycleBsize;
    double bestCyclesLength = 0;


    CandidatesMoves(Instance instance, RandomStart startingCycles){
        cycles = startingCycles.getCycles();
        distanceMatrix = instance.getDistanceMatrix();
        bestCyclesLength = calcCycleLength(cycles.get(0)) + calcCycleLength(cycles.get(1));
        cycleAsize = cycles.get(0).size();
        cycleBsize = cycles.get(1).size();
        solve();
    }

    private double calcCycleLength(List<Integer> solution){
        double length = 0;
        for(int i= 0; i<solution.size()-1; i++){
            length += distanceMatrix.get(solution.get(i)).get(solution.get(i+1));
        }
        length += distanceMatrix.get(solution.get(solution.size() - 1)).get(solution.get(0));
        return length;
    }

    private List<List<Integer>> getNeighbours(){
        int n = cycleAsize + cycleBsize;
        List<List<Integer>> closestCities = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            List<Integer> cityList = distanceMatrix.get(i);
            List<List<Integer>> sortedCity = new ArrayList<>();
            for (int j=0; j<n; j++){
                sortedCity.add(Arrays.asList(cityList.get(j), j));
            }
            sortedCity.sort(Comparator.comparingInt(list -> list.get(0)));
            List<List<Integer>> nearest = sortedCity.subList(1, 11);
            List<Integer> nearestIndex = new ArrayList<>();
            for (List<Integer> j : nearest) {
                nearestIndex.add(j.get(1));

            }
            closestCities.add(nearestIndex);
        }
        return closestCities;
    }


    private List<Integer> swapVertex(int a, int b){
        List<Integer> cycleA = cycles.get(0);
        List<Integer> cycleB = cycles.get(1);

        int prevA,prevB;
        if (a==0){
            prevA = cycleA.get(cycleAsize-1);
        } else {
            prevA = cycleA.get((a-1)%cycleAsize);
        }
        int A = cycleA.get(a);
        int nextA = cycleA.get((a+1)%cycleAsize);

        if (b==0){
            prevB = cycleB.get(cycleBsize-1);
        } else {
            prevB = cycleB.get((b-1)%cycleBsize);
        }
        int B = cycleB.get(b);
        int nextB = cycleB.get((b+1)%cycleBsize);

        int delta = calculateDeltaVertex(prevA,A,nextA,prevB,B,nextB);
        return new ArrayList<>(Arrays.asList(delta,swappedVertex,prevA,A,nextA,prevB,B,nextB));
    }

    private int calculateDeltaVertex(int a1,int a2,int a3,int b1,int b2,int b3){
        int newA = distanceMatrix.get(a1).get(b2) + distanceMatrix.get(b2).get(a3);
        int oldA = distanceMatrix.get(a1).get(a2) + distanceMatrix.get(a2).get(a3);
        int newB = distanceMatrix.get(b1).get(a2) + distanceMatrix.get(a2).get(b3);
        int oldB = distanceMatrix.get(b1).get(b2) + distanceMatrix.get(b2).get(b3);
        return newA-oldA+newB-oldB;
    }



    private Integer calculateDeltaEdges(int a1, int a2, int b1, int b2){
        int newEdge = distanceMatrix.get(a1).get(b1)+distanceMatrix.get(a2).get(b2);
        int oldEdge = distanceMatrix.get(a1).get(a2)+distanceMatrix.get(b1).get(b2);
        return newEdge-oldEdge;
    }

    private void applyEdgeMove(int num, int a, int b){
        List<Integer> newCycle = new ArrayList<>(cycles.get(num));
        int n = newCycle.size();
        int d = (b-a)%n;
        if(d<0){
            d+=n;
        }

        for (int k=0; k<(abs(d)/2)+1; k++){
            int indexA = (a+k)%n;
            int indexB = (a+d-k)%n;
            if(indexA<0){
                indexA+=n;
            }
            if(indexB<0){
                indexB+=n;
            }
            newCycle.set(indexA,cycles.get(num).get(indexB));
            newCycle.set(indexB, cycles.get(num).get(indexA));
        }
        cycles.set(num,newCycle);
    }

    private void applyMove(List<Integer> move){
        int moveType = move.get(1);

        if (moveType == swappedEdgeCycleA){
            int a1 = move.get(2);
            int b1 = move.get(4);
            int a1Index = cycles.get(0).indexOf(a1);
            int b1Index = cycles.get(0).indexOf(b1);
            applyEdgeMove(0, (a1Index+1)%cycleAsize,b1Index);

        } else if (moveType == swappedEdgeCycleB){
            int a1 = move.get(2);
            int b1 = move.get(4);
            int a1Index = cycles.get(1).indexOf(a1);
            int b1Index = cycles.get(1).indexOf(b1);
            applyEdgeMove(1, (a1Index+1)%cycleBsize,b1Index);

        } else if (moveType == swappedVertex){
            int a2 = move.get(3);
            int b2 = move.get(6);
            int a2Index = cycles.get(0).indexOf(a2);
            int b2Index = cycles.get(1).indexOf(b2);

            int temp = cycles.get(0).get(a2Index);
            cycles.get(0).set(a2Index, cycles.get(1).get(b2Index));
            cycles.get(1).set(b2Index, temp);
        }
    }

    public void solve(){
        List<List<Integer>> nearestVertex = getNeighbours();
        while(true){
            List<Integer> bestMove = new ArrayList<>();
            int bestDelta = 0;
            for(int a1 =0; a1<cycleAsize+cycleBsize; a1++){
                for(int b1 : nearestVertex.get(a1)){
                    List<Integer> move;
                    int delta;

                    int aCycle =-1;
                    int bCycle =-1;

                    for(int c=0; c<2; c++){
                        boolean a1IsInCycleA = cycles.get(0).contains(a1);
                        boolean b1IsInCycleA = cycles.get(0).contains(b1);
                        if(a1IsInCycleA){
                            aCycle = 0;
                        } else {
                            aCycle = 1;
                        }
                        if(b1IsInCycleA){
                            bCycle = 0;
                        } else {
                            bCycle = 1;
                        }
                    }
                    int indexA = cycles.get(aCycle).indexOf(a1);
                    int indexB = cycles.get(bCycle).indexOf(b1);
                    if (aCycle== 0 && bCycle==0) {
                        int a2 = cycles.get(0).get((indexA + 1) % cycleAsize);
                        int b2 = cycles.get(0).get((indexB + 1) % cycleAsize);
                        delta = calculateDeltaEdges(a1, a2, b1, b2);
                        move = Arrays.asList(delta, swappedEdgeCycleA, a1, a2, b1, b2);
                    } else if (aCycle== 1 && bCycle==1) {
                        int a2 = cycles.get(1).get((indexA + 1) % cycleBsize);
                        int b2 = cycles.get(1).get((indexB + 1) % cycleBsize);
                        delta = calculateDeltaEdges(a1, a2, b1, b2);
                        move = Arrays.asList(delta, swappedEdgeCycleB, a1, a2, b1, b2);
                    } else if (aCycle == 0){
                        move = swapVertex(indexA,indexB);
                        delta = move.get(0);
                    } else {
                        move = swapVertex(indexB, indexA);
                        delta = move.get(0);
                    }if(delta < bestDelta){
                        bestDelta = delta;
                        bestMove = move;
                    }
                }
            }
            if(bestMove.isEmpty()){
                break;
            } else {
                applyMove(bestMove);
                bestCyclesLength= calcCycleLength(cycles.get(0)) + calcCycleLength(cycles.get(1));
            }
        }
    }

    public double getSolutionValue(){
        return calcCycleLength(cycles.get(0))+calcCycleLength(cycles.get(1));
    }
    public List<List<Integer>> getBestCycles(){
        return cycles;
    }

    public void solutionToCsv(String path,Instance instance) throws IOException {
        FileWriter fileWriter = new FileWriter(path);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.print("cycle,x,y\n");
        for (Integer a : cycles.get(0)) {
            printWriter.printf("%s,%d,%d\n","a", instance.coordinates.get(a).getKey(), instance.coordinates.get(a).getValue());
        }
        for (Integer a : cycles.get(1)) {
            printWriter.printf("%s,%d,%d\n","b", instance.coordinates.get(a).getKey(), instance.coordinates.get(a).getValue());
        }
        printWriter.close();
    }
}
