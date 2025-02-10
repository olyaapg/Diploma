package ru.nsu.fit.check;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import ru.nsu.fit.TiffProcessor;

import static ru.nsu.fit.utils.Utils.normalizeComponent;

class CheckCorrectnessQuadrupoleMoment {
    private static final int RADIUS = 8;

    private static final Logger LOGGER = LogManager.getLogger(CheckCorrectnessQuadrupoleMoment.class);

    public void runSlidingWindow(double[][] normalizedMatrix, TiffProcessor tiffProcessor) {
        int rows = normalizedMatrix[0].length;
        int cols = normalizedMatrix.length;

        double[] maxDiff = new double[]{Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE};
        double[] minDiff = new double[]{Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
        int squareRadius = RADIUS * RADIUS;

        for (int x = RADIUS; x < rows - RADIUS; x++) {
            for (int y = RADIUS; y < cols - RADIUS; y++) {
                double[] arrQ = new double[]{0, 0, 0};
                // Ограничиваем прямоугольную область
                for (int i = x - RADIUS; i <= x + RADIUS; i++) {
                    for (int j = y - RADIUS; j <= y + RADIUS; j++) {
                        if (Math.pow((double) i - x, 2) + Math.pow((double) j - y, 2) <= squareRadius) {
                            double xi = (j - y);
                            double yi = (i - x);
                            double squareXi = xi * xi;
                            double squareYi = yi * yi;
                            arrQ[0] += normalizedMatrix[j][i] * (squareXi - squareYi); //Qxx
                            arrQ[1] += normalizedMatrix[j][i] * (2 * xi * yi); //Qxy
                            arrQ[2] += normalizedMatrix[j][i] * (squareYi - squareXi); //Qyy
                        }
                    }
                }
                if (maxDiff[0] < arrQ[0]) {
                    maxDiff[0] = arrQ[0];
                }
                if (minDiff[0] > arrQ[0]) {
                    minDiff[0] = arrQ[0];
                }
                if (maxDiff[1] < arrQ[1]) {
                    maxDiff[1] = arrQ[1];
                }
                if (minDiff[1] > arrQ[1]) {
                    minDiff[1] = arrQ[1];
                }
                if (maxDiff[2] < arrQ[2]) {
                    maxDiff[2] = arrQ[2];
                }
                if (minDiff[2] > arrQ[2]) {
                    minDiff[2] = arrQ[2];
                }
                tiffProcessor.highlightPixel(y, x,
                        normalizeComponent(arrQ[2], 496) << 16 |
                                normalizeComponent(arrQ[1], 676) << 8 |
                                normalizeComponent(arrQ[0], 496));
            }
        }
        LOGGER.info("max Qxx = {}, min Qxx = {}", maxDiff[0], minDiff[0]);
        LOGGER.info("max Qxy = {}, min Qxy = {}", maxDiff[1], minDiff[1]);
        LOGGER.info("max Qyy = {}, min Qyy = {}", maxDiff[2], minDiff[2]);
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
