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
        System.out.println("I'm a " + rc.getType() + " and I just got created! I have health " + rc.getHealth());

        // You can also use indicators to save debug notes in replays.
        rc.setIndicatorString("HAHA BUTT");

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
                    //case LAUNCHER: runLauncher(rc); break;
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
        MapLocation nearestWell = nearWell[0].getMapLocation();
        MapLocation spawnLocation = rc.adjacentLocation(rc.getLocation().directionTo(nearestWell));
        //Direction targetAdWell = rc.getLocation().directionTo(nearestAdWell);
        //Build Carriers, if we can build carriers -- NEED TO CHANGE THIS LOGIC/ADD AND CONDITION
        if(rc.canBuildRobot(RobotType.CARRIER, spawnLocation)){
            rc.buildRobot(RobotType.CARRIER, spawnLocation);
        }
        System.out.println(me.toString());
        System.out.println(decToButt(me,width,height));
        rc.setIndicatorString("Trying to write location!" + me);
        int buttTranslation = decToButt(me, width, height);
        rc.setIndicatorString("HQ1 - Writing to Shared Array + " + buttTranslation);
        rc.writeSharedArray(buttTranslation, lookingForIndex);

        //Adding HQ location to Shared Array using Butt
        /*if(rc.canWriteSharedArray(buttTranslation,lookingForIndex)){
            rc.setIndicatorString("HQ1 - Writing to Shared Array + " + me);
            rc.writeSharedArray(buttTranslation, lookingForIndex);
         } else if (rc.canWriteSharedArray(buttTranslation, lookingForIndex - 1)){
            rc.writeSharedArray(buttTranslation, lookingForIndex-1);
        } else if (rc.canWriteSharedArray(buttTranslation, lookingForIndex - 2)){
            rc.writeSharedArray(buttTranslation, lookingForIndex-2);
        } else if (rc.canWriteSharedArray(buttTranslation, lookingForIndex - 3)){
            rc.writeSharedArray(buttTranslation, lookingForIndex-3); */
        //} 
        MapLocation hqOne = buttToDec(rc.readSharedArray(lookingForIndex), width, height);
        //MapLocation hqTwo = buttToDec(rc.readSharedArray(lookingForIndex-1), width, height);
        //MapLocation hqThree = buttToDec(rc.readSharedArray(lookingForIndex-2), width, height);
        //MapLocation hqFour = buttToDec(rc.readSharedArray(lookingForIndex-3), width, height);
        //rc.setIndicatorString(hqOne.x + " " + hqOne.y + " " + hqTwo.x + " " + hqTwo.y + " " + hqThree.x + " " + hqThree.y + " " + hqFour.x + " " + hqFour.y + " ");
    }

    /**
     * Run a single turn for a Carrier.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    static void runCarrier(RobotController rc) throws GameActionException {
        //Define Variables
        int lookingForIndex = 63;
        int[] distanceOfHQ = new int[4];
        float width = rc.getMapWidth();
        float height = rc.getMapHeight();
        MapLocation me = rc.getLocation();
        int idealIndex = 0;
        
        //Find Wells
        WellInfo[] nearWell = rc.senseNearbyWells();
        MapLocation nearestWell = nearWell[0].getMapLocation();
        int desiredResourceAmount = 40;
        
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
        if(nearWell.length >= 1){
            Direction dir = rc.getLocation().directionTo(nearestWell);
            if(rc.canMove(dir)){
                rc.move(dir);
            }
        }

        //If Robot is full, go to the closest HQ to deposit.
        int elAmt = rc.getResourceAmount(ResourceType.ELIXIR);
        int adAmt = rc.getResourceAmount(ResourceType.ADAMANTIUM);
        int maAmt = rc.getResourceAmount(ResourceType.MANA);
        int total = elAmt + adAmt + maAmt;
        if (total == desiredResourceAmount) {
            rc.setIndicatorString("Heading Back");
            //List<int> distanceOFHQ = new ArrayList();
            int arrayCounter = 0;
            while(rc.readSharedArray(lookingForIndex) != 0){
                while(lookingForIndex > 59){
                    int x = me.distanceSquaredTo(buttToDec(rc.readSharedArray(lookingForIndex), width, height));
                    distanceOfHQ[arrayCounter] = x;
                    lookingForIndex -= 1;
                    arrayCounter += 1;
                }
            }
            for(int counter = 0; distanceOfHQ.length > counter; counter++){
                int smallestNumber = 360;
                if(distanceOfHQ[counter] != 0 && distanceOfHQ[counter] < smallestNumber){
                    smallestNumber = distanceOfHQ[counter];
                    idealIndex = counter;
                }
            }
            //int shortestHQID = robotID.get(idealIndex);
            Direction nearestHQ = me.directionTo(buttToDec(rc.readSharedArray(lookingForIndex-idealIndex),width, height));
            rc.setIndicatorString("Going to " + rc.readSharedArray(lookingForIndex-idealIndex));
            rc.move(nearestHQ);
    }
    }
    /**
     * Run a single turn for a Launcher.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    //static void runLauncher(RobotController rc) throws GameActionException {
        
    //}

    public static int decToButt(MapLocation loc, float width, float height){
        //float xRes = rc.getMapWidth() / 10;
        //float yRes = rc.getMapHeight() / 10;
        //Scanner scanner = new Scanner(loc.toString());
        //scanner.useDelimiter(",");
        //String x = scanner.next();
        //String y = scanner.next();
        //x = x.replace("[", "");
        //y = y.replace("]", "");
        //int newx = Integer.parseInt(x);
        //int newy = Integer.parseInt(y);
        System.out.println(loc.toString());
        String s = loc.toString();
        char x = s.charAt(1);
        char y = s.charAt(3);
        int newx = Integer.parseInt(String.valueOf(x));
        int newy = Integer.parseInt(String.valueOf(y));
        width = width/10;
        height = height/10;
        int ten = Math.round(newx / width) * 10;
        int one = Math.round(newy / height);
        //System.out.println("STOP RIGHT HERE THIS IS YOUR TEXT" + (ten + one));
        return ten + one;
        //return x;
    }

    public static MapLocation buttToDec(int buttNum, float width, float height){
        //float xRes = rc.getMapWidth() / 10;
        //float yRes = rc.getMapHeight() / 10;
        width = width/10;
        height = height/10;
        double dx = Math.floor((buttNum * width) / 10);
        int x = (int)dx;
        int y = Math.round((buttNum * height)) % 10;
        MapLocation newLoc = new MapLocation(x,y);
        return newLoc;
    }
}
