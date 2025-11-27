package comp2450.model;

import com.google.common.base.Preconditions;
import java.util.*;

/** A user account/profile on the family PC. Holds personal gear, activities, and follows. */
public class Profile {
    private final UUID id;
    private String displayName;
    private final ArrayList<gear> myGear = new ArrayList<>();
    private final ArrayList<ActivityRecord> myActivities = new ArrayList<>();
    private final HashSet<UUID> following = new HashSet<>();

    public Profile(String displayName) {
        Preconditions.checkNotNull(displayName, "displayName");
        Preconditions.checkArgument(!displayName.trim().isEmpty(), "displayName empty");
        this.id = UUID.randomUUID();
        this.displayName = displayName.trim();
        checkInvariant();
    }

    private void checkInvariant() {
        Preconditions.checkState(id != null, "Invariant: id");
        Preconditions.checkState(displayName != null && !displayName.isEmpty(), "Invariant: name");
        Preconditions.checkState(myGear != null && myActivities != null && following != null, "Invariant: collections");
    }

    public UUID id() { checkInvariant(); return id; }
    public String name() { checkInvariant(); return displayName; }
    public void rename(String newName) {
        Preconditions.checkNotNull(newName, "newName");
        Preconditions.checkArgument(!newName.trim().isEmpty(), "newName empty");
        this.displayName = newName.trim();
        checkInvariant();
    }

    // Gear management (personal)
    public void addGear(gear g) {
        Preconditions.checkNotNull(g, "gear");
        myGear.add(g);
        Preconditions.checkState(myGear.contains(g), "Post: gear added");
        checkInvariant();
    }
    public void removeGear(gear g) {
        Preconditions.checkNotNull(g, "gear");
        Preconditions.checkState(myGear.contains(g), "Pre: gear exists");
        Preconditions.checkState(myGear.remove(g), "Post: gear removed");
        checkInvariant();
    }
    public ArrayList<gear> listGear() { checkInvariant(); return new ArrayList<>(myGear); }

    // Activities
    public void addActivity(ActivityRecord record) {
        Preconditions.checkNotNull(record, "record");
        myActivities.add(record);
        Preconditions.checkState(myActivities.contains(record), "Post: activity added");
        checkInvariant();
    }
    public ArrayList<ActivityRecord> activities() {
        checkInvariant();
        // sorted newest first for convenience
        ArrayList<ActivityRecord> copy = new ArrayList<>(myActivities);
        copy.sort((a,b) -> b.timestamp().compareTo(a.timestamp()));
        return copy;
    }

    // Follow
    public void follow(UUID other) {
        Preconditions.checkNotNull(other, "other");
        Preconditions.checkArgument(!other.equals(this.id), "Cannot follow self");
        following.add(other);
        checkInvariant();
    }
    public void unfollow(UUID other) {
        Preconditions.checkNotNull(other, "other");
        following.remove(other);
        checkInvariant();
    }
    public Set<UUID> followingIds() { checkInvariant(); return new HashSet<>(following); }
}
