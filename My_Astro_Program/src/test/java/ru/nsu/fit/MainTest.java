package ru.nsu.fit;

import com.github.romankh3.image.comparison.ImageComparison;
import com.github.romankh3.image.comparison.ImageComparisonUtil;
import com.github.romankh3.image.comparison.model.ImageComparisonResult;
import com.github.romankh3.image.comparison.model.ImageComparisonState;
import ij.IJ;
import ij.ImagePlus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
//        BufferedImage expectedImage = ImageComparisonUtil.readImageFromResources(pathToExpected);
//        BufferedImage actualImage = ImageComparisonUtil.readImageFromResources(pathToSave);
//         Создание объекта ImageComparison и сравнение изображений
//        ImageComparisonResult imageComparisonResult = new ImageComparison(expectedImage, actualImage).compareImages();
//         Проверка результата
//        assertEquals(ImageComparisonState.MATCH, imageComparisonResult.getImageComparisonState());

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
        String dirActual = "src/test/resources/test_dipole/actual/";
        String dirExpected = "src/test/resources/test_dipole/expected/";

        String pathToImage = dirOriginals + name + ".tif";
        String pathToSave = dirActual + name + "_" + threshold + ".tif";
        String pathToExpected = dirExpected + name + "_" + threshold + ".tif";

        run(pathToImage, pathToSave, pathToExpected, "testDipole ~ " + name);
    }

    @Test
    void testDipole() {
        String threshold = "";
//        runDipole("cropped", threshold);
        runDipole("012", threshold);
    }

    @Test
    void testQuadrupole() {
        String pathToImage = dirOriginals + "cropped.tif";
        String pathToSave = "src/test/resources/test_quadrupole/cropped.tif";
        double start = System.currentTimeMillis();
        Main.main(new String[]{pathToImage, pathToSave});
        double end = System.currentTimeMillis();
        var timeSec = (end - start) / 1000;
        LOGGER.info("{} ~ Time: {} sec, {} min", "testQuadrupole", timeSec, new DecimalFormat("#.###").format(timeSec / 60));
    }

    @Test
    void test() {
        String file = "012.tif";
        double start = System.currentTimeMillis();
        Main.main(new String[]{dirOriginals + file, "src/test/resources/test/" + file});
        double end = System.currentTimeMillis();
        var timeSec = (end - start) / 1000;
        LOGGER.info("Time: {} sec, {} min", timeSec, new DecimalFormat("#.###").format(timeSec / 60));

//        BufferedImage image = null;
//        try {
//            image = ImageIO.read(new File("src/test/resources/test/" + file));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        // Извлекаем и сохраняем красный канал
//        BufferedImage redChannel = extractChannel(image, "red");
//        try {
//            ImageIO.write(redChannel, "png", new File("src/test/resources/test/" + "red_channel.png"));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    private static BufferedImage extractChannel(BufferedImage image, String channelName) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage channelImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);

                // Извлекаем компоненты RGB
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                // Определяем значение для выбранного канала
                int channelValue;
                switch (channelName.toLowerCase()) {
                    case "red":
                        channelValue = red;
                        break;
                    case "green":
                        channelValue = green;
                        break;
                    case "blue":
                        channelValue = blue;
                        break;
                    default:
                        throw new IllegalArgumentException("Некорректное имя канала: " + channelName);
                }

                // Создаем оттенок серого для визуализации
                int gray = (channelValue << 16);
                channelImage.setRGB(x, y, gray);
            }
        }

        return channelImage;
    }

    @Test
    void test2() {
        BufferedImage expectedImage = ImageComparisonUtil.readImageFromResources("src/test/resources/test/012_all.tif (blue).tif");
        BufferedImage actualImage = ImageComparisonUtil.readImageFromResources("src/test/resources/test/012.tif (blue).tif");
        ImageComparisonResult imageComparisonResult = new ImageComparison(expectedImage, actualImage).compareImages();
        assertEquals(ImageComparisonState.MATCH, imageComparisonResult.getImageComparisonState());
    }
}
