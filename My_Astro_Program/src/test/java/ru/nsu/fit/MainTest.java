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

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    private static final Logger LOGGER = LogManager.getLogger(MainTest.class);
    private final String dirOriginals = "src/test/resources/original_images/";

    private void run(String pathToImage, String pathToSave, String pathToExpected, String nameTest) {
        LOGGER.info("{} started", nameTest);
        double start = System.currentTimeMillis();
        Main.main(new String[]{pathToImage, pathToSave});
        double end = System.currentTimeMillis();
        var timeSec = (end - start) / 1000;
        LOGGER.info("{} ~ Time: {} sec, {} min", nameTest, timeSec, new DecimalFormat("#.###").format(timeSec / 60));

        // Загрузка изображений для сравнения
        BufferedImage expectedImage = ImageComparisonUtil.readImageFromResources(pathToExpected);
        BufferedImage actualImage = ImageComparisonUtil.readImageFromResources(pathToSave);
        // Создание объекта ImageComparison и сравнение изображений
        ImageComparisonResult imageComparisonResult = new ImageComparison(expectedImage, actualImage).compareImages();
        // Проверка результата
        assertEquals(ImageComparisonState.MATCH, imageComparisonResult.getImageComparisonState());

    }

    @Test
    void testZeroMoment() {
        String dirActual = "src/test/resources/test_zero_moment/actual/";
        String dirExpected = "src/test/resources/test_zero_moment/expected/";

        // cropped.tif
        String pathToImage = dirOriginals + "cropped.tif";
        String pathToSave = dirActual + "color_cropped.tif";
        String pathToExpected = dirExpected + "color_cropped.tif";

        run(pathToImage, pathToSave, pathToExpected, "testZeroMoment ~ cropped");

        // 012.tif
        pathToImage = dirOriginals + "012.tif";
        pathToSave = dirActual + "color_012.tif";
        pathToExpected = dirExpected + "color_012.tif";

        run(pathToImage, pathToSave, pathToExpected, "testZeroMoment ~ 012");
    }

    private void runDipole(String name, String threshold) {
        String dirActual = "src/test/resources/test/";
        String dirExpected = "src/test/resources/test_dipole/expected/";

        String pathToImage = dirOriginals + name + ".tif";
        String pathToSave = dirActual + name + "_" + threshold + ".tif";
        String pathToExpected = dirExpected + name + "_" + threshold + ".tif";

        run(pathToImage, pathToSave, pathToExpected, "testDipole ~ " + name);
    }

    @Test
    void testDipole1() {
        String threshold = "";
        runDipole("cropped", threshold);
//        runDipole("012", threshold);
    }

    @Test
    void testDipole2() {
        String threshold = "250_000_000";
        runDipole("cropped", threshold);
        runDipole("012", threshold);
    }

    @Test
    void testQuadrupole() {
        String pathToImage = dirOriginals + "012.tif";
        String pathToSave = "src/test/resources/test_quadrupole/012.tif";
        double start = System.currentTimeMillis();
        Main.main(new String[]{pathToImage, pathToSave});
        double end = System.currentTimeMillis();
        var timeSec = (end - start) / 1000;
        LOGGER.info("{} ~ Time: {} sec, {} min", "testQuadrupole", timeSec, new DecimalFormat("#.###").format(timeSec / 60));
    }


}
