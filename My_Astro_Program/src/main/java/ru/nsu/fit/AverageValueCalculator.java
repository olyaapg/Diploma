package ru.nsu.fit;

import ru.nsu.fit.moment_calculators.WindowCalculator;

public class AverageValueCalculator extends WindowCalculator {

    protected AverageValueCalculator(double[][] matrix, boolean[][] mask) {
        super(matrix, mask);
    }

    @Override
    public double[] calculate(int centerX, int centerY, int startX, int endX, int startY, int endY) {
        double sum = 0;
        int n = 0;
        int doubleCenterX = 2 * centerX;
        int doubleCenterY = 2 * centerY;
        for (int i = startX; i <= endX; i++) {
            int offsetX = i - startX;
            int i1 = doubleCenterX - i;
            for (int j = startY; j <= endY; j++) {
                if (!mask[offsetX][j - startY]) continue;
                int j1 = doubleCenterY - j;
                n++;
                sum += matrix[j][i];
                if (j1 != j) {
                    n++;
                    sum += matrix[j1][i];
                }
                if (i1 != i) {
                    n++;
                    sum += matrix[j][i1];
                    if (j1 != j) {
                        n++;
                        sum += matrix[j1][i1];
                    }
                }
            }
        }
        return new double[]{sum / n};
    }
}
