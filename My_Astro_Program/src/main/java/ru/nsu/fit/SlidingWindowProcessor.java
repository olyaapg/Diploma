package ru.nsu.fit;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SlidingWindowProcessor {
    private static final Logger LOGGER = LogManager.getLogger(SlidingWindowProcessor.class);

    private final TiffProcessor tiffProcessor;
    private final int[][] matrix;
    private boolean[][] mask;

    public SlidingWindowProcessor(TiffProcessor tiffProcessor) {
        this.tiffProcessor = tiffProcessor;
        matrix = tiffProcessor.getOriginTiffMatrix();
    }

    private void createMask(int radius) {
        double squareRadius = Math.pow(radius, 2);
        mask = new boolean[radius + 1][radius + 1];
        for (int x = 0; x <= radius; x++) {
            for (int y = 0; y <= radius; y++) {
                if (Math.pow((double) x - radius, 2) + Math.pow((double) y - radius, 2) <= squareRadius) {
                    mask[x][y] = true;
                }
            }
        }
    }

    private int calcReflectedPixels(int x, int y, int x1, int y1) {
        // (x; y)
        int sum = matrix[y][x];
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

    private long sumUpWindow(int centerX, int startX, int endX, int endY, int radius) {
        long sum = 0;
        int doubleCenterX = 2 * centerX;
        int doubleRadius = 2 * radius;
        // Ограничиваем прямоугольную область
        for (int x = startX; x <= endX; x++) {
            int x1 = doubleCenterX - x;
            int offsetX = x - startX;
            for (int y = 0; y <= endY; y++) {
                // Проверяем, находится ли точка внутри окружности с помощью маски
                if (mask[offsetX][y]) {
                    int y1 = doubleRadius - y;
                    sum += calcReflectedPixels(x, y, x1, y1);
                }
            }
        }
        return sum;
    }

    private long calcNextWindow(int centerX, int centerY, int startX, int endX, int startY, int endY, long prevSum) {
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

    private void calcSumsForDipoleMoment(int x, int y, int x1, int y1, int centerY, int[] sums) {
        sums[0] = matrix[y][x];
        sums[1] = matrix[y][x];
        sums[2] = 0;
        sums[3] = 0;
        if (x1 != x) {
            sums[2] += matrix[y][x1];
            sums[1] += matrix[y][x1];
        }
        if (y1 != y) {
            sums[0] += matrix[y1][x];
            sums[3] += matrix[y1][x];
            if (x1 != x) {
                sums[2] += matrix[y1][x1];
                sums[3] += matrix[y1][x1];
            }
            sums[3] *= (y1 - centerY);
        }
    }

    // TODO: проверить всё на long и int (суммы и т.п.)
    private void calcDipoleMomentWindow(int centerX, int centerY, int startX, int endX, int startY, int endY) {
        double[] pXpY = new double[]{0, 0};
        int threshold = 250_000_000;
        int doubleCenterX = 2 * centerX;
        int doubleCenterY = 2 * centerY;
        int[] sums = new int[4];
        for (int x = startX; x <= endX; x++) {
            int offsetX = x - startX;
            int x1 = doubleCenterX - x;
            double distanceX = (double) x - centerX;
            double distanceX1 = (double) x1 - centerX;
            for (int y = startY; y <= endY; y++) {
                if (!mask[offsetX][y - startY]) continue;
                int y1 = doubleCenterY - y;
                double distanceY = (double) y - centerY;
                calcSumsForDipoleMoment(x, y, x1, y1, centerY, sums);
                pXpY[0] += distanceX * sums[0] + distanceX1 * sums[2];
                pXpY[1] += distanceY * sums[1] + sums[3];
            }
        }
        if (Math.hypot(pXpY[0], pXpY[1]) < threshold) {
            tiffProcessor.highlightPixel(centerY, centerX, 255);
        }
    }

    public void runSlidingWindow(int radius, int threshold) {
        if (radius == 0) {
            LOGGER.error("The radius must not be zero!");
            return;
        }
        int rows = matrix[0].length; // 2822
        int cols = matrix.length; // 4144
        createMask(radius);
        int progress = rows / 10; // нужно для отслеживания прогресса
        LOGGER.info("Progress of the sliding window: 0%");
        // Перебираем центральные точки
        // Пока что только с полным вхождением окна в границы картинки
        int endY = Math.min(matrix.length - 1, radius);
        var redColor = (255 << 16);
        for (int x = radius; x < rows - radius; x++) {
            int startX = Math.max(0, x - radius);
            int endX = Math.min(matrix[0].length - 1, x);
            long sum = sumUpWindow(x, startX, endX, endY, radius);
            if (sum <= threshold) {
                tiffProcessor.highlightPixel(radius, x, (255 << 16));
            } else {
                calcDipoleMomentWindow(x, radius, startX, endX, 0, endY);
            }
            for (int y = radius + 1; y < cols - radius; y++) {
                sum = calcNextWindow(x, y, startX, endX, Math.max(0, y - radius), Math.min(matrix.length - 1, y), sum);
                if (sum <= threshold) {
                    tiffProcessor.highlightPixel(y, x, redColor);
                } else {
                    calcDipoleMomentWindow(x, y, startX, endX, Math.max(0, y - radius), Math.min(matrix.length - 1, y));
                }
            }
            if (x % progress == 0) {
                LOGGER.info("Progress of the sliding window: {}%", (x / progress) * 10);
            }
        }
    }
}
