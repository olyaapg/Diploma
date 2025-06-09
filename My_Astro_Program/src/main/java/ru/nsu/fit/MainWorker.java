package ru.nsu.fit;

import ru.nsu.fit.points.KeyPoint;
import ru.nsu.fit.points.KeyPointsProcessor;

import java.util.List;
import java.util.concurrent.Callable;

public class MainWorker implements Callable<List<KeyPoint>> {

    private final String pathToImage;
    private final String pathToSave;
    private final int radius;
    private final double threshold;
    private final int distance;

    public MainWorker(String pathToImage, String pathToSave, int radius, double threshold, int distance) {
        this.pathToImage = pathToImage;
        this.pathToSave = pathToSave;
        this.radius = radius;
        this.threshold = threshold;
        this.distance = distance;
    }

    @Override
    public List<KeyPoint> call() {
        TiffProcessor tiffProcessor = new TiffProcessor(pathToImage);
        SlidingWindowProcessor slidingWindowProcessor = new SlidingWindowProcessor(tiffProcessor);
        List<KeyPoint> points = slidingWindowProcessor.runSlidingWindow(radius, threshold);

        KeyPointsProcessor keyPointsProcessor = new KeyPointsProcessor(distance);
        List<KeyPoint> resultKeyPoints = keyPointsProcessor.findKeyPointsFromPuddles(points, tiffProcessor);

        tiffProcessor.saveColorTiff(pathToSave);
        return resultKeyPoints;
    }
}
