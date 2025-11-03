package comp2450.model;

import com.google.common.base.Preconditions;
import java.util.*;

/** Map model: stores grid, obstacles, and paints/validates routes. */
public class map {
    private final String name;
    private final ArrayList<obstacle> obstacles = new ArrayList<>();
    private final int gridWidth;
    private final int gridHeight;
    private final char[][] grid;

    // Symbols
    public static final char EMPTY = '.';
    public static final char OBSTACLE = '*';
    public static final char ROUTE = '>';

    public map(String name) { this(name, 10, 10); }

    public map(String name, int width, int height) {
        checkArgs(name, width, height);
        this.name = name.trim();
        this.gridWidth = width;
        this.gridHeight = height;
        this.grid = new char[gridHeight][gridWidth];
        initializeGrid();
        checkInvariant();
    }

    // ---- Validation helpers ----
    private static void checkArgs(String name, int width, int height) {
        Preconditions.checkNotNull(name, "Name must not be null");
        Preconditions.checkArgument(!name.trim().isEmpty(), "Name must not be empty");
        Preconditions.checkArgument(width > 0, "Grid width must be positive");
        Preconditions.checkArgument(height > 0, "Grid height must be positive");
    }

    private void checkInvariant() {
        Preconditions.checkState(name != null && !name.isEmpty(), "Invariant: name");
        Preconditions.checkState(gridWidth > 0 && gridHeight > 0, "Invariant: grid dims");
        Preconditions.checkState(grid != null && grid.length == gridHeight
                && grid[0].length == gridWidth, "Invariant: grid shape");
        Preconditions.checkState(obstacles != null, "Invariant: obstacles list");
    }

    private void checkBounds(int x, int y) {
        Preconditions.checkArgument(isInBounds(x, y), "Coordinates out of bounds");
    }

    // ---- Core ----
    private void initializeGrid() {
        for (int y = 0; y < gridHeight; y++)
            for (int x = 0; x < gridWidth; x++)
                grid[y][x] = EMPTY;
    }

    public boolean isInBounds(int x, int y) { return x >= 0 && x < gridWidth && y >= 0 && y < gridHeight; }
    public boolean isObstacle(int x, int y) { return isInBounds(x,y) && grid[y][x] == OBSTACLE; }
    public boolean isRoute(int x, int y)    { return isInBounds(x,y) && grid[y][x] == ROUTE; }
    public boolean isFree(int x, int y)     { return isInBounds(x,y) && grid[y][x] == EMPTY; }

    // ---- Obstacles ----
    public void addObstacle(obstacle newObstacle) {
        Preconditions.checkNotNull(newObstacle, "newObstacle must not be null");
        int x = newObstacle.getObstacleCoordinate().getX();
        int y = newObstacle.getObstacleCoordinate().getY();
        checkBounds(x, y);
        Preconditions.checkArgument(!isRoute(x, y), "Cannot place obstacle on a route");
        grid[y][x] = OBSTACLE;
        obstacles.add(newObstacle);
        Preconditions.checkState(obstacles.contains(newObstacle), "Post: obstacle added");
        checkInvariant();
    }

    public void removeObstacle(obstacle oldObstacle) {
        Preconditions.checkNotNull(oldObstacle, "oldObstacle must not be null");
        Preconditions.checkState(obstacles.contains(oldObstacle), "Obstacle must exist");
        int x = oldObstacle.getObstacleCoordinate().getX();
        int y = oldObstacle.getObstacleCoordinate().getY();
        if (isInBounds(x, y) && grid[y][x] == OBSTACLE) grid[y][x] = EMPTY;
        Preconditions.checkState(obstacles.remove(oldObstacle), "Post: obstacle removed");
        checkInvariant();
    }

    public ArrayList<obstacle> getObstacles() { checkInvariant(); return new ArrayList<>(obstacles); }

    // ---- Routes (with route object) ----
    /** Returns true and paints the route if all waypoints are in-bounds and NOT obstacles. */
    public boolean addRoute(route r) {
        Preconditions.checkNotNull(r, "route must not be null");
        var pts = r.points();

        // Validate first (no partial writes)
        for (coordinate c : pts) {
            int x = c.getX(), y = c.getY();
            if (!isInBounds(x, y)) return false;
            if (isObstacle(x, y))  return false; // route cannot pass obstacles
        }

        // Paint
        for (coordinate c : pts) {
            int x = c.getX(), y = c.getY();
            if (grid[y][x] == EMPTY) grid[y][x] = ROUTE;
        }
        checkInvariant();
        return true;
    }

    /** Back-compat helper if you still pass waypoint lists directly. */
    public boolean addRoutePath(java.util.List<coordinate> pts) {
        return addRoute(new route(pts));
    }

    public void clearRoutePath() {
        for (int y = 0; y < gridHeight; y++)
            for (int x = 0; x < gridWidth; x++)
                if (grid[y][x] == ROUTE) grid[y][x] = EMPTY;
        checkInvariant();
    }

    /** Compute a shortest path (4-neighbor) that avoids obstacles, return as a route. */
    public Optional<route> planShortestRoute(coordinate start, coordinate end) {
        Preconditions.checkNotNull(start, "start must not be null");
        Preconditions.checkNotNull(end, "end must not be null");

        int sx = start.getX(), sy = start.getY();
        int ex = end.getX(), ey = end.getY();
        if (!isInBounds(sx, sy) || !isInBounds(ex, ey)) return Optional.empty();
        if (isObstacle(sx, sy) || isObstacle(ex, ey))   return Optional.empty();
        if (start.equals(end)) return Optional.of(route.single(start));

        boolean[][] vis = new boolean[gridHeight][gridWidth];
        coordinate[][] parent = new coordinate[gridHeight][gridWidth];
        ArrayDeque<coordinate> q = new ArrayDeque<>();
        q.add(new coordinate(sx, sy));
        vis[sy][sx] = true;

        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};
        boolean found = false;

        while (!q.isEmpty() && !found) {
            coordinate c = q.removeFirst();
            int x = c.getX(), y = c.getY();
            for (int k = 0; k < 4; k++) {
                int nx = x + dx[k], ny = y + dy[k];
                if (!isInBounds(nx, ny) || vis[ny][nx] || isObstacle(nx, ny)) continue;
                vis[ny][nx] = true;
                parent[ny][nx] = c;
                if (nx == ex && ny == ey) { found = true; break; }
                q.add(new coordinate(nx, ny));
            }
        }

        if (!found) return Optional.empty();

        // Reconstruct path from end -> start (skip the start cell if you wish)
        ArrayList<coordinate> pts = new ArrayList<>();
        coordinate cur = new coordinate(ex, ey);
        while (cur != null && !(cur.getX() == sx && cur.getY() == sy)) {
            pts.add(cur);
            cur = parent[cur.getY()][cur.getX()];
        }
        Collections.reverse(pts);
        if (pts.isEmpty()) pts.add(start); // edge case if start==end

        return Optional.of(new route(pts));
    }

    // ---- Snapshots / queries ----
    public char[][] getGridSnapshot() {
        checkInvariant();
        char[][] copy = new char[gridHeight][gridWidth];
        for (int y = 0; y < gridHeight; y++)
            System.arraycopy(grid[y], 0, copy[y], 0, gridWidth);
        return copy;
    }

    public String getName() { checkInvariant(); return name; }
    public int getGridWidth() { checkInvariant(); return gridWidth; }
    public int getGridHeight() { checkInvariant(); return gridHeight; }
}
