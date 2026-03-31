package ru.nsu.fit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.fit.points.KeyPoint;
import ru.nsu.fit.points.KeyPointsProcessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class KeyPointsProcessorTest {

    private void check(List<KeyPoint> points, List<KeyPoint> expectedResult) {
        checkWithDistance(points, expectedResult, 2);
    }

    private void checkWithDistance(List<KeyPoint> points, List<KeyPoint> expectedResult, int distance) {
        // tiffProcessor с null сломает все
        KeyPointsProcessor kpp = new KeyPointsProcessor(distance, null);
        List<KeyPoint> result = new ArrayList<>();
        kpp.findKeyPoints(points, result);
        Collections.sort(result);
        Collections.sort(expectedResult);
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    void test1() {
        List<KeyPoint> points = new ArrayList<>();
        List<KeyPoint> expectedResult = new ArrayList<>();
        expectedResult.add(new KeyPoint(6, 7, 10, 0));
        points.add(new KeyPoint(3, 6, 2, 0));
        points.add(new KeyPoint(4, 6, 2, 0));
        points.add(new KeyPoint(5, 6, 2, 0));
        expectedResult.add(new KeyPoint(2, 5, 10, 0));
        points.add(new KeyPoint(4, 5, 2, 0));
        points.add(new KeyPoint(5, 5, 2, 0));
        points.add(new KeyPoint(6, 5, 2, 0));
        expectedResult.add(new KeyPoint(6, 4, 10, 0));
        expectedResult.add(new KeyPoint(2, 2, 10, 0));
        expectedResult.add(new KeyPoint(5, 2, 10, 0));
        points.addAll(expectedResult);

        check(points, expectedResult);
    }

    @Test
    void test2() {
        List<KeyPoint> points = new ArrayList<>();
        List<KeyPoint> expectedResult = new ArrayList<>();
        expectedResult.add(new KeyPoint(3, 2, 2, 0));
        points.add(new KeyPoint(5, 3, 6, 0));
        points.add(new KeyPoint(6, 3, 6, 0));
        points.add(new KeyPoint(6, 4, 6, 0));
        expectedResult.add(new KeyPoint(5, 4, 2, 0));
        expectedResult.add(new KeyPoint(8, 1, 6, 0));
        points.add(new KeyPoint(9, 6, 5, 0));
        points.add(new KeyPoint(8, 6, 6, 0));
        points.add(new KeyPoint(8, 7, 5, 0));
        points.add(new KeyPoint(7, 7, 6, 0));
        points.add(new KeyPoint(8, 8, 5, 0));
        expectedResult.add(new KeyPoint(9, 8, 2, 0));
        points.add(new KeyPoint(2, 5, 8, 0));
        expectedResult.add(new KeyPoint(2, 6, 1, 0));
        points.add(new KeyPoint(1, 6, 2, 0));
        points.add(new KeyPoint(2, 7, 8, 0));
        points.add(new KeyPoint(3, 7, 8, 0));
        points.add(new KeyPoint(2, 8, 8, 0));
        points.add(new KeyPoint(3, 8, 8, 0));
        expectedResult.add(new KeyPoint(4, 8, 1, 0));
        points.add(new KeyPoint(4, 9, 3, 0));
        points.add(new KeyPoint(3, 9, 2, 0));
        points.addAll(expectedResult);

        checkWithDistance(points, expectedResult, 1);
    }
}
