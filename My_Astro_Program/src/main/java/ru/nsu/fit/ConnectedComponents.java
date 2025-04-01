package ru.nsu.fit;

import java.util.Set;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ConnectedComponents {
    private final int squaredDistance;

    public ConnectedComponents(int distance) {
        this.squaredDistance = distance * distance;
    }

    private double getDistanceSquared(Point a, Point b) {
        return Math.pow((double) a.x() - b.x(), 2) + Math.pow((double) a.y() - b.y(), 2);
    }

    private void exploreComponent(Point start, Set<Point> component, Set<Point> notVisited) {
        Deque<Point> stack = new ArrayDeque<>();
        stack.push(start);
        notVisited.remove(start);

        while (!stack.isEmpty()) {
            Point current = stack.pop();
            component.add(current);
            Iterator<Point> iterator = notVisited.iterator();
            while (iterator.hasNext()) {
                Point neighbor = iterator.next();
                if (getDistanceSquared(current, neighbor) <= squaredDistance) {
                    iterator.remove();
                    stack.push(neighbor);
                }
            }
        }
    }

    public List<Set<Point>> findConnectedComponents(List<Point> points) {
        List<Set<Point>> components = new ArrayList<>();
        Set<Point> notVisited = new HashSet<>(points); // непотокобезопасно, нужен ConcurrentHashMap

        for (Point currPoint : points) {
            if (notVisited.contains(currPoint)) {
                Set<Point> component = new HashSet<>();
                exploreComponent(currPoint, component, notVisited);
                components.add(component);
            }
        }
        return components;
    }
}
