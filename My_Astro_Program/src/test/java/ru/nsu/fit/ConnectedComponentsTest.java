package ru.nsu.fit;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class ConnectedComponentsTest {

    @Test
    void test1() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(6, 7));
        points.add(new Point(3, 6));
        points.add(new Point(4, 6));
        points.add(new Point(5, 6));
        points.add(new Point(2, 5));
        points.add(new Point(4, 5));
        points.add(new Point(5, 5));
        points.add(new Point(6, 5));
        points.add(new Point(6, 4));
        points.add(new Point(2, 2));
        points.add(new Point(5, 2));

        ConnectedComponents components = new ConnectedComponents(2);
        List<Set<Point>> list = components.findConnectedComponents(points);

        for (Set<Point> set : list) {
            for (Point point : set) {
                System.out.print("(" + point.x() + ";" + point.y() + "), ");
            }
            System.out.println();
        }
    }

    @Test
    void test2() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(0, 0));
        points.add(new Point(1, 0));
        points.add(new Point(0, 1));
        points.add(new Point(5, 5));
        points.add(new Point(5, 6));
        points.add(new Point(10, 10));

        ConnectedComponents components = new ConnectedComponents(2);
        List<Set<Point>> list = components.findConnectedComponents(points);

        for (Set<Point> set : list) {
            for (Point point : set) {
                System.out.print("(" + point.x() + ";" + point.y() + "), ");
            }
            System.out.println();
        }
    }
}
