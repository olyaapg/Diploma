package ru.nsu.fit;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

import static ru.nsu.fit.utils.QuickSelect.findKthLargest;

/**
 * TiffProcessor класс представляет собой процессор для работы с изображениями формата TIFF.
 * Включает в себя закрашивание пикселей, открытие и сохранение файла, нормализацию значений пикселей.
 */
public class TiffProcessor {
    private static final Logger LOGGER = LogManager.getLogger(TiffProcessor.class);

    private final ImagePlus originalImage;
    private ImagePlus colorImage;
    private double[][] normalizedMatrix;

    private static final double PERCENT_FOR_BRIGHTNESS = 0.98;
    private static final int APPROXIMATION = 100_000;

    /**
     * Создает объект класса TiffProcessor.
     * Проверяет корректность изображения по входному пути, нормализует матрицу пикселей исходного изображения.
     *
     * @param path путь до исходного изображения.
     */
    public TiffProcessor(String path) {
        originalImage = IJ.openImage(path);
        if (originalImage == null || originalImage.getBitDepth() != 16) {
            String exMessage = "The image was not found or is not 16-bit.";
            LOGGER.error(exMessage);
            throw new IllegalArgumentException(exMessage);
        }
        LOGGER.info("The processing of the image \"{}\" has begun", originalImage.getTitle());
        normalizeAndSetMatrix(originalImage.getProcessor().getIntArray());
    }

    /**
     * Возвращает нормализованную матрицу исходного изображения.
     *
     * @return нормализованная матрица изображения.
     */
    public double[][] getNormalizedMatrix() {
        return normalizedMatrix;
    }

    private void normalizeAndSetMatrix(int[][] originalMatrix) {
        int[] pixelArray = Arrays.stream(originalMatrix)
                .flatMapToInt(Arrays::stream)
                .toArray();
        int n = pixelArray.length;
        int index = (int) Math.ceil(PERCENT_FOR_BRIGHTNESS * n) - 1;
        int maxBrightness = findKthLargest(pixelArray, index);
        LOGGER.info("Maximum brightness for {}% of pixels: {}", PERCENT_FOR_BRIGHTNESS * 100, maxBrightness);
        int rows = originalMatrix.length;
        int cols = originalMatrix[0].length;
        normalizedMatrix = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double value = Math.round(((double) originalMatrix[i][j] / maxBrightness) * APPROXIMATION) / (double) APPROXIMATION;
                normalizedMatrix[i][j] = Math.min(value, 1.0);
            }
        }
        LOGGER.info("Normalization completed.");
    }

    private void createColorImage() {
        int height = originalImage.getHeight();
        int length = originalImage.getWidth();
        ImageProcessor originalProcessor = originalImage.getProcessor();
        ColorProcessor colorProcessor = new ColorProcessor(length, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < length; x++) {
                int pixelValue = originalProcessor.getPixel(x, y);
                int scaledValue = (int) ((pixelValue / 65535.0) * 255.0);
                int rgb = (scaledValue << 16) | (scaledValue << 8) | scaledValue;
                colorProcessor.putPixel(x, y, rgb);
            }
        }
        colorImage = new ImagePlus("Colorized Image", colorProcessor);
        colorImage.show();
    }

    /**
     * Закрашивает цветом пиксель с координатами (u, v) изображения. Используется для отладочных целей.
     *
     * @param u     первая координата пикселя.
     * @param v     вторая координата пикселя.
     * @param color цвет пикселя.
     */
    public void highlightPixel(int u, int v, int color) {
        if (colorImage == null) {
            createColorImage();
        }
        // Закрашиваем пиксель цветом
        var p = colorImage.getProcessor();
        p.putPixel(u, v, color);
    }

    public void highlightPixelWithSpecificColor(int u, int v, char colorLetter) {
        if (colorImage == null) {
            createColorImage();
        }
        var p = colorImage.getProcessor();
        int oldColor = p.getPixel(u, v);
        if (colorLetter == 'R') {
            p.putPixel(u, v, (255 << 16) | ((oldColor >> 8) & 0xFF << 8) | oldColor & 0xFF);
        } else if (colorLetter == 'B') {
            p.putPixel(u, v, ((oldColor >> 16) & 0xFF << 16) | ((oldColor >> 8) & 0xFF << 8) | 255);
        } else {
            p.putPixel(u, v, ((oldColor >> 16) & 0xFF << 16) | (255 << 8) | oldColor & 0xFF);
        }
    }

    public void highlightArea(int centerX, int centerY, int radius, int color) {
        if (colorImage == null) {
            createColorImage();
        }
        ImageProcessor colorProcessor = colorImage.getProcessor();
        int height = colorImage.getHeight();
        int length = colorImage.getWidth();
        double squareRadius = Math.pow(radius, 2);
        for (int x = Math.max(0, centerX - radius); x <= Math.min(length - 1, centerX + radius); x++) {
            for (int y = Math.max(0, centerY - radius); y <= Math.min(height - 1, centerY + radius); y++) {
                int diffX = x - centerX;
                int diffY = y - centerY;
                var res = diffX * diffX + diffY * diffY;
                if (res <= squareRadius && squareRadius - 300 <= res) {
                    colorProcessor.putPixel(x, y, color);
                }
            }
        }
        colorProcessor.putPixel(centerX, centerY, color);
    }

    /**
     * Сохранить цветное изображение TIFF.
     *
     * @param path путь для сохранения файла.
     */
    public void saveColorTiff(String path) {
        IJ.saveAsTiff(colorImage, path);
        LOGGER.info("The result was saved to \"{}\"", path);
    }
}
