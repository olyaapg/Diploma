package ru.nsu.fit;


public class Main {

    public static void main(String[] args) {
        TiffProcessor tiffProcessor = new TiffProcessor("src/main/resources/Moon_IR_7/cropped.tif");
        int[][] pixelArray = tiffProcessor.getTiffMatrix();
        int radius = 128;

        // код для сбора аналитической инфы о яркости пикселей
        int min = -1, max = Integer.MAX_VALUE, xMin = -1, xMax = -1, yMin = -1, yMax = -1;
        double[] newArray = new double[pixelArray.length];
        for (int i = 0; i < pixelArray.length; i++) {
            int sum = 0;
            for (int j = 0; j < pixelArray[0].length; j++) {
                sum += pixelArray[i][j];
                if (pixelArray[i][j] < max) {
                    max = pixelArray[i][j];
                    xMax = i;
                    yMax = j;
                }
                if (pixelArray[i][j] > min) {
                    min = pixelArray[i][j];
                    xMin = i;
                    yMin = j;
                }
            }
            newArray[i] = (double) sum / pixelArray[0].length;
        }
        double sum = 0;
        for (double v : newArray) {
            sum += v;
        }
        System.out.println(sum / newArray.length);
        System.out.println(min + "x=" + xMin + " y=" + yMin);
        System.out.println(max + "x=" + xMax + " y=" + yMax);
        for (int i = 0; i < 150; i++) {
            System.out.print(pixelArray[i][0] + " ");
        }
        System.out.println(pixelArray[200][200]);
        tiffProcessor.highlightArea(xMin, yMin, radius, 65535);
        tiffProcessor.highlightArea(xMax, yMax, radius, 65535);
        tiffProcessor.showTiff();


//        SlidingWindow slidingWindow = new SlidingWindow(tiffProcessor);
//        slidingWindow.slidingWindow(pixelArray, radius, Integer.MAX_VALUE);
//        tiffProcessor.showTiff();
    }
}