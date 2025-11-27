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
# Phase 2 description:
There are some update from my phase 1 submission, what I've done in phase 2:

## Domain Model Changes from phase 1:
    - Add some more classes related to phase 2
    - Add invariants for these classes

## Flow of interaction:
    - Complete the flow of interaction for this phase so you can follow it easily
    - Notes: the code maybe more complicated than the interaction but this flow cover basically how the code will be used

## My code changes from phase 1:
    - There is huge difference from phase 1
    - Add some classes (LinkedListStack, Stack, PathFinder, ActivityRecord, World, FeedService, Profile, exception classes) 
    - Add folders (algorithm folder, exceptions folder in the model)
    - Changes a little bit old classes from phase 1 to fit with the 2nd phase
    - The World class is the "hard-coded" class as intruction

## Resources:
    - ChatGPT: sometimes my code doesn't work for some reason (maybe because problems in my logic) so I used GPT to fixed it
    - Stack Overflow: see how people solving the problems similar to mine

### How to run my code:
    - The same way as describe above
    - Enter your name (aka name of your profile)
    - Choose any option you like but I will focus mainly on these things: route for your activity, see other profile
    
    Route:
    (- If you want you can enter the obstacle first to see how it works when having obstacles
    - enter 4 and add obstacles manually) I REALLY SUGGEST DOING THIS because it's really cool
    - Choose option add activity (enter 5) -> name your activity
    - Then choose options from 1-4, the most impressive thing in this is option 2
    - Enter 2 -> enter arrival and destination points 
    - it will show you the shortest way to your destination and automatically avoid obstacles
    - Then it will send you back to main if you wanna see map enter 4 then enter 1 to display grid
    
    Profile:
    - Enter 8 to switch profile and then create new profile
    - Then choose follow people (enter a to follow and number of that person) => TADA! you have that person in your feed
    - Then enter 3 go to your feed and see what activities they did, and when they did it (even yours)

    - You can do more than that if you add the other things

