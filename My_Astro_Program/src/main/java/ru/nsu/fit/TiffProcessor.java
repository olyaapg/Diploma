package ru.nsu.fit;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

// TODO: А можно сразу стек изображений открывать?

// TODO: в будущем можно распараллелить обработку и подсчет моментов для тифов
public class TiffProcessor {
    private final ImagePlus imagePlus;
    private ImagePlus colorImage;

    public TiffProcessor(String path) {
        imagePlus = IJ.openImage(path);
        if (imagePlus == null || imagePlus.getBitDepth() != 16) {
            System.out.println("Изображение не найдено или не является 16-битным.");
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

    // TODO: мб со сдвигами поиграться?
    private boolean isCircle(int centerX, int centerY, int x, int y, int radius) {
        var res = Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2);
        return res <= Math.pow(radius, 2) && Math.pow(radius, 2) - 500 <= res;
    }

    public void highlightArea(int centerX, int centerY, int radius, int color) {
        if (colorImage == null) {
            createColorImage();
        }
        ImageProcessor colorProcessor = colorImage.getProcessor();
        int height = colorImage.getHeight();
        int width = colorImage.getWidth();
        // Ограничиваем прямоугольную область
        // for (int x = Math.max(0, centerX - radius); x <= Math.min(rows - 1, centerX + radius); x++) {
        for (int x = Math.max(0, centerX - radius); x <= Math.min(height - 1, centerX + radius); x++) {
            for (int y = Math.max(0, centerY - radius); y <= Math.min(width - 1, centerY + radius); y++) {
                // Проверяем, находится ли точка внутри окружности
                if (isCircle(centerX, centerY, x, y, radius)) {
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
