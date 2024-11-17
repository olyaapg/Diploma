package ru.nsu.fit;

import ij.IJ;
import ij.ImagePlus;

// TODO: А можно сразу стек изображений открывать?
// TODO: в будущем можно распараллелить обработку и подсчет моментов для тифов
public class TiffProcessor {
    private final ImagePlus imagePlus;
    private int[][] pixelArray;

    public TiffProcessor(String path) {
        imagePlus = IJ.openImage(path);
        if (imagePlus == null || imagePlus.getBitDepth() != 16) {
            System.out.println("Изображение не найдено или не является 16-битным.");
            return;
        }
        pixelArray = imagePlus.getProcessor().getIntArray();
    }

    public int[][] getTiffMatrix() {
        return pixelArray;
    }

     public void showTiff() {
        imagePlus.getProcessor().setIntArray(pixelArray);
        imagePlus.show();
    }

    public void highlightArea(int centerX, int centerY, int radius, int color) {
        int rows = pixelArray.length;
        int cols = pixelArray[0].length;

        // Ограничиваем прямоугольную область
//        for (int x = Math.max(0, centerX - radius); x <= Math.min(rows - 1, centerX + radius); x++) {
        for (int x = Math.max(0, centerX - radius); x <= Math.min(pixelArray.length - 1, centerX + radius); x++) {
            for (int y = Math.max(0, centerY - radius); y <= Math.min(pixelArray[0].length - 1, centerY + radius); y++) {
                // Проверяем, находится ли точка внутри окружности
                if (isCircle(centerX, centerY, x, y, radius)) {
                    pixelArray[x][y] = color;
                }
            }
        }
        pixelArray[centerX][centerY] = color;
    }

    // TODO: мб со сдвигами поиграться?
    private boolean isCircle(int centerX, int centerY, int x, int y, int radius) {
        var res = Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2);
        return res <= Math.pow(radius, 2) && Math.pow(radius, 2) - 1500 <= res;
    }

    public void setPixelValue(int x, int y, int value) {
        pixelArray[x][y] = value;
    }
}
