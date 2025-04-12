package ru.nsu.fit.utils;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.RGBStackMerge;
import ij.process.ImageConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MergeTiffsRGB {
    private static final Logger LOGGER = LogManager.getLogger(MergeTiffsRGB.class);

    /**
     * Смержить TIFF-файлы в один, используя RGB-каналы и сохранить итоговый файл в директорию с исходными.
     * Может смержить не более 3 файлов (ибо всего три канала). На конце пути к директории должен быть /.
     *
     * @param args путь к директории с файлами, которые нужно смержить; названия файлов.
     */
    public static void main(String[] args) {
        if (!(args.length == 3 || args.length == 4)) {
            LOGGER.error("Incorrect number of arguments passed!");
            return;
        }
        String dir = args[0];
        ImagePlus[] channels = new ImagePlus[3];
        for (int i = 1; i < args.length; i++) {
            ImagePlus img = IJ.openImage(dir + args[i]);
            new ImageConverter(img).convertToGray8();
            channels[i - 1] = img;
        }
        ImagePlus composite = RGBStackMerge.mergeChannels(channels, true); // true = Create Composite
        ImageConverter converter = new ImageConverter(composite);
        converter.convertToRGB();
        IJ.save(composite, dir + "merged_output.tif");
    }
}
