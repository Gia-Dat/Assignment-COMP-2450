package comp2450.ui;

import comp2450.logic.FeedService;
import comp2450.logic.PathFinder;
import comp2450.logic.PathFinder.Mode;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import comp2450.model.*;
import comp2450.exceptions.InvalidCoordinateException;
import comp2450.exceptions.ObstacleCollisionException;
import comp2450.exceptions.DuplicateProfileException;


/**
 * Phase-2 console UI. Uses existing World/Profile/FeedService/PathFinder.
 * No new classes created; only updates to hook features together.
 */
public class menuPrinter {
    private final Scanner scanner = new Scanner(System.in);
    private final World world = new World(); // hard-coded empty world map at startup
    private final menu appMenu; // thin façade over model operations

    private Profile current;

    public menuPrinter(String firstUserName) {
        // create a first profile for convenience
        this.appMenu = new menu(firstUserName);
        try {
            current = world.createProfile(firstUserName);
        } catch (DuplicateProfileException e){
            System.out.println("Profile already exists, using existing one.");
        }
        System.out.println("Created profile for: " + current.name());
    }

    public void start() {
        while (true) {
            if (current == null) {
                authScreen();
            } else {
                mainScreen();
            }
        }
    }

    private void authScreen() {
        System.out.println("\n=== ACTIVITY TRACKER — SIGN IN ===");
        System.out.println("1) Create profile");
        System.out.println("2) Choose existing profile");
        System.out.println("3) Exit");
        System.out.print("Choice: ");
        switch (scanner.nextLine().trim()) {
            case "1" -> {
                System.out.print("Display name: ");
                try {
                    current = world.createProfile(scanner.nextLine().trim());
                } catch (DuplicateProfileException e) {
                    System.out.println("Profile already exists. Try another name.");
                }
            }
            case "2" -> {
                var profiles = world.allProfiles();
                if (profiles.isEmpty()) { System.out.println("No profiles. Create one first."); return; }
                for (int i = 0; i < profiles.size(); i++)
                    System.out.println((i + 1) + ") " + profiles.get(i).name());
                System.out.print("Pick: ");
                try {
                    int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
                    current = profiles.get(idx);
                } catch (Exception e) { System.out.println("Invalid."); }
            }
            case "3" -> {
                System.out.println("Bye!");
                System.exit(0);
            }
            default -> System.out.println("Invalid.");
        }
    }

    private void mainScreen() {
        System.out.println("\n=== MAIN — Signed in as " + current.name() + " ===");
        System.out.println("1) My profile (rename, gear)");
        System.out.println("2) Follow people");
        System.out.println("3) Feed (me + following)");
        System.out.println("4) Map (display, obstacles)");
        System.out.println("5) Add activity");
        System.out.println("6) Duplicate my previous route");
        System.out.println("7) Find route (NORMAL / only MY routes / FEED routes)");
        System.out.println("8) Switch profile");
        System.out.println("9) Exit");
        System.out.print("Choice: ");
        switch (scanner.nextLine().trim()) {
            case "1" -> profileScreen();
            case "2" -> followScreen();
            case "3" -> showFeed();
            case "4" -> mapScreen();
            case "5" -> addActivityFlow();
            case "6" -> duplicateRouteFlow();
            case "7" -> pathFindFlow();
            case "8" -> current = null;
            case "9" -> { System.out.println("Bye!"); System.exit(0); }
            default -> System.out.println("Invalid.");
        }
    }

    // ---- Profile ----
    private void profileScreen() {
        while (true) {
            System.out.println("\n--- Profile: " + current.name() + " ---");
            System.out.println("1) Rename");
            System.out.println("2) Add gear");
            System.out.println("3) Show gear");
            System.out.println("4) Remove gear");
            System.out.println("5) Back");
            System.out.print("Choice: ");
            String c = scanner.nextLine().trim();
            if (c.equals("5")) return;
            switch (c) {
                case "1" -> {
                    System.out.print("New name: ");
                    current.rename(scanner.nextLine().trim());
                }
                case "2" -> {
                    gear g = buildGearInteractive();
                    if (g != null) current.addGear(g);
                }
                case "3" -> printGear(current.listGear());
                case "4" -> {
                    var list = current.listGear();
                    if (list.isEmpty()) { System.out.println("No gear."); break; }
                    printGear(list);
                    System.out.print("Remove # : ");
                    try {
                        int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
                        current.removeGear(list.get(idx));
                    } catch (Exception e) { System.out.println("Invalid."); }
                }
                default -> {}
            }
        }
    }

    private gear buildGearInteractive() {
        try {
            System.out.print("Gear name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Quality (0-100): ");
            int q = Integer.parseInt(scanner.nextLine().trim());
            System.out.println("Type: 1) SUPPORT  2) PROTECTION  3) NUTRITION");
            int t = Integer.parseInt(scanner.nextLine().trim());
            gearType type = switch (t) {
                case 1 -> gearType.SUPPORT;
                case 2 -> gearType.PROTECTION;
                case 3 -> gearType.NUTRITION;
                default -> throw new IllegalArgumentException("Bad type");
            };
            return new gear(name, q, type);
        } catch (Exception e) {
            System.out.println("Invalid input.");
            return null;
        }
    }

