package comp2450.model;

import com.google.common.base.Preconditions;
import java.util.*;
import comp2450.exceptions.DuplicateProfileException;

/** Singleton-like app state: hard-coded world map + all profiles. */
public class World {
    private final map worldMap;
    private final LinkedHashMap<UUID, Profile> profiles = new LinkedHashMap<>();

    public World() {
        // Hard-coded empty world map (you can seed default obstacles here if desired)
        this.worldMap = new map("World", 20, 20);
        checkInvariant();
    }

    private void checkInvariant() {
        Preconditions.checkState(worldMap != null, "Invariant: worldMap");
        Preconditions.checkState(profiles != null, "Invariant: profiles");
    }

    public map map() { checkInvariant(); return worldMap; }

    // Profiles
    public Profile createProfile(String name) throws DuplicateProfileException {
        Preconditions.checkArgument(name != null && !name.trim().isEmpty(), "Display name required");
        boolean taken = profiles.values().stream()
                .anyMatch(p -> p.name().equalsIgnoreCase(name.trim()));
        if (taken) throw new DuplicateProfileException();
        Profile p = new Profile(name.trim());
        profiles.put(p.id(), p);
        return p;
    }

    public Optional<Profile> getProfile(UUID id) {
        checkInvariant();
        return Optional.ofNullable(profiles.get(id));
    }

    public ArrayList<Profile> allProfiles() {
        checkInvariant();
        return new ArrayList<>(profiles.values());
    }
}

