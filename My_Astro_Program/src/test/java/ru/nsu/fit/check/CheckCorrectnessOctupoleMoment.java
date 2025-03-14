package ru.nsu.fit.check;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import ru.nsu.fit.TiffProcessor;

import static ru.nsu.fit.utils.Utils.normalizeComponent;
import static ru.nsu.fit.utils.Utils.normalizeModule;

class CheckCorrectnessOctupoleMoment {
    private static final int RADIUS = 8;

    private static final Logger LOGGER = LogManager.getLogger(CheckCorrectnessOctupoleMoment.class);

    public void runSlidingWindow(double[][] normalizedMatrix, TiffProcessor tiffProcessor) {
        int rows = normalizedMatrix[0].length;
        int cols = normalizedMatrix.length;

        double[] maxDiff = new double[]{Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE};
        double[] minDiff = new double[]{Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
        int squareRadius = RADIUS * RADIUS;

        double sumOiik = 0;
        double sumOijj = 0;
        double sum = 0;

        double norm = Double.MIN_VALUE;

        for (int x = RADIUS; x < rows - RADIUS; x++) {
            for (int y = RADIUS; y < cols - RADIUS; y++) {
                double[] arrQ = new double[]{0, 0, 0, 0};
                // Ограничиваем прямоугольную область
                for (int i = x - RADIUS; i <= x + RADIUS; i++) {
                    for (int j = y - RADIUS; j <= y + RADIUS; j++) {
                        if (Math.pow((double) i - x, 2) + Math.pow((double) j - y, 2) <= squareRadius) {
                            double xi = (j - y);
                            double yi = (i - x);
                            double squareXi = xi * xi;
                            double squareYi = yi * yi;
                            double squareRi = squareXi + squareYi;
                            arrQ[0] += normalizedMatrix[j][i] * (5 * squareXi - 3 * squareRi) * xi; //Oxxx
                            arrQ[1] += normalizedMatrix[j][i] * (5 * squareXi - squareRi) * yi; //Oxxy
                            arrQ[2] += normalizedMatrix[j][i] * (5 * squareYi - squareRi) * xi; //Oxyy
                            arrQ[3] += normalizedMatrix[j][i] * (5 * squareYi - 3 * squareRi) * yi; //Oyyy
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
                if (maxDiff[3] < arrQ[3]) {
                    maxDiff[3] = arrQ[3];
                }
                if (minDiff[3] > arrQ[3]) {
                    minDiff[3] = arrQ[3];
                }
                tiffProcessor.highlightPixel(y, x, (int)arrQ[3]);
//                sumOiik += arrQ[1];
//                sumOijj += arrQ[2];
//                tiffProcessor.highlightPixel(y, x,
//                        normalizeComponent(arrQ[0], 5136) << 16 |
//                        normalizeComponent(arrQ[1], 4166) << 8 |
//                        normalizeComponent(arrQ[2], 4224));
//                var tmp = Math.sqrt(Math.pow(arrQ[0], 2) + Math.pow(arrQ[1], 2) + Math.pow(arrQ[2], 2) + Math.pow(arrQ[3], 2));
//                tiffProcessor.highlightPixel(y, x, normalizeModule(tmp, 7156));
//                if (tmp < 500) {
//                    tiffProcessor.highlightPixel(y, x, 255 << 16);
//                }
            }
        }
        LOGGER.info("MAX Oxxx = {}, Oxxy = {}, Oxyy = {}, Oyyy = {}", maxDiff[0], maxDiff[1], maxDiff[2], maxDiff[3]);
        LOGGER.info("MIN Oxxx = {}, Oxxy = {}, Oxyy = {}, Oyyy = {}", minDiff[0], minDiff[1], minDiff[2], minDiff[3]);
        LOGGER.info("sumOiik = {}, sumOijj = {}", sumOiik, sumOijj);
//        LOGGER.info("max norm of octupole is {}", norm);
    }

    @Test
    void test() {
        String image = "board.tif";
        String pathToImage = "src/test/resources/original_images/" + image;
        String pathToSave = "src/test/resources/check/new_" + image;

        TiffProcessor tiffProcessor = new TiffProcessor(pathToImage);
        runSlidingWindow(tiffProcessor.getNormalizedMatrix(), tiffProcessor);

        tiffProcessor.saveColorTiff(pathToSave);
    }
}
