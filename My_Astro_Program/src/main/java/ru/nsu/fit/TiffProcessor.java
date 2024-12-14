package ru.nsu.fit;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

import static ru.nsu.fit.utils.QuickSelect.findKthLargest;

// TODO: А можно сразу стек изображений открывать?

public class TiffProcessor {
    private static final Logger LOGGER = LogManager.getLogger(TiffProcessor.class);

    private final ImagePlus originalImage;
    private ImagePlus colorImage;
    private double[][] normalizedMatrix;

    public TiffProcessor(String path, double percentForBrightness) {
        originalImage = IJ.openImage(path);
        if (originalImage == null || originalImage.getBitDepth() != 16) {
            String exMessage = "The image was not found or is not 16-bit.";
            LOGGER.error(exMessage);
            throw new IllegalArgumentException(exMessage);
        }
        LOGGER.info("The processing of the image \"{}\" has begun", originalImage.getTitle());
        normalizeAndSetMatrix(originalImage.getProcessor().getIntArray(), percentForBrightness);
    }

    private void normalizeAndSetMatrix(int[][] originalMatrix, double percentForBrightness) {
        if (percentForBrightness <= 0 || percentForBrightness > 1) {
            percentForBrightness = 0.98;
            LOGGER.error("When normalizing the matrix, the percentage was set incorrectly! " +
                    "It should be in [0...1]. The default value is set to {}.", percentForBrightness);
        }
        int[] pixelArray = Arrays.stream(originalMatrix)
                .flatMapToInt(Arrays::stream)
                .toArray();
        int n = pixelArray.length;
        int index = (int) Math.ceil(percentForBrightness * n) - 1;
        int maxBrightness = findKthLargest(pixelArray, index);
        LOGGER.info("Maximum brightness for {}%: {}", percentForBrightness * 100, maxBrightness);
        int rows = originalMatrix.length;
        int cols = originalMatrix[0].length;
        normalizedMatrix = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double value = Math.round(((double) originalMatrix[i][j] / maxBrightness) * 10_000) / 10_000.0;
                normalizedMatrix[i][j] = Math.min(value, 1.0);
            }
        }
        LOGGER.info("Normalization completed.");
    }

    public double[][] getNormalizedMatrix() {
        return normalizedMatrix;
    }

    public int getHeight() {
        return originalImage.getHeight();
    }

    private boolean isCircle(int centerX, int centerY, double x, double y, double squareRadius) {
        var res = Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2);
        return res <= squareRadius;
    }

    public void highlightPixel(int u, int v, int color) {
        if (colorImage == null) {
            createColorImage();
        }
        // Закрашиваем пиксель цветом
        colorImage.getProcessor().putPixel(u, v, color);
    }

    public void highlightArea(int centerX, int centerY, int radius, int color) {
        if (colorImage == null) {
            createColorImage();
        }
        ImageProcessor colorProcessor = colorImage.getProcessor();
        int height = colorImage.getHeight();
        int length = colorImage.getWidth();
        double squareRadius = Math.pow(radius, 2);
        // Ограничиваем прямоугольную область
        for (int x = Math.max(0, centerX - radius); x <= Math.min(length - 1, centerX + radius); x++) {
            for (int y = Math.max(0, centerY - radius); y <= Math.min(height - 1, centerY + radius); y++) {
                // Проверяем, находится ли точка внутри окружности
                if (isCircle(centerX, centerY, x, y, squareRadius)) {
                    colorProcessor.putPixel(x, y, color);
                }
            }
        }
        colorProcessor.putPixel(centerX, centerY, color);
    }

    private void createColorImage() {
        int height = originalImage.getHeight();
        int length = originalImage.getWidth();
        ImageProcessor processor = originalImage.getProcessor();
        ColorProcessor colorProcessor = new ColorProcessor(length, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < length; x++) {
                int intensity = processor.getPixel(x, y); // Яркость пикселя
                int normalized = (int) ((intensity / 65535.0) * 255); // Нормализация в диапазон 0-255
                colorProcessor.putPixel(x, y, (normalized << 16) | (normalized << 8) | normalized); // Серый цвет
            }
        }
        colorImage = new ImagePlus("Colorized Image", colorProcessor);
    }

    public void saveColorTiff(String path) {
        IJ.saveAsTiff(colorImage, path);
        LOGGER.info("The result was saved to \"{}\"", path);
    }
}
