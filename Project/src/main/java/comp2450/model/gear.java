package comp2450.model;

import com.google.common.base.Preconditions;

/** Represents a gear item with name, quality, and type (model only). */
public class gear {
    private final String name;
    private int quality;
    private final gearType type;

    public gear(String name, int quality, gearType type) {
        checkArgs(name, quality, type);
        this.name = name.trim();
        this.quality = quality;
        this.type = type;
        checkInvariant();
    }

    // ----- Validation helpers -----
    private static void checkArgs(String name, int quality, gearType type) {
        Preconditions.checkNotNull(name, "Name must not be null");
        Preconditions.checkArgument(!name.trim().isEmpty(), "Name must not be empty");
        Preconditions.checkArgument(quality >= 0, "Quality must be non-negative");
        Preconditions.checkNotNull(type, "Gear type must not be null");
    }

    private void checkInvariant() {
        Preconditions.checkState(name != null && !name.isEmpty(),
                "Invariant violated: Name must not be null or empty");
        Preconditions.checkState(quality >= 0, "Invariant violated: Quality must be non-negative");
        Preconditions.checkState(type != null, "Invariant violated: Gear type must not be null");
    }

    // ----- Commands -----
    public void changeQuality(int newQuality) {
        Preconditions.checkArgument(newQuality >= 0, "Quality must be non-negative");
        int old = this.quality;
        this.quality = newQuality;
        Preconditions.checkState(this.quality != old, "Postcondition failed: quality should change");
        checkInvariant();
    }

    // ----- Queries -----
    public String getName() { checkInvariant(); return name; }
    public gearType type() { checkInvariant(); return type; }
    public int getQuality() { checkInvariant(); return quality; }
}
