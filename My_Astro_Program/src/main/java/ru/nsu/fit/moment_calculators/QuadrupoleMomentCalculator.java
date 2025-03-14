package ru.nsu.fit.moment_calculators;

/**
 * Класс представляет собой калькулятор подсчета квадрупольного момента для круглой области с центром в (x, y).
 */
public class QuadrupoleMomentCalculator extends WindowCalculator {
    private double[] arrQ;

    /**
     * Создает объект класса QuadrupoleMomentCalculator, требует необходимые для подсчета дипольного момента параметры.
     *
     * @param matrix нормализованная матрица изображения.
     * @param mask   маска, представляющая собой левую верхнюю четверть окружности (окна).
     */
    public QuadrupoleMomentCalculator(double[][] matrix, boolean[][] mask) {
        super(matrix, mask);
    }

    private void calcQuadrupoleMoment(int x, int y, int x1, int y1, double squareX, int centerX, int centerY) {
        // arrQ[0] = Qxx, arrQ[1] = Qxy, arrQ[2] = Qyy, arrQ[3] = Qzz
        int valueX = x - centerX;
        int valueY = y - centerY;
        double squareY = Math.pow(valueY, 2);
        double coeffQxx = 2 * squareX - squareY;
        int coeffQxy = 3 * valueX * valueY;
        double coeffQyy = 2 * squareY - squareX;
        double coeffQzz = -squareX - squareY;
        arrQ[0] += matrix[y][x] * coeffQxx; // Qxx += qi * (2 * Xi^2 - Yi^2)
        arrQ[1] += matrix[y][x] * coeffQxy; // Qxy += qi * (3 * Xi * Yi)
        arrQ[2] += matrix[y][x] * coeffQyy; // Qyy += qi * (2 * Yi^2 - Xi^2)
        arrQ[3] += matrix[y][x] * coeffQzz; // Qzz += qi * (-Xi^2 - Yi^2)
        if (x != x1) {
            arrQ[0] += matrix[y][x1] * coeffQxx;
            arrQ[1] -= matrix[y][x1] * coeffQxy;
            arrQ[2] += matrix[y][x1] * coeffQyy;
            arrQ[3] += matrix[y][x1] * coeffQzz;
        }
        if (y != y1) {
            arrQ[0] += matrix[y1][x] * coeffQxx;
            arrQ[1] -= matrix[y1][x] * coeffQxy;
            arrQ[2] += matrix[y1][x] * coeffQyy;
            arrQ[3] += matrix[y1][x] * coeffQzz;
            if (x != x1) {
                arrQ[0] += matrix[y1][x1] * coeffQxx;
                arrQ[1] += matrix[y1][x1] * coeffQxy;
                arrQ[2] += matrix[y1][x1] * coeffQyy;
                arrQ[3] += matrix[y1][x1] * coeffQzz;
            }
        }
    }

    @Override
    public double[] calculate(int centerX, int centerY, int startX, int endX, int startY, int endY) {
        arrQ = new double[]{0, 0, 0, 0}; // arrQ[0] = Qxx, arrQ[1] = Qxy, arrQ[2] = Qyy, arrQ[3] = Qzz.
        int doubleCenterX = 2 * centerX;
        int doubleCenterY = 2 * centerY;
        for (int x = startX; x <= endX; x++) {
            int offsetX = x - startX;
            int x1 = doubleCenterX - x;
            double squareX = Math.pow((double) x - centerX, 2);
            for (int y = startY; y <= endY; y++) {
                if (!mask[offsetX][y - startY]) continue;
                int y1 = doubleCenterY - y;
                calcQuadrupoleMoment(x, y, x1, y1, squareX, centerX, centerY);
            }
        }
        return arrQ;
    }
}
