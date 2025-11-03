package comp2450.model;

/**
 * Represents a coordinate point with x and y values.
 */
public record coordinate(int x, int y) {
    public int getX() { return x; }
    public int getY() { return y; }
}
