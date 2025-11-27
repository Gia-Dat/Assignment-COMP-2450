package comp2450.model;

import com.google.common.base.Preconditions;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/** One finished activity by a person: route, used gear, optional added obstacles, and a timestamp. */
public class ActivityRecord {
    private final String title;           // short description, e.g., "Morning Run"
    private final route routeTaken;
    private final ArrayList<gear> usedGear;
    private final ArrayList<obstacle> obstaclesAdded; // added during this activity (if encountered)
    private final Instant timestamp;

    public ActivityRecord(String title, route routeTaken, List<gear> usedGear, List<obstacle> obstaclesAdded, Instant timestamp) {
        checkArgs(title, routeTaken, usedGear, obstaclesAdded, timestamp);
        this.title = title.trim();
        this.routeTaken = routeTaken;
        this.usedGear = new ArrayList<>(usedGear);
        this.obstaclesAdded = new ArrayList<>(obstaclesAdded);
        this.timestamp = timestamp;
        checkInvariant();
    }

    private static void checkArgs(String t, route r, List<gear> g, List<obstacle> o, Instant ts) {
        Preconditions.checkNotNull(t, "title");
        Preconditions.checkArgument(!t.trim().isEmpty(), "title empty");
        Preconditions.checkNotNull(r, "route");
        Preconditions.checkNotNull(g, "usedGear");
        Preconditions.checkNotNull(o, "obstaclesAdded");
        Preconditions.checkNotNull(ts, "timestamp");
    }

    private void checkInvariant() {
        Preconditions.checkState(!title.isEmpty(), "Invariant: title");
        Preconditions.checkState(routeTaken != null, "Invariant: route");
        Preconditions.checkState(usedGear != null && obstaclesAdded != null, "Invariant: lists");
        Preconditions.checkState(timestamp != null, "Invariant: timestamp");
    }

    public String title() { checkInvariant(); return title; }
    public route routeTaken() { checkInvariant(); return routeTaken; }
    public ArrayList<gear> usedGear() { checkInvariant(); return new ArrayList<>(usedGear); }
    public ArrayList<obstacle> obstaclesAdded() { checkInvariant(); return new ArrayList<>(obstaclesAdded); }
    public Instant timestamp() { checkInvariant(); return timestamp; }
}
