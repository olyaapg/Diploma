package ru.nsu.fit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.nsu.fit.points.KeyPoint;
import ru.nsu.fit.points.KeyPointsProcessor;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

public class MainWorker implements Callable<List<KeyPoint>> {

    private static final Logger LOGGER = LogManager.getLogger(MainWorker.class);

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
        List<KeyPoint> puddles = slidingWindowProcessor.runSlidingWindow(radius, threshold);

        // LOG PUDDLES
        //logPoints(puddles, tiffProcessor, "PUDDLES");

        tiffProcessor.highlightArea(radius, radius, radius, 255 << 16 | 255 << 8 | 150);

        KeyPointsProcessor keyPointsProcessor = new KeyPointsProcessor(distance);
        List<KeyPoint> resultKeyPoints = keyPointsProcessor.findKeyPointsFromPuddles(puddles, tiffProcessor);

        // CREATE HEATMAP
        //List<KeyPoint> resultKeyPoints = createHeatmap(slidingWindowProcessor, tiffProcessor, puddles);

        // LOG POINTS
        //logPoints(resultKeyPoints, tiffProcessor, "RESULT KEY POINTS");

        tiffProcessor.saveColorTiff(pathToSave);
        return resultKeyPoints;
    }

    private void logPoints(List<KeyPoint> points, TiffProcessor tiffProcessor, String logTitle) {
        LOGGER.info(logTitle);
        double maxTmp = Double.MIN_VALUE;
        double minTmp = Double.MAX_VALUE;
        double averageTmp = 0;
        StringBuilder sb = new StringBuilder();
        for (KeyPoint k : points) {
            sb.append("[(").append(k.getX()).append(";").append(k.getY()).append("), ")
                    .append(k.getTmp()).append(", ").append(k.getTheta()).append("]")
                    .append(System.lineSeparator());
            if (!logTitle.equals("PUDDLES")) {
//                tiffProcessor.highlightArea(k.getY(), k.getX(), radius, 255 << 16 | 255 << 8 | 150);
                tiffProcessor.highlightPixel(k.getY(), k.getX(), 255 << 16);
            }
            if (k.getTmp() > maxTmp) {
                maxTmp = k.getTmp();
            }
            if (k.getTmp() < minTmp) {
                minTmp = k.getTmp();
            }
            averageTmp += k.getTmp();
        }
        averageTmp /= points.size();
        LOGGER.info(sb);
        LOGGER.info("Max tmp = {}", maxTmp);
        LOGGER.info("Min tmp = {}", minTmp);
        LOGGER.info("Average tmp = {}", averageTmp);
    }

    private List<KeyPoint> createHeatmap(SlidingWindowProcessor slidingWindowProcessor, TiffProcessor tiffProcessor, List<KeyPoint> puddles) {
        List<KeyPoint> resultKeyPoints = Collections.emptyList();
        var maxLog = Math.log(slidingWindowProcessor.getMaxTmp());
        var minLog = Math.log(slidingWindowProcessor.getMinTmp());
        for (KeyPoint keyPoint : puddles) {
            double tmpLog = Math.log(keyPoint.getTmp());
            double t = (tmpLog - minLog) / (maxLog - minLog);
            if (t < 0) t = 0;
            if (t > 1) t = 1;
            int value = (int) (t * 255);
            int b = 255 - value;
            int color = (value << 16) | (0) | b;
            tiffProcessor.highlightPixel(keyPoint.getY(), keyPoint.getX(), color);
        }
        return resultKeyPoints;
    }
}