    private void printGear(List<gear> list) {
        if (list.isEmpty()) { System.out.println("(empty)"); return; }
        for (int i = 0; i < list.size(); i++) {
            gear g = list.get(i);
            System.out.println((i + 1) + ". " + g.getName() + "  q=" + g.getQuality() + "  type=" + g.type());
        }
    }

    // ---- Follow ----
    private void followScreen() {
        var all = world.allProfiles();
        if (all.size() <= 1) { System.out.println("No one else to follow yet."); return; }
        System.out.println("\n--- People ---");
        for (int i = 0; i < all.size(); i++) {
            Profile p = all.get(i);
            if (p.id().equals(current.id())) continue;
            System.out.println((i + 1) + ") " + p.name() + (current.followingIds().contains(p.id()) ? " (following)" : ""));
        }
        System.out.println("a) Follow by #   b) Unfollow by #   x) back");
        String s = scanner.nextLine().trim();
        switch (s) {
            case "a" -> {
                System.out.print("#: ");
                try {
                    int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
                    Profile p = all.get(idx);
                    if (!p.id().equals(current.id())) current.follow(p.id());
                } catch (Exception e) { System.out.println("Invalid."); }
            }
            case "b" -> {
                System.out.print("#: ");
                try {
                    int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
                    Profile p = all.get(idx);
                    if (!p.id().equals(current.id())) current.unfollow(p.id());
                } catch (Exception e) { System.out.println("Invalid."); }
            }
            default -> {}
        }
    }

    private void showFeed() {
        var feed = FeedService.feedFor(current, world);
        System.out.println("\n--- FEED ---");
        if (feed.isEmpty()) { System.out.println("(empty)"); return; }
        for (var rec : feed) {
            System.out.println("• " + rec.title() + " @ " + rec.timestamp());
        }
    }

    // ---- Map & Obstacles ----
    private void mapScreen() {
        map m = world.map();
        while (true) {
            System.out.println("\n--- Map: " + m.getName() + " (" + m.getGridWidth() + "x" + m.getGridHeight() + ") ---");
            System.out.println("1) Display grid");
            System.out.println("2) Show obstacles");
            System.out.println("3) Add obstacle");
            System.out.println("4) Remove obstacle");
            System.out.println("5) Clear route drawing");
            System.out.println("6) Back");
            System.out.print("Choice: ");
            String c = scanner.nextLine().trim();
            if (c.equals("6")) return;
            switch (c) {
                case "1" -> displayGrid(m);
                case "2" -> showMapObstacles(m);
                case "3" -> addObstacleUI(m);
                case "4" -> removeObstacleUI(m);
                case "5" -> { m.clearRoutePath(); System.out.println("Route cleared."); }
                default -> {}
            }
        }
    }

    private void displayGrid(map m) {
        char[][] g = m.getGridSnapshot();
        System.out.println("\nLegend: . empty   * obstacle   > route");
        System.out.print("   ");
        for (int x = 0; x < m.getGridWidth(); x++) System.out.print(x + " ");
        System.out.println();
        for (int y = 0; y < m.getGridHeight(); y++) {
            System.out.print(y + "  ");
            for (int x = 0; x < m.getGridWidth(); x++) System.out.print(g[y][x] + " ");
            System.out.println();
        }
    }

    private void showMapObstacles(map m) {
        var list = m.getObstacles();
        if (list.isEmpty()) { System.out.println("No obstacles."); return; }
        for (int i = 0; i < list.size(); i++) {
            obstacle o = list.get(i);
            System.out.println((i + 1) + ". " + o.getName() + " @ (" + o.getObstacleCoordinate().getX() + "," + o.getObstacleCoordinate().getY() + ")");
        }
    }

    private void addObstacleUI(map m) {
        try {
            System.out.print("Obstacle name: ");
            String name = scanner.nextLine().trim();
            System.out.print("X: "); int x = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Y: "); int y = Integer.parseInt(scanner.nextLine().trim());
            m.addObstacle(new obstacle(name, new coordinate(x, y)));
            System.out.println("Added.");
        } catch (Exception e) { System.out.println("Invalid / blocked."); }
    }

    private void removeObstacleUI(map m) {
        var obs = m.getObstacles();
        if (obs.isEmpty()) { System.out.println("No obstacles."); return; }
        showMapObstacles(m);
        System.out.print("Remove # : ");
        try {
            int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
            m.removeObstacle(obs.get(idx));
            System.out.println("Removed.");
        } catch (Exception e) { System.out.println("Invalid."); }
    }

