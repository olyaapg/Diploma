package ru.nsu.fit;

public record Point(int x, int y) {

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Point other)) return false;
        return this.x == other.x && this.y == other.y;
    }

}
