package ru.nsu.fit.points;

import ij.ImagePlus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.nsu.fit.utils.MergeTiffsRGB;

import java.util.*;

public class KeyPointMatcher {
    private static final Logger LOGGER = LogManager.getLogger(KeyPointMatcher.class);

    private static final double THRESHOLD_TMP = 0.5;
    private static final double THRESHOLD_THETA = 5;

    private KeyPointMatcher() {
        throw new IllegalStateException("Utility class");
    }

    public static void mapKeyPointsOnFiles(
            String dirPath,
            List<String> fileNames,
            List<List<KeyPoint>> points,
            int radius) {
        String[] args = new String[fileNames.size() + 1];
        args[0] = dirPath;
        for (int i = 1; i <= fileNames.size(); i++) {
            args[i] = fileNames.get(i - 1);
        }
        ImagePlus image = MergeTiffsRGB.mergeTiffsFiles(args);

        StringBuilder b = new StringBuilder();
//        for (List<KeyPoint> list : points) {
//            for (KeyPoint keyPoint : list) {
//                b.append(keyPoint).append("| ");
//            }
//            LOGGER.info(b);
//            b = new StringBuilder();
//        }

        List<NavigableSet<KeyPoint>> sortedGroups = points.stream()
                .map(group -> (NavigableSet<KeyPoint>) new TreeSet<>(group))
                .toList();
        List<List<KeyPoint>> result = new ArrayList<>();
        matchRecursive(sortedGroups, radius, 0, new ArrayList<>(), result);
        List<List<KeyPoint>> cleaned = filterDuplicatesAndPickBest(result);

        b = new StringBuilder();
        LOGGER.info("СОПОСТАВЛЕННЫЕ КОРТЕЖИ");
        for (List<KeyPoint> tuple : cleaned) {
            for (KeyPoint keyPoint : tuple) {
                b.append(keyPoint).append(" -- ");
            }
            b.append("\n");
        }
        LOGGER.info(b);
    }

    public static void matchRecursive(
            List<NavigableSet<KeyPoint>> groups,
            double maxDistance,
            int depth,
            List<KeyPoint> currentMatch,
            List<List<KeyPoint>> result
    ) {
        if (depth == groups.size()) {
            result.add(new ArrayList<>(currentMatch));
            return;
        }

        NavigableSet<KeyPoint> currentGroup = groups.get(depth);
        if (depth == 0) {
            for (KeyPoint kp : currentGroup) {
                currentMatch.add(kp);
                matchRecursive(groups, maxDistance, depth + 1, currentMatch, result);
                currentMatch.remove(currentMatch.size() - 1);
            }
            return;
        }

        KeyPoint reference = currentMatch.get(0);
        double minTmp = reference.getTmp() - THRESHOLD_TMP;
        double maxTmp = reference.getTmp() + THRESHOLD_TMP;

        KeyPoint lower = new KeyPoint(0, 0, minTmp, -Double.MAX_VALUE);
        KeyPoint upper = new KeyPoint(0, 0, maxTmp, Double.MAX_VALUE);

        for (KeyPoint candidate : currentGroup.subSet(lower, true, upper, true)) {
            if (isCompatible(currentMatch, candidate, maxDistance)) {
                currentMatch.add(candidate);
                matchRecursive(groups, maxDistance, depth + 1, currentMatch, result);
                currentMatch.remove(currentMatch.size() - 1);
            }
        }
    }

    private static boolean isCompatible(
            List<KeyPoint> group,
            KeyPoint candidate,
            double maxDistance
    ) {
        for (KeyPoint kp : group) {
            if (Math.abs(kp.getTmp() - candidate.getTmp()) > THRESHOLD_TMP) return false;
//            if (Math.abs(kp.getTheta() - candidate.getTheta()) > THRESHOLD_THETA) return false;

            int dx = kp.getX() - candidate.getX();
            int dy = kp.getY() - candidate.getY();
            double distance = Math.hypot(dx, dy);
            if (distance > maxDistance) return false;
        }
        return true;
    }

    public static List<List<KeyPoint>> filterDuplicatesAndPickBest(List<List<KeyPoint>> rawMatches) {
        Set<KeyPoint> allPoints = new HashSet<>();
        List<List<KeyPoint>> result = new ArrayList<>();
        for (List<KeyPoint> match : rawMatches) {
            boolean unique = true;
            for (KeyPoint kp : match) {
                if (allPoints.contains(kp)) {
                    unique = false;
                } else {
                    allPoints.add(kp);
                }
            }
            if (unique) {
                result.add(match);
            }
        }
        return result;
    }
}
