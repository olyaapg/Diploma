package ru.nsu.fit._magic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import ru.nsu.fit.Main;
import ru.nsu.fit.MainWorker;

import java.io.File;
import java.text.DecimalFormat;

class MasterTest {
    private static final Logger LOGGER = LogManager.getLogger(ru.nsu.fit._magic.MasterTest.class);

    private static final String DIR_ORIGINALS = "src/test/resources/new/originals/stack_cropped/";
    private static final String DIR_RESULT = "src/test/resources/new/result/stack_cropped/";

    private static final int DISTANCE = 3;

    @Test
    void testFiles() {
        String imageName = "067_lower_left_corner.tif";

        deleteFile(DIR_RESULT + "res_" + imageName);

        runMain(imageName, 64, 1.0, true);
    }

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

    private void deleteFile(String path) {
        File file = new File(path);
        if (file.delete()) {
            LOGGER.info("Файл успешно удален");
        } else {
            LOGGER.info("Не удалось удалить файл. Возможно, он не существует или нет прав доступа.");
        }
    }
}
