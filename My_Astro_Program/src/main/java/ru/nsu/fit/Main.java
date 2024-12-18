package ru.nsu.fit;

// TODO: в будущем нужно распараллеливать обработку и подсчет моментов для тифов

public class Main {
    private static final int RADIUS = 64;
    private static final int THRESHOLD_FOR_ZERO_MOMENT = 2_000;

    // Аргументы: путь к оригинальному изображению; путь для сохранения итогового файла
    public static void main(String[] args) {
        String pathToImage = args[0];
        String pathToSave = args[1];

        TiffProcessor tiffProcessor = new TiffProcessor(pathToImage);
        SlidingWindowProcessor slidingWindowProcessor = new SlidingWindowProcessor(tiffProcessor);
        slidingWindowProcessor.runSlidingWindow(RADIUS, THRESHOLD_FOR_ZERO_MOMENT);

        tiffProcessor.saveColorTiff(pathToSave);
    }
}