package ru.nsu.fit;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// TODO: А можно сразу стек изображений открывать?

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
        LOGGER.info("The processing of the image \"{}\" has begun", imagePlus.getTitle());
    }

    public int[][] getOriginTiffMatrix() {
        return imagePlus.getProcessor().getIntArray();
    }

    public int getHeight() {
        return imagePlus.getHeight();
    }

    public void highlightPixel(int u, int v, int color) {
        if (colorImage == null) {
            createColorImage();
        }
        // Закрашиваем пиксель цветом
        colorImage.getProcessor().putPixel(u, v, color);
    }

    private boolean isCircle(int centerX, int centerY, double x, double y, double squareRadius) {
        var res = Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2);
        return res <= squareRadius;
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
        int height = imagePlus.getHeight();
        int length = imagePlus.getWidth();
        ImageProcessor processor = imagePlus.getProcessor();
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
    }
}
