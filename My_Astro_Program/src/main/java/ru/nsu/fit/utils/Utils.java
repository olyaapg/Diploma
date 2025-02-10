package ru.nsu.fit.utils;

public class Utils {
    private Utils() {
        throw new IllegalStateException("Utility class");
    }

    public static int normalizeModule(double value, double maxValue) {
        double normalized = value / maxValue;
        normalized = Math.max(0.0, Math.min(1.0, normalized));
        return (int) (normalized * 255);
    }

    public static int normalizeComponent(double value, double maxAbsValue) {
        // maxAbsValue - максимальное значение модуля (отрицательное и положительное) в скалярном смысле.
        double normalized = value / maxAbsValue; // Приведение к диапазону [-1, 1]
        normalized = Math.max(-1.0, Math.min(1.0, normalized)); // Ограничение на случай погрешностей
        return (int) (normalized * 127 + 128); // Сдвиг в диапазон [0, 255] с фиксацией 0 в центре
    }
}
