package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CoordinateList {
    String path;
    int[][] intCoordinateList;

    CoordinateList(String path){
        List<List<String>> preparedLinesFromFileKroA = prepareLinesFromFile(path);
        intCoordinateList = convertTo2DArray(convertStringToListMatrix(preparedLinesFromFileKroA));
    }

    private static List<List<String>> prepareLinesFromFile(String path) {
        List<List<String>> correctsLines = new ArrayList<>();
        int count = -1;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                List<String> correctLine = new ArrayList<>();
                correctLine.add(line);
                count++;
                if (count > 5 && count < 106) { //todo: change 5 and 106
                    correctsLines.add(correctLine);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return correctsLines;
    }


    private static List<List<Integer>> convertStringToListMatrix(List<List<String>> stringMatrix){
        List<List<Integer>> intMatrix = new ArrayList<>();
        for (int i = 0; i < stringMatrix.size(); i++) {
            List<String> row = stringMatrix.get(i);
            List<Integer> row2 = splitAndConvertToNumbers(row.get(0));
            intMatrix.add(row2);
        }
        return intMatrix;
    }

    private static List<Integer> splitAndConvertToNumbers(String inputString) {
        String[] tokens = inputString.split("\\s+");
        return Arrays.asList(
                Integer.parseInt(tokens[0]),
                Integer.parseInt(tokens[1]),
                Integer.parseInt(tokens[2])
        );
    }

    public static int[][] convertTo2DArray(List<List<Integer>> listOfLists) {
        int rows = listOfLists.size();
        int columns = listOfLists.get(0).size();
        int[][] table = new int[rows][columns];

        for (int i = 0; i < rows; i++) {
            List<Integer> row = listOfLists.get(i);
            for (int j = 0; j < columns; j++) {
                table[i][j] = row.get(j);
            }
        }

        for (int i = 0; i < 100; i++){
            table[i][0] = table[i][0]-1;
        }

        return table;
    }

}