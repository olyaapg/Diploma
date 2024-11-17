package ru.nsu.fit;

import java.util.ArrayList;
import java.util.List;

public class SlidingWindow {
    private final TiffProcessor tiffProcessor;

    public SlidingWindow(TiffProcessor tiffProcessor) {
        this.tiffProcessor = tiffProcessor;
    }

    public void slidingWindow(int[][] matrix, int radius, int threshold) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        // Перебираем центральные точки
        // сначала с полным вхождением окна в границы картинки
        int k = rows / 10;
        System.out.println("0%");
        for (int i = radius; i < rows - radius; i++) {
            for (int j = radius; j < cols - radius; j++) {
                // Получаем элементы внутри окружности
                List<Integer> windowElements = getCircleWindow(matrix, i, j, radius);

                // Обрабатываем окно (например, считаем сумму)
                var sum = windowElements.parallelStream().mapToInt(Integer::intValue).sum();
                if (sum <= threshold) {
                    this.tiffProcessor.setPixelValue(i, j, 65535);
                }
            }
            if (i % k == 0) {
                System.out.println((i / k) * 10 + "%");
            }
        }
    }

    private List<Integer> getCircleWindow(int[][] matrix, int centerX, int centerY, int radius) {
        List<Integer> elements = new ArrayList<>();
        int rows = matrix.length;
        int cols = matrix[0].length;

        // Ограничиваем прямоугольную область
//        for (int x = Math.max(0, centerX - radius); x <= Math.min(rows - 1, centerX + radius); x++) {
        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int y = centerY - radius; y <= centerY + radius; y++) {
                // Проверяем, находится ли точка внутри окружности
                if (isInsideCircle(centerX, centerY, x, y, radius)) {
                    elements.add(matrix[x][y]);
                }
            }
        }

        return elements;
    }

    private boolean isInsideCircle(int centerX, int centerY, int x, int y, int radius) {
        return Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) <= Math.pow(radius, 2);
    }
}
