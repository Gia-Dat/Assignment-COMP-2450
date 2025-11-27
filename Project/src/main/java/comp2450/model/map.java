package comp2450.model;

import com.google.common.base.Preconditions;
import java.util.ArrayList;

import comp2450.exceptions.InvalidCoordinateException;
import comp2450.exceptions.ObstacleCollisionException;

public class map {
    private final String name;
    private final ArrayList<obstacle> obstacles = new ArrayList<>();
    private final int gridWidth;
    private final int gridHeight;
    private final char[][] grid;

    public static final char EMPTY = '.', OBSTACLE = '*', ROUTE = '>';

    public map(String name) {
        this(name, 20, 20);
    }

    public map(String name, int width, int height) {
        checkArgs(name, width, height);
        this.name = name.trim();
        this.gridWidth = width;
        this.gridHeight = height;
        this.grid = new char[gridHeight][gridWidth];
        initializeGrid();
        checkInvariant();
    }

    private static void checkArgs(String name, int w, int h) {
        Preconditions.checkNotNull(name, "name");
        Preconditions.checkArgument(!name.trim().isEmpty(), "empty name");
        Preconditions.checkArgument(w > 0 && h > 0, "dims > 0");
    }

    private void checkInvariant() {
        Preconditions.checkState(name != null && !name.isEmpty(), "name");
        Preconditions.checkState(grid != null && grid.length == gridHeight && grid[0].length == gridWidth,
                "grid shape");
        Preconditions.checkState(obstacles != null, "obstacles");
    }

    private void initializeGrid() {
        for (int y = 0; y < gridHeight; y++)
            for (int x = 0; x < gridWidth; x++)
                grid[y][x] = EMPTY;
    }

    public boolean isInBounds(int x, int y) {
        return x >= 0 && x < gridWidth && y >= 0 && y < gridHeight;
    }

    public boolean isObstacle(int x, int y) {
        return isInBounds(x, y) && grid[y][x] == OBSTACLE;
    }

    public boolean isRoute(int x, int y) {
        return isInBounds(x, y) && grid[y][x] == ROUTE;
    }

    public boolean isFree(int x, int y) {
        return isInBounds(x, y) && grid[y][x] == EMPTY;
    }

    // Obstacles
    public void addObstacle(obstacle o)
            throws InvalidCoordinateException, ObstacleCollisionException {
        Preconditions.checkNotNull(o, "obstacle");
        coordinate c = o.getObstacleCoordinate();
        int x = c.getX(), y = c.getY();

        if (!isInBounds(x, y)) {
            throw new InvalidCoordinateException();
        }
        if (grid[y][x] == OBSTACLE || grid[y][x] == ROUTE) {
            // treat placing on existing obstacle OR route as collision
            throw new ObstacleCollisionException();
        }

        obstacles.add(o);
        grid[y][x] = OBSTACLE;
    }

    public void removeObstacle(obstacle ob) {
        Preconditions.checkNotNull(ob, "obstacle");
        Preconditions.checkState(obstacles.contains(ob), "must exist");
        int x = ob.getObstacleCoordinate().getX(), y = ob.getObstacleCoordinate().getY();
        if (isInBounds(x, y) && grid[y][x] == OBSTACLE)
            grid[y][x] = EMPTY;
        Preconditions.checkState(obstacles.remove(ob), "post: removed");
        checkInvariant();
    }

    public ArrayList<obstacle> getObstacles() {
        checkInvariant();
        return new ArrayList<>(obstacles);
    }

    // Routes (from route object)
    public boolean addRoute(route r)
            throws InvalidCoordinateException, ObstacleCollisionException {
        Preconditions.checkNotNull(r, "route");

        // Validate all points first
        for (coordinate p : r.points()) {
            int x = p.getX(), y = p.getY();
            if (!isInBounds(x, y))
                throw new InvalidCoordinateException();
            if (grid[y][x] == OBSTACLE)
                throw new ObstacleCollisionException();
        }

        // Paint after validation
        for (coordinate p : r.points()) {
            grid[p.getY()][p.getX()] = ROUTE;
        }
        return true;
    }

    public void clearRoutePath() {
        for (int y = 0; y < gridHeight; y++)
            for (int x = 0; x < gridWidth; x++)
                if (grid[y][x] == ROUTE)
                    grid[y][x] = EMPTY;
        checkInvariant();
    }

    // Snapshot
    public char[][] getGridSnapshot() {
        checkInvariant();
        char[][] copy = new char[gridHeight][gridWidth];
        for (int y = 0; y < gridHeight; y++)
            System.arraycopy(grid[y], 0, copy[y], 0, gridWidth);
        return copy;
    }

    public String getName() {
        checkInvariant();
        return name;
    }

    public int getGridWidth() {
        checkInvariant();
        return gridWidth;
    }

    public int getGridHeight() {
        checkInvariant();
        return gridHeight;
    }
}
