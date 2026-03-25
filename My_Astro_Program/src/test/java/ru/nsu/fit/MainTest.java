package ru.nsu.fit;

import com.github.romankh3.image.comparison.ImageComparison;
import com.github.romankh3.image.comparison.ImageComparisonUtil;
import com.github.romankh3.image.comparison.model.ImageComparisonResult;
import com.github.romankh3.image.comparison.model.ImageComparisonState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import ru.nsu.fit.utils.MergeTiffsRGB;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MainTest {
    private static final Logger LOGGER = LogManager.getLogger(MainTest.class);

    private static final String DIR_ORIGINALS = "src/test/resources/boards/";
    private static final String DIR_RESULT = "src/test/resources/boards/result/";

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
//        String pathToResult = "src/test/resources/original_images/part_1/many_craters/merged_output.tif";
//        deleteFile(pathToResult);

        runMain("", 100, 5.0, false);
//        checkImagesForMatch(
//                "src/test/resources/original_images/part_1/many_craters/expected/original_merged_output.tif",
//                pathToResult);
    }

    @Test
    void testMergeTiffsRGB() {
        String pathToResult = "src/test/resources/testMergeTiffsRGB/merged_output.tif";
        deleteFile(pathToResult);

        String[] args = new String[]{
                "src/test/resources/testMergeTiffsRGB/",
                "res_001_many_craters.tif",
                "res_002_many_craters.tif",
                "res_003_many_craters.tif"
        };
        MergeTiffsRGB.mergeTiffsFiles(args);
        checkImagesForMatch("src/test/resources/testMergeTiffsRGB/original_merged_output.tif", pathToResult);
    }

    private void checkImagesForMatch(String pathToExpected, String pathToSave) {
        BufferedImage expectedImage = ImageComparisonUtil.readImageFromResources(pathToExpected);
        BufferedImage actualImage = ImageComparisonUtil.readImageFromResources(pathToSave);
        ImageComparisonResult imageComparisonResult = new ImageComparison(expectedImage, actualImage).compareImages();
        assertEquals(ImageComparisonState.MATCH, imageComparisonResult.getImageComparisonState());
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
