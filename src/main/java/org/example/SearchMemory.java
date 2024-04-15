package org.example;

import javafx.util.Pair;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static java.lang.Math.abs;

public class SearchMemory {
    List<List<Integer>> solution;
    List<List<Integer>> cycles;
    List<List<Integer>> distanceMatrix;
    double bestCyclesLength = 0;


    SearchMemory(Instance instance, RandomStart startingCycles){
        solution=new ArrayList<>();
        cycles = startingCycles.getCycles();
        distanceMatrix = instance.getDistanceMatrix();
        bestCyclesLength = calcCycleLength(cycles.get(0)) + calcCycleLength(cycles.get(1));
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

    public void solve(){

        LinkedHashMap<Integer,List<Integer>> moves = new LinkedHashMap<>(initMoves());

        while (true){
            List<Integer> deleteMoves = new ArrayList<>();
            List<Integer> bestMove = new ArrayList<>();
            int typeBestMove = 0;

            for(int delta : moves.keySet()){
                if(moves.get(delta).size() == 4 ){
                    int a = moves.get(delta).get(0);
                    int b = moves.get(delta).get(1);
                    int c = moves.get(delta).get(2);
                    int d = moves.get(delta).get(3);
                    int e1 = isEdgeInCycle(a,b);
                    int e2 = isEdgeInCycle(c,d);
                    if (e1 != e2 || e1 == 0){
                        deleteMoves.add(delta);
                    }
                    else if (e1>=10 && e1==e2){
                        deleteMoves.add(delta);
                        bestMove = moves.get(delta);
                        typeBestMove = e1;
                        break;
                    }
                    else if (e1<=-10 && e1==e2){
                        deleteMoves.add(delta);
                        bestMove.addAll(Arrays.asList(b,a,c,d));
                        break;
                    }

                }
                if (moves.get(delta).size() == 6 ){
                    int e1 = isEdgeInCycle(moves.get(delta).get(0),moves.get(delta).get(1));
                    int e2 = isEdgeInCycle(moves.get(delta).get(1),moves.get(delta).get(2));
                    int e3 = isEdgeInCycle(moves.get(delta).get(3),moves.get(delta).get(4));
                    int e4 = isEdgeInCycle(moves.get(delta).get(4),moves.get(delta).get(5));
                    deleteMoves.add(delta);
                    if(e1 == e2 && e3==e4 && e1==10 && e3==11){
                        bestMove = moves.get(delta);
                        typeBestMove = 1;
                        break;
                    }
                }
            }
            removeMove(moves, deleteMoves);
            if (moves.isEmpty() ||bestCyclesLength<145000.0){
                break;
            }
            applyMove(bestMove, typeBestMove);
            bestCyclesLength= calcCycleLength(cycles.get(0)) + calcCycleLength(cycles.get(1));
            HashMap<Integer,List<Integer>> newMoves = new HashMap<>(moves);
            newMoves.putAll(createNewMoves(typeBestMove));
            LinkedHashMap<Integer,List<Integer>> sortedByKeys = new LinkedHashMap<>();
            newMoves.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEachOrdered(entry -> sortedByKeys.put(entry.getKey(), entry.getValue()));
            moves = sortedByKeys;
        }
    }

    private void removeMove(LinkedHashMap<Integer,List<Integer>> moves, List<Integer> deleteMoves ){
        for(Integer move : deleteMoves){
            moves.remove(move);
        }
    }

    private void applyMove(List<Integer> bestMove, int typeBestMove ){
        if(typeBestMove ==1){
            List<Integer> cycleA = swapVertexSolution(cycles.get(0), cycles.get(1), bestMove.get(1), bestMove.get(4));
            List<Integer> cycleB = swapVertexSolution(cycles.get(1), cycles.get(0), bestMove.get(4), bestMove.get(1));
            List<List<Integer>> newCycles = new ArrayList<>();
            newCycles.add(cycleA);
            newCycles.add(cycleB);
            cycles = newCycles;
        } else if (typeBestMove>=10){
            List<Integer> cycle = new ArrayList<>();
            int index;
            if (cycles.get(0).contains(bestMove.get(0))){
                cycle = cycles.get(0);
                index = 0;
            } else {
                cycle = cycles.get(1);
                index = 1;
            }
            List<Integer> newCycle = swapEdgeSolution(cycle, bestMove);
            cycles.set(index, newCycle);
        }

    }

    private List<Integer> swapEdgeSolution(List<Integer> cycle, List<Integer> bestMove){
        int n = cycle.size();
        int i1 = cycle.indexOf(bestMove.get(0));
        int i = (i1+1)%n;
        int j = cycle.indexOf(bestMove.get(2));

        List<Integer> newCycle = new ArrayList<>(cycle);
        int d1 = (j-i)%n;
        int d =  (abs(d1)/2)+1;
        for (int k=0; k<d; k++){
            int a = (i+k)%n;
            int b = (i+d1-k)%n;
            if(a<0){
                a=(n-b)%n;
            }
            if(b<0){
                b=(n-b)%n;
            }
            newCycle.set(a,cycle.get(b));
            newCycle.set(b, cycle.get(a));
        }
        return newCycle;
    }

    private List<Integer> swapVertexSolution(List<Integer> cycleModified, List<Integer> cycleRef, int elModify, int elRef){
        int i = cycleModified.indexOf(elModify);
        int j = cycleRef.indexOf(elRef);
        List<Integer> alternativeCycle = new ArrayList<>(cycleModified);
        alternativeCycle.set(i, elRef);
        return alternativeCycle;
    }

    private HashMap<Integer,List<Integer>> createNewMoves(Integer typeBestMove){
        HashMap<Integer,List<Integer>> moves = new HashMap<>();
        if(typeBestMove>=10){
            List<Integer> cycle = cycles.get(typeBestMove-10);
            int n= cycle.size();
            List<Pair<Integer, Integer>> pairVertex = generatePairVertex(n);
            for (Pair<Integer, Integer> vertex : pairVertex) {
                Pair<Integer, List<Integer>> newMove = swapEdges(cycle, vertex);
                if (newMove.getKey() < 0) {
                    moves.put(newMove.getKey(),newMove.getValue());
                }
            }
        }
        else if (typeBestMove==1) {
            for (int i = 0; i < cycles.get(0).size(); i++) {
                for (int j = 0; j < cycles.get(1).size(); j++) {
                    Pair<Integer, List<Integer>> newMove = swapVertex(i, j);
                    if (newMove.getKey() < 0) {
                        moves.put(newMove.getKey(), newMove.getValue());
                    }
                }
            }
        }
        return moves;
    }

    private int isEdgeInCycle ( int a, int b ){
        int num = 0;
        for (int i=0; i<2; i++){
            int n = cycles.get(i).size();
            if(cycles.get(i).contains(a)){
                int x = cycles.get(i).indexOf(a);
                if(cycles.get(i).get((x+1)%n) == b){
                    return 10+i;
                }
                if(x==0) {
                    if(cycles.get(i).get(n-1)==b){
                        return -10 - i;
                    }
                } else {
                    if(cycles.get(i).get((x-1)%n) == b){
                        return -10 - i;
                    }
                }
            }
        }
        return 0;
    }

    private LinkedHashMap<Integer,List<Integer>> initMoves(){
        HashMap<Integer,List<Integer>> moves = new HashMap<>();
        for (int i=0; i<2; i++){
            List<Integer> cycle = cycles.get(i);
            int n= cycle.size();
            List<Pair<Integer, Integer>> pairVertex = generatePairVertex(n);
            for (Pair<Integer, Integer> vertex : pairVertex) {
                Pair<Integer, List<Integer>> newMove = swapEdges(cycle, vertex);
                if (newMove.getKey() < 0) {
                    moves.put(newMove.getKey(),newMove.getValue());
                }
            }
        }
        for(int i=0; i< cycles.get(0).size();i++){
            for(int j=0; j<cycles.get(1).size();j++){
                Pair<Integer, List<Integer>> newMove = swapVertex(i,j);
                if (newMove.getKey() < 0) {
                    moves.put(newMove.getKey(),newMove.getValue());
                }
            }
        }
        LinkedHashMap<Integer,List<Integer>> sortedMoves = new LinkedHashMap<>();
        moves.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEachOrdered(entry -> sortedMoves.put(entry.getKey(), entry.getValue()));
        return sortedMoves;
    }

    private Pair<Integer, List<Integer>> swapVertex(int a, int b){
        List<Integer> cycleA = cycles.get(0);
        List<Integer> cycleB = cycles.get(1);
        int a1, b1;
        if (a==0){
            a1 = cycleA.get(cycleA.size()-1);
        } else {
            a1 = cycleA.get((a-1)%cycleA.size());
        };
        if (b==0){
            b1 = cycleB.get(cycleB.size()-1);
        } else {
            b1 = cycleB.get((b-1)%cycleB.size());
        }
        int a2 = cycleA.get(a);
        int a3 = cycleA.get((a+1)%cycleA.size());
        int b2 = cycleB.get(b);
        int b3 = cycleB.get((b+1)%cycleA.size());
        List<Integer> vertexA = new ArrayList<>(Arrays.asList(a1,a2,a3));
        List<Integer> vertexB = new ArrayList<>(Arrays.asList(b1,b2,b3));
        List<Integer> vertexCycles = new ArrayList<>(vertexA);
        vertexCycles.addAll(vertexB);
        int delta = calculateDeltaVertex(vertexA, vertexB);
        return new Pair<>(delta, vertexCycles);
    }

    private Integer calculateDeltaVertex(List<Integer> vertexA, List<Integer> vertexB){
        int oldA = distanceMatrix.get(vertexA.get(0)).get(vertexA.get(1)) + distanceMatrix.get(vertexA.get(1)).get(vertexA.get(2));
        int newA = distanceMatrix.get(vertexA.get(0)).get(vertexB.get(1)) + distanceMatrix.get(vertexB.get(1)).get(vertexA.get(2));
        int oldB = distanceMatrix.get(vertexB.get(0)).get(vertexB.get(1)) + distanceMatrix.get(vertexB.get(1)).get(vertexB.get(2));
        int newB = distanceMatrix.get(vertexB.get(0)).get(vertexA.get(1)) + distanceMatrix.get(vertexA.get(1)).get(vertexB.get(2));
        return newA -oldA + newB -oldB;
    }

    private Pair<Integer, List<Integer>> swapEdges(List<Integer> cycle, Pair<Integer,Integer> pairVertex){
        int n = cycle.size();
        int a = pairVertex.getKey();
        int c = pairVertex.getValue();
        int x1 = cycle.get(a);
        int x2 = cycle.get((a+1)%n);
        int y1 = cycle.get(c);
        int y2 = cycle.get((c+1)%n);
        List<Integer> twoEdges = new ArrayList<>(Arrays.asList(x1,x2,y1,y2));
        int delta = calculateDeltaEdges(twoEdges);
        return new Pair<>(delta,twoEdges);
    }

    private Integer calculateDeltaEdges(List<Integer> twoEdges){
        int a = twoEdges.get(0);
        int b = twoEdges.get(1);
        int c = twoEdges.get(2);
        int d = twoEdges.get(3);
        if(a == d || a == b || a == c || b == c || b == d || c == d){
            return 999999999;
        }
        return distanceMatrix.get(a).get(c)+distanceMatrix.get(b).get(d)-distanceMatrix.get(a).get(b)-distanceMatrix.get(c).get(d);
    }

    private List<Pair<Integer, Integer>> generatePairVertex(Integer n){
        List<Pair<Integer, Integer>> pairVertex = new ArrayList<>();
        for(int i=0; i<n; i++){
            for(int j=2; j<n-1; j++){
                pairVertex.add(new Pair<>(i, (i+j)%n));
            }
        }
        return pairVertex;
    }

    public double getSolutionValue(){
        return calcCycleLength(cycles.get(0))+calcCycleLength(cycles.get(1));
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


