package ru.nsu.fit.moment_calculators;

/**
 * Класс представляет собой калькулятор подсчета квадрупольного момента для круглой области с центром в (x, y).
 */
public class QuadrupoleMomentCalculator extends MomentCalculator {
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
        // arrQ[0] = Qxx, arrQ[1] = Qxy, arrQ[2] = Qyy
        int valueX = x - centerX;
        int valueY =  y - centerY;
        double squareY = Math.pow(valueY, 2);
        double coeffQxx = 2 * squareX - squareY;
        int coeffQxy = valueX * valueY;
        double coeffQyy = 2 * squareY - squareX;
        arrQ[0] += matrix[y][x] * coeffQxx; // Qxx += qi * (2 * Xi^2 - Yi^2)
        arrQ[1] += matrix[y][x] * coeffQxy; // Qxy += qi * (Xi * Yi)
        arrQ[2] += matrix[y][x] * coeffQyy; // Qyy += qi * (2 * Yi^2 - Xi^2)
        if (x != x1) {
            arrQ[0] += matrix[y][x1] * coeffQxx;
            arrQ[1] -= matrix[y][x1] * coeffQxy;
            arrQ[2] += matrix[y][x1] * coeffQyy;
        }
        if (y != y1) {
            arrQ[0] += matrix[y1][x] * coeffQxx;
            arrQ[1] -= matrix[y1][x] * coeffQxy;
            arrQ[2] += matrix[y1][x] * coeffQyy;
            if (x != x1) {
                arrQ[0] += matrix[y1][x1] * coeffQxx;
                arrQ[1] += matrix[y1][x1] * coeffQxy;
                arrQ[2] += matrix[y1][x1] * coeffQyy;
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

            for (int y = startY; y <= endY; y++) {
                if (!mask[offsetX][y - startY]) continue;
                int y1 = doubleCenterY - y;
                calcQuadrupoleMoment(x, y, x1, y1, squareX, centerX, centerY);
            }
        }
        arrQ[0] /= 2; // а надо ли ещё их делить на 2?
        arrQ[1] *= 1.5;
        arrQ[2] /= 2;
        return arrQ;
    }
}
