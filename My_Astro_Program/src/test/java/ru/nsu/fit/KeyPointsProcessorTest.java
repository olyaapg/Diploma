package ru.nsu.fit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class KeyPointsProcessorTest {

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

        KeyPointsProcessor kpp = new KeyPointsProcessor(2);
        List<KeyPoint> result = new ArrayList<>();
        kpp.findKeyPoints(points, result);
        Collections.sort(result);
        Collections.sort(expectedResult);
        Assertions.assertEquals(expectedResult, result);
    }
}
