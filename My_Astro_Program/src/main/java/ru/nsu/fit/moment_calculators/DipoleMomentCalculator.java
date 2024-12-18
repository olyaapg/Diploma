package ru.nsu.fit.moment_calculators;

public class DipoleMomentCalculator extends MomentCalculator {

    public DipoleMomentCalculator(double[][] matrix, boolean[][] mask) {
        super(matrix, mask);
    }

    private void calcSums(int x, int y, int x1, int y1, int centerY, double[] sums) {
        // sums[0] = sumX, sums[1] = sumY, sums[2] = sumX1, sums[3] = sumY1
        sums[0] = matrix[y][x]; // sumX += яркость (y; x)
        sums[1] = matrix[y][x]; // sumY += яркость (y; x)
        sums[2] = 0;
        sums[3] = 0;
        if (x1 != x) {
            sums[2] += matrix[y][x1]; // sumX1 += яркость (y; x1)
            sums[1] += matrix[y][x1]; // sumY += яркость (y; x1)
        }
        if (y1 != y) {
            sums[0] += matrix[y1][x]; // sumX += яркость (y1; x)
            sums[3] += matrix[y1][x]; // sumY1 += яркость (y1; x)
            if (x1 != x) {
                sums[2] += matrix[y1][x1]; // sumX1 += яркость (y1; x1)
                sums[3] += matrix[y1][x1]; // sumY1 += яркость (y1; x1)
            }
            sums[3] *= (y1 - centerY);
        }
    }

    @Override
    public double[] calculate(int centerX, int centerY, int startX, int endX, int startY, int endY) {
        double[] pXpY = new double[]{0, 0};
        int doubleCenterX = 2 * centerX;
        int doubleCenterY = 2 * centerY;
        double[] sums = new double[4];  // sums[0] = sumX, sums[1] = sumY, sums[2] = sumX1, sums[3] = sumY1
        for (int x = startX; x <= endX; x++) {
            int offsetX = x - startX;
            int x1 = doubleCenterX - x;
            double distanceX = (double) x - centerX;
            double distanceX1 = (double) x1 - centerX;
            for (int y = startY; y <= endY; y++) {
                if (!mask[offsetX][y - startY]) continue;
                int y1 = doubleCenterY - y;
                double distanceY = (double) y - centerY;
                calcSums(x, y, x1, y1, centerY, sums);
                pXpY[0] += distanceX * sums[0] + distanceX1 * sums[2];
                pXpY[1] += distanceY * sums[1] + sums[3];
            }
        }
        return pXpY;
    }
}
