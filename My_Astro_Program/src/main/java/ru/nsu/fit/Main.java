package ru.nsu.fit;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        // Вот это можно вынести в параметры командной строки
        String pathToImage = "src/main/resources/Moon_IR_7/012.tif";
        int radius = 64;
        int threshold = 25_000_000;

        TiffProcessor tiffProcessor = new TiffProcessor(pathToImage);
        SlidingWindowProcessor slidingWindowProcessor = new SlidingWindowProcessor(tiffProcessor);
        double start = System.currentTimeMillis();
        slidingWindowProcessor.runSlidingWindow(radius, threshold);
        double end = System.currentTimeMillis();
        var timeSec = (end - start) / 1000;
        LOGGER.info("Time: {} sec, {} min", timeSec, timeSec / 60);
        var redColor = (255 << 16);
        tiffProcessor.highlightArea(radius, tiffProcessor.getWidth() - radius, radius, redColor);
        tiffProcessor.saveColorTiff("src/main/resources/Moon_IR_7/color_012_2.tif");
    }
}