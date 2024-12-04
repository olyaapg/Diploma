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

    private long calcWindow(int centerX, int centerY, int startX, int endX, int endY) {
        long sum = 0;
        // Ограничиваем прямоугольную область
        for (int x = startX; x <= endX; x++) {
            int x1 = 2 * centerX - x;
            for (int y = 0; y <= endY; y++) {
                // Проверяем, находится ли точка внутри окружности с помощью маски
                if (!mask[x - startX][y]) {
                    continue;
                }
                int y1 = 2 * centerY - y;
                sum += calcReflectedPixels(x, y, x1, y1);
            }
        }
        return sum;
    }

    private long calcNextWindow(int nextCenterX, int nextCenterY, long prevSum, int startX, int endX, int startY, int endY) {
        for (int x = startX; x <= endX; x++) {
            var x1 = 2 * nextCenterX - x;
            for (int y = startY; y <= endY; y++) {
                if (mask[x - startX][y - startY]) {
                    var y1 = 2 * nextCenterY - y;
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

    // TODO: проверить всё на long и int (суммы и т.п.)
    private int[] calcDipoleMoment(int centerX, int centerY, int startX, int endX, int startY, int endY, long sum) {
        double pX = 0;
        double pY = 0;
        int threshold = 100_000_000;
        for (int x = startX; x <= endX; x++) {
            var x1 = 2 * centerX - x;
            double distanceX = (double) x - centerX;
            double distanceX1 = (double) x1 - centerX;
            for (int y = startY; y <= endY; y++) {
                if (mask[x - startX][y - startY]) {
                    var y1 = 2 * centerY - y;
                    double distanceY = (double) y - centerY;
                    pX += distanceX * matrix[y][x];
                    pY += distanceY * matrix[y][x];
                    if (x1 != x) {
                        pX += distanceX1 * matrix[y][x1];
                        pY += distanceY * matrix[y][x1];
                    }
                    if (y1 != y) {
                        double distanceY1 = (double) y1 - centerY;
                        pX += distanceX * matrix[y1][x];
                        pY += distanceY1 * matrix[y1][x];
                        if (x1 != x) {
                            pX += distanceX1 * matrix[y1][x1];
                            pY += distanceY1 * matrix[y1][x1];
                        }
                    }
                }
            }
            if (pX >= threshold || pY >= threshold) {
                return new int[]{0, 0};
            }
        }
//        int centerBrightnessX = (int) Math.round(pX / sum);
//        int centerBrightnessY = (int) Math.round(pY / sum);
        if (Math.sqrt(Math.pow(pX, 2) + Math.pow(pY, 2)) < threshold) {
            tiffProcessor.highlightPixel(centerY, centerX, 255);
        }
        return new int[]{0, 0};
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
        for (int x = radius; x < rows - radius; x++) {
            int startX = Math.max(0, x - radius);
            int endX = Math.min(matrix[0].length - 1, x);
            long sum = calcWindow(x, radius, startX, endX, radius);
            if (sum <= threshold) {
                tiffProcessor.highlightPixel(radius, x, (255 << 16));
            } else {
                calcDipoleMoment(x, radius, startX, endX, 0, radius, sum);
            }
            for (int y = radius + 1; y < cols - radius; y++) {
                sum = calcNextWindow(x, y, sum, startX, endX, Math.max(0, y - radius), Math.min(matrix.length - 1, y));
                if (sum <= threshold) {
                    tiffProcessor.highlightPixel(y, x, (255 << 16));
                } else {
                    calcDipoleMoment(x, y, startX, endX, Math.max(0, y - radius), Math.min(matrix.length - 1, y), sum);
                }
            }
            if (x % progress == 0) {
                LOGGER.info("Progress of the sliding window: {}%", (x / progress) * 10);
            }
        }
    }
}
