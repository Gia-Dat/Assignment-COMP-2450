package comp2450.model;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import comp2450.exceptions.InvalidCoordinateException;
import comp2450.exceptions.ObstacleCollisionException;

/** Pure data manager (no I/O). */
public class menu {
    private final String userName;
    private final ArrayList<gear> items = new ArrayList<>();
    private final ArrayList<activity> exercise = new ArrayList<>();
    private final ArrayList<map> mymap = new ArrayList<>();
    private final ArrayList<statistics> stat = new ArrayList<>();

    public menu(String userName) {
        checkArgs(userName);
        this.userName = userName.trim();
        checkInvariant();
    }

    // ----- Validation helpers -----
    private static void checkArgs(String userName) {
        Preconditions.checkNotNull(userName, "userName must not be null");
        Preconditions.checkArgument(!userName.trim().isEmpty(), "userName must not be empty");
    }

    private void checkInvariant() {
        Preconditions.checkState(userName != null && !userName.isEmpty(), "Invariant: userName");
        Preconditions.checkState(items != null, "Invariant: items list");
        Preconditions.checkState(exercise != null, "Invariant: exercise list");
        Preconditions.checkState(mymap != null, "Invariant: mymap list");
        Preconditions.checkState(stat != null, "Invariant: stat list");
    }

    // ----- Gear -----
    public void addGear(gear g) {
        Preconditions.checkNotNull(g, "new gear must not be null");
        items.add(g);
        Preconditions.checkState(items.contains(g), "Postcondition: gear added");
        checkInvariant();
    }
    public void removeGear(gear g) {
        Preconditions.checkNotNull(g, "gear must not be null");
        Preconditions.checkState(items.contains(g), "Gear must exist");
        Preconditions.checkState(items.remove(g), "Postcondition: gear removed");
        checkInvariant();
    }
    public ArrayList<gear> getItems() { checkInvariant(); return new ArrayList<>(items); }

    // ----- Activity -----
    public void addActivity(activity a) {
        Preconditions.checkNotNull(a, "activity must not be null");
        exercise.add(a);
        Preconditions.checkState(exercise.contains(a), "Postcondition: activity added");
        checkInvariant();
    }
    public void removeActivity(activity a) {
        Preconditions.checkNotNull(a, "activity must not be null");
        Preconditions.checkState(exercise.contains(a), "Activity must exist");
        Preconditions.checkState(exercise.remove(a), "Postcondition: activity removed");
        checkInvariant();
    }
    public ArrayList<activity> getActivities() { checkInvariant(); return new ArrayList<>(exercise); }

    // ----- Map -----
    public void addMap(map m) {
        Preconditions.checkNotNull(m, "map must not be null");
        mymap.add(m);
        Preconditions.checkState(mymap.contains(m), "Postcondition: map added");
        checkInvariant();
    }
    public void removeMap(map m) {
        Preconditions.checkNotNull(m, "map must not be null");
        Preconditions.checkState(mymap.contains(m), "Map must exist");
        Preconditions.checkState(mymap.remove(m), "Postcondition: map removed");
        checkInvariant();
    }
    public ArrayList<map> getMaps() { checkInvariant(); return new ArrayList<>(mymap); }

    // ----- Statistics -----
    public void addStat(statistics s) {
        Preconditions.checkNotNull(s, "statistics must not be null");
        stat.add(s);
        Preconditions.checkState(stat.contains(s), "Postcondition: stat added");
        checkInvariant();
    }
    public void removeStat(statistics s) {
        Preconditions.checkNotNull(s, "statistics must not be null");
        Preconditions.checkState(stat.contains(s), "Record must exist");
        Preconditions.checkState(stat.remove(s), "Postcondition: stat removed");
        checkInvariant();
    }
    public ArrayList<statistics> getStatistics() { checkInvariant(); return new ArrayList<>(stat); }

    public String getUserName() { checkInvariant(); return userName; }

    public void addObstacleToWorld(World world, obstacle o)
            throws InvalidCoordinateException, ObstacleCollisionException {
        Preconditions.checkNotNull(world, "world");
        Preconditions.checkNotNull(o, "obstacle");
        world.map().addObstacle(o);
    }

    public boolean paintRouteOnWorld(World world, route r)
            throws InvalidCoordinateException, ObstacleCollisionException {
        Preconditions.checkNotNull(world, "world");
        Preconditions.checkNotNull(r, "route");
        return world.map().addRoute(r);
    }
}
