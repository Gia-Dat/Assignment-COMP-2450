package comp2450.model;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * UI/console layer. All input/output and interaction lives here.
 */
public class menuPrinter {
    private final String userName;
    private final menu mymenu;
    private final Scanner scanner = new Scanner(System.in);
    private boolean running = true;

    public menuPrinter(String userName) {
        this.userName = userName;
        this.mymenu = new menu(userName);
    }

    public void start() {
        System.out.println("=== Activity Tracker System ===");
        System.out.println("Welcome, " + userName + "!");

        while (running) {
            showMainMenu();
            System.out.print("Enter your choice (1-15): ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("15") || input.equalsIgnoreCase("exit")) {
                break;
            }

            try {
                int choice = Integer.parseInt(input);
                processMenuChoice(choice);
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a number between 1-15");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

            System.out.println();
        }

        System.out.println("Goodbye!");
    }

    private void showMainMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1.  Add Gear");
        System.out.println("2.  Show All Gear");
        System.out.println("3.  Remove Gear");
        System.out.println("4.  Add Activity");
        System.out.println("5.  Show All Activities");
        System.out.println("6.  Remove Activity");
        System.out.println("7.  Add Map");
        System.out.println("8.  Show All Maps");
        System.out.println("9.  Remove Map");
        System.out.println("10. Add Statistic");
        System.out.println("11. Show Statistics");
        System.out.println("12. Remove Statistic");
        System.out.println("13. Display Grid of a Map");
        System.out.println("14. Show Obstacles of a Map");
        System.out.println("15. Exit");
    }

    private void processMenuChoice(int choice) {
        Preconditions.checkArgument(choice >= 1 && choice <= 15,
                "Choice must be between 1-15");

        switch (choice) {
            case 1 -> addGearUI();
            case 2 -> showGear(true);
            case 3 -> removeGearUI();
            case 4 -> addActivityUI();
            case 5 -> showActivity(true);
            case 6 -> removeActivityUI();
            case 7 -> addMapUI();
            case 8 -> showMap(true);
            case 9 -> removeMapUI();
            case 10 -> addStatUI();
            case 11 -> showStat(true);
            case 12 -> removeStatUI();
            case 13 -> displayMapGridUI();
            case 14 -> showMapObstaclesUI();
            case 15 -> running = false;
            default -> System.out.println("Invalid choice.");
        }
    }

    // -------------------- Gear --------------------
    private void addGearUI() {
        System.out.println("\n--- Add New Gear ---");
        System.out.print("Enter gear name: ");
        String name = scanner.nextLine().trim();
        Preconditions.checkArgument(!name.isEmpty(), "Gear name cannot be empty");

        System.out.print("Enter gear quality (0-100): ");
        int quality = Integer.parseInt(scanner.nextLine().trim());
        Preconditions.checkArgument(quality >= 0 && quality <= 100,
                "Quality must be between 0-100");

        System.out.println("Select gear type:");
        System.out.println("1. SUPPORT");
        System.out.println("2. PROTECTION");
        System.out.println("3. NUTRITION");
        System.out.print("Enter choice (1-3): ");
        int typeChoice = Integer.parseInt(scanner.nextLine().trim());

        gearType type = switch (typeChoice) {
            case 1 -> gearType.SUPPORT;
            case 2 -> gearType.PROTECTION;
            case 3 -> gearType.NUTRITION;
            default -> throw new IllegalArgumentException("Invalid gear type selection");
        };

        mymenu.addGear(new gear(name, quality, type));
        System.out.println("✓ Gear added successfully: " + name);
    }

    private void removeGearUI() {
        var list = showGear(true);
        if (list.isEmpty()) return;
        System.out.print("Enter the number of gear to remove: ");
        int choice = Integer.parseInt(scanner.nextLine().trim());
        Preconditions.checkArgument(choice >= 1 && choice <= list.size(), "Invalid gear selection");
        mymenu.removeGear(list.get(choice - 1));
        System.out.println("✓ Gear removed.");
    }

    public ArrayList<gear> showGear(boolean interactive) {
        ArrayList<gear> list = mymenu.getItems();
        if (interactive) {
            System.out.println("\n--- All Gear ---");
            if (list.isEmpty()) {
                System.out.println("No gear available.");
            } else {
                for (int i = 0; i < list.size(); i++) {
                    gear g = list.get(i);
                    System.out.println((i + 1) + ". " + g.getName() +
                            " - Quality: " + g.getQuality() + " - Type: " + g.type());
                }
            }
        }
        return list;
    }

