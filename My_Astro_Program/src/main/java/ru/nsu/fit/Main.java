package ru.nsu.fit;

// TODO: в будущем нужно распараллеливать обработку и подсчет моментов для тифов

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    private static int radius = 64;
    private static double threshold = 1;
    private static final int distance = 3;

    private static void findKeyPointsFromPuddles(List<KeyPoint> points, TiffProcessor tiffProcessor) {
        KeyPointsProcessor pointsProcessor = new KeyPointsProcessor(distance);
        List<KeyPoint> resultKeyPoints = new ArrayList<>();
        pointsProcessor.findKeyPoints(points, resultKeyPoints);
        for (KeyPoint keyPoint : resultKeyPoints) {
//            tiffProcessor.highlightArea(keyPoint.getY(), keyPoint.getX(), radius,
//                    255 << 16 | 255 << 8 | 150);
            tiffProcessor.highlightPixel(keyPoint.getY(), keyPoint.getX(),
                    255 << 16 | 255 << 8);
        }
        LOGGER.info("Всего найдено точек: {}", resultKeyPoints.size());
    }

    private static void saveIntermediateFile(TiffProcessor tiffProcessor, String pathToImage) {
        String[] tokens = pathToImage.split("/");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss");
        String pathToSave = "src/test/resources/a-tmp/"
                + LocalDateTime.now().format(formatter)
                + "_" + tokens[tokens.length - 1];
        tiffProcessor.highlightArea(radius, radius, radius, 255 << 16);
        tiffProcessor.saveColorTiff(pathToSave);
    }


    /**
     * Запуск программы.
     *
     * @param args путь к оригинальному изображению;
     *             путь для сохранения файла;
     *             int радиус окр-ти (опц.);
     *             double стартовый порог для нахождения ключевых точек: (x^2-y^2)/(x^2+y^2) < threshold (опц)
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            LOGGER.error("Not enough arguments passed!");
            return;
        }
        String pathToImage = args[0];
        String pathToSave = args[1];
        if (args.length > 2) {
            radius = Integer.parseInt(args[2]);
        }
        if (args.length > 3) {
            threshold = Double.parseDouble(args[3]);
        }
        TiffProcessor tiffProcessor = new TiffProcessor(pathToImage);
        SlidingWindowProcessor slidingWindowProcessor = new SlidingWindowProcessor(tiffProcessor);
        List<KeyPoint> points = slidingWindowProcessor.runSlidingWindow(radius, threshold);

        saveIntermediateFile(tiffProcessor, pathToImage);
        findKeyPointsFromPuddles(points, tiffProcessor);

        tiffProcessor.saveColorTiff(pathToSave);
    }
}