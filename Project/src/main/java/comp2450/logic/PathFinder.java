package comp2450.logic;

import comp2450.algorithm.LinkedListStack;
import comp2450.algorithm.Stack;
import comp2450.model.route;
import comp2450.model.coordinate;
import comp2450.model.map;
import comp2450.model.Profile;
import comp2450.model.World;
import comp2450.model.ActivityRecord;
import com.google.common.base.Preconditions;
import java.util.*;

/**
 * Backtracking using a Stack (LinkedListStack).
 * Supports:
 *  - NORMAL mode (avoid obstacles)
 *  - ROUTE_NETWORK modes using only previously covered cells.
 */
public class PathFinder {
    public enum Mode { NORMAL, ROUTE_NETWORK_MINE, ROUTE_NETWORK_FEED }

    /** Find route from start to end with given mode. Returns Optional<route>. */
    public static Optional<route> findRoute(map m, coordinate start, coordinate end,
                                            Mode mode, Profile me, World world) {
        Preconditions.checkNotNull(m, "map");
        Preconditions.checkNotNull(start, "start");
        Preconditions.checkNotNull(end, "end");
        Preconditions.checkNotNull(mode, "mode");

        if (!m.isInBounds(start.getX(), start.getY())) return Optional.empty();
        if (!m.isInBounds(end.getX(), end.getY())) return Optional.empty();
        if (m.isObstacle(start.getX(), start.getY()) || m.isObstacle(end.getX(), end.getY())) return Optional.empty();

        // Build "visitable" predicate based on the mode
        Visitable visitable = switch (mode) {
            case NORMAL -> (x, y) -> m.isFree(x, y) || (x == start.getX() && y == start.getY()) || (x == end.getX() && y == end.getY());
            case ROUTE_NETWORK_MINE -> {
                Set<Long> allowed = buildRoutedCellsSet(m, me, /*includeFollowed=*/false, world);
                yield (x, y) -> allowed.contains(key(x, y));
            }
            case ROUTE_NETWORK_FEED -> {
                Set<Long> allowed = buildRoutedCellsSet(m, me, /*includeFollowed=*/true, world);
                yield (x, y) -> allowed.contains(key(x, y));
            }
        };

        return backtrack(m, start, end, visitable);
    }

    private interface Visitable { boolean canVisit(int x, int y); }

    /** Backtracking with a stack (COMP 2450 spec): */
    private static Optional<route> backtrack(map m, coordinate start, coordinate end, Visitable visitable) {
        int W = m.getGridWidth(), H = m.getGridHeight();
        boolean[][] visited = new boolean[H][W];
        coordinate[][] parent = new coordinate[H][W];

        Stack<coordinate> stack = new LinkedListStack<>();
        coordinate current = start;

        // Loop invariant (informal):
        // - current is always in bounds
        // - parent references build a valid partial path from start to current
        // - all visited cells have been considered for pushing neighbors
        while (!(current.getX() == end.getX() && current.getY() == end.getY())) {
            int cx = current.getX(), cy = current.getY();
            visited[cy][cx] = true;

            // Push visitable, unvisited 4-neighbors
            // Collect visitable, unvisited 4-neighbors
            java.util.ArrayList<coordinate> nbrs = new java.util.ArrayList<>(4);
            int[] dx = {1, -1, 0, 0};
            int[] dy = {0, 0, 1, -1};
            for (int k = 0; k < 4; k++) {
                int nx = cx + dx[k], ny = cy + dy[k];
                if (!m.isInBounds(nx, ny)) continue;
                if (visited[ny][nx]) continue;
                if (!visitable.canVisit(nx, ny)) continue;
                nbrs.add(new coordinate(nx, ny));
            }

            // Prefer neighbors closer to the goal (Manhattan distance)
            nbrs.sort((a,b) -> {
                int da = Math.abs(a.getX() - end.getX()) + Math.abs(a.getY() - end.getY());
                int db = Math.abs(b.getX() - end.getX()) + Math.abs(b.getY() - end.getY());
                return Integer.compare(da, db);
            });

            // Short-circuit if the goal is adjacent; otherwise push in reverse so the
            // closest neighbor is popped first
            for (int i = nbrs.size() - 1; i >= 0; i--) {
                coordinate nb = nbrs.get(i);
                parent[nb.getY()][nb.getX()] = current;
                if (nb.getX() == end.getX() && nb.getY() == end.getY()) {
                    // Found end next — jump directly
                    stack = new LinkedListStack<>(); // clear stack path
                    stack.push(nb);
                    break;
                }
                stack.push(nb);
            }

            if (stack.isEmpty()) {
                return Optional.empty(); // no path
            }

            // Backtracking step: take the last pushed neighbor
            current = stack.pop();
        }

        // Reconstruct path from end back to start via parent[]
        ArrayList<coordinate> pts = new ArrayList<>();
        coordinate cur = end;
        while (cur != null && !(cur.getX() == start.getX() && cur.getY() == start.getY())) {
            pts.add(cur);
            cur = parent[cur.getY()][cur.getX()];
        }
        if (pts.isEmpty() && !(start.getX() == end.getX() && start.getY() == end.getY())) {
            // defensive: no parent chain found
            return Optional.empty();
        }
        Collections.reverse(pts);
        if (pts.isEmpty()) pts.add(start); // start==end edge case

        return Optional.of(new route(pts));
    }

    private static long key(int x, int y) { return (((long)y) << 32) ^ (x & 0xffffffffL); }

    /** Build a set of cells that are part of routes for the chosen scope. */
    private static Set<Long> buildRoutedCellsSet(map m, Profile me, boolean includeFollowed, World world) {
        Preconditions.checkNotNull(m, "map");
        Preconditions.checkNotNull(me, "me");
        Preconditions.checkNotNull(world, "world");

        HashSet<Long> cells = new HashSet<>();
        // My routes
        for (ActivityRecord ar : me.activities()) {
            for (coordinate c : ar.routeTaken().points()) {
                if (m.isInBounds(c.getX(), c.getY())) cells.add(key(c.getX(), c.getY()));
            }
        }
        // Followed users’ routes (if requested)
        if (includeFollowed) {
            for (UUID uid : me.followingIds()) {
                world.getProfile(uid).ifPresent(p -> {
                    for (ActivityRecord ar : p.activities()) {
                        for (coordinate c : ar.routeTaken().points()) {
                            if (m.isInBounds(c.getX(), c.getY())) cells.add(key(c.getX(), c.getY()));
                        }
                    }
                });
            }
        }
        return cells;
    }
}

