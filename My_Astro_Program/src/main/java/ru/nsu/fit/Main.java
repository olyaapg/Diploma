package ru.nsu.fit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.nsu.fit.points.KeyPoint;
import ru.nsu.fit.points.KeyPointMatcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    private static int radius = 64;
    private static double threshold = 1;
    private static final int DISTANCE = 3;

    private static String pathToImages;
    private static String pathToSave;

    /**
     * Запуск программы. На концах путей в аргументах должны быть /.
     *
     * @param args путь к изображениям, которые нужно обработать;
     *             путь для сохранения файлов;
     *             int радиус окр-ти (опц.);
     *             double стартовый порог для нахождения ключевых точек: (x^2-y^2)/(x^2+y^2) < threshold (опц)
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            LOGGER.error("Not enough arguments passed!");
            return;
        }
        pathToImages = args[0];
        pathToSave = args[1];
        if (args.length > 2) {
            radius = Integer.parseInt(args[2]);
        }
        if (args.length > 3) {
            threshold = Double.parseDouble(args[3]);
        }

        List<String> fileNames;
        try (Stream<Path> filesStream = Files.list(Paths.get(pathToImages))) {
            fileNames = filesStream
                    .filter(file -> file.toString().endsWith(".tif"))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .toList();
        } catch (IOException e) {
            LOGGER.error("Error reading the directory: {}", pathToImages, e);
            return;
        }
        List<List<KeyPoint>> results = getKeyPointsFromFiles(fileNames);
        KeyPointMatcher.mapKeyPointsOnFiles(pathToSave, fileNames, results);
    }

    private static List<List<KeyPoint>> getKeyPointsFromFiles(List<String> fileNames) {
        try (ExecutorService executor = Executors.newFixedThreadPool(4)) {
            List<Future<List<KeyPoint>>> futures = new ArrayList<>();
            for (String file : fileNames) {
                String pathToImage = pathToImages + file;
                String pathToResult = pathToSave + "res_" + file;
                futures.add(executor.submit(new MainWorker(pathToImage, pathToResult, radius, threshold, DISTANCE)));
            }
            List<List<KeyPoint>> results = new ArrayList<>();
            for (Future<List<KeyPoint>> future : futures) {
                try {
                    results.add(future.get());
                } catch (InterruptedException | ExecutionException e) {
                    LOGGER.error("Internal error occurred");
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            }
            return results;
        }
    }
}