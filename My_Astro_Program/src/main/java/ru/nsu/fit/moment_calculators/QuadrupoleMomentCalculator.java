package ru.nsu.fit.moment_calculators;

public class QuadrupoleMomentCalculator extends MomentCalculator {
    private double[] arrQ;

    public QuadrupoleMomentCalculator(double[][] matrix, boolean[][] mask) {
        super(matrix, mask);
    }

    private void calcQuadrupoleMoment(int x, int y, int x1, int y1, double squareX, double squareX1, int centerX, int centerY) {
        // arrQ[0] = Qxx, arrQ[1] = Qxy, arrQ[2] = Qyy
        int valueX = x - centerX;
        int valueX1 = x1 - centerX;
        int valueY =  y - centerY;
        int valueY1 = y1 - centerY;
        double squareY = Math.pow(valueY, 2);
        double squareY1;
        arrQ[0] += matrix[y][x] * (2 * squareX - squareY);
        arrQ[1] += matrix[y][x] * valueX * valueY;
        arrQ[2] += matrix[y][x] * (2 * squareY - squareX);
        if (valueX1 != valueX) {
            arrQ[0] += matrix[y][x1] * (2 * squareX1 - squareY);
            arrQ[1] += matrix[y][x1] * valueX1 * valueY;
            arrQ[2] += matrix[y][x1] * (2 * squareY - squareX1);
        }
        if (valueY1 != valueY) {
            squareY1 = Math.pow(valueY1, 2);
            arrQ[0] += matrix[y1][x] * (2 * squareX - squareY1);
            arrQ[1] += matrix[y1][x] * valueX * valueY1;
            arrQ[2] += matrix[y1][x] * (2 * squareY1 - squareX);
            if (valueX1 != valueX) {
                arrQ[0] += matrix[y1][x1] * (2 * squareX1 - squareY1);
                arrQ[1] += matrix[y1][x1] * valueX1 * valueY1;
                arrQ[2] += matrix[y1][x1] * (2 * squareY1 - squareX1);
            }
        }
    }

    @Override
    public double[] calculate(int centerX, int centerY, int startX, int endX, int startY, int endY) {
        arrQ = new double[]{0, 0, 0}; // arrQ[0] = Qxx, arrQ[1] = Qxy, arrQ[2] = Qyy
        int doubleCenterX = 2 * centerX;
        int doubleCenterY = 2 * centerY;
        for (int x = startX; x <= endX; x++) {
            int offsetX = x - startX;
            int x1 = doubleCenterX - x;
            double squareX = Math.pow((double) x - centerX, 2);
            double squareX1 = Math.pow((double) x1 - centerX, 2);
            for (int y = startY; y <= endY; y++) {
                if (!mask[offsetX][y - startY]) continue;
                int y1 = doubleCenterY - y;
                calcQuadrupoleMoment(x, y, x1, y1, squareX, squareX1, centerX, centerY);
            }
        }
        return arrQ;
    }
}
