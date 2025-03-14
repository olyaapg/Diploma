package ru.nsu.fit.moment_calculators;

/**
 * Класс представляет собой калькулятор подсчета нулевого момента для круглой области с центром в (x, y).
 */
public class ZeroMomentCalculator extends WindowCalculator {
    private final int radius;
    private double prevSum;

    /**
     * Создает объект класса ZeroMomentCalculator, требует необходимые для подсчета нулевого момента параметры.
     *
     * @param matrix нормализованная матрица изображения.
     * @param mask   маска, представляющая собой левую верхнюю четверть окружности (окна).
     * @param radius радиус области (окна).
     */
    public ZeroMomentCalculator(double[][] matrix, boolean[][] mask, int radius) {
        super(matrix, mask);
        this.radius = radius;
    }

    private double calcReflectedPixels(int x, int y, int x1, int y1) {
        // (x; y)
        double sum = matrix[y][x];
        // (x1; y)
        if (x1 != x) {
            sum += matrix[y][x1];
        }
        // (x; y1)
        if (y1 != y) {
            sum += matrix[y1][x];
            // (x1; y1)
            if (x1 != x) {
                sum += matrix[y1][x1];
            }
        }
        return sum;
    }

    private double sumUpWindow(int centerX, int centerY, int startX, int endX, int startY, int endY) {
        double sum = 0.0;
        int doubleCenterX = 2 * centerX;
        int doubleCenterY = 2 * centerY;
        // Ограничиваем прямоугольную область
        for (int x = startX; x <= endX; x++) {
            int x1 = doubleCenterX - x;
            int offsetX = x - startX;
            // у всегда от 0 до radius, поскольку это первое окно ряда
            for (int y = startY; y <= endY; y++) {
                // Проверяем, находится ли точка внутри окружности с помощью маски
                if (mask[offsetX][y]) {
                    int y1 = doubleCenterY - y;
                    sum += calcReflectedPixels(x, y, x1, y1);
                }
            }
        }
        prevSum = sum;
        return sum;
    }

    private double calcNextWindow(int centerX, int centerY, int startX, int endX, int startY, int endY) {
        int doubleCenterX = 2 * centerX;
        int doubleCenterY = 2 * centerY;
        for (int x = startX; x <= endX; x++) {
            int offsetX = x - startX;
            var x1 = doubleCenterX - x;
            for (int y = startY; y <= endY; y++) {
                if (mask[offsetX][y - startY]) {
                    var y1 = doubleCenterY - y;
                    prevSum -= matrix[y - 1][x];
                    prevSum += matrix[y1][x];
                    if (x != x1) { // предполагается, что мы всегда сдвигаем окно только вдоль одной оси, а не двух
                        prevSum -= matrix[y - 1][x1];
                        prevSum += matrix[y1][x1];
                    }
                    break;
                }
            }
        }
        return prevSum;
    }

    @Override
    public double[] calculate(int centerX, int centerY, int startX, int endX, int startY, int endY) {
        if (endY == radius) {
            return new double[]{sumUpWindow(centerX, centerY, startX, endX, startY, endY)};
        } else {
            return new double[]{calcNextWindow(centerX, centerY, startX, endX, startY, endY)};
        }
    }
}
