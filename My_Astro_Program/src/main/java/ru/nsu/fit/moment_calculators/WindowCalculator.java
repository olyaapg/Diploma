package ru.nsu.fit.moment_calculators;

public abstract class WindowCalculator {
    protected double[][] matrix;
    protected boolean[][] mask;

    protected WindowCalculator(double[][] matrix, boolean[][] mask) {
        this.matrix = matrix;
        this.mask = mask;
    }

    // Абстрактный метод для подсчёта момента, его реализуют наследники
    public abstract double[] calculate(int centerX, int centerY, int startX, int endX, int startY, int endY);
}
