import java.awt.image.AreaAveragingScaleFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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
        matrixA = getMatrixList(lines, "A");
        matrixB = getMatrixList(lines, "B");
        matrixX = getMatrixList(lines, "X");
        List<Matrix> matrixListMiA = externalAlgorithm(matrixA, numberElements);
        List<Matrix> matrixListMiB = externalAlgorithm(matrixB, numberElements);
        compareMiList(matrixListMiA, matrixListMiB, matrixX, numberElements);


    }

    private void compareMiList(List<Matrix> matrixListMiA, List<Matrix> matrixListMiB, List<Matrix> matrixX, int numberElements) {
        List<List<Matrix>> matrixListListMiA = new ArrayList<>();
        List<List<Matrix>> matrixListListMiB = new ArrayList<>();
        List<List<Double>> distanceA = new ArrayList<>();
        List<List<Double>> distanceB = new ArrayList<>();
        for (int i = 0; i < numberElements; i++) {
            int finalI = i;
            matrixListListMiA.add(matrixListMiA.stream().filter(matrix -> matrix.getIdentifier().equals(String.valueOf(finalI))).collect(toList()));
            matrixListListMiB.add(matrixListMiB.stream().filter(matrix -> matrix.getIdentifier().equals(String.valueOf(finalI))).collect(toList()));
        }
        matrixListListMiA = matrixListListMiA.stream().filter(matrixList -> !matrixList.isEmpty()).collect(toList());
        matrixListListMiB = matrixListListMiB.stream().filter(matrixList -> !matrixList.isEmpty()).collect(toList());

        for (int i = 0; i < matrixX.size(); i++) {
            for (int j = 0; j < matrixListListMiA.size() - 1; j++) {
                System.out.println("Błąd" + j + "   " + i);
                distanceA.add(calculateDistance(matrixListListMiA.get(j), matrixListListMiA.get(j + 1), matrixX, 1));
            }
        }
        for (int i = 0; i < matrixX.size(); i++) {
            for (int j = 0; j < matrixListListMiB.size() - 1; j++) {
                System.out.println("Błąd" + j + "   " + i);
                distanceB.add(calculateDistance(matrixListListMiB.get(j), matrixListListMiB.get(j + 1), matrixX, 1));
            }
        }
        List<Double> minDistanceA = new ArrayList<>();
        for (int i = 0; i < matrixX.size(); i++) {
            for (int j = 0; j < distanceA.size(); j++) {
                minDistanceA.add(Collections.min(distanceA.get(j)));
            }
        }
        List<Double> minDistanceB = new ArrayList<>();
        for (int i = 0; i < matrixX.size(); i++) {
            for (int j = 0; j < distanceB.size(); j++) {
                minDistanceB.add(Collections.min(distanceB.get(j)));
            }
        }
        if (Collections.min(minDistanceA) > Collections.min(minDistanceB)) {
            System.out.println("Nalezy do B");
        } else {
            System.out.println("Nalezy do A");
        }

//        System.out.println(Collections.min(distance.keySet()));
    }

    private List<Matrix> externalAlgorithm(List<Matrix> matrixList, int numberElements) {
        List<Matrix> randElements = new ArrayList<>();
//        for (int i = 0; i < numberElements; i++) {
//            int randomNumber = generator.nextInt(matrix.size());
//            randElements.add(matrix.get(randomNumber));
//            matrix.remove(randomNumber);
//        }
//        randElements.forEach(System.out::println);
//        System.out.println();
//        matrix.forEach(System.out::println);
        randElements.add(matrixList.get(matrixList.size() - 1));
        randElements.add(matrixList.get(matrixList.size() - 2));
        randElements.add(matrixList.get(matrixList.size() - 3));
        List<Matrix> randElementsFinal = new ArrayList<>(randElements);
        matrixList.remove(matrixList.size() - 1);
        matrixList.remove(matrixList.size() - 1);
        matrixList.remove(matrixList.size() - 1);
        boolean firstTime = true;
        List<String> identifierEmptyList = new ArrayList<>();
        List<Matrix> miList = matrixList;
        List<Matrix> miListTemp = new ArrayList<>(miList);
        List<Matrix> randElementsEnded = new ArrayList<>(randElements);

        int j = 0;
        do {
            miListTemp = new ArrayList<>(miList);
            randElementsEnded = new ArrayList<>(randElements);
            miList = setIdentifierForMi(miList, randElements);
            firstTime = backToTable(randElementsFinal, miList, firstTime);
            System.out.println(randElements.size() + "dadadas");
            randElements = calculateAvgForMiList(randElements, miList);
            randElements.forEach(System.out::println);
            j++;
        } while (isEnd(miList, miListTemp, randElements, randElementsEnded));
        System.out.println(j + "taki rozmiar");
        return miList;
    }

    private boolean isEnd(List<Matrix> miList, List<Matrix> miListTemp, List<Matrix> randElements, List<Matrix> randElementsEnded) {
        for (int i = 0; i < miList.size(); i++) {
            if (!miList.get(i).getIdentifier().equals(miListTemp.get(i).getIdentifier())) {
                return true;
            }
        }
        for (int i = 0; i < randElements.size(); i++) {
            for (int j = 0; j < randElements.get(0).getPoint().length; j++) {
                if (Math.abs(randElements.get(i).getPoint()[j] - randElementsEnded.get(i).getPoint()[j]) > 0.1) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean backToTable(List<Matrix> randElementsFinal, List<Matrix> miList, boolean firstTime) {
        List<String> identifierEmptyList = new ArrayList<>();
        if (firstTime) {
            for (int i = 0; i < randElementsFinal.size(); i++)
                randElementsFinal.get(i).setIdentifier(String.valueOf(i));
        }
        for (int i = 0; i < randElementsFinal.size(); i++) {
            int finalI = i;
            if (miList.stream().anyMatch(matrix -> matrix.getIdentifier().equals(String.valueOf(randElementsFinal.get(finalI).getIdentifier())))) {
                identifierEmptyList.add(String.valueOf(i));
            }
        }
        for (int i = 0; i < randElementsFinal.size(); i++) {
            int finalI = i;
            if (miList.stream().anyMatch(matrix -> matrix.getIdentifier().contains(String.valueOf(randElementsFinal.get(finalI).getIdentifier())))) {
                miList.add(randElementsFinal.get(i));
                randElementsFinal.remove(i);
                i--;
            }
        }
        return false;
    }

    private List<Matrix> calculateAvgForMiList(List<Matrix> randElements, List<Matrix> miList) {
        List<Matrix> randElementsTemp = new ArrayList<>();
        double[] tabTemp = new double[miList.get(0).getPoint().length];
        for (int j = 0; j < randElements.size(); j++) {

            for (int i = 0; i < miList.get(0).getPoint().length; i++) {
                int finalJ = j;
                tabTemp[i] = avgFeatures(miList.stream().filter(matrix -> matrix.getIdentifier().equals(String.valueOf(finalJ))).collect(toList()), i);
            }
            int finalJ1 = j;
            if (miList.stream().noneMatch(matrix -> String.valueOf(finalJ1).contains(matrix.getIdentifier()))) {
                randElementsTemp.add(new Matrix(String.valueOf(j), randElements.get(j).getPoint()));
            } else {
                randElementsTemp.add(new Matrix(String.valueOf(j), tabTemp));
            }
            tabTemp = new double[miList.get(0).getPoint().length];
        }
        return randElementsTemp;
    }

    private List<Matrix> randKElements(int numberElements, List<Matrix> matrix) {
        Random generator = new Random();
        List<Matrix> randElements = new ArrayList<>();
        for (int i = 0; i < numberElements; i++) {
            int randomNumber = generator.nextInt(matrix.size());
            randElements.add(matrix.get(randomNumber));
            matrix.remove(randomNumber);
        }
        return randElements;
    }

    private List<Matrix> setIdentifierForMi(List<Matrix> matrixList, List<Matrix> randElements) {
        List<Matrix> miList = new ArrayList<>();
        for (Matrix matrix : matrixList) {
            List<Double> tempDistance = new ArrayList<>();
            for (Matrix randElement : randElements) {
                tempDistance.add(getDistance(matrix.getPoint(), randElement.getPoint()));
            }
            double smallestValue = Collections.min(tempDistance);
            miList.add(new Matrix(String.valueOf(tempDistance.indexOf(smallestValue)), matrix.getPoint()));
        }
        return miList;
    }

    private double calculateAverageDistance(List<Matrix> matrixA, List<Matrix> matrixB, List<Matrix> matrixX) {
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
//        Arrays.stream(tabAverageA).forEach(System.out::println);
//        Arrays.stream(tabAverageX).forEach(System.out::println);
//        System.out.println(distanceA);
        if (distanceA > distanceB) {
            System.out.println("Srednia punktów  nalezy do zbioru B");
            return distanceB;
        } else {
            System.out.println("Srednia punktów  nalezy do zbioru A");
            return distanceA;
        }
    }

    private double avgFeatures(List<Matrix> matrix, int featureIndex) {
        System.out.println();
        double sum = 0;
        for (Matrix m : matrix) {
            System.out.println(m.getPoint()[featureIndex]);
            sum += m.getPoint()[featureIndex];
        }
        System.out.println(sum);
        return sum / matrix.size();
    }

    private List<Double> calculateDistance(List<Matrix> matrixA, List<Matrix> matrixB, List<Matrix> matrixX, int numberElements) {
        List<Double> distanceA = new ArrayList<>();
        List<Double> distanceB = new ArrayList<>();
        List<Double> distance = new ArrayList<>();
        for (Matrix X : matrixX) {
            for (Matrix A : matrixA) {
                distanceA.add(getDistance(A.getPoint(), X.getPoint()));
            }
            for (Matrix B : matrixB) {
                distanceB.add(getDistance(B.getPoint(), X.getPoint()));
            }
            compareMatrix(distanceA, distanceB, X);
            if (Collections.min(distanceA) > Collections.min(distanceB)) {
                distance.add(Collections.min(distanceB));
            } else {
                distance.add(Collections.min(distanceA));
            }
            compareMatrixForKNumberElements(distanceA, distanceB, X, numberElements);
            distanceA = new ArrayList<>();
            distanceB = new ArrayList<>();
        }
        return distance;
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
        Path filePath = Paths.get(this.getClass().getResource("daneTemp.txt").getPath());
        System.out.println(filePath);
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
