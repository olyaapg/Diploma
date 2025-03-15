package ru.nsu.fit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AverageValueCalculatorTest {
    private boolean[][] createMask(int radius) {
        int squareRadius = radius * radius;
        boolean[][] mask = new boolean[radius + 1][radius + 1];
        for (int x = 0; x <= radius; x++) {
            for (int y = 0; y <= radius; y++) {
                if (Math.pow((double) x - radius, 2) + Math.pow((double) y - radius, 2) <= squareRadius) {
                    mask[x][y] = true;
                }
            }
        }
        return mask;
    }

    @Test
    void test1() {
        double[][] matrix = {
                {1, 0, 0, 0, 1},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {1, 0, 0, 0, 1}
        };
        AverageValueCalculator avc = new AverageValueCalculator(matrix, createMask(2));
        Assertions.assertEquals(0, avc.calculate(2, 2, 0, 2, 0, 2)[0]);
    }

    @Test
    void test2() {
        double[][] matrix = {
                {1, 0, 2, 0, 1},
                {0, 2, 2, 2, 0},
                {2, 2, 2, 2, 2},
                {0, 2, 2, 2, 0},
                {1, 0, 2, 0, 1}
        };
        AverageValueCalculator avc = new AverageValueCalculator(matrix, createMask(2));
        Assertions.assertEquals(2, avc.calculate(2, 2, 0, 2, 0, 2)[0]);
    }

    @Test
    void test3() {
        double[][] matrix = {
                {1, -10, 5, -10, 1},
                {-10, 8, 10, 8, -10},
                {2, 5, 10, 7, 5},
                {-10, 4, 7, 4, -10},
                {1, -10, 3, -10, 1}
        };
        AverageValueCalculator avc = new AverageValueCalculator(matrix, createMask(2));
        Assertions.assertEquals(6, avc.calculate(2, 2, 0, 2, 0, 2)[0]);
    }
}
