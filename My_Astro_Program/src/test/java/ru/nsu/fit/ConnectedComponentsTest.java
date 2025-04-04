package ru.nsu.fit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

class ConnectedComponentsTest {

    @Test
    void test1() {
        List<KeyPoint> points = new ArrayList<>();
        List<KeyPoint> puddle1 = new ArrayList<>();
        List<KeyPoint> puddle2 = new ArrayList<>();
        List<KeyPoint> puddle3 = new ArrayList<>();
        puddle1.add(new KeyPoint(6, 7, 2, 0));
        puddle1.add(new KeyPoint(3, 6, 2, 0));
        puddle1.add(new KeyPoint(4, 6, 2, 0));
        puddle1.add(new KeyPoint(5, 6, 2, 0));
        puddle1.add(new KeyPoint(2, 5, 2, 0));
        puddle1.add(new KeyPoint(4, 5, 2, 0));
        puddle1.add(new KeyPoint(5, 5, 2, 0));
        puddle1.add(new KeyPoint(6, 5, 2, 0));
        puddle1.add(new KeyPoint(6, 4, 2, 0));
        puddle2.add(new KeyPoint(2, 2, 2, 0));
        puddle3.add(new KeyPoint(5, 2, 2, 0));
        points.addAll(puddle1);
        points.addAll(puddle2);
        points.addAll(puddle3);

        ConnectedComponents<KeyPoint> components = new ConnectedComponents<>(2);
        List<List<KeyPoint>> puddles = components.findConnectedComponents(points);

        Assertions.assertEquals(new HashSet<>(puddles.get(0)), new HashSet<>(puddle1));
        Assertions.assertEquals(new HashSet<>(puddles.get(1)), new HashSet<>(puddle2));
        Assertions.assertEquals(new HashSet<>(puddles.get(2)), new HashSet<>(puddle3));
    }

    @Test
    void test2() {
        List<KeyPoint> points = new ArrayList<>();
        List<KeyPoint> puddle1 = new ArrayList<>();
        List<KeyPoint> puddle2 = new ArrayList<>();
        List<KeyPoint> puddle3 = new ArrayList<>();
        puddle1.add(new KeyPoint(0, 0, 2, 0));
        puddle1.add(new KeyPoint(1, 0, 2, 0));
        puddle1.add(new KeyPoint(0, 1, 2, 0));
        puddle2.add(new KeyPoint(5, 5, 2, 0));
        puddle2.add(new KeyPoint(5, 6, 2, 0));
        puddle3.add(new KeyPoint(10, 10, 2, 0));
        points.addAll(puddle1);
        points.addAll(puddle2);
        points.addAll(puddle3);

        ConnectedComponents<KeyPoint> components = new ConnectedComponents<>(2);
        List<List<KeyPoint>> puddles = components.findConnectedComponents(points);

        Assertions.assertEquals(new HashSet<>(puddles.get(0)), new HashSet<>(puddle1));
        Assertions.assertEquals(new HashSet<>(puddles.get(1)), new HashSet<>(puddle2));
        Assertions.assertEquals(new HashSet<>(puddles.get(2)), new HashSet<>(puddle3));
    }
}