    // -------------------- Activity --------------------
    private void addActivityUI() {
        System.out.println("\n--- Add New Activity ---");
        System.out.print("Enter activity name: ");
        String name = scanner.nextLine().trim();
        Preconditions.checkArgument(!name.isEmpty(), "Activity name cannot be empty");
        mymenu.addActivity(new activity(name));
        System.out.println("✓ Activity added successfully: " + name);
    }

    private void removeActivityUI() {
        var list = showActivity(true);
        if (list.isEmpty()) return;
        System.out.print("Enter the number of activity to remove: ");
        int choice = Integer.parseInt(scanner.nextLine().trim());
        Preconditions.checkArgument(choice >= 1 && choice <= list.size(), "Invalid activity selection");
        mymenu.removeActivity(list.get(choice - 1));
        System.out.println("✓ Activity removed.");
    }

    public ArrayList<activity> showActivity(boolean interactive) {
        ArrayList<activity> list = mymenu.getActivities();
        if (interactive) {
            System.out.println("\n--- All Activities ---");
            if (list.isEmpty()) {
                System.out.println("No activities available.");
            } else {
                for (int i = 0; i < list.size(); i++) {
                    System.out.println((i + 1) + ". " + list.get(i).getName());
                }
            }
        }
        return list;
    }

    // -------------------- Map --------------------
    private void addMapUI() {
        System.out.println("\n--- Add New Map ---");
        System.out.print("Enter map name: ");
        String name = scanner.nextLine().trim();
        Preconditions.checkArgument(!name.isEmpty(), "Map name cannot be empty");

        map newMap;
        try {
            System.out.print("Enter grid width (default 10): ");
            int width = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Enter grid height (default 10): ");
            int height = Integer.parseInt(scanner.nextLine().trim());
            newMap = new map(name, width, height);
        } catch (Exception e) {
            newMap = new map(name);
        }
        mymenu.addMap(newMap);
        System.out.println("✓ Map added successfully: " + name);
    }

    private void removeMapUI() {
        var list = showMap(true);
        if (list.isEmpty()) return;
        System.out.print("Enter the number of map to remove: ");
        int choice = Integer.parseInt(scanner.nextLine().trim());
        Preconditions.checkArgument(choice >= 1 && choice <= list.size(), "Invalid map selection");
        mymenu.removeMap(list.get(choice - 1));
        System.out.println("✓ Map removed.");
    }

    public ArrayList<map> showMap(boolean interactive) {
        ArrayList<map> maps = mymenu.getMaps();
        if (interactive) {
            System.out.println("\n--- All Maps ---");
            if (maps.isEmpty()) {
                System.out.println("No maps available.");
            } else {
                for (int i = 0; i < maps.size(); i++) {
                    map m = maps.get(i);
                    System.out.println((i + 1) + ". " + m.getName() + " (" + m.getGridWidth() + "x" + m.getGridHeight() + ")");
                }
                // Optional: quick manage loop
                System.out.print("\nManage obstacles/routes now? (yes/no): ");
                String ans = scanner.nextLine().trim().toLowerCase();
                if (ans.equals("yes") || ans.equals("y")) {
                    manageMapObstaclesRoutesUI();
                }
            }
        }
        return maps;
    }

    // ----- Display grid / Show obstacles from UI -----
    private map selectMapOrNull() {
        ArrayList<map> maps = mymenu.getMaps();
        if (maps.isEmpty()) {
            System.out.println("No maps available.");
            return null;
        }
        System.out.println("\n--- Select a Map ---");
        for (int i = 0; i < maps.size(); i++) {
            map m = maps.get(i);
            System.out.println((i + 1) + ". " + m.getName() + " (" + m.getGridWidth() + "x" + m.getGridHeight() + ")");
        }
        System.out.print("Enter number: ");
        try {
            int idx = Integer.parseInt(scanner.nextLine().trim());
            if (idx < 1 || idx > maps.size()) {
                System.out.println("Invalid selection.");
                return null;
            }
            return maps.get(idx - 1);
        } catch (Exception e) {
            System.out.println("Please enter a valid number.");
            return null;
        }
    }

