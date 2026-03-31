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

        KeyPointsProcessor keyPointsProcessor = new KeyPointsProcessor(distance, tiffProcessor);
        List<KeyPoint> resultKeyPoints = keyPointsProcessor.findKeyPointsFromPuddles(puddles);

        // CREATE HEATMAP
//        List<KeyPoint> resultKeyPoints = createHeatmap(slidingWindowProcessor, tiffProcessor, puddles);

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

    private List<KeyPoint> createHeatmap(SlidingWindowProcessor slidingWindowProcessor, TiffProcessor tiffProcessor, List<KeyPoint> points) {
        var maxLog = Math.log(slidingWindowProcessor.getMaxTmp());
        var minLog = Math.log(slidingWindowProcessor.getMinTmp());
        var diff = maxLog - minLog;
        for (KeyPoint keyPoint : points) {
            double tmp = Math.max(keyPoint.getTmp(), 1e-12);
            double tmpLog = Math.log(tmp);
            double t = (tmpLog - minLog) / diff;
            t = Math.pow(t, 0.4);
            if (t < 0) t = 0;
            if (t > 1) t = 1;
            int r = (int) (255 * t); //красный → высокие
            int g = (int) (255 * (1 - Math.abs(t - 0.5) * 2)); //зелёный → средние
            int b = (int) (255 * (1 - t)); //синий → низкие
            int color = (r << 16) | (g << 8) | b;
            tiffProcessor.highlightPixel(keyPoint.getY(), keyPoint.getX(), color);
        }
        return Collections.emptyList();
    }
}
