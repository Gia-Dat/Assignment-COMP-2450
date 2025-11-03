package comp2450.model;

import com.google.common.base.Preconditions;
import java.util.ArrayList;

/** Activity with a name and its gear (no UI). */
public class activity {
    private final String name;
    private final ArrayList<gear> items = new ArrayList<>();

    public activity(String name){
        checkArgs(name);
        this.name = name.trim();
        checkInvariant();
    }

    // ----- Validation helpers -----
    private static void checkArgs(String name) {
        Preconditions.checkNotNull(name, "name must not be null");
        Preconditions.checkArgument(!name.trim().isEmpty(), "name must not be empty");
    }

    private void checkInvariant() {
        Preconditions.checkState(name != null && !name.isEmpty(),
                "Invariant violated: activity name");
        Preconditions.checkState(items != null, "Invariant violated: items list");
    }

    // ----- Commands -----
    public void addGear(gear newGear) {
        Preconditions.checkNotNull(newGear, "newGear must not be null");
        items.add(newGear);
        Preconditions.checkState(items.contains(newGear), "Postcondition: gear added");
        checkInvariant();
    }

    public void removeGear(gear oldGear) {
        Preconditions.checkNotNull(oldGear, "oldGear must not be null");
        Preconditions.checkState(items.contains(oldGear), "Gear must exist in activity");
        Preconditions.checkState(items.remove(oldGear), "Postcondition: gear removed");
        checkInvariant();
    }

    // ----- Queries -----
    public String getName(){ checkInvariant(); return name; }
    public ArrayList<gear> listGear() { checkInvariant(); return new ArrayList<>(items); }
}
