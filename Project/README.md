---
title: Activity-Tracker-System
author: Gia Dat Ly (lygd@myumanitoba.ca)
date: Fall 2025
---
# Overview

    Activity Tracker System is an implementation for COMP 2450 in Fall 2025.
    It helps the user to track when exercising and calculate some statistics 
    to give them insights help them make right decision on their health

# Runnning

This project was developed using IntelliJ IDEA and uses Maven, so there are two
ways to run it:

1. Open the class called `main.java` and click the green play button on the
   `comp2450.main`method, or
2. Run Maven on the command line:
   ```
   mvn compile exec:java '-Dexec.mainClass=comp2450.main'
   ```

# Changes phase 1

## Diagram changes
    - Add class menuPrinter which is responsible for printing information of the other classes
    - Changing the data type from non-programmer language to programmer language (text -> string...)
    - Moving (addGear() moveGear()) to activities class (not menu anymore)
    - Now each class responsible for its function
    - Adjust activity to classes as suggestion in feedback

## Code changes
    - What I've done so far is that rewrite most of my code: add menuPrinter class, breakdown menu and map class
    - Move all the Printer from the other classes to this class (showMap, showGear, showActivities, displayMap....)
    - I also added method to check Invariants and Arguments to each class so method will be more readable
    - I apologise about my map class mess up at first and now here is my quick instruction how to use this system

### Code Instruction

    1. First you run this using the RUNNING above
    2. Then enter your name
    3. Now you choose any options you want (enter using number only, just name when appropriate)
    I will go more specific on map class (I askes my instructor is my map class right because you said it look like a game 
    and I was really confused about that and my instructor said I did it right maybe I didn't add comment properly):
    
    Map Instruction:
    Add a map → Name: (for ex: city) with size 10 x 10.
    Show maps → choose to manage (enter yes/no) (for ex: yes) → Add obstacle (for ex:Tree) at (2,2).
    Display grid → you’ll see a * at row 2, column 2.
    The same when you want to add route → Add route waypoints → 0 0, 1 0, 1 1 → route cells marked with >.
    Of course, if you enter the route on the obstacle then you have the wrong route and need enter the right path for it
    Now you can display grid and see all the map tracker now
    
    Something more fun: (you can do more than this and more stuff will appear on phase 2)
    Add stat → time 30, distance 5.0.
    Show statistics → see totals and averages.
    
    4. Enter 15 to exit.


# Domain model

## Diagram
Here is the digram for my domain model

``` mermaid
---
config:
  layout: elk
  look: classic
  theme: default
title: Activity Tracker System
---
classDiagram
direction TB
    class obstacle {
	    string name
	    coordinate obstaclePosition
	    getName() void
	    getObstacleCoordinate() coordinate
    }
    class menu {
	    string userName
	    list~activity~ exercise
	    list~map~ mymap
	    list~statistics~ stat
	    addMap(map newMap) void
	    removeMap(map oldMap) void
	    addActivity(activity newAc) void
	    removeActivity(activity oldAc) void
		addStat(statistics newStat) void
		removeStat(statistics oldStat) void
    }

	class menuPrinter{
		showGear() items
		showMap() mymap
		showActivity() exercise
		showStat() stat
		displayMap()
		showObstacle() obstacles
	}

    class activity {
	    string name
		list~gear~ items
	    getName() string
		addGear(gear newGear) void
	    removeGear(gear oldGear) void
    }
    class gear {
	    string name
	    double quality
	    gearType type
	    changeQuality(double newQuality) : void
	    type() gearType
    }
    class gearType {
	    SUPPORT
	    PROTECTION
	    NUTRITION
    }
    class coordinate {
	    int x
	    int y
	    getX() int
	    getY() int
    }
    class map {
	    string name
	    list~obstacle~ obstacles
		int width
		int height
	    getName() string
	    addObstacle(obstacle newOne) void
	    removeObstacle(obstacle oldOne) void
    }
    class statistics {
	    double time
	    double length
	    averageTime() double
	    averageLength() double
    }
    class route {
	    coordinate start
	    coordinate end
	    checkRoute() valid/ invalid
    }
	
	<<enumeration>> gearType
	<<record>> coordinate
	
	note for menu "Invariant properties:
		* userName.length >= 1
		* exercise != null
		* mymap != null
		* stat != null
		"
	note for menu"It controls all the system"
	note for map "Invariant properties:
		* name.length >= 1
		* obstacles != null	
		"
	note for statistics "Invariant properties:
		* time > 0
		* length > 0
		"
	note for route "Invariant properties:
		* start != end
	"
	note for gear "Invariant properties:
		* name.length >= 1
		* quality >= 0
		* type != null
	"
	note for obstacle "Invariant properties:
		* name.length >= 1
	"
	note for activity "Invariant properties:
		* name.length >= 1
		* items != null
	"
	
    menu --* map : composed
    menu --* statistics : composed
    menu --* activity : composed
	menu ..> menuPrinter: uses
	map ..> menuPrinter: uses
    map --o obstacle : aggregates
    map --* route : composed
    obstacle --o coordinate : aggregate
    route ..> obstacle
    route --o coordinate : aggregates
    statistics ..> route
    activity o.. gear: aggregates
	activity ..> menuPrinter: uses
    gear --* gearType : composed
```

