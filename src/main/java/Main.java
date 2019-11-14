import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class Main {

    public static void main(String[] args) {
        Main main = new Main();
        int arg = Integer.parseInt(args[0]);
        main.calculator(arg);
    }

    private void calculator(int numberElements) {
        List<String> lines = this.getData();
        lines = lines.stream()
                .map(String::trim)
                .collect(toList());

        List<Matrix> matrixA = getMatrixList(lines, "A");
        List<Matrix> matrixB = getMatrixList(lines, "B");
        List<Matrix> matrixX = getMatrixList(lines, "X");
        calculateDistance(matrixA, matrixB, matrixX, numberElements);
        calculateAverageDistance(matrixA, matrixB, matrixX);

    }

    private void calculateAverageDistance(List<Matrix> matrixA, List<Matrix> matrixB, List<Matrix> matrixX) {
        int nofFeaturesA = matrixA.get(0).getPoint().length;
        int nofFeaturesB = matrixB.get(0).getPoint().length;
        int nofFeaturesX = matrixX.get(0).getPoint().length;
        double[] tabAverageA = new double[nofFeaturesA];
        double[] tabAverageB = new double[nofFeaturesB];
        double[] tabAverageX = new double[nofFeaturesX];

        for (int i = 0; i < nofFeaturesA; i++) {
            tabAverageA[i] = avgFeatures(matrixA, i);
            tabAverageB[i] = avgFeatures(matrixB, i);
            tabAverageX[i] = avgFeatures(matrixX, i);
        }

        double distanceA = getDistance(tabAverageA, tabAverageX);
        double distanceB = getDistance(tabAverageB, tabAverageX);

        if (distanceA > distanceB) {
            System.out.println("Srednia punktów  nalezy do zbioru B");
        } else {
            System.out.println("Srednia punktów  nalezy do zbioru A");
        }
    }

    private double avgFeatures(List<Matrix> matrix, int featureIndex) {
        double sum = 0;
        for (Matrix m : matrix) {
            sum += m.getPoint()[featureIndex];
        }
        return sum / matrix.size();
    }

    private void calculateDistance(List<Matrix> matrixA, List<Matrix> matrixB, List<Matrix> matrixX, int numberElements) {
        List<Double> distanceA = new ArrayList<>();
        List<Double> distanceB = new ArrayList<>();
        for (Matrix X : matrixX) {
            for (Matrix A : matrixA) {
                distanceA.add(getDistance(A.getPoint(), X.getPoint()));
            }
            for (Matrix B : matrixB) {
                distanceB.add(getDistance(B.getPoint(), X.getPoint()));
            }
            compareMatrix(distanceA, distanceB, X);
            compareMatrixForKNumberElements(distanceA, distanceB, X, numberElements);
            distanceA = new ArrayList<>();
            distanceB = new ArrayList<>();
        }
    }

    private void compareMatrixForKNumberElements(List<Double> distanceA, List<Double> distanceB, Matrix matrixX, int numberElements) {
        System.out.println("KNN: ");
        int numberA = 0;
        int numberB = 0;
        for (int i = 0; i < numberElements; i++) {
            if (Collections.min(distanceA) < Collections.min(distanceB)) {
                numberA++;
                distanceA.remove(Collections.min(distanceA));
            } else {
                numberB++;
                distanceB.remove(Collections.min(distanceB));
            }
        }
        if (numberA > numberB) {
            System.out.println(numberA + " elementy nalezą do zbioru A na " + numberElements + " elementy dla punktu " + matrixX.toString());
        } else {
            System.out.println(numberB + " elementy naleza do zbioru B na " + numberElements + " elementy dla punktu " + matrixX.toString());
        }
    }

    private void compareMatrix(List<Double> distanceA, List<Double> distanceB, Matrix matrixX) {
        System.out.println("NN: ");
        if (Collections.min(distanceA) > Collections.min(distanceB)) {
            System.out.println("Punkt " + matrixX.toString() + " nalezy do zbioru B");
        } else {
            System.out.println("Punkt " + matrixX.toString() + " nalezy do zbioru A");
        }
    }

    private double getDistance(double[] list, double[] X) {
        double distance = 0;
        for (int i = 0; i < list.length; i++) {
            distance = distance + Math.pow((list[i] - X[i]), 2);
        }
        distance = Math.sqrt(distance);
        return distance;
    }

    private List<String> getData() {
        Path filePath = Paths.get(this.getClass().getResource("dane.txt").getPath());
        return readFile(filePath);
    }

    private static List<String> readFile(Path file) {
        try {
            return Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Cannot read close voucher agreement file " + e);
        }
    }

    private List<Matrix> getMatrixList(List<String> lines, String identifier) {
        lines = lines.stream()
                .filter(line -> line.contains(identifier))
                .map(line -> line.replaceAll(identifier + ";", ""))
                .collect(Collectors.toList());
        return lines.stream()
                .map(line -> convertToMatrix(identifier, line))
                .collect(Collectors.toList());
    }

    private Matrix convertToMatrix(String identifier, String line) {
        double[] tab = Arrays.stream(line.split("\\;")).mapToDouble(Double::valueOf).toArray();
        return new Matrix(identifier, tab);
    }
}
