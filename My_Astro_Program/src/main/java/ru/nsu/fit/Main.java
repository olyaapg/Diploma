package ru.nsu.fit;

// TODO: в будущем нужно распараллеливать обработку и подсчет моментов для тифов

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    private static int radius = 64;
    private static double threshold = 0.5;

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
        slidingWindowProcessor.runSlidingWindow(radius, threshold);

        tiffProcessor.saveColorTiff(pathToSave);
    }
}