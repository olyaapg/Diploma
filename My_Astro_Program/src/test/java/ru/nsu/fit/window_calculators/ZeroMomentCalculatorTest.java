package ru.nsu.fit.window_calculators;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ZeroMomentCalculatorTest {
    @Test
    void testSum() {
        double[][] matrix = {
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
        boolean[][] mask = {
                {false, false, true},
                {false, true, true},
                {true, true, true}
        };
        int radius = 2;
        ZeroMomentCalculator zeroMomentCalculator = new ZeroMomentCalculator(matrix, mask, radius);
        Assertions.assertEquals(14, zeroMomentCalculator.calculate(radius, radius, 0, radius, 0, radius)[0]);
        Assertions.assertEquals(1, zeroMomentCalculator.calculate(
                matrix[0].length - 1 - radius, radius,
                matrix[0].length - 1 - 2 * radius, matrix[0].length - 1 - radius,
                0, radius)[0]);
        for (int y = radius + 1; y <= matrix.length - 1 - radius; y++) {
            Assertions.assertEquals(0, zeroMomentCalculator.calculate(
                    matrix[0].length - 1 - radius, y,
                    matrix[0].length - 1 - 2 * radius, matrix[0].length - 1 - radius,
                    y - radius, y)[0]);
        }
    }
}
