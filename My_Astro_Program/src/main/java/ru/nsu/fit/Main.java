package ru.nsu.fit;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        // Вот это можно вынести в параметры командной строки
        String pathToImage = "src/main/resources/Moon_IR_7/013.tif";
        int radius = 64;
        int threshold = 25_000_000;

        LOGGER.info("The processing of the image \"{}\" has begun", pathToImage);

        TiffProcessor tiffProcessor = new TiffProcessor(pathToImage);
        SlidingWindowProcessor slidingWindowProcessor = new SlidingWindowProcessor(tiffProcessor);

        double start = System.currentTimeMillis();
        slidingWindowProcessor.runSlidingWindowOLD(radius, threshold);
        double end = System.currentTimeMillis();
        var timeSec = (end - start) / 1000;
        LOGGER.info("Time: {} sec, {} min", timeSec, timeSec / 60);

        var redColor = (255 << 16);
        tiffProcessor.highlightArea(radius, tiffProcessor.getHeight() - radius, radius, redColor);
        tiffProcessor.saveColorTiff("src/main/resources/Moon_IR_7_processed/color_013_old.tif");
    }
}