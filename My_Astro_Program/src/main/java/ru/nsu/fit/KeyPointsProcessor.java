package ru.nsu.fit;

import java.util.*;

public class KeyPointsProcessor {
    private final ConnectedComponents<KeyPoint> cc;

    public KeyPointsProcessor(int distance) {
        cc = new ConnectedComponents<>(distance);
    }

    private void processPuddle(List<KeyPoint> puddle, List<KeyPoint> resultKeyPoints) {
        if (puddle.size() <= 4) {
            resultKeyPoints.add(puddle.get(puddle.size() - 1));
            return;
        }

        int size = puddle.size();
        int n = size % 4 == 0 ? size / 4 : size / 4 + 1;
        List<KeyPoint> croppedPuddle = new ArrayList<>(puddle.subList(size - n, size));
        findKeyPoints(croppedPuddle, resultKeyPoints);
    }

    public void findKeyPoints(List<KeyPoint> points, List<KeyPoint> resultKeyPoints) {
        List<List<KeyPoint>> puddles = cc.findConnectedComponents(points);

        for (List<KeyPoint> puddle : puddles) {
            Collections.sort(puddle);
            processPuddle(puddle, resultKeyPoints);
        }
    }
}
