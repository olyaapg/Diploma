package ru.nsu.fit;


import java.util.Objects;

public class KeyPoint extends Point implements Comparable<KeyPoint> {
    private final double tmp;
    private final double theta;

    public KeyPoint(int x, int y, double tmp, double theta) {
        super(x, y);
        this.tmp = tmp;
        this.theta = theta;
    }

    public double getTmp() {
        return tmp;
    }

    public double getTheta() {
        return theta;
    }

    @Override
    public int compareTo(KeyPoint o) {
        int cmp = Double.compare(this.tmp, o.tmp);
        if (cmp != 0) return cmp;

        cmp = Double.compare(this.theta, o.theta);
        if (cmp != 0) return cmp;

        cmp = Integer.compare(this.getX(), o.getX());
        if (cmp != 0) return cmp;

        return Integer.compare(this.getY(), o.getY());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof KeyPoint other)) return false;
        return this.getX() == other.getX() &&
                this.getY() == other.getY() &&
                Double.compare(this.tmp, other.tmp) == 0 &&
                Double.compare(this.theta, other.theta) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY(), tmp, theta);
    }
}

