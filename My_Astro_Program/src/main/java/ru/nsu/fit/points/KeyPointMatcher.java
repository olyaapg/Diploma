package ru.nsu.fit.points;

import ij.ImagePlus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.nsu.fit.utils.MergeTiffsRGB;

import java.util.*;

public class KeyPointMatcher {
    private static final Logger LOGGER = LogManager.getLogger(KeyPointMatcher.class);

    private static final double THRESHOLD_TMP = 0.05;
    private static final double THRESHOLD_THETA = 5;

    private KeyPointMatcher() {
        throw new IllegalStateException("Utility class");
    }

    public static void mapKeyPointsOnFiles(String dirPath, List<String> fileNames, List<List<KeyPoint>> points) {
        String[] args = new String[fileNames.size() + 1];
        args[0] = dirPath;
        for (int i = 1; i <= fileNames.size(); i++) {
            args[i] = fileNames.get(i - 1);
        }
        ImagePlus image = MergeTiffsRGB.mergeTiffsFiles(args);

        List<List<KeyPoint>> matches = new ArrayList<>();
        matchRecursive(points, 0, new ArrayList<>(), matches);
    }

    public static void matchRecursive(
            List<List<KeyPoint>> groups,
            int depth,
            List<KeyPoint> current,
            List<List<KeyPoint>> result
    ) {
        if (depth == groups.size()) {
            result.add(new ArrayList<>(current));
            return;
        }

        List<KeyPoint> currentGroup = groups.get(depth);

        for (KeyPoint candidate : currentGroup) {
            if (isCompatible(current, candidate)) {
                current.add(candidate);
                matchRecursive(groups, depth + 1, current, result);
                current.remove(current.size() - 1);
            }
        }
    }

    private static boolean isCompatible(List<KeyPoint> current, KeyPoint candidate) {
        for (KeyPoint kp : current) {
            if (Math.abs(kp.getTmp() - candidate.getTmp()) > THRESHOLD_TMP
                    || Math.abs(kp.getTheta() - candidate.getTheta()) > THRESHOLD_THETA) {
                return false;
            }
        }
        return true;
    }
}
