package ru.nsu.fit.points;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.nsu.fit.ConnectedComponents;
import ru.nsu.fit.TiffProcessor;

import java.util.*;

public class KeyPointsProcessor {
    private static final Logger LOGGER = LogManager.getLogger(KeyPointsProcessor.class);
    private final ConnectedComponents<KeyPoint> cc;
    private final TiffProcessor tiffProcessor;

    public KeyPointsProcessor(int distance, TiffProcessor tiffProcessor) {
        cc = new ConnectedComponents<>(distance);
        this.tiffProcessor = tiffProcessor;
    }

    public void findKeyPoints(List<KeyPoint> points, List<KeyPoint> resultKeyPoints) {
        Queue<List<KeyPoint>> puddleQueue = new LinkedList<>(cc.findConnectedComponents(points));
        int count = 1;

        while (!puddleQueue.isEmpty()) {
            List<KeyPoint> puddle = puddleQueue.poll();
            if (puddle.size() <= 4) {
                resultKeyPoints.add(puddle.get(puddle.size() / 2));
                continue;
            }
            puddle.sort(Comparator.comparingDouble(KeyPoint::getTmp));

            int size = puddle.size();
            int n = (size + 3) / 4;
            List<KeyPoint> cropped = new ArrayList<>(puddle.subList(0, n));

            List<List<KeyPoint>> newPuddles = cc.findConnectedComponents(cropped);
            puddleQueue.addAll(newPuddles);

//            tiffProcessor.createColorImage();
//            for (List<KeyPoint> p : puddleQueue) {
//                highlightPointsList(p);
//            }
//            tiffProcessor.saveColorTiff("src/test/resources/new/result/for_debug/" + count++ + ".tif");
        }
    }

    public List<KeyPoint> findKeyPointsFromPuddles(List<KeyPoint> points) {
        List<KeyPoint> resultKeyPoints = new ArrayList<>();
        findKeyPoints(points, resultKeyPoints);
        highlightPointsList(resultKeyPoints);
        LOGGER.info("Всего найдено точек: {}", resultKeyPoints.size());
        return resultKeyPoints;
    }

    private void highlightPointsList(List<KeyPoint> points) {
        for (KeyPoint keyPoint : points) {
//            tiffProcessor.highlightArea(keyPoint.getY(), keyPoint.getX(), radius, 255 << 16 | 255 << 8 | 150);
            tiffProcessor.highlightPixel(keyPoint.getY(), keyPoint.getX(), 255 << 16 | 255 << 8);
//            tiffProcessor.highlightPixelWithSpecificColor(keyPoint.getY(), keyPoint.getX(), 'B');
        }
    }
}
