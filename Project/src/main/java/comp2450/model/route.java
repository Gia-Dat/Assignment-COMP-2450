package comp2450.model;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;

/** A route is an ordered list of adjacent waypoints (4-neighbor by default). */
public class route {
    private final ArrayList<coordinate> waypoints;

    public route(List<coordinate> points) {
        checkArgs(points);
        this.waypoints = new ArrayList<>(points);
        checkInvariant();
    }

    // ---- Validation helpers ----
    private static void checkArgs(List<coordinate> points) {
        Preconditions.checkNotNull(points, "Route points must not be null");
        Preconditions.checkArgument(!points.isEmpty(), "Route must have at least one point");
        for (coordinate c : points) {
            Preconditions.checkNotNull(c, "Waypoint must not be null");
        }
        // Enforce 4-neighbor adjacency (no diagonals) when length >= 2
        for (int i = 1; i < points.size(); i++) {
            coordinate a = points.get(i - 1);
            coordinate b = points.get(i);
            int dx = Math.abs(a.getX() - b.getX());
            int dy = Math.abs(a.getY() - b.getY());
            Preconditions.checkArgument(dx + dy == 1,
                    "Waypoints must be 4-neighbor adjacent: %s -> %s", a, b);
        }
    }

    private void checkInvariant() {
        Preconditions.checkState(waypoints != null && !waypoints.isEmpty(), "Invariant: waypoints");
        for (coordinate c : waypoints) Preconditions.checkState(c != null, "Invariant: null waypoint");
        // adjacency already checked in ctor
    }

    // ---- Queries ----
    public ArrayList<coordinate> points() { checkInvariant(); return new ArrayList<>(waypoints); }
    public coordinate start() { checkInvariant(); return waypoints.get(0); }
    public coordinate end()   { checkInvariant(); return waypoints.get(waypoints.size()-1); }

    // Convenience factory for a single point (e.g., start==end)
    public static route single(coordinate c) {
        Preconditions.checkNotNull(c, "point must not be null");
        return new route(java.util.List.of(c));
    }
}
