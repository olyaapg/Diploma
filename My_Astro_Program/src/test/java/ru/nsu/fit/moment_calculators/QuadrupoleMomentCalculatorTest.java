package ru.nsu.fit.moment_calculators;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class QuadrupoleMomentCalculatorTest {
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

    @Test
    void test1() {
        QuadrupoleMomentCalculator quadrupoleMomentCalculator = new QuadrupoleMomentCalculator(matrix, mask);
        double[] result = quadrupoleMomentCalculator.calculate(radius, radius, 0, radius, 0, radius);
        Assertions.assertEquals(-4, result[0]);
        Assertions.assertEquals(0, result[1]);
        Assertions.assertEquals(32, result[2]);
    }

    @Test
    void test2() {
        QuadrupoleMomentCalculator quadrupoleMomentCalculator = new QuadrupoleMomentCalculator(matrix, mask);
        double[] result = quadrupoleMomentCalculator.calculate(
                matrix[0].length - 1 - radius, matrix.length - 1 - radius,
                matrix[0].length - 1 - 2 * radius, matrix[0].length - 1 - radius,
                matrix.length - 1 - radius * 2, matrix.length - 1 - radius);
        Assertions.assertEquals(0, result[0]);
        Assertions.assertEquals(0, result[1]);
        Assertions.assertEquals(0, result[2]);
    }
}
