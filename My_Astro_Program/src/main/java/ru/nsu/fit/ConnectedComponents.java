package ru.nsu.fit;

import java.util.*;


public class ConnectedComponents<T extends KeyPoint> {
    private final int squaredDistance;

    public ConnectedComponents(int distance) {
        this.squaredDistance = distance * distance;
    }

    private int getDistanceSquared(T a, T b) {
        int dx = a.getX() - b.getX();
        int dy = a.getY() - b.getY();
        return dx * dx + dy * dy;
    }

    private void exploreComponent(T start, List<T> component, Set<T> notVisited) {
        Deque<T> stack = new ArrayDeque<>();
        stack.push(start);
        notVisited.remove(start);

        while (!stack.isEmpty()) {
            T current = stack.pop();
            component.add(current);
            Iterator<T> iterator = notVisited.iterator();
            while (iterator.hasNext()) {
                T neighbor = iterator.next();
                if (getDistanceSquared(current, neighbor) <= squaredDistance) {
                    iterator.remove();
                    stack.push(neighbor);
                }
            }
        }
    }

    public List<List<T>> findConnectedComponents(List<T> points) {
        List<List<T>> components = new ArrayList<>();
        Set<T> notVisited = new HashSet<>(points);

        for (T currPoint : points) {
            if (notVisited.contains(currPoint)) {
                List<T> component = new ArrayList<>();
                exploreComponent(currPoint, component, notVisited);
                components.add(component);
            }
        }
        return components;
    }
}
