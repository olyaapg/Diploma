package ru.nsu.fit.check;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import ru.nsu.fit.TiffProcessor;

import static ru.nsu.fit.utils.Utils.normalizeComponent;

class CheckCorrectnessDipoleMoment {
    private static final int RADIUS = 8;

    private static final Logger LOGGER = LogManager.getLogger(CheckCorrectnessDipoleMoment.class);

    public void runSlidingWindow(double[][] normalizedMatrix, TiffProcessor tiffProcessor) {
        int rows = normalizedMatrix[0].length;
        int cols = normalizedMatrix.length;

        double maxDiff0 = Double.MIN_VALUE;
        double minDiff0 = Double.MAX_VALUE;
        double maxDiff1 = Double.MIN_VALUE;
        double minDiff1 = Double.MAX_VALUE;
        int squareRadius = RADIUS * RADIUS;

        for (int x = RADIUS; x < rows - RADIUS; x++) {
            for (int y = RADIUS; y < cols - RADIUS; y++) {
                double[] pXpY = new double[]{0, 0};
                // Ограничиваем прямоугольную область
                for (int i = x - RADIUS; i <= x + RADIUS; i++) {
                    for (int j = y - RADIUS; j <= y + RADIUS; j++) {
                        if (Math.pow((double) i - x, 2) + Math.pow((double) j - y, 2) <= squareRadius) {
                            pXpY[0] += normalizedMatrix[j][i] * (j - y);
                            pXpY[1] += normalizedMatrix[j][i] * (i - x);
                        }
                    }
                }
                tiffProcessor.highlightPixel(y, x,
                        normalizeComponent(pXpY[1], 115) << 16 |
                                normalizeComponent(pXpY[0], 120));
                if (maxDiff0 < pXpY[0]) {
                    maxDiff0 = pXpY[0];
                }
                if (minDiff0 > pXpY[0]) {
                    minDiff0 = pXpY[0];
                }
                if (maxDiff1 < pXpY[1]) {
                    maxDiff1 = pXpY[1];
                }
                if (minDiff1 > pXpY[1]) {
                    minDiff1 = pXpY[1];
                }
            }
        }
        LOGGER.info("max Px = {}, min Px = {}", maxDiff0, minDiff0);
        LOGGER.info("max Py = {}, min Py = {}", maxDiff1, minDiff1);
    }

    @Test
    void test() {
        String image = "crater1.tif";
        String pathToImage = "src/test/resources/original_images/" + image;
        String pathToSave = "src/test/resources/check/new_" + image;

        TiffProcessor tiffProcessor = new TiffProcessor(pathToImage);
        runSlidingWindow(tiffProcessor.getNormalizedMatrix(), tiffProcessor);

        tiffProcessor.saveColorTiff(pathToSave);
    }
}
