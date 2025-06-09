package ru.nsu.fit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.fit.points.KeyPoint;
import ru.nsu.fit.points.KeyPointMatcher;
import ru.nsu.fit.points.Point;

import java.util.*;

import static ru.nsu.fit.points.KeyPointMatcher.matchRecursive;

class KeyPointMatcherTest {

    @Test
    void test() {
        List<List<KeyPoint>> points = new ArrayList<>();
        List<KeyPoint> list = new ArrayList<>();
        list.add(new KeyPoint(1, 1, 0.06, 54));
        list.add(new KeyPoint(2, 1, 0.1, 28));
        list.add(new KeyPoint(3, 0, 0.001, 10));
        points.add(list);

        list = new ArrayList<>();
        list.add(new KeyPoint(1, 2, 0.01, 50));
        list.add(new KeyPoint(2, 2, 0.15, 25));
        list.add(new KeyPoint(3, 3, 0.05, 44));
        points.add(list);

        List<NavigableSet<KeyPoint>> sortedGroups = points.stream()
                .map(group -> (NavigableSet<KeyPoint>) new TreeSet<>(group))
                .toList();
        List<List<KeyPoint>> result = new ArrayList<>();
        matchRecursive(sortedGroups, 2, new ArrayList<>(), result);

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(2, result.get(0).size());
        Assertions.assertEquals(2, result.get(1).size());
    }

    @Test
    void testFindPointsLine() {
        List<Point> points = KeyPointMatcher.getLinePoints(2, 3, 5, 1);
        for (Point p : points) {
            System.out.println(p);
        }
        System.out.println();
        points = KeyPointMatcher.getLinePoints(1, 6, 2, 2);
        for (Point p : points) {
            System.out.println(p);
        }
        System.out.println();
        points = KeyPointMatcher.getLinePoints(2, 2, 2, 2);
        for (Point p : points) {
            System.out.println(p);
        }
    }
}
