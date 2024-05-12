package org.example;
import javafx.util.Pair;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import static java.lang.Math.abs;


public class HillClimbing {
    List<List<Integer>> solution;
    List<List<Integer>> cycles;
    List<List<Integer>> distanceMatrix;
    int swappedEdgeCycleA = 0;
    int swappedEdgeCycleB = 1;
    int swappedVertex = 2;
    int cycleAsize;
    int cycleBsize;
    double bestCyclesLength = 0;


    HillClimbing(Instance instance, RandomStart startingCycles){
        solution=new ArrayList<>();
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



    private List<List<Integer>> getAllMoves() {
        List<List<Integer>> moves = new ArrayList<>();

        for (int i = 0; i < cycleAsize; i++) {
            for (int j = 2; j < cycleBsize; j++) {
                List<Integer> newMove = swapVertex(i,j);
                int delta = newMove.get(0);
                if (delta < 0) {
                    moves.add(newMove);
                }
            }
        }

        List<Pair<Integer, Integer>> listOfEdgesCycleA = new ArrayList<>();
        for(int i=0; i<cycleAsize; i++){
            for(int j=2; j<cycleAsize-1; j++){
                listOfEdgesCycleA.add(new Pair<>(i, (i+j)%cycleAsize));
            }
        }
        List<Pair<Integer, Integer>> listOfEdgesCycleB = new ArrayList<>();
        for(int i=0; i<cycleBsize; i++){
            for(int j=2; j<cycleBsize-1; j++){
                listOfEdgesCycleB.add(new Pair<>(i, (i+j)%cycleBsize));
            }
        }

        for (Pair<Integer, Integer> edge : listOfEdgesCycleA) {
            List<Integer> newMoveCycleA = swapEdges(cycles.get(0), edge.getKey(), edge.getValue());
            if (newMoveCycleA.get(0) < 0) {
                newMoveCycleA.add(1, swappedEdgeCycleA);
                moves.add(newMoveCycleA);
            }
        }
        for (Pair<Integer, Integer> edge : listOfEdgesCycleB) {
            List<Integer> newMoveCycleB = swapEdges(cycles.get(1), edge.getKey(), edge.getValue());
            if (newMoveCycleB.get(0) < 0) {
                newMoveCycleB.add(1, swappedEdgeCycleB);
                moves.add(newMoveCycleB);
            }
        }
        return moves;
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

    private List<Integer> swapEdges(List<Integer> cycle, int i, int j){
        int n = cycle.size();
        int a1 = cycle.get(i);
        int a2 = cycle.get((i+1)%n);
        int b1 = cycle.get(j);
        int b2 = cycle.get((j+1)%n);
        int delta = calculateDeltaEdges(a1,a2,b1,b2);
        return new ArrayList<>(Arrays.asList(delta,a1,a2,b1,b2));
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
        while(true){
            List<List<Integer>> allMoves = new ArrayList<>(getAllMoves());
            if(allMoves.isEmpty()){
                break;
            }
            allMoves.sort(Comparator.comparing(list -> list.get(0)));
            List<Integer> bestMove = allMoves.get(0);
            applyMove(bestMove);

        }
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