package comp2450.model;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;

/** Ordered list of 4-neighbor-adjacent waypoints. */
public class route {
    private final ArrayList<coordinate> waypoints;

    public route(List<coordinate> points) {
        checkArgs(points);
        this.waypoints = new ArrayList<>(points);
        checkInvariant();
    }

    private static void checkArgs(List<coordinate> points){
        Preconditions.checkNotNull(points, "points");
        Preconditions.checkArgument(!points.isEmpty(), "route needs points");
        for (coordinate c : points) Preconditions.checkNotNull(c, "waypoint null");
        for (int i=1;i<points.size();i++){
            coordinate a=points.get(i-1), b=points.get(i);
            int dx=Math.abs(a.getX()-b.getX()), dy=Math.abs(a.getY()-b.getY());
            Preconditions.checkArgument(dx+dy==1, "non-adjacent: %s -> %s", a, b);
        }
    }

    private void checkInvariant(){
        Preconditions.checkState(waypoints!=null && !waypoints.isEmpty(), "invariant points");
        for (coordinate c:waypoints) Preconditions.checkState(c!=null, "invariant waypoint");
    }

    public ArrayList<coordinate> points(){ checkInvariant(); return new ArrayList<>(waypoints); }
    public coordinate start(){ checkInvariant(); return waypoints.get(0); }
    public coordinate end(){ checkInvariant(); return waypoints.get(waypoints.size()-1); }
}

