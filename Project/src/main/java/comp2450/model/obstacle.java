package comp2450.model;

import com.google.common.base.Preconditions;

/** Obstacle with name and position (model only). */
public class obstacle {
    private final String name;
    private final coordinate obstaclePosition;

    public obstacle(String name, coordinate obstaclePosition) {
        checkArgs(name, obstaclePosition);
        this.name = name.trim();
        this.obstaclePosition = obstaclePosition;
        checkInvariant();
    }

    // ----- Validation helpers -----
    private static void checkArgs(String name, coordinate pos) {
        Preconditions.checkNotNull(name, "Name must not be null");
        Preconditions.checkArgument(!name.trim().isEmpty(), "Name must not be empty");
        Preconditions.checkNotNull(pos, "Obstacle position must not be null");
    }

    private void checkInvariant() {
        Preconditions.checkState(name != null && !name.isEmpty(),
                "Invariant violated: name");
        Preconditions.checkState(obstaclePosition != null,
                "Invariant violated: obstaclePosition");
    }

    // ----- Queries -----
    public String getName() { checkInvariant(); return name; }
    public coordinate getObstacleCoordinate() { checkInvariant(); return obstaclePosition; }
}
