package ru.nsu.fit.moment_calculators;

/**
 * Класс представляет собой калькулятор подсчета дипольного момента для круглой области с центром в (x, y).
 */
public class DipoleMomentCalculator extends WindowCalculator {
    /**
     * Создает объект класса DipoleMomentCalculator, требует необходимые для подсчета дипольного момента параметры.
     *
     * @param matrix нормализованная матрица изображения.
     * @param mask   маска, представляющая собой левую верхнюю четверть окружности (окна).
     */
    public DipoleMomentCalculator(double[][] matrix, boolean[][] mask) {
        super(matrix, mask);
    }

    private void calcSums(int x, int y, int x1, int y1, int centerY, double[] sums) {
        // sums[0] = sumX, sums[1] = sumY, sums[2] = sumX1, sums[3] = sumY1
        double qi = matrix[y][x] - avgVal;
        sums[0] = qi; // sumX += яркость (y; x)
        sums[1] = qi; // sumY += яркость (y; x)
        sums[2] = 0;
        sums[3] = 0;
        if (x1 != x) {
            qi = matrix[y][x1] - avgVal;
            sums[2] += qi; // sumX1 += яркость (y; x1)
            sums[1] += qi; // sumY += яркость (y; x1)
        }
        if (y1 != y) {
            qi = matrix[y1][x] - avgVal;
            sums[0] += qi; // sumX += яркость (y1; x)
            sums[3] += qi; // sumY1 += яркость (y1; x)
            if (x1 != x) {
                qi = matrix[y1][x1] - avgVal;
                sums[2] += qi; // sumX1 += яркость (y1; x1)
                sums[3] += qi; // sumY1 += яркость (y1; x1)
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
            for (int y = startY; y <= endY; y++) {
                if (!mask[offsetX][y - startY]) continue;
                int y1 = doubleCenterY - y;
                double distanceY = (double) y - centerY;
                calcSums(x, y, x1, y1, centerY, sums);
                pXpY[0] += distanceX * sums[0] - distanceX * sums[2];
                pXpY[1] += distanceY * sums[1] + sums[3];
            }
        }
        return pXpY;
    }
}
