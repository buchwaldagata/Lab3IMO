package org.example;

import java.util.List;

class DistanceMatrix {

    Long[][] distanceMatrix;
    public DistanceMatrix(int[][] intCoordinateList){
        distanceMatrix = countEuclideanDistanceFromFile(intCoordinateList);
    }


    private static Long[][] countEuclideanDistanceFromFile(int[][] intCoordinateList){
        Long[][] distanceMatrix = new Long[100][100];
        if (intCoordinateList.length > 1) {
            for (int i = 0; i < intCoordinateList.length; i++) {
                for (int j = 0; j < intCoordinateList.length; j++){

                    int x1 = intCoordinateList[i][1];
                    int y1 = intCoordinateList[i][2];
                    int x2 = intCoordinateList[j][1];
                    int y2 = intCoordinateList[j][2];

                    Long countEuclideanDistance = countEuclideanDistance(x1, y1, x2, y2);
                    distanceMatrix[i][j] = countEuclideanDistance;
                }
            }
        }
        return distanceMatrix;
    }


    private static Long countEuclideanDistance(Integer x1, Integer y1, Integer x2, Integer y2){
        Integer squareFirstSubtraction = subtractionSquared(x1, x2);
        Integer squareSecondSubtraction = subtractionSquared(y1, y2);
        double distance = Math.sqrt(squareFirstSubtraction+squareSecondSubtraction);
        return Math.round(distance);
    }

    private static Integer subtractionSquared(Integer a, Integer b){
        return ((b-a)*(b-a));
    }


}