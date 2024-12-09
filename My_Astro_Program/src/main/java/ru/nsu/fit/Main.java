package ru.nsu.fit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// TODO: в будущем нужно распараллеливать обработку и подсчет моментов для тифов

public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    // В аргументы первым передается путь к файлу, затем путь, куда сохранять итоговый файл
    public static void main(String[] args) {
        String pathToImage = args[0];
        String pathToSave = args[1];
        int radius = 64; // TODO: вынести для настройки пользователем?
        int threshold = 20_000_000; // TODO: менять при изменении радиуса окна

        TiffProcessor tiffProcessor = new TiffProcessor(pathToImage);
        SlidingWindowProcessor slidingWindowProcessor = new SlidingWindowProcessor(tiffProcessor);
        slidingWindowProcessor.runSlidingWindow(radius, threshold);

        // удалить потом
//        var redColor = (255 << 16);
//        tiffProcessor.highlightArea(radius, tiffProcessor.getHeight() - radius, radius, redColor);
        tiffProcessor.saveColorTiff(pathToSave);

        LOGGER.info("The result was saved to \"{}\"", pathToSave);
    }
}