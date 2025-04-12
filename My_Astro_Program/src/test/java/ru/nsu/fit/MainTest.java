package ru.nsu.fit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;

class MainTest {
    private static final Logger LOGGER = LogManager.getLogger(MainTest.class);

    private static final String DIR_ORIGINALS = "src/test/resources/original_images/part_1/many_craters/";
    private static final String DIR_RESULT = "src/test/resources/final_test/";

    private static final int DISTANCE = 3;

    /**
     * Запустить программу для изображения с кастомными параметрами.
     *
     * @param imageName название файла .tif
     * @param radius    радиус скользящего окна
     * @param threshold стартовый порог для нахождения ключевых точек: (x^2-y^2)/(x^2+y^2) < threshold
     */
    private void runMain(String imageName, Integer radius, Double threshold, boolean mainWorkerFlag) {
        LOGGER.info("Test started");
        double start = System.currentTimeMillis();

        if (mainWorkerFlag) {
            String pathToImage = DIR_ORIGINALS + imageName;
            String pathToResult = DIR_RESULT + "res_" + imageName;
            MainWorker mainWorker = new MainWorker(pathToImage, pathToResult, radius, threshold, DISTANCE);
            mainWorker.call();
        } else {
            String[] args = radius == 0
                    ? new String[]{DIR_ORIGINALS, DIR_RESULT}
                    : new String[]{DIR_ORIGINALS, DIR_RESULT, radius.toString(), threshold.toString()};
            Main.main(args);
        }

        double end = System.currentTimeMillis();
        var timeSec = (end - start) / 1000;
        LOGGER.info("Test finished: {}. Time: {} sec, {} min",
                imageName, timeSec, new DecimalFormat("#.###").format(timeSec / 60));
    }

    @Test
    void testFiles() {
        runMain("", 0, 0.0, false);
    }
}
