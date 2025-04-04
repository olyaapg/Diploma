package ru.nsu.fit;

import com.github.romankh3.image.comparison.ImageComparison;
import com.github.romankh3.image.comparison.ImageComparisonUtil;
import com.github.romankh3.image.comparison.model.ImageComparisonResult;
import com.github.romankh3.image.comparison.model.ImageComparisonState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    private static final Logger LOGGER = LogManager.getLogger(MainTest.class);

    private void checkImagesForMatch(String pathToExpected, String pathToSave) {
        // Загрузка изображений для сравнения
        BufferedImage expectedImage = ImageComparisonUtil.readImageFromResources(pathToExpected);
        BufferedImage actualImage = ImageComparisonUtil.readImageFromResources(pathToSave);
        // Создание объекта ImageComparison и сравнение изображений
        ImageComparisonResult imageComparisonResult = new ImageComparison(expectedImage, actualImage).compareImages();
        // Проверка результата
        assertEquals(ImageComparisonState.MATCH, imageComparisonResult.getImageComparisonState());
    }

    /**
     * Запустить программу для изображения со стандартными параметрами и возможностью сверить
     * получившееся изображение с нужным.
     *
     * @param imageName название файла .tif
     * @param testName название исполняемого теста (для логгера)
     * @param checkFlag true: если нужно проверять правильность полученного изображения, false: иначе
     */
    private void runAndCheck(String imageName, String testName, boolean checkFlag) {
        String dirOriginals = "src/test/resources/original_images/";
        String dirActual = "src/test/resources/" + testName + "/actual/";
        String dirExpected = "src/test/resources/" + testName + "/expected/";
        String pathToImage = dirOriginals + imageName;
        String pathToResult = dirActual + "res_" + imageName;
        String pathToExpected = dirExpected + "color_" + imageName;
        LOGGER.info("{} started", testName);
        double start = System.currentTimeMillis();

        Main.main(new String[]{pathToImage, pathToResult});

        double end = System.currentTimeMillis();
        var timeSec = (end - start) / 1000;
        LOGGER.info("{}: {}. Time: {} sec, {} min", testName, imageName, timeSec, new DecimalFormat("#.###").format(timeSec / 60));
        if (checkFlag) {
            checkImagesForMatch(pathToExpected, pathToResult);
        }
    }

    /**
     * Запустить программу для изображения с кастомными параметрами.
     *
     * @param imageName название файла .tif
     * @param testName название исполняемого теста (для логгера)
     * @param radius радиус скользящего окна
     * @param threshold стартовый порог для нахождения ключевых точек: (x^2-y^2)/(x^2+y^2) < threshold
     */
    private void runMain(String imageName, String testName, Integer radius, Double threshold) {
        String dirActual = "src/test/resources/" + testName + "/actual/";
        String pathToImage = "src/test/resources/original_images/" + imageName;
        String pathToResult = dirActual + "res_" + imageName;
        LOGGER.info("{} started", testName);
        double start = System.currentTimeMillis();

        Main.main(new String[]{pathToImage, pathToResult, radius.toString(), threshold.toString()});

        double end = System.currentTimeMillis();
        var timeSec = (end - start) / 1000;
        LOGGER.info("{}: {}. Time: {} sec, {} min", testName, imageName, timeSec, new DecimalFormat("#.###").format(timeSec / 60));
    }

    @Test
    void testZeroMoment() {
        // Для тестирования добавить след. строчку в runSlidingWindow() и закомментить всё, что ниже неё
//        if (sum <= threshold) {
//            tiffProcessor.highlightPixel(y, x, 255 << 16);
//            continue;
//        }
        runAndCheck("cropped.tif", "test_zero_moment", true);
        runAndCheck("012.tif", "test_zero_moment", true);
    }

    @Test
    void testDipole() {
        runAndCheck("cropped.tif", "test_dipole", false);
        runAndCheck("012.tif", "test_dipole", false);
    }

    @Test
    void testQuadrupole() {
//        runAndCheck("stretched_circles1.tif", "test_quadrupole", false);
//        runAndCheck("013.tif", "test_quadrupole", false);
//            runAndCheck("stretched_board.tif", "test_quadrupole", false);
//        runAndCheck("test_circle_16bit.tif", "test_quadrupole", false);
//        runMain("rotated_45_crater2.tif", "test_quadrupole", 49, 0.5);
        runMain("two_circles.tif", "test_quadrupole", 90, 50.0);
    }
}
