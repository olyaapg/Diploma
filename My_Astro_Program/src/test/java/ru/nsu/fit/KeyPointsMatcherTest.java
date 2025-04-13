package ru.nsu.fit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.fit.points.KeyPoint;

import java.util.ArrayList;
import java.util.List;

import static ru.nsu.fit.points.KeyPointMatcher.matchRecursive;

class KeyPointsMatcherTest {

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

        List<List<KeyPoint>> matches = new ArrayList<>();
        matchRecursive(points, 0, new ArrayList<>(), matches);

        Assertions.assertEquals(2, matches.size());
        Assertions.assertEquals(2, matches.get(0).size());
        Assertions.assertEquals(2, matches.get(1).size());
    }
}
