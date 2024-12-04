package ru.nsu.fit;

import com.github.romankh3.image.comparison.ImageComparison;
import com.github.romankh3.image.comparison.ImageComparisonUtil;
import com.github.romankh3.image.comparison.model.ImageComparisonResult;
import com.github.romankh3.image.comparison.model.ImageComparisonState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    private static final Logger LOGGER = LogManager.getLogger(MainTest.class);
    private final String dirOriginals = "src/test/resources/original_images/";
    private final String dirActualProcessed = "src/test/resources/actual_processed_images/";
    private final String dirExpectedProcessed = "src/test/resources/expected_processed_images/";

    @Test
    void testCropped() {
        String pathToImage = dirOriginals + "cropped.tif";
        String pathToSave = dirActualProcessed + "color_cropped.tif";
        String pathToExpected = dirExpectedProcessed + "color_cropped.tif";

        double start = System.currentTimeMillis();
        Main.main(new String[]{pathToImage, pathToSave});
        double end = System.currentTimeMillis();
        var timeSec = (end - start) / 1000;
        LOGGER.info("testCropped ~ Time: {} sec, {} min", timeSec, timeSec / 60);

        // Загрузка изображений для сравнения
        BufferedImage expectedImage = ImageComparisonUtil.readImageFromResources(pathToExpected);
        BufferedImage actualImage = ImageComparisonUtil.readImageFromResources(pathToSave);
        // Создание объекта ImageComparison и сравнение изображений
        ImageComparisonResult imageComparisonResult = new ImageComparison(expectedImage, actualImage).compareImages();
        // Проверка результата
        assertEquals(ImageComparisonState.MATCH, imageComparisonResult.getImageComparisonState());
    }

    @Test
    void test012() {
        String pathToImage = dirOriginals + "012.tif";
        String pathToSave = dirActualProcessed + "color_012.tif";
        String pathToExpected = dirExpectedProcessed + "color_012.tif";

        double start = System.currentTimeMillis();
        Main.main(new String[]{pathToImage, pathToSave});
        double end = System.currentTimeMillis();
        var timeSec = (end - start) / 1000;
        LOGGER.info("test012 ~ Time: {} sec, {} min", timeSec, timeSec / 60);

        // Загрузка изображений для сравнения
        BufferedImage expectedImage = ImageComparisonUtil.readImageFromResources(pathToExpected);
        BufferedImage actualImage = ImageComparisonUtil.readImageFromResources(pathToSave);
        // Создание объекта ImageComparison и сравнение изображений
        ImageComparisonResult imageComparisonResult = new ImageComparison(expectedImage, actualImage).compareImages();
        // Проверка результата
        assertEquals(ImageComparisonState.MATCH, imageComparisonResult.getImageComparisonState());
    }

    @Test
    void test() {
        String pathToImage = dirOriginals + "012.tif";
        String pathToSave = "src/test/resources/for_testing/" + "012_100_000_000.tif";

        double start = System.currentTimeMillis();
        Main.main(new String[]{pathToImage, pathToSave});
        double end = System.currentTimeMillis();
        var timeSec = (end - start) / 1000;
        LOGGER.info("test ~ Time: {} sec, {} min", timeSec, timeSec / 60);
    }
}
