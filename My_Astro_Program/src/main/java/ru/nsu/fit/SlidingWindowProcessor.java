package ru.nsu.fit;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.nsu.fit.moment_calculators.DipoleMomentCalculator;
import ru.nsu.fit.moment_calculators.QuadrupoleMomentCalculator;
import ru.nsu.fit.moment_calculators.ZeroMomentCalculator;

// TODO: проверить всё на double и int (суммы и т.п.)

/**
 * Класс SlidingWindowProcessor представляет собой алгоритм прохождения скользящего окна по входному изображению TIFF.
 * Манипулирует классами-калькуляторами моментов, запуская их при необходимости в зависимости от значений предыдущих
 * моментов в рассматриваемом окне.
 */
public class SlidingWindowProcessor {
    private static final Logger LOGGER = LogManager.getLogger(SlidingWindowProcessor.class);

    private final TiffProcessor tiffProcessor;
    private final double[][] normalizedMatrix;
    private boolean[][] mask;

    private static final int THRESHOLD_DIPOLE = 5_000;
    private static final int THRESHOLD_QUADRUPOLE = 10_000;

    /**
     * Создает объект класса SlidingWindowProcessor, получая нормализованную матрицу исходного изображения.
     *
     * @param tiffProcessor процессор, работающий с изображениями TIFF и связанный с исходным изображением.
     */
    public SlidingWindowProcessor(TiffProcessor tiffProcessor) {
        this.tiffProcessor = tiffProcessor;
        normalizedMatrix = tiffProcessor.getNormalizedMatrix();
    }

    private void createMask(int radius) {
        int squareRadius = radius * radius;
        mask = new boolean[radius + 1][radius + 1];
        for (int x = 0; x <= radius; x++) {
            for (int y = 0; y <= radius; y++) {
                if (Math.pow((double) x - radius, 2) + Math.pow((double) y - radius, 2) <= squareRadius) {
                    mask[x][y] = true;
                }
            }
        }
    }

    /**
     * Запускает алгоритм скользящего окна с указанными радиусом и трешхолдом для нулевого момента (для отсеивания темноты).
     *
     * @param radius    радиус скользящего окна.
     * @param threshold порог для отбрасывания ненужных темных пикселей.
     */
    public void runSlidingWindow(int radius, double threshold) {
        if (radius == 0) {
            LOGGER.error("The radius must not be zero!");
            return;
        }
        int rows = normalizedMatrix[0].length; // 2822
        int cols = normalizedMatrix.length; // 4144
        createMask(radius);
        int progress = rows / 10; // нужно для отслеживания прогресса
        LOGGER.info("Progress of the sliding window: 0%");

        double[] pXpY;
        double sum;
        double maxValDiff = Double.MIN_VALUE;
        double minValDiff = Double.MAX_VALUE;

        ZeroMomentCalculator zeroMomentCalculator = new ZeroMomentCalculator(normalizedMatrix, mask, radius);
        DipoleMomentCalculator dipoleMomentCalculator = new DipoleMomentCalculator(normalizedMatrix, mask);
        QuadrupoleMomentCalculator quadrupoleMomentCalculator = new QuadrupoleMomentCalculator(normalizedMatrix, mask);
        // Перебираем центральные точки
        // Пока что только с полным вхождением окна в границы картинки
        for (int x = radius; x < rows - radius; x++) {
            // пока в этом нет смысла, но оставлю на будущее, когда границы будут совпадать с границами исходного изображения
            int startX = Math.max(0, x - radius);
            int endX = Math.min(rows - 1, x);
            for (int y = radius; y < cols - radius; y++) {
                int startY = y - radius;
                sum = zeroMomentCalculator.calculate(x, y, startX, endX, startY, y)[0];
//                if (sum <= threshold) {
//                    continue;
//                }
//                double[] arrQ = quadrupoleMomentCalculator.calculate(x, y, startX, endX, startY, y);
//                var tmp = arrQ[0] + arrQ[2];
                var tmp = sum;
                if (tmp > maxValDiff) {
                    maxValDiff = tmp;
                }
                if (tmp < minValDiff) {
                    minValDiff = tmp;
                }
                //tiffProcessor.highlightPixel(y, x, );
//                pXpY = dipoleMomentCalculator.calculate(x, y, startX, endX, startY, y);
//                var module = Math.hypot(pXpY[0], pXpY[1]);
//                if (module < THRESHOLD_DIPOLE) {
//                    double[] arrQ = quadrupoleMomentCalculator.calculate(x, y, startX, endX, startY, y);
//                    if (arrQ[1] < THRESHOLD_QUADRUPOLE && arrQ[1] > -1 * THRESHOLD_QUADRUPOLE) {
//                        tiffProcessor.highlightPixel(y, x, 255 << 16);
//                    }
//                }
            }
            if (x % progress == 0) {
                LOGGER.info("Progress of the sliding window: {}%", (x / progress) * 10);
            }
        }
        LOGGER.info("maxValDiff = {}", maxValDiff);
        LOGGER.info("minValDiff = {}", minValDiff);
    }
}
