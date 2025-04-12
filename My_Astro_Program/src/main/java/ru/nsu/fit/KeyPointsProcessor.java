package ru.nsu.fit;

import java.util.*;

public class KeyPointsProcessor {
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
}