    private void displayMapGridUI() {
        map m = selectMapOrNull();
        if (m == null) return;
        char[][] grid = m.getGridSnapshot();
        System.out.println("\n--- Map: " + m.getName() + " ---");
        System.out.println("Grid Size: " + m.getGridWidth() + "x" + m.getGridHeight());
        System.out.println("\nLegend:");
        System.out.println(". - Empty location");
        System.out.println("* - Obstacle");
        System.out.println("> - Activity route");
        System.out.println("\nGrid:");
        // header
        System.out.print("   ");
        for (int x = 0; x < m.getGridWidth(); x++) System.out.print(x + " ");
        System.out.println();
        // rows
        for (int y = 0; y < m.getGridHeight(); y++) {
            System.out.print(y + "  ");
            for (int x = 0; x < m.getGridWidth(); x++) System.out.print(grid[y][x] + " ");
            System.out.println();
        }
    }

    private void showMapObstaclesUI() {
        map m = selectMapOrNull();
        if (m == null) return;
        var obstacles = m.getObstacles();
        System.out.println("\n--- Obstacles in Map: " + m.getName() + " ---");
        if (obstacles.isEmpty()) {
            System.out.println("No obstacles in this map.");
        } else {
            for (int i = 0; i < obstacles.size(); i++) {
                obstacle o = obstacles.get(i);
                coordinate c = o.getObstacleCoordinate();
                System.out.println((i + 1) + ". " + o.getName() + " at (" + c.getX() + ", " + c.getY() + ")");
            }
        }
    }