# Flow of interaction
I add all the things may contain in each subgraph first and then I create the logic later
Here is my flow of interaction:
``` mermaid
---
config:
  layout: elk
  look: classic
  theme: default
title: Activity Tracker System Interaction Flow
---
flowchart TB

subgraph Interacting with activity tracker system
  MAIN[[Main menu]]

  subgraph World
    wOpen[[Open profiles]]
    wHave{Have profile?}
    wCreate[Create profile]
    wDup{Name already used?}
    wCreated[[Profile created]]
    wGet[Load profile]
  end


  subgraph Profile
    pSigned[[Signed in]]
    pFollow[Follow/Unfollow people]
    pBackFromProfile[[Back to main]]
  end


  subgraph map
    mMenu[[Map menu]]
    mChoice{Action?}
    mDisplay[Display grid]
    mShowObs[Show obstacles]
    mAddObs[Add obstacle]
    mValid{In-bounds and free?}
    mPlaced[[Obstacle added]]
    mRejected[[Placement rejected]]
    mRemObs[Remove obstacle]
    mRemoved[[Obstacle removed]]
    mClear[Clear route overlay]
  end


  subgraph gear
    gMenu[[Gear menu]]
    gChoice{Action?}
    gList[Show gear]
    gAdd[Add gear to activity]
    gRem[Remove gear from activity]
    gQual[Change gear quality]
    gErr[[Invalid choice]]
  end

  subgraph activity
    aStart[[Add activity]]
    aTitle[Enter activity title]
    aSrc{Route source?}
    aManual[Manual waypoints]
    aDup[Duplicate previous route]
    aHasPrev{Has previous?}
    aValid{Route valid on map?}
    aSaved[[Activity saved + route painted]]
    aBlocked[[Blocked by obstacle / out of bounds]]
    aNoPrev[[No previous route]]
  end

  subgraph PathFinder
    pfPlan[[Plan route]]
    pfMode{Mode? Normal / Only-My / Feed}
    pfSE[Enter start and end]
    pfRun{Backtracking using LinkedListStack}
    pfFound{Path found?}
    pfFail[[No path found]]
  end

  subgraph FeedService
    fOpen[[Open feed]]
    fScope{Scope?}
    fMine[Show my activities]
    fMixed[Show mixed feed me + following]
    fList[[Activities listed]]
  end


  subgraph statistics
    sInput[Provide time & distance]
    sDone[[Stats captured]]
  end


  %% menu → modules
  MAIN ==Profiles==> wOpen
  MAIN ==Map==> mMenu
  MAIN ==Gear==> gMenu
  MAIN ==Add activity==> aStart
  MAIN ==Find route==> pfPlan
  MAIN ==Feed==> fOpen

  %% modules → menu
  wOpen ==don't have profile==> MAIN
  wOpen ==back==> MAIN
  mMenu ==back==> MAIN
  gMenu ==back==> MAIN
  aSaved ==back==> MAIN
  pfFail ==back==> MAIN
  fOpen ==don't have feed==> MAIN
  fList ==back==> MAIN

  %% World (create/select) → Profile
  wOpen --> wHave
  wHave -- No --> wCreate ==display name==> wDup
  wDup -.Taken.-> wCreate
  wDup -.Available.-> wCreated --> wGet
  wHave -- Yes --> wGet
  wGet -.Loaded.-> pSigned

  %% Profile follow/unfollow → back to main
  pSigned ==choose users==> pFollow
  pFollow -.Following updated.-> pBackFromProfile
  pBackFromProfile --> MAIN

  %% map actions
  mMenu --> mChoice
  mChoice -- Display grid --> mDisplay -.Grid printed.-> mMenu
  mChoice -- Show obstacles --> mShowObs -.List printed.-> mMenu
  mChoice -- Add obstacle --> mAddObs ==name,x,y==> mValid
  mValid -- Yes --> mPlaced --> mMenu
  mValid -- No --> mRejected --> mAddObs
  mChoice -- Remove obstacle --> mRemObs -.Pick id.-> mRemoved --> mMenu
  mChoice -- Clear overlay --> mClear -.Overlay cleared.-> mMenu

  %% gear actions
  gMenu --> gChoice
  gChoice -- Show --> gList -.Items listed.-> gMenu
  gChoice -- Add --> gAdd ==name,quality,type==> gMenu
  gChoice -- Remove --> gRem -.Pick item.-> gMenu
  gChoice -- Change quality --> gQual ==new quality==> gMenu
  gChoice -- Other --> gErr --> gMenu

  %% activity creation (manual / duplicate / planned)
  aStart --> aTitle --> aSrc
  aSrc -- Manual --> aManual ==waypoints==> aValid
  aManual ====> aSaved
  aDup ====> aSaved
  aSrc -- Duplicate --> aDup -.Pick activity.-> aHasPrev
  aHasPrev -- No --> aNoPrev --> aStart
  aHasPrev -- Yes --> aValid

  %% activity ↔ PathFinder (planned)
  aSrc -- Planned --> pfPlan

  pfPlan --> pfMode ==pick mode==> pfSE ==start,end==> pfRun
  pfRun --> pfFound
  pfFound -- No --> pfFail --> aStart
  pfFound -- Yes --> aValid

  %% activity validate & save (uses map)
  aValid -- Yes --> sInput --> sDone --> aSaved
  aValid -- No --> aBlocked --> aStart

  %% feed
  fOpen --> fScope
  fScope -- Mine --> fMine --> fList
  fScope -- Mixed --> fMixed --> fList
end
```

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

	class World {
		Map worldMap
		Map~String,Profile~ profiles
		map() Map
		profiles() Collection~Profile~
		createProfile(String name) Profile
		getProfile(String id) Profile
	}

	class Profile {
		String id
		String displayName
		Set~String~ followingIds
		List~ActivityRecord~ activities
		id() String
		name() String
		rename(String newName) void
		follow(String profileId) void
		unfollow(String profileId) void
		addActivity(ActivityRecord) void
		myRoutes() List~Route~
		feed(World) List~ActivityRecord~
	}

	class ActivityRecord {
		String title
		long timestamp
		Route routeTaken
		List~Gear~ gearUsed
		Statistics stats
		title() String
		routeTaken() Route
		stats() Statistics
	}

	class FeedService {
  		mixedFeed() List~ActivityRecord~
	}

	class PathFinder {
  		findRoute() route
	}

	class Stack~T~ {
		<<interface>>
		push(T) void
		pop() T
		peek() T
		isEmpty() boolean
		size() int
	}

	class LinkedListStack~T~ {
		Node~T~ head
		int n
		push(T) void
		pop() T
		peek() T
		isEmpty() boolean
		size() int
	}

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

	note for ActivityRecord "Invariant properties:
		* title.length >= 1
		* gearUsed != null
	"

	note for World "Invariant properties:
		* worldMap.length >= 1
		* profiles != null
	"

	note for Profile "Invariant properties:
		* id.length >= 1
		* displayName.length >= 1
		* activities != null
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
	LinkedListStack~T~ --* Stack~T~ : composed
	World --* map: composed
	World --* Profile: composed
	Profile --* ActivityRecord: composed
	ActivityRecord --* route: composed
	ActivityRecord --* statistics: composed
	ActivityRecord --o gear: aggregates
	map ..> menuPrinter : displayed by
	FeedService ..> World : reads
	FeedService ..> Profile : reads
	FeedService ..> ActivityRecord : produces list
	PathFinder ..> map : queries
	PathFinder ..> route : returns
	PathFinder --* LinkedListStack~T~: composed
```

