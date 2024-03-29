package org.example;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.round;

public class Instance {
    private List<List<String>> preparedLinesFromFile;
    private List<List<Integer>> distanceMatrix;

    public List<Pair<Integer,Integer>> coordinates;
    Instance(String path){
        coordinates=new ArrayList<>();
        prepareLinesFromFile(path);
        calcEuclideanDistanceMatrix();
    }
    public List<List<Integer>> getDistanceMatrix(){
        return distanceMatrix;
    }

    private void prepareLinesFromFile(String path) {
        preparedLinesFromFile = new ArrayList<>();
        int count = -1;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                List<String> correctLine = new ArrayList<>();
                correctLine.add(line);
                count++;
                if (count > 5 && count < 206) { //todo: change 5 and 106
                    preparedLinesFromFile.add(correctLine);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private  void calcEuclideanDistanceMatrix(){
        distanceMatrix=new ArrayList<>();
        List<Integer> temporary;
        if (preparedLinesFromFile.size() > 1) {
            for (int i = 0; i < preparedLinesFromFile.size(); i++) {
                temporary=new ArrayList<>();
                String[] firstList = preparedLinesFromFile.get(i).get(0).split("\\s+");
                Integer x1 = Integer.parseInt(firstList[1]);
                Integer y1 = Integer.parseInt(firstList[2]);
                coordinates.add(new Pair<>(x1,y1));
                for (int j = 0; j < preparedLinesFromFile.size(); j++){
                    String[] secondList = preparedLinesFromFile.get(j).get(0).split("\\s+");
                    Integer x2 = Integer.parseInt(secondList[1]);
                    Integer y2 = Integer.parseInt(secondList[2]);

                    temporary.add(countEuclideanDistance(x1, y1, x2, y2));
                }
                distanceMatrix.add(temporary);
            }
        }
    }

    private static Integer countEuclideanDistance(Integer x1, Integer y1, Integer x2, Integer y2){
        Integer squareFirstSubtraction = subtractionSquared(x1, x2);
        Integer squareSecondSubtraction = subtractionSquared(y1, y2);
        double distance = Math.sqrt(squareFirstSubtraction+squareSecondSubtraction);
        return Math.toIntExact(round(distance));
    }

    private static Integer subtractionSquared(Integer a, Integer b){
        return ((b-a)*(b-a));
    }
}