    // ---- Activities ----
    private void addActivityFlow() {
        map m = world.map();

        System.out.print("Activity title: ");
        String title = scanner.nextLine().trim();

        // Choose route: manual, plan NORMAL, plan MY, plan FEED
        System.out.println("Route source: 1) Manual waypoints  2) Plan NORMAL  3) Plan ONLY-MY-ROUTES  4) Plan FEED-ROUTES");
        String pick = scanner.nextLine().trim();

        Optional<route> maybe = Optional.empty();
        try {
            switch (pick) {
                case "1" -> maybe = Optional.of(readManualRoute());
                case "2" -> maybe = planRouteUI(Mode.NORMAL);
                case "3" -> maybe = planRouteUI(Mode.ROUTE_NETWORK_MINE);
                case "4" -> maybe = planRouteUI(Mode.ROUTE_NETWORK_FEED);
                default -> { System.out.println("Invalid."); return; }
            }
        } catch (Exception e) { System.out.println("Route input invalid."); return; }

        if (maybe.isEmpty()) { System.out.println("No route found."); return; }
        route r = maybe.get();

        // Choose used gear (zero or more)
        var gearList = current.listGear();
        var used = new ArrayList<gear>();
        if (!gearList.isEmpty()) {
            System.out.println("Select used gear indices separated by spaces (or blank for none):");
            printGear(gearList);
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                for (String s : line.split("\\s+")) {
                    int idx = Integer.parseInt(s) - 1;
                    used.add(gearList.get(idx));
                }
            }
        }

        // Encounter obstacles
        var addedObs = new ArrayList<obstacle>();

        // Paint route on the map (validated against obstacles)
        try {
            world.map().clearRoutePath();
            if (!m.addRoute(r)) {
                System.out.println("Route intersects an obstacle or out-of-bounds — not recorded.");
                return;
            }
        } catch (InvalidCoordinateException e) {
            System.out.println("Route out of bounds — not recorded.");
            return;
        } catch (ObstacleCollisionException e) {
            System.out.println("Route collides with obstacle — not recorded.");
            return;
        }

        // Save the activity record
        ActivityRecord rec = new ActivityRecord(title, r, used, addedObs, Instant.now());
        current.addActivity(rec);

        System.out.println("Activity saved and route painted.");
    }

    private void duplicateRouteFlow() {
        var acts = current.activities();
        if (acts.isEmpty()) { System.out.println("No past activities."); return; }
        for (int i = 0; i < acts.size(); i++)
            System.out.println((i + 1) + ". " + acts.get(i).title() + " @ " + acts.get(i).timestamp());
        System.out.print("Duplicate which # ? ");
        try {
            int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
            route r = acts.get(idx).routeTaken();
            world.map().clearRoutePath();
            if (world.map().addRoute(r)) System.out.println("Route duplicated/painted.");
            else System.out.println("Duplicate route blocked (obstacle changed?).");
        } catch (Exception e) { System.out.println("Invalid."); }
    }

    private void pathFindFlow() {
        System.out.println("Mode: 1) NORMAL  2) ONLY-MY-ROUTES  3) FEED-ROUTES");
        String m = scanner.nextLine().trim();

        PathFinder.Mode mode; // <- declare first

        switch (m) {
            case "1" -> mode = PathFinder.Mode.NORMAL;
            case "2" -> mode = PathFinder.Mode.ROUTE_NETWORK_MINE;
            case "3" -> mode = PathFinder.Mode.ROUTE_NETWORK_FEED;
            default -> { System.out.println("Invalid."); return; }
        }

        Optional<route> found = planRouteUI(mode);
        try {
            world.map().clearRoutePath();
            if (world.map().addRoute(found.get())) {
                System.out.println("Planned route painted.");
            } else {
                System.out.println("Planned route blocked (unexpected).");
            }
        } catch (InvalidCoordinateException e) {
            System.out.println("Error: route contains invalid coordinates.");
        } catch (ObstacleCollisionException e) {
            System.out.println("Error: route intersects an obstacle.");
        }
    }

    private route readManualRoute() {
        System.out.println("Enter comma-separated waypoints (adjacent steps): x1 y1, x2 y2, ...");
        String line = scanner.nextLine().trim();
        String[] toks = line.split(",");
        var pts = new ArrayList<coordinate>();
        for (String t : toks) {
            String[] xy = t.trim().split("\\s+");
            pts.add(new coordinate(Integer.parseInt(xy[0]), Integer.parseInt(xy[1])));
        }
        return new route(pts); // enforces adjacency
    }

    private Optional<route> planRouteUI(Mode mode) {
        try {
            System.out.print("Start X: "); int sx = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Start Y: "); int sy = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("End   X: "); int ex = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("End   Y: "); int ey = Integer.parseInt(scanner.nextLine().trim());
            return PathFinder.findRoute(world.map(), new coordinate(sx, sy), new coordinate(ex, ey), mode, current, world);
        } catch (Exception e) {
            System.out.println("Invalid coordinates.");
            return Optional.empty();
        }
    }
}
