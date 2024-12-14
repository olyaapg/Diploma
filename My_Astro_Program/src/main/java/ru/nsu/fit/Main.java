package ru.nsu.fit;

// TODO: в будущем нужно распараллеливать обработку и подсчет моментов для тифов

public class Main {
    // В аргументы первым передается путь к файлу, затем путь, куда сохранять итоговый файл
    public static void main(String[] args) {
        String pathToImage = args[0];
        String pathToSave = args[1];
        int radius = 64; // TODO: вынести для настройки пользователем?
        double thresholdForZeroMoment = 2000;

        TiffProcessor tiffProcessor = new TiffProcessor(pathToImage, 0.98);
        SlidingWindowProcessor slidingWindowProcessor = new SlidingWindowProcessor(tiffProcessor);
        slidingWindowProcessor.runSlidingWindow(radius, thresholdForZeroMoment);

        // удалить потом
//        var redColor = (255 << 16);
//        tiffProcessor.highlightArea(radius, tiffProcessor.getHeight() - radius, radius, redColor);

        tiffProcessor.saveColorTiff(pathToSave);
    }
}