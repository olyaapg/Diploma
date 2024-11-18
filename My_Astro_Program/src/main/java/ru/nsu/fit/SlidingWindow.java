package ru.nsu.fit;


public class SlidingWindow {
    private final TiffProcessor tiffProcessor;

    public SlidingWindow(TiffProcessor tiffProcessor) {
        this.tiffProcessor = tiffProcessor;
    }

    public void runSlidingWindow(int radius, int threshold) {
        int[][] matrix = tiffProcessor.getOriginTiffMatrix();
        int rows = matrix.length;
        int cols = matrix[0].length;

        // Перебираем центральные точки
        // Пока что только с полным вхождением окна в границы картинки
        int progress = rows / 10; // нужно для отслеживания прогресса
        System.out.println("0%");

        for (int i = radius; i < rows - radius; i++) {
            for (int j = radius; j < cols - radius; j++) {
                var sum = getCircleWindow(matrix, i, j, radius);
                if (sum <= threshold) {
                    this.tiffProcessor.highlightPixel(i, j, (255 << 16));
                }
            }
            if (i % progress == 0) {
                System.out.println((i / progress) * 10 + "%");
            }
        }
    }

    private Integer getCircleWindow(int[][] matrix, int centerX, int centerY, int radius) {
        int sum = 0;
        // int rows = matrix.length;
        // int cols = matrix[0].length;
        // Ограничиваем прямоугольную область
        // for (int x = Math.max(0, centerX - radius); x <= Math.min(rows - 1, centerX + radius); x++) {
        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int y = centerY - radius; y <= centerY + radius; y++) {
                // Проверяем, находится ли точка внутри окружности
                if (Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) <= Math.pow(radius, 2)) {
                    // TODO: вставить функцию обработки пикселей внутри окна
                    sum += matrix[x][y];
                }
            }
        }
        return sum;
    }
}
