package ru.nsu.fit;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SlidingWindowProcessor {
    private static final Logger LOGGER = LogManager.getLogger(SlidingWindowProcessor.class);

    private final TiffProcessor tiffProcessor;
    private int[][] matrix;
    private boolean[][] mask;

    public SlidingWindowProcessor(TiffProcessor tiffProcessor) {
        this.tiffProcessor = tiffProcessor;
        matrix = tiffProcessor.getOriginTiffMatrix();
    }

    public void setMatrix(int[][] matrix) {
        this.matrix = matrix;
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

    private int calcWindow(int centerX, int centerY, int radius) {
        int sum = 0;
        // Ограничиваем прямоугольную область
        int startX = Math.max(0, centerX - radius);
        int endX = Math.min(matrix[0].length - 1, centerX);
        int startY = Math.max(0, centerY - radius);
        int endY = Math.min(matrix.length - 1, centerY);
        for (int x = startX; x <= endX; x++) {
            int x1 = 2 * centerX - x;
            for (int y = startY; y <= endY; y++) {
                // Проверяем, находится ли точка внутри окружности с помощью маски
                if (!mask[x - startX][y - startY]) {
                    continue;
                }
                int y1 = 2 * centerY - y;
                sum += calcReflectedPixels(x, y, x1, y1);
                // TODO: вставить функцию обработки пикселей внутри окна
            }
        }
        return sum;
    }

    private int calcNextWindow(int nextCenterX, int nextCenterY, int radius, int prevSum) {
        int startX = Math.max(0, nextCenterX - radius);
        int endX = Math.min(matrix[0].length - 1, nextCenterX);
        int startY = Math.max(0, nextCenterY - radius);
        int endY = Math.min(matrix.length - 1, nextCenterY);
        for (int x = startX; x <= endX; x++) {
            var x1 = 2 * nextCenterX - x;
            for (int y = startY; y <= endY; y++) {
                if (!mask[x - startX][y - startY]) {
                    continue;
                }
                var y1 = 2 * nextCenterY - y;
                prevSum -= matrix[y - 1][x];
                prevSum += matrix[y1][x];
                if (x != x1) {
                    prevSum -= matrix[y - 1][x1];
                    prevSum += matrix[y1][x1];
                }
                break;
            }
        }
        return prevSum;
    }

    public void runSlidingWindow(int radius, int threshold) {
        if (radius == 0) {
            LOGGER.error("The radius must not be zero!");
            return;
        }
        LOGGER.info("Quick func is working");

        int rows = matrix[0].length; // 2822
        int cols = matrix.length; // 4144
        createMask(radius);
        int progress = rows / 10; // нужно для отслеживания прогресса
        LOGGER.info("Progress of the sliding window: 0%");
        // Перебираем центральные точки
        // Пока что только с полным вхождением окна в границы картинки
        for (int x = radius; x < rows - radius; x++) {
            var sum = calcWindow(x, radius, radius);
            if (sum <= threshold) {
                tiffProcessor.highlightPixel(radius, x, 255);
            }
            for (int y = radius + 1; y < cols - radius; y++) {
                sum = calcNextWindow(x, y, radius, sum);
                if (sum <= threshold) {
                    tiffProcessor.highlightPixel(y, x, (255 << 16));
                }
            }
            if (x % progress == 0) {
                LOGGER.info("Progress of the sliding window: {}%", (x / progress) * 10);
            }
        }
    }


    public void runSlidingWindowOLD(int radius, int threshold) {
        if (radius == 0) {
            LOGGER.error("The radius must not be zero!");
            return;
        }
        LOGGER.info("Old func is working");

        int rows = matrix[0].length; // 2822
        int cols = matrix.length; // 4144
        createMask(radius);
        int progress = rows / 10; // нужно для отслеживания прогресса
        LOGGER.info("Progress of the sliding window: 0%");
        // Перебираем центральные точки
        // Пока что только с полным вхождением окна в границы картинки
        for (int x = radius; x < rows - radius; x++) {
            for (int y = radius; y < cols - radius; y++) {
                var sum = calcWindow(x, y, radius);
                if (sum <= threshold) {
                    tiffProcessor.highlightPixel(y, x, (255 << 16));
                }
            }
            if (x % progress == 0) {
                LOGGER.info("Progress of the sliding window: {}%", (x / progress) * 10);
            }
        }
    }

    public void runSlidingWindowCompare(int radius, int threshold) {
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
            var sum = calcWindow(x, radius, radius);
            var prevSum = sum;
            for (int y = radius + 1; y < cols - radius; y++) {
                sum = calcWindow(x, y, radius);
                prevSum = calcNextWindow(x, y, radius, prevSum);
                if (prevSum != sum) {
                    throw new AssertionError("Суммы не равны");
                }
            }
            if (x % progress == 0) {
                LOGGER.info("Progress of the sliding window: {}%", (x / progress) * 10);
            }
        }
    }
}
