package ru.nsu.fit.check;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import ru.nsu.fit.TiffProcessor;

class CheckCorrectnessMoment {
    private final static int RADIUS = 16;

    private static final Logger LOGGER = LogManager.getLogger(CheckCorrectnessMoment.class);

    public void runSlidingWindow(double[][] normalizedMatrix, TiffProcessor tiffProcessor) {
        int rows = normalizedMatrix[0].length;
        int cols = normalizedMatrix.length;

        double maxDiff = Double.MIN_VALUE;
        double minDiff = Double.MAX_VALUE;
        int squareRadius = RADIUS * RADIUS;

        for (int x = RADIUS; x < rows - RADIUS; x++) {
            for (int y = RADIUS; y < cols - RADIUS; y++) {
                double sum = 0.0;
                // Ограничиваем прямоугольную область
                for (int i = x - RADIUS; i <= x + RADIUS; i++) {
                    for (int j = y - RADIUS; j <= y + RADIUS; j++) {
                        if (Math.pow((double) i - x, 2) + Math.pow((double) j - y, 2) <= squareRadius) {
                            sum += normalizedMatrix[j][i];
                        }
                    }
                }
                if (maxDiff < sum) {
                    maxDiff = sum;
                }
                if (minDiff > sum) {
                    minDiff = sum;
                }
            }
        }
        LOGGER.info("max zeroMoment = {}", maxDiff);
        LOGGER.info("min zeroMoment = {}", minDiff);
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
