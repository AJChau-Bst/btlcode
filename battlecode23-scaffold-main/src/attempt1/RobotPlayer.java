package attempt1;

import battlecode.common.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.*;
import java.lang.*;

/**
 * RobotPlayer is the class that describes your main robot strategy.
 * The run() method inside this class is like your main function: this is what we'll call once your robot
 * is created!
 */
public strictfp class RobotPlayer {

    /**
     * We will use this variable to count the number of turns this robot has been alive.
     * You can use static variables like this to save any information you want. Keep in mind that even though
     * these variables are static, in Battlecode they aren't actually shared between your robots.
     */
    static int turnCount = 0;

    /**
     * A random number generator.
     * We will use this RNG to make some random moves. The Random class is provided by the java.util.Random
     * import at the top of this file. Here, we *seed* the RNG with a constant number (6147); this makes sure
     * we get the same sequence of numbers every time this code is run. This is very useful for debugging!
     */
    static final Random rng = new Random(6147);

    /** Array containing all the possible movement directions. */
    static final Direction[] directions = {
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH,
        Direction.SOUTHWEST,
        Direction.WEST,
        Direction.NORTHWEST,
    };

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * It is like the main function for your robot. If this method returns, the robot dies!
     *
     * @param rc  The RobotController object. You use it to perform actions from this robot, and to get
     *            information on its current status. Essentially your portal to interacting with the world.
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        // Hello world! Standard output is very useful for debugging.
        // Everything you say here will be directly viewable in your terminal when you run a match!
        // System.out.println("I'm a " + rc.getType() + " and I just got created! I have health " + rc.getHealth());

        // You can also use indicators to save debug notes in replays.
        //rc.setIndicatorString("HAHA BUTT");

        while (true) {
            // This code runs during the entire lifespan of the robot, which is why it is in an infinite
            // loop. If we ever leave this loop and return from run(), the robot dies! At the end of the
            // loop, we call Clock.yield(), signifying that we've done everything we want to do.

            turnCount += 1;  // We have now been alive for one more turn!

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode.
            try {
                // The same run() function is called for every robot on your team, even if they are
                // different types. Here, we separate the control depending on the RobotType, so we can
                // use different strategies on different robots. If you wish, you are free to rewrite
                // this into a different control structure!
                switch (rc.getType()) {
                    case HEADQUARTERS:     runHeadquarters(rc);  break;
                    case CARRIER:      runCarrier(rc);   break;
                    case LAUNCHER: runLauncher(rc); break;
                    case BOOSTER: // Examplefuncsplayer doesn't use any of these robot types below.
                    case DESTABILIZER: // You might want to give them a try!
                    case AMPLIFIER:       break;
                }

            } catch (GameActionException e) {
                // Oh no! It looks like we did something illegal in the Battlecode world. You should
                // handle GameActionExceptions judiciously, in case unexpected events occur in the game
                // world. Remember, uncaught exceptions cause your robot to explode!
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();

            } catch (Exception e) {
                // Oh no! It looks like our code tried to do something bad. This isn't a
                // GameActionException, so it's more likely to be a bug in our code.
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();

            } finally {
                // Signify we've done everything we want to do, thereby ending our turn.
                // This will make our code wait until the next turn, and then perform this loop again.
                Clock.yield();
            }
            // End of loop: go back to the top. Clock.yield() has ended, so it's time for another turn!
        }

        // Your code should never reach here (unless it's intentional)! Self-destruction imminent...
    }

    /**
     * Run a single turn for a Headquarters.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    static void runHeadquarters(RobotController rc) throws GameActionException {
        //Declare Variables
        int lookingForIndex = 63;
        MapLocation me = rc.getLocation();
        float width = rc.getMapWidth();
        float height = rc.getMapHeight();

        //Find Nearby Wells
        WellInfo[] nearWell = rc.senseNearbyWells();
        if(nearWell.length > 0) {
        	MapLocation nearestWell = nearWell[0].getMapLocation();
            MapLocation spawnLocation = rc.adjacentLocation(rc.getLocation().directionTo(nearestWell));
        //Direction targetAdWell = rc.getLocation().directionTo(nearestAdWell);
        //Build Carriers, if we can build carriers -- NEED TO CHANGE THIS LOGIC/ADD AND CONDITION
        //Get Array of Nearby Robots, Count the NUmber of Carriers
        int carrierCounter = 0;
        Team friendly = rc.getTeam();
        RobotInfo[] friends = rc.senseNearbyRobots(rc.getType().visionRadiusSquared, friendly);
            if(friends.length > 0) {
                for(RobotInfo bot : friends){
                    if (bot.type == RobotType.CARRIER){
                        carrierCounter = carrierCounter + 1;
                        }
                    }
                }
            if(carrierCounter <= 2){
                //System.out.println("Printing Carrier");
                System.out.println("Printing Carrier, + " + carrierCounter);
                if(rc.canBuildRobot(RobotType.CARRIER, spawnLocation)){
                    rc.buildRobot(RobotType.CARRIER, spawnLocation);
            }

        //if(rc.canBuildRobot(RobotType.CARRIER, spawnLocation)){
                //rc.buildRobot(RobotType.CARRIER, spawnLocation);
            }
        }

        //System.out.println(me.toString());
        //System.out.println(decToButt(me,width,height));
        //rc.setIndicatorString("Trying to write location!" + me);
        int buttTranslation = decToButt(me, width, height);
        //rc.setIndicatorString("HQ1 - Writing to Shared Array + " + buttTranslation);
        //rc.writeSharedArray(lookingForIndex, buttTranslation);
        //int buttRead = rc.readSharedArray(lookingForIndex);
        //rc.setIndicatorString(buttTranslation + "");

        //Adding HQ location to Shared Array using Butt
        if(rc.readSharedArray(lookingForIndex) == 0){
        	rc.writeSharedArray(lookingForIndex, buttTranslation);
        } else if (rc.readSharedArray(lookingForIndex - 1) == 0){
        	rc.writeSharedArray(lookingForIndex - 1, buttTranslation);
        } else if (rc.readSharedArray(lookingForIndex - 2) == 0){
        	rc.writeSharedArray(lookingForIndex - 2, buttTranslation);
        } else if (rc.readSharedArray(lookingForIndex - 3) == 0){
        	rc.writeSharedArray(lookingForIndex - 3, buttTranslation);
        } 
        MapLocation hqOne = buttToDec(rc.readSharedArray(lookingForIndex), width, height);
        MapLocation hqTwo = buttToDec(rc.readSharedArray(lookingForIndex-1), width, height);
        MapLocation hqThree = buttToDec(rc.readSharedArray(lookingForIndex-2), width, height);
        MapLocation hqFour = buttToDec(rc.readSharedArray(lookingForIndex-3), width, height);
        rc.setIndicatorString(hqOne.x + " " + hqOne.y + " " + hqTwo.x + " " + hqTwo.y + " " + hqThree.x + " " + hqThree.y + " " + hqFour.x + " " + hqFour.y + " ");
        
    }

    /**
     * Run a single turn for a Carrier.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    static void runCarrier(RobotController rc) throws GameActionException {
        //Define Variables
        float width = rc.getMapWidth();
        float height = rc.getMapHeight();
        MapLocation me = rc.getLocation();
        int desiredResourceAmount = 40;
        
        
        //If Robot is full, go to the closest HQ to deposit.
        int elAmt = rc.getResourceAmount(ResourceType.ELIXIR);
        int adAmt = rc.getResourceAmount(ResourceType.ADAMANTIUM);
        int maAmt = rc.getResourceAmount(ResourceType.MANA);
        int total = elAmt + adAmt + maAmt;
        
        if (total >= desiredResourceAmount) {
        	boolean hqSpotted = false;
        	rc.setIndicatorString("Returning to HQ!");
        	//if full of resource, scan for nearest HQ and move there. 
        	Team friendly = rc.getTeam();
            RobotInfo[] friends = rc.senseNearbyRobots(rc.getType().visionRadiusSquared, friendly);
            if(friends.length > 0) {
            	for(RobotInfo bot : friends){
                    if (bot.type == RobotType.HEADQUARTERS){
                    	hqSpotted = true;
                        MapLocation preciseTarget = bot.getLocation();
                        Direction dir = me.directionTo(preciseTarget);
                        if(me.isAdjacentTo(preciseTarget)){
                            rc.setIndicatorString("IM CONSTIPATED!!");
                            if(rc.canTransferResource(preciseTarget, ResourceType.ADAMANTIUM, rc.getResourceAmount(ResourceType.ADAMANTIUM))){
                                rc.transferResource(preciseTarget, ResourceType.ADAMANTIUM, rc.getResourceAmount(ResourceType.ADAMANTIUM));
                                rc.setIndicatorString("(A) i think i pooped :(");
                            }
                            if(rc.canTransferResource(preciseTarget, ResourceType.MANA, rc.getResourceAmount(ResourceType.MANA))){
                                rc.transferResource(preciseTarget, ResourceType.MANA, rc.getResourceAmount(ResourceType.MANA));
                                rc.setIndicatorString("(M) i think i pooped :(");
                            }
                            if(rc.canTransferResource(preciseTarget, ResourceType.ELIXIR, rc.getResourceAmount(ResourceType.ELIXIR))){
                                rc.transferResource(preciseTarget, ResourceType.ELIXIR, rc.getResourceAmount(ResourceType.ELIXIR));
                                rc.setIndicatorString("(E) i think i pooped :(");
                            }
                        } else if(rc.canMove(dir)) {
                            rc.move(dir);
                            break;
                        }
                    }
                    
                }
            }
            
            
        	if(hqSpotted == false) {
        		rc.setIndicatorString("Heading Back");
                /*//List<int> distanceOFHQ = new ArrayList();
                int arrayCounter = 0;
                while(lookingForIndex <= 63){
                    int x = me.distanceSquaredTo(buttToDec(rc.readSharedArray(lookingForIndex), width, height));
                    distanceOfHQ[arrayCounter] = x;
                    lookingForIndex += 1;
                    arrayCounter += 1;
                }
                for(int counter = 0; counter < distanceOfHQ.length; ++counter){
                    int smallestNumber = 7201;
                    if(distanceOfHQ[counter] < smallestNumber){
                        smallestNumber = distanceOfHQ[counter];
                        idealIndex = counter;
                    }
                }
                //int shortestHQID = robotID.get(idealIndex);
                Direction nearestHQ = me.directionTo(buttToDec(rc.readSharedArray(60+idealIndex),width, height));
                rc.setIndicatiorString("Going to: " + rc.readSharedArray(60+idealIndex) + "HQ Distance is " + distanceOfHQ.toString());
                //rc.setIndicatorString("Going to " + rc.readSharedArray(60+idealIndex));
                if(rc.canMove(nearestHQ)){
                	rc.move(nearestHQ);
                }*/
    	        int nearHQidx = 0;
    	        int nearHQdist = 7201;
    	        int dist = 7202;
    	        MapLocation[] hqs = new MapLocation[4];
    	        for(int i = 0; i < 4; ++i) {
    	        	hqs[i] = buttToDec(rc.readSharedArray(63-i), width, height);
    	        	dist = me.distanceSquaredTo(hqs[i]);
    	            if(dist < nearHQdist) {
    	            	nearHQidx = i;
    	            	nearHQdist = dist;
    	            }
    	        }
    	        Direction nearestHQ = me.directionTo(hqs[nearHQidx]);
                //MapLocation targetHQ = hqs[nearHQidx];
                rc.setIndicatorString("Going to " + hqs[nearHQidx].x + " " + hqs[nearHQidx].y);
                if(rc.canMove(nearestHQ)){
                	rc.move(nearestHQ);
                }
        	}
            
        }
        //Find Wells
        WellInfo[] nearWell = rc.senseNearbyWells();
        
        //if adjacent to well, start fucking collecting
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                MapLocation wellLocation = new MapLocation(me.x + dx, me.y + dy);
                if (rc.canCollectResource(wellLocation, -1)){
                    rc.collectResource(wellLocation, -1);
                    rc.setIndicatorString("Collecting, now have, AD:" + 
                    rc.getResourceAmount(ResourceType.ADAMANTIUM) + 
                    " MN: " + rc.getResourceAmount(ResourceType.MANA) + 
                    " EX: " + rc.getResourceAmount(ResourceType.ELIXIR));
                }
            }
        }

        //if wells nearby, move to them
        if(nearWell.length >= 1 && total < desiredResourceAmount){
            MapLocation nearestWell = nearWell[0].getMapLocation();
            Direction dir = rc.getLocation().directionTo(nearestWell);
            if(rc.canMove(dir)){
                rc.move(dir);
            }
        }

        
    }
    /**
     * Run a single turn for a Launcher.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    static void runLauncher(RobotController rc) throws GameActionException {
        
    }

    public static int decToButt(MapLocation loc, float width, float height){
        int x = loc.x;
        int y = loc.y;
        width = width/10;
        height = height/10;
        int ten = Math.round(x / width) * 10;
        int one = Math.round(y / height);
        return ten + one + 100;
    }

    public static MapLocation buttToDec(int buttNum, float width, float height){
        width = width/10;
        height = height/10;
        buttNum = buttNum - 100;
        float dx = (buttNum * width) / 10;
        int x = (int) Math.floor(dx);
        int y = Math.round((buttNum % 10) * height);
        MapLocation newLoc = new MapLocation(x,y);
        return newLoc;
    }
}
    public static Direction dirSecDir(MapLocation fromLoc, MapLocation toLoc) {
        if (fromLoc == null) {
            return null;
        }

        if (toLoc == null) {
            return null;
        }

        double dx = toLoc.x - fromLoc.x;
        double dy = toLoc.y - fromLoc.y;

        if (Math.abs(dx) >= 2.414 * Math.abs(dy)) {
            if (dx > 0) {
                if (dy > 0) {
                    return Direction.NORTHEAST;
                } else {
                    return Direction.SOUTHEAST;
                }
            } else if (dx < 0) {
                 if (dy > 0) {
                    return Direction.NORTHWEST;
                } else {
                    return Direction.SOUTHWEST;
                }
            } else {
                return Direction.CENTER;
            }
        } else if (Math.abs(dy) >= 2.414 * Math.abs(dx)) {
            if (dy > 0) {
                 if (dx > 0) {
                    return Direction.NORTHEAST;
                } else {
                    return Direction.NORTHWEST;
                }
            } else {
                if (dx > 0) {
                    return Direction.SOUTHEAST;
                } else {
                    return Direction.SOUTHWEST;
                }
            }
        } else {
            if (dy > 0) {
                if (dx > 0) {
                    if (Math.abs(dx) > Math.abs(dy)) {
                        return Direction.EAST;
                    } else {
                        return Direction.NORTH;
                    }
                } else {
                    if (Math.abs(dx) > Math.abs(dy)) {
                        return Direction.WEST;
                    } else {
                        return Direction.NORTH;
                    }
                }
            } else {
                if (dx > 0) {
                    if (Math.abs(dx) > Math.abs(dy)) {
                        return Direction.EAST;
                    } else {
                        return Direction.SOUTH;
                    }
                } else {
                    if (Math.abs(dx) > Math.abs(dy)) {
                        return Direction.WEST;
                    } else {
                        return Direction.SOUTH;
                    }
                }
            }
        }
    }

    public static void mooTwo(RobotController rc, MapLocation loc) {
        Direction dir = rc.getLocation().directionTo(loc);
        Direction secDir = dirSecdir(rc.getLocation(), loc);
        if (rc.canMove(dir)) {
            rc.move(dir);
        } else if (rc.canMove(secDir)) {
            rc.move(secDir);
        } else if (dir.rotateLeft() == secDir && rc.canMove(dir.rotateRight())) {
            rc.move(dir.rotateRight());
        } else if (rc.canMove(dir.rotateLeft())) {
            rc.move(dir.rotateLeft());
        } else
    }
}
