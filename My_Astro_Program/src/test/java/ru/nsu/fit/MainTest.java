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

    @Test
    void testCropped() {
        // Вот это можно вынести в параметры командной строки
        String pathToImage = "src/test/resources/original_images/cropped.tif";
        String radius = "64";
        String pathToSave = "src/test/resources/actual_processed_images/color_cropped.tif";
        String pathToExpected = "src/test/resources/expected_processed_images/color_cropped.tif";

        double start = System.currentTimeMillis();
        Main.main(new String[]{pathToImage, radius, pathToSave});
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
        // Вот это можно вынести в параметры командной строки
        String pathToImage = "src/test/resources/original_images/012.tif";
        String radius = "64";
        String pathToSave = "src/test/resources/actual_processed_images/color_012.tif";
        String pathToExpected = "src/test/resources/expected_processed_images/color_012.tif";

        double start = System.currentTimeMillis();
        Main.main(new String[]{pathToImage, radius, pathToSave});
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
}