    // ----- Full interactive manager for obstacles/routes (UI-only) -----
    private void manageMapObstaclesRoutesUI() {
        map selected = selectMapOrNull();
        if (selected == null) return;

        boolean back = false;
        while (!back) {
            System.out.println("\n--- Manage Map: " + selected.getName() + " ---");
            System.out.println("1. Display grid");
            System.out.println("2. Show obstacles");
            System.out.println("3. Add obstacle");
            System.out.println("4. Remove obstacle");
            System.out.println("5. Add route waypoints");
            System.out.println("6. Clear route");
            System.out.println("7. Plan shortest route (avoid obstacles)");
            System.out.println("8. Back");
            System.out.print("Choose an option: ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1" -> {
                    char[][] grid = selected.getGridSnapshot();
                    System.out.println();
                    printGrid(selected, grid);
                }
                case "2" -> showMapObstacles(selected);
                case "3" -> addObstacleUI(selected);
                case "4" -> removeObstacleUI(selected);
                case "5" -> addRouteUI(selected);         // builds a route object
                case "6" -> { selected.clearRoutePath(); System.out.println("Route cleared."); }
                case "7" -> planShortestRouteUI(selected); // returns a route object and paints it
                case "8" -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void printGrid(map m, char[][] grid) {
        System.out.println("--- Map: " + m.getName() + " ---");
        System.out.print("   ");
        for (int x = 0; x < m.getGridWidth(); x++) System.out.print(x + " ");
        System.out.println();
        for (int y = 0; y < m.getGridHeight(); y++) {
            System.out.print(y + "  ");
            for (int x = 0; x < m.getGridWidth(); x++) System.out.print(grid[y][x] + " ");
            System.out.println();
        }
    }

    private void showMapObstacles(map m) {
        var obs = m.getObstacles();
        if (obs.isEmpty()) System.out.println("No obstacles.");
        else {
            for (int i = 0; i < obs.size(); i++) {
                var o = obs.get(i);
                var c = o.getObstacleCoordinate();
                System.out.println((i + 1) + ". " + o.getName() + " at (" + c.getX() + ", " + c.getY() + ")");
            }
        }
    }

    private void addObstacleUI(map m) {
        System.out.print("Enter obstacle name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) { System.out.println("Name cannot be empty."); return; }

        try {
            System.out.print("Enter X (0-" + (m.getGridWidth()-1) + "): ");
            int x = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Enter Y (0-" + (m.getGridHeight()-1) + "): ");
            int y = Integer.parseInt(scanner.nextLine().trim());
            if (!m.isInBounds(x, y)) { System.out.println("Out of bounds."); return; }
            m.addObstacle(new obstacle(name, new coordinate(x, y)));
            System.out.println("Obstacle added.");
        } catch (Exception e) {
            System.out.println("Invalid coordinates.");
        }
    }

    private void removeObstacleUI(map m) {
        var obs = m.getObstacles();
        if (obs.isEmpty()) { System.out.println("No obstacles to remove."); return; }
        for (int i = 0; i < obs.size(); i++) {
            var o = obs.get(i);
            var c = o.getObstacleCoordinate();
            System.out.println((i + 1) + ". " + o.getName() + " at (" + c.getX() + ", " + c.getY() + ")");
        }
        System.out.print("Select obstacle number: ");
        try {
            int idx = Integer.parseInt(scanner.nextLine().trim());
            if (idx < 1 || idx > obs.size()) { System.out.println("Invalid selection."); return; }
            m.removeObstacle(obs.get(idx - 1));
            System.out.println("Obstacle removed.");
        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
    }

    // Build a route from user-entered waypoints and apply it to the map.
// Blocks if any waypoint is out-of-bounds, non-adjacent, or on an obstacle.
    private void addRouteUI(map m) {
        System.out.println("Enter comma-separated waypoints like: x1 y1, x2 y2, x3 y3");
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) { System.out.println("No waypoints provided."); return; }

        String[] tokens = line.split(",");
        ArrayList<coordinate> pts = new ArrayList<>();
        try {
            for (String t : tokens) {
                String[] xy = t.trim().split("\\s+");
                int x = Integer.parseInt(xy[0]);
                int y = Integer.parseInt(xy[1]);
                pts.add(new coordinate(x, y));
            }
            route r = new route(pts);              // validates adjacency
            boolean ok = m.addRoute(r);            // validates bounds & obstacles
            if (ok) System.out.println("Route added.");
            else    System.out.println("Route blocked (out of bounds or through an obstacle).");
        } catch (Exception e) {
            System.out.println("Invalid waypoint format or non-adjacent steps.");
        }
    }

    // Ask map to plan a shortest route (BFS) that avoids obstacles, then paint it.
    private void planShortestRouteUI(map m) {
        try {
            System.out.print("Start X: "); int sx = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Start Y: "); int sy = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("End   X: "); int ex = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("End   Y: "); int ey = Integer.parseInt(scanner.nextLine().trim());

            var maybe = m.planShortestRoute(new coordinate(sx, sy), new coordinate(ex, ey));
            if (maybe.isEmpty()) {
                System.out.println("No available path (blocked or out of bounds).");
                return;
            }
            boolean painted = m.addRoute(maybe.get());
            if (painted) System.out.println("Planned and painted route successfully.");
            else         System.out.println("Planned route could not be painted (unexpected block).");
        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
    }


    // -------------------- Statistics --------------------
    private void addStatUI() {
        System.out.print("Enter activity duration (minutes): ");
        int duration = Integer.parseInt(scanner.nextLine().trim());
        Preconditions.checkArgument(duration > 0, "Duration must be positive");

        System.out.print("Enter distance (km): ");
        double distance = Double.parseDouble(scanner.nextLine().trim());
        Preconditions.checkArgument(distance > 0, "Distance must be positive");

        mymenu.addStat(new statistics(duration, distance));
        System.out.println("✓ Stat added successfully");
    }

    private void removeStatUI() {
        var list = showStat(true);
        if (list.isEmpty()) return;
        System.out.print("Enter the number of record to remove: ");
        int choice = Integer.parseInt(scanner.nextLine().trim());
        Preconditions.checkArgument(choice >= 1 && choice <= list.size(), "Invalid record selection");
        mymenu.removeStat(list.get(choice - 1));
        System.out.println("✓ Record removed.");
    }

    public ArrayList<statistics> showStat(boolean interactive) {
        ArrayList<statistics> list = mymenu.getStatistics();
        if (interactive) {
            System.out.println("\n--- Statistics ---");
            if (list.isEmpty()) {
                System.out.println("No statistics available.");
            } else {
                double totalTime = 0;
                double totalLength = 0;
                for (int i = 0; i < list.size(); i++) {
                    statistics s = list.get(i);
                    totalTime += s.getTime();
                    totalLength += s.getLength();
                    System.out.println((i + 1) + ". Time: " + s.getTime() +
                            " min, Length: " + s.getLength() + " km");
                }
                System.out.println("\n--- Summary ---");
                System.out.println("Total Activities: " + list.size());
                System.out.println("Total Time: " + totalTime + " minutes");
                System.out.println("Total Distance: " + totalLength + " km");
                if (!list.isEmpty()) {
                    System.out.println("Average Time: " + String.format("%.1f", totalTime / list.size()) + " minutes/activity");
                    System.out.println("Average Distance: " + String.format("%.1f", totalLength / list.size()) + " km/activity");
                }
            }
        }
        return list;
    }
}
