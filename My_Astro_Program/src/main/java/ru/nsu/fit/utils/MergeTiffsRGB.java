package ru.nsu.fit.utils;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.RGBStackMerge;
import ij.process.ImageConverter;

public class MergeTiffsRGB {
    public static void main(String[] args) {
        String dir = "src/test/resources/final_test/";
        ImagePlus img1 = IJ.openImage(dir + "res_001_many_craters.tif");
        ImagePlus img2 = IJ.openImage(dir + "res_002_many_craters.tif");
        ImagePlus img3 = IJ.openImage(dir + "res_003_many_craters.tif");

        new ImageConverter(img1).convertToGray8();
        new ImageConverter(img2).convertToGray8();
        new ImageConverter(img3).convertToGray8();

        ImagePlus[] channels = new ImagePlus[3];
        channels[0] = img1;
        channels[1] = img2;
        channels[2] = img3;

        ImagePlus composite = RGBStackMerge.mergeChannels(channels, true); // true = Create Composite

        ImageConverter converter = new ImageConverter(composite);
        converter.convertToRGB();

        IJ.save(composite, dir + "merged_output.tif");
    }
}
