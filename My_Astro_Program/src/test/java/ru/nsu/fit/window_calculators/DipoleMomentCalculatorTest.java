package ru.nsu.fit.window_calculators;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DipoleMomentCalculatorTest {
    private final double[][] matrix = {
            {0, 0, 1, 0, 0, 0, 1, 1, 0},
            {0, 2, 2, 2, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 2, 2, 2, 0, 0, 0, 0, 0},
            {1, 0, 1, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0}
    };
    private final boolean[][] mask = {
            {false, false, true},
            {false, true, true},
            {true, true, true}
    };
    private final int radius = 2;
    private final DipoleMomentCalculator dipoleMomentCalculator = new DipoleMomentCalculator(matrix, mask);

    @Test
    void test1() {
        double[] moment = dipoleMomentCalculator.calculate(radius, radius, 0, radius, 0, radius);
        Assertions.assertEquals(0.0, moment[0]);
        Assertions.assertEquals(0.0, moment[1]);
        Assertions.assertEquals(0.0, Math.hypot(moment[0], moment[1]));
    }

    @Test
    void test2() {
        int centerX = 3;
        int centerY = 4;
        double[] moment = dipoleMomentCalculator.calculate(centerX, centerY, centerX - radius, centerX, centerY - radius, centerY);
        Assertions.assertEquals(-3.0, moment[0]);
        Assertions.assertEquals(-4.0, moment[1]);
        Assertions.assertEquals(5.0, Math.hypot(moment[0], moment[1]));

    }
}
