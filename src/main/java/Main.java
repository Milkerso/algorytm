import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        List<String> listA = lines.stream().filter(line -> line.contains("A")).collect(toList());
        List<String> listB = lines.stream().filter(line -> line.contains("B")).collect(toList());
        List<String> listX = lines.stream().filter(line -> line.contains("X")).collect(toList());
        List<Double> distanceA = new ArrayList<>();
        List<Double> distanceB = new ArrayList<>();
        double[] tabX = null;
        for (String X : listX) {
            X = X.replace("X;", "");
            tabX = Arrays.stream(X.split("\\;")).mapToDouble(Double::valueOf).toArray();
        }
        for (String A : listA) {
            A = A.replace("A;", "");
            double[] tabA = Arrays.stream(A.split("\\;")).mapToDouble(Double::valueOf).toArray();
            distanceA.add(getDistance(tabA, tabX));
        }
        for (String B : listB) {
            B = B.replace("B;", "");
            double[] tabB = Arrays.stream(B.split("\\;")).mapToDouble(Double::valueOf).toArray();
            distanceB.add(getDistance(tabB, tabX));
        }
        if (Collections.min(distanceA) > Collections.min(distanceB)) {
            System.out.println("Punkt X nalezy do zbioru B");
        } else {
            System.out.println("Punkt X nalezy do zbioru A");
        }
        Collections.sort(distanceA);
        Collections.sort(distanceB);
        System.out.println("Odległości A: " + distanceA);
        System.out.println("Odległości B: " + distanceB);
        int j = 0;
        int c = 0;
        int numberA = 0;
        int numberB = 0;
        for (int i = 0; i < numberElements; i++) {
            if (distanceA.get(j) < distanceB.get(c)) {
                numberA++;
                j++;
                System.out.println("A: " + distanceA.get(j));
            } else {
                numberB++;
                c++;
                System.out.println("B: " + distanceB.get(c));
            }
        }
        if (numberA > numberB) {
            System.out.println(numberA + " elementy nalezą do zbioru A na " + numberElements + " elementów");
        } else {
            System.out.println(numberB + " naleza do zbiorą do zbioru B na " + numberElements + " elementów");
        }

//        double sumA = 0;
//        double sumB = 0;
//        for (int i = 0; i < numberElements; i++) {
//            sumA += distanceA.get(i);
//            sumB += distanceB.get(i);
//        }
//        if (sumA > sumB) {
//            System.out.println("Punkt X nalezy do zbioru B dla " + numberElements + " elementów");
//        } else {
//            System.out.println("Punkt X nalezy do zbioru A dla " + numberElements + " elementów");
//
//        }
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
}
