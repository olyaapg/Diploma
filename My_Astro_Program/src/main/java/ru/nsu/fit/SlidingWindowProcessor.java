package ru.nsu.fit;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// TODO: проверить всё на double и int (суммы и т.п.)

public class SlidingWindowProcessor {
    private static final Logger LOGGER = LogManager.getLogger(SlidingWindowProcessor.class);

    private final TiffProcessor tiffProcessor;
    private final double[][] normalizedMatrix;
    private boolean[][] mask;

    public SlidingWindowProcessor(TiffProcessor tiffProcessor) {
        this.tiffProcessor = tiffProcessor;
        normalizedMatrix = tiffProcessor.getNormalizedMatrix();
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

    private double calcReflectedPixels(int x, int y, int x1, int y1) {
        // (x; y)
        double sum = normalizedMatrix[y][x];
        // (x1; y)
        if (x1 != x) {
            sum += normalizedMatrix[y][x1];
        }
        // (x; y1)
        if (y1 != y) {
            sum += normalizedMatrix[y1][x];
            // (x1; y1)
            if (x1 != x) {
                sum += normalizedMatrix[y1][x1];
            }
        }
        return sum;
    }

    private double sumUpWindow(int centerX, int startX, int endX, int radius) {
        double sum = 0;
        int doubleCenterX = 2 * centerX;
        int doubleRadius = 2 * radius;
        // Ограничиваем прямоугольную область
        for (int x = startX; x <= endX; x++) {
            int x1 = doubleCenterX - x;
            int offsetX = x - startX;
            for (int y = 0; y <= radius; y++) {
                // Проверяем, находится ли точка внутри окружности с помощью маски
                if (mask[offsetX][y]) {
                    int y1 = doubleRadius - y;
                    sum += calcReflectedPixels(x, y, x1, y1);
                }
            }
        }
        return sum;
    }

    private double calcNextWindow(int centerX, int centerY, int startX, int endX, int startY, int endY, double prevSum) {
        int doubleCenterX = 2 * centerX;
        int doubleCenterY = 2 * centerY;
        for (int x = startX; x <= endX; x++) {
            int offsetX = x - startX;
            var x1 = doubleCenterX - x;
            for (int y = startY; y <= endY; y++) {
                if (mask[offsetX][y - startY]) {
                    var y1 = doubleCenterY - y;
                    prevSum -= normalizedMatrix[y - 1][x];
                    prevSum += normalizedMatrix[y1][x];
                    if (x != x1) { // предполагается, что мы всегда сдвигаем окно только вдоль одной оси, а не двух
                        prevSum -= normalizedMatrix[y - 1][x1];
                        prevSum += normalizedMatrix[y1][x1];
                    }
                    break;
                }
            }
        }
        return prevSum;
    }

    private void calcSumsForDipoleMoment(int x, int y, int x1, int y1, int centerY, double[] sums) {
        // sums[0] = sumX, sums[1] = sumY, sums[2] = sumX1, sums[3] = sumY1
        sums[0] = normalizedMatrix[y][x]; // sumX += яркость (y; x)
        sums[1] = normalizedMatrix[y][x]; // sumY += яркость (y; x)
        sums[2] = 0;
        sums[3] = 0;
        if (x1 != x) {
            sums[2] += normalizedMatrix[y][x1]; // sumX1 += яркость (y; x1)
            sums[1] += normalizedMatrix[y][x1]; // sumY += яркость (y; x1)
        }
        if (y1 != y) {
            sums[0] += normalizedMatrix[y1][x]; // sumX += яркость (y1; x)
            sums[3] += normalizedMatrix[y1][x]; // sumY1 += яркость (y1; x)
            if (x1 != x) {
                sums[2] += normalizedMatrix[y1][x1]; // sumX1 += яркость (y1; x1)
                sums[3] += normalizedMatrix[y1][x1]; // sumY1 += яркость (y1; x1)
            }
            sums[3] *= (y1 - centerY);
        }
    }

    private void calcDipoleMomentWindow(int centerX, int centerY, int startX, int endX, int startY, int endY) {
        double[] pXpY = new double[]{0, 0};
        int threshold = 5_000;
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
                calcSumsForDipoleMoment(x, y, x1, y1, centerY, sums);
                pXpY[0] += distanceX * sums[0] + distanceX1 * sums[2];
                pXpY[1] += distanceY * sums[1] + sums[3];
            }
        }
        double module = Math.hypot(pXpY[0], pXpY[1]);
        if (module <= threshold) {
            tiffProcessor.highlightPixel(centerY, centerX, 255);
//            calcQuadrupoleMomentWindow(centerX, centerY, startX, endX, startY, endY);
        }
    }

    private void calcQuadrupoleMoment(int x, int y, int x1, int y1, double squareX, double squareX1, int centerY, double[] arrQ) {
        // arrQ[0] = Qxx, arrQ[1] = Qxy, arrQ[2] = Qyy
        double squareY = Math.pow((double) y - centerY, 2);
        double squareY1;
        arrQ[0] += normalizedMatrix[y][x] * (2 * squareX - squareY);
        arrQ[1] += normalizedMatrix[y][x] * x * y;
        arrQ[2] += normalizedMatrix[y][x] * (2 * squareY - squareX);
        if (x1 != x) {
            arrQ[0] += normalizedMatrix[y][x1] * (2 * squareX1 - squareY);
            arrQ[1] += normalizedMatrix[y][x1] * x1 * y;
            arrQ[2] += normalizedMatrix[y][x1] * (2 * squareY - squareX1);
        }
        if (y1 != y) {
            squareY1 = Math.pow((double) y1 - centerY, 2);
            arrQ[0] += normalizedMatrix[y1][x] * (2 * squareX - squareY1);
            arrQ[1] += normalizedMatrix[y1][x] * x * y1;
            arrQ[1] += normalizedMatrix[y1][x] * (2 * squareY1 - squareX);
            if (x1 != x) {
                arrQ[0] += normalizedMatrix[y1][x1] * (2 * squareX1 - squareY1);
                arrQ[1] += normalizedMatrix[y1][x1] * x1 * y1;
                arrQ[2] += normalizedMatrix[y1][x1] * (2 * squareY1 - squareX1);
            }
        }
    }

    private void calcQuadrupoleMomentWindow(int centerX, int centerY, int startX, int endX, int startY, int endY) {
        double[] arrQ = new double[]{0, 0, 0}; // arrQ[0] = Qxx, arrQ[1] = Qxy, arrQ[2] = Qyy
        int doubleCenterX = 2 * centerX;
        int doubleCenterY = 2 * centerY;
        for (int x = startX; x <= endX; x++) {
            int offsetX = x - startX;
            int x1 = doubleCenterX - x;
            double squareX = Math.pow((double) x - centerX, 2);
            double squareX1 = Math.pow((double) x1 - centerX, 2);
            for (int y = startY; y <= endY; y++) {
                if (!mask[offsetX][y - startY]) continue;
                int y1 = doubleCenterY - y;
                calcQuadrupoleMoment(x, y, x1, y1, squareX, squareX1, centerY, arrQ);
            }
        }
        tiffProcessor.highlightPixel(centerY, centerX,
                (((int) arrQ[0] * 10_000) << 16) | (((int) arrQ[1] * 10_000) << 8) | ((int) arrQ[2] * 10_000));
    }

    public void runSlidingWindow(int radius, double threshold) {
        if (radius == 0) {
            LOGGER.error("The radius must not be zero!");
            return;
        }
        int rows = normalizedMatrix[0].length; // 2822
        int cols = normalizedMatrix.length; // 4144
        createMask(radius);
        int progress = rows / 10; // нужно для отслеживания прогресса
        LOGGER.info("Progress of the sliding window: 0%");
        // Перебираем центральные точки
        // Пока что только с полным вхождением окна в границы картинки

        for (int x = radius; x < rows - radius; x++) {
            int startX = Math.max(0, x - radius);
            int endX = Math.min(normalizedMatrix[0].length - 1, x);
            double sum = sumUpWindow(x, startX, endX, radius);
            if (sum <= threshold) {
                tiffProcessor.highlightPixel(radius, x, (255 << 16));
            } else {
                calcDipoleMomentWindow(x, radius, startX, endX, 0, radius);
//            calcQuadrupoleMomentWindow(x, radius, startX, endX, 0, radius);
            }
            for (int y = radius + 1; y < cols - radius; y++) {
                sum = calcNextWindow(x, y, startX, endX, Math.max(0, y - radius), Math.min(normalizedMatrix.length - 1, y), sum);
                if (sum <= threshold) {
                    tiffProcessor.highlightPixel(y, x, (255 << 16));
                } else {
                    calcDipoleMomentWindow(x, y, startX, endX, Math.max(0, y - radius), Math.min(normalizedMatrix.length - 1, y));
//                calcQuadrupoleMomentWindow(x, y, startX, endX, Math.max(0, y - radius), Math.min(normalizedMatrix.length - 1, y));
                }
            }
            if (x % progress == 0) {
                LOGGER.info("Progress of the sliding window: {}%", (x / progress) * 10);
            }
        }
    }
}
