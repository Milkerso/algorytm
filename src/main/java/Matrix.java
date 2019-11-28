import java.util.Arrays;

public class Matrix {

    private String identifier;
    private double[] point;

    public Matrix(String identifier, double[] point) {
        this.identifier = identifier;
        this.point = point;
    }
    public Matrix() {
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public double[] getPoint() {
        return point;
    }

    public void setPoint(double[] point) {
        this.point = point;
    }

    @Override
    public String toString() {
        return "Matrix{" +
                "identifier='" + identifier + '\'' +
                ", point=" + Arrays.toString(point) +
                '}';
    }
}

