package ru.nsu.fit.points;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.nsu.fit.utils.MergeTiffsRGB;

import java.util.*;

public class KeyPointMatcher {
    private static final Logger LOGGER = LogManager.getLogger(KeyPointMatcher.class);

    private static final double THRESHOLD_TMP = 0.6;
    private static final double THRESHOLD_THETA = 6;
    private static final double THRESHOLD_DISTANCE = 32;

    private KeyPointMatcher() {
        throw new IllegalStateException("Utility class");
    }

    public static void mapKeyPointsOnFiles(
            String dirPath,
            List<String> fileNames,
            List<List<KeyPoint>> points) {
        String[] args = new String[fileNames.size() + 1];
        args[0] = dirPath;
        for (int i = 1; i <= fileNames.size(); i++) {
            args[i] = "res_" + fileNames.get(i - 1);
        }
        ImagePlus image = MergeTiffsRGB.mergeTiffsFiles(args);
        assert image != null;
        ImageProcessor processor = image.getProcessor();

        List<NavigableSet<KeyPoint>> sortedGroups = points.stream()
                .map(group -> (NavigableSet<KeyPoint>) new TreeSet<>(group))
                .toList();
        List<List<KeyPoint>> result = new ArrayList<>();
        matchRecursive(sortedGroups, 0, new ArrayList<>(), result);
        StringBuilder b = new StringBuilder();
        LOGGER.info("ВСЕ КОРТЕЖИ");
        for (List<KeyPoint> tuple : result) {
            for (KeyPoint keyPoint : tuple) {
                b.append(keyPoint).append(" -- ");
            }
            b.append("\n");
        }
        LOGGER.info(b);

        List<List<KeyPoint>> cleaned = filterDuplicatesAndPickBest(result);
        for (List<KeyPoint> list : cleaned) {
            KeyPoint first = list.get(0);
            for (int i = 1; i < list.size(); i++) {
                KeyPoint second = list.get(i);
                List<Point> pointsBetween = getLinePoints(first.getX(), first.getY(), second.getX(), second.getY());
                for (int j = 0; j < pointsBetween.size() - 1; j++) {
                    var p = pointsBetween.get(j);
                    processor.putPixel(p.getY(), p.getX(), 255 << 16 | 255 << 8 | 255);
                }
            }
        }
        IJ.save(image, dirPath + "RESULT.tif");

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

    /**
     * Находит точки, лежащие на прямой от (x0; y0) до (x1; y1). Последняя точка в списке всегда (x1; y1).
     *
     * @param x0 первая координата первой точки.
     * @param y0 вторая координата первой точки.
     * @param x1 первая координата второй точки.
     * @param y1 вторая координата второй точки.
     * @return список точек, лежащих между заданными, включая вторую.
     */
    public static List<Point> getLinePoints(int x0, int y0, int x1, int y1) {
        List<Point> points = new ArrayList<>();
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;
        while (x0 != x1 || y0 != y1) {
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
            points.add(new Point(x0, y0));
        }
        return points;
    }

    public static void matchRecursive(
            List<NavigableSet<KeyPoint>> groups,
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
                matchRecursive(groups, depth + 1, currentMatch, result);
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
            if (isCompatible(currentMatch, candidate)) {
                currentMatch.add(candidate);
                matchRecursive(groups, depth + 1, currentMatch, result);
                currentMatch.remove(currentMatch.size() - 1);
            }
        }
    }

    private static boolean isCompatible(
            List<KeyPoint> group,
            KeyPoint candidate) {
        for (KeyPoint kp : group) {
            int dx = kp.getX() - candidate.getX();
            int dy = kp.getY() - candidate.getY();
            double distance = Math.hypot(dx, dy);
            if (distance > THRESHOLD_DISTANCE) return false;
            if ((Math.abs(kp.getTmp() - candidate.getTmp()) <= THRESHOLD_TMP) ||
                    (Math.abs(kp.getTheta() - candidate.getTheta()) <= THRESHOLD_THETA)
            )
                return true;
        }
        return false;
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
