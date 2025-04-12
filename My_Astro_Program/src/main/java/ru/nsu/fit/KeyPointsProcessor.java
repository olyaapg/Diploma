package ru.nsu.fit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class KeyPointsProcessor {
    private static final Logger LOGGER = LogManager.getLogger(KeyPointsProcessor.class);
    private final ConnectedComponents<KeyPoint> cc;

    public KeyPointsProcessor(int distance) {
        cc = new ConnectedComponents<>(distance);
    }

    public void findKeyPoints(List<KeyPoint> points, List<KeyPoint> resultKeyPoints) {
        Queue<List<KeyPoint>> puddleQueue = new LinkedList<>(cc.findConnectedComponents(points));

        while (!puddleQueue.isEmpty()) {
            List<KeyPoint> puddle = puddleQueue.poll();
            Collections.sort(puddle);

            if (puddle.size() <= 4) {
                KeyPoint keyPoint = puddle.get(0);
                resultKeyPoints.add(keyPoint);
                continue;
            }

            int size = puddle.size();
            int n = (size + 3) / 4;
            List<KeyPoint> cropped = new ArrayList<>(puddle.subList(0, n));

            List<List<KeyPoint>> newPuddles = cc.findConnectedComponents(cropped);
            puddleQueue.addAll(newPuddles);
        }
    }

    public List<KeyPoint> findKeyPointsFromPuddles(List<KeyPoint> points, TiffProcessor tiffProcessor) {
        List<KeyPoint> resultKeyPoints = new ArrayList<>();
        findKeyPoints(points, resultKeyPoints);
        for (KeyPoint keyPoint : resultKeyPoints) {
//            tiffProcessor.highlightArea(keyPoint.getY(), keyPoint.getX(), radius,
//                    255 << 16 | 255 << 8 | 150);
            tiffProcessor.highlightPixel(keyPoint.getY(), keyPoint.getX(),
                    255 << 16 | 255 << 8);
        }
        LOGGER.info("Всего найдено точек: {}", resultKeyPoints.size());
        return resultKeyPoints;
    }
}
