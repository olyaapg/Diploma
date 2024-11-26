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

    private int calcReflectedPixels(int x, int y, int x1, int y1) {
        // (x; y)
        int sum = matrix[x][y];
        // (x1; y)
        if (x1 != x) {
            sum += matrix[x1][y];
        }
        // (x; y1)
        if (y1 != y) {
            sum += matrix[x][y1];
            // (x1; y1)
            if (x1 != x) {
                sum += matrix[x1][y1];
            }
        }
        return sum;
    }

    private int calcWindow(int centerX, int centerY, int radius) {
        int sum = 0;
        // Ограничиваем прямоугольную область
        int startX = Math.max(0, centerX - radius);
        int endX = Math.min(matrix.length - 1, centerX);
        int startY = Math.max(0, centerY - radius);
        int endY = Math.min(matrix[0].length - 1, centerY);
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

    public void runSlidingWindow(int radius, int threshold) {
        if (radius == 0) {
            LOGGER.error("The radius must not be zero!");
            return;
        }
        int rows = matrix.length;
        int cols = matrix[0].length;
        createMask(radius);
        int progress = rows / 10; // нужно для отслеживания прогресса
        LOGGER.info("Progress of the sliding window: 0%");
        // Перебираем центральные точки
        // Пока что только с полным вхождением окна в границы картинки
        for (int i = radius; i < rows - radius; i++) {
            for (int j = radius; j < cols - radius; j++) {
                var sum = calcWindow(i, j, radius);
                if (sum <= threshold) {
                    tiffProcessor.highlightPixel(i, j, (255 << 16));
                }
            }
            if (i % progress == 0) {
                LOGGER.info("Progress of the sliding window: {}%", (i / progress) * 10);
            }
        }
    }
}
