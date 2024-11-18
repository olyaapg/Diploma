package ru.nsu.fit;


public class Main {

    public static void main(String[] args) {
        // Вот это можно вынести в параметры командной строки
        String pathToImage = "src/main/resources/Moon_IR_7/012.tif";
        int radius = 64;
        int threshold = 25_000_000;

        TiffProcessor tiffProcessor = new TiffProcessor(pathToImage);
        SlidingWindow slidingWindow = new SlidingWindow(tiffProcessor);
        double start = System.currentTimeMillis();
        slidingWindow.runSlidingWindow(radius, threshold);
        double end = System.currentTimeMillis();
        var timeSec = (end - start) / 1000;
        System.out.println("Time: " + timeSec + " sec, " + timeSec / 60 + " min");
        var redColor = (255 << 16);
        tiffProcessor.highlightArea(radius, tiffProcessor.getWidth() - radius, radius, redColor);
        tiffProcessor.saveColorTiff("src/main/resources/Moon_IR_7/color_012.tif");
    }
}