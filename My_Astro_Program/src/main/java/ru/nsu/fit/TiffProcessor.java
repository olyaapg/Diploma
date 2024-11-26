package ru.nsu.fit;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// TODO: А можно сразу стек изображений открывать?

// TODO: в будущем можно распараллелить обработку и подсчет моментов для тифов
public class TiffProcessor {
    private static final Logger LOGGER = LogManager.getLogger(TiffProcessor.class);

    private final ImagePlus imagePlus;
    private ImagePlus colorImage;

    public TiffProcessor(String path) {
        imagePlus = IJ.openImage(path);
        if (imagePlus == null || imagePlus.getBitDepth() != 16) {
            String exMessage = "The image was not found or is not 16-bit.";
            LOGGER.error(exMessage);
            throw new IllegalArgumentException(exMessage);
        }
    }

    public int[][] getOriginTiffMatrix() {
        return imagePlus.getProcessor().getIntArray();
    }

    public int getHeight() {
        return imagePlus.getHeight();
    }

    public int getWidth() {
        return imagePlus.getWidth();
    }

    public void highlightPixel(int x, int y, int color) {
        if (colorImage == null) {
            createColorImage();
        }
        // Закрашиваем пиксель красным цветом
        colorImage.getProcessor().putPixel(x, y, color); // Красный цвет
    }

    private boolean isCircle(int centerX, int centerY, double x, double y, double squareRadius) {
        var res = Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2);
        return res <= squareRadius && squareRadius - 500 <= res; // TODO: -500 задает толщину окружности
    }

    public void highlightArea(int centerX, int centerY, int radius, int color) {
        if (colorImage == null) {
            createColorImage();
        }
        ImageProcessor colorProcessor = colorImage.getProcessor();
        int height = colorImage.getHeight();
        int width = colorImage.getWidth();
        double squareRadius = Math.pow(radius, 2);
        // Ограничиваем прямоугольную область
        for (int x = Math.max(0, centerX - radius); x <= Math.min(height - 1, centerX + radius); x++) {
            for (int y = Math.max(0, centerY - radius); y <= Math.min(width - 1, centerY + radius); y++) {
                // Проверяем, находится ли точка внутри окружности
                if (isCircle(centerX, centerY, x, y, squareRadius)) {
                    colorProcessor.putPixel(x, y, color);
                }
            }
        }
        colorProcessor.putPixel(centerX, centerY, color);
    }

    private void createColorImage() {
        int height = imagePlus.getHeight();
        int width = imagePlus.getWidth();
        ImageProcessor processor = imagePlus.getProcessor();
        ColorProcessor colorProcessor = new ColorProcessor(width, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int intensity = processor.getPixel(x, y); // Яркость пикселя
                int normalized = (int) ((intensity / 65535.0) * 255); // Нормализация в диапазон 0-255
                colorProcessor.putPixel(x, y, (normalized << 16) | (normalized << 8) | normalized); // Серый цвет
            }
        }
        colorImage = new ImagePlus("Colorized Image", colorProcessor);
    }

    public void saveColorTiff(String path) {
        colorImage.show();
        IJ.saveAsTiff(colorImage, path);
    }
}
