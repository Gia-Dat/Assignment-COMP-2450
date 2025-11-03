package comp2450.model;

import com.google.common.base.Preconditions;

/** Stores activity statistics including time and distance (model only). */
public class statistics {
    private final int time;       // minutes
    private final double length;  // km

    public statistics(int time, double length) {
        checkArgs(time, length);
        this.time = time;
        this.length = length;
        checkInvariant();
    }

    // ----- Validation helpers -----
    private static void checkArgs(int time, double length) {
        Preconditions.checkArgument(time > 0, "Time must be positive");
        Preconditions.checkArgument(length > 0, "Length must be positive");
    }

    private void checkInvariant() {
        Preconditions.checkState(time > 0, "Invariant violated: time must be positive");
        Preconditions.checkState(length > 0, "Invariant violated: length must be positive");
    }

    // ----- Queries -----
    public double averageTime() { checkInvariant(); return (double) time; }
    public double averageLength() { checkInvariant(); return length; }
    public int getTime() { checkInvariant(); return time; }
    public double getLength() { checkInvariant(); return length; }
}

