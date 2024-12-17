package ru.nsu.fit.moment_calculators;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ZeroMomentCalculatorTest {
    @Test
    void testSum() {
        double[][] pixelArray = {
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
        ZeroMomentCalculator zeroMomentCalculator = new ZeroMomentCalculator(pixelArray, mask, radius);
        Assertions.assertEquals(14, zeroMomentCalculator.calculate(radius, radius, 0, radius, 0, radius));
        Assertions.assertEquals(1, zeroMomentCalculator.calculate(
                pixelArray[0].length - 1 - radius, radius,
                pixelArray[0].length - 1 - 2 * radius, pixelArray[0].length - 1 - radius,
                0, radius));
        for (int y = radius + 1; y <= pixelArray.length - 1 - radius; y++) {
            Assertions.assertEquals(0, zeroMomentCalculator.calculate(
                    pixelArray[0].length - 1 - radius, y,
                    pixelArray[0].length - 1 - 2 * radius, pixelArray[0].length - 1 - radius,
                    y - radius, y));
        }
    }
}
