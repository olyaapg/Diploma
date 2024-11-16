package ru.nsu.fit;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;

public class Utils {
    // А можно сразу стек изображений открывать?
    public static int[][] tiffToMatrix(String path) {
        ImagePlus imagePlus = IJ.openImage(path);
        ImageProcessor imageProcessor = imagePlus.getProcessor();
        return imageProcessor.getIntArray();
    }
}
