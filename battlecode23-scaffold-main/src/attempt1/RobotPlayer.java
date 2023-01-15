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
            ///***ADJUST NUMBER OF CARRIERS HERE***///
            if(carrierCounter <= 6){
                //System.out.println("Printing Carrier");
                //System.out.println("Printing Carrier, + " + carrierCounter);
                if(rc.canBuildRobot(RobotType.CARRIER, spawnLocation)){
                    rc.buildRobot(RobotType.CARRIER, spawnLocation);
                    rc.setIndicatorString("Building a carrier!");
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
        if (rc.readSharedArray(lookingForIndex) == 0){
        	rc.writeSharedArray(lookingForIndex, buttTranslation);
        	rc.setIndicatorString("Documenting my location!");
        } else if (rc.readSharedArray(lookingForIndex - 1) == 0){
        	rc.writeSharedArray(lookingForIndex - 1, buttTranslation);
        	rc.setIndicatorString("Documenting my location!");
        } else if (rc.readSharedArray(lookingForIndex - 2) == 0){
        	rc.writeSharedArray(lookingForIndex - 2, buttTranslation);
        	rc.setIndicatorString("Documenting my location!");
        } else if (rc.readSharedArray(lookingForIndex - 3) == 0){
        	rc.writeSharedArray(lookingForIndex - 3, buttTranslation);
        	rc.setIndicatorString("Documenting my location!");
        } 
        /*MapLocation hqOne = buttToDec(rc.readSharedArray(lookingForIndex), width, height);
        MapLocation hqTwo = buttToDec(rc.readSharedArray(lookingForIndex-1), width, height);
        MapLocation hqThree = buttToDec(rc.readSharedArray(lookingForIndex-2), width, height);
        MapLocation hqFour = buttToDec(rc.readSharedArray(lookingForIndex-3), width, height);
        //rc.setIndicatorString(hqOne.x + " " + hqOne.y + " " + hqTwo.x + " " + hqTwo.y + " " + hqThree.x + " " + hqThree.y + " " + hqFour.x + " " + hqFour.y + " ");*/
        
        //When threshold for resources, create an anchor and a carrier, then transfer the anchor to carrier
/*         if (rc.getResourceAmount(ResourceType.ADAMANTIUM) >= 500 && rc.getResourceAmount(ResourceType.MANA) >= 500) {
            if (rc.canBuildAnchor(Anchor.STANDARD)) {
                rc.buildAnchor(Anchor.STANDARD);
                rc.buildRobot(RobotType.CARRIER, spawnLocation);
                if (rc.canTakeAnchor(spawnLocation, Anchor.STANDARD)) {
                    rc.takeAnchor(spawnLocation, Anchor.STANDARD);
                    rc.setIndicatorString("I am carrying an anchor!");
                }
            }
        } */

         //Spawn Launcher Code
        int centerWidth = Math.round(width/2);
        MapLocation centerOfMap = new MapLocation(centerWidth, centerWidth);
        Direction launcherDir = me.directionTo(centerOfMap);
        MapLocation launcherSpawn = rc.getLocation().add(launcherDir);
        if(rc.getResourceAmount(ResourceType.MANA) >= 100 && rng.nextInt(4) != 4)
            if (rc.canBuildRobot(RobotType.LAUNCHER,launcherSpawn)){
                rc.buildRobot(RobotType.LAUNCHER, launcherSpawn);
            }
        
    
        //When threshold for resources, create an anchor
        if (rc.senseRobotAtLocation(me).getTotalAnchors() == 0) {
        	if (rc.getResourceAmount(ResourceType.ELIXIR) >= 1000) {
        		if (rc.canBuildAnchor(Anchor.ACCELERATING)) {
        			rc.buildAnchor(Anchor.ACCELERATING);
        			rc.setIndicatorString("Building an achor!");
        		}
        	} else if (rc.getResourceAmount(ResourceType.ADAMANTIUM) >= 600 && rc.getResourceAmount(ResourceType.MANA) >= 100) {
        		if (rc.canBuildAnchor(Anchor.STANDARD)) {
	                rc.buildAnchor(Anchor.STANDARD);
	                rc.setIndicatorString("Building an anchor!");
	        	}
	     	} 
	    }
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
    	boolean hqSpotted = false;
        
        //If Robot is full, go to the closest HQ to deposit.
        int elAmt = rc.getResourceAmount(ResourceType.ELIXIR);
        int adAmt = rc.getResourceAmount(ResourceType.ADAMANTIUM);
        int maAmt = rc.getResourceAmount(ResourceType.MANA);
        int total = elAmt + adAmt + maAmt;
        
        MapLocation preciseTarget = new MapLocation(61,61);
        
        //Find friendly HQs
        Team friendly = rc.getTeam();
        RobotInfo[] friends = rc.senseNearbyRobots(rc.getType().visionRadiusSquared, friendly);
        if(friends.length > 0) {
        	for(RobotInfo bot : friends){
                if (bot.type == RobotType.HEADQUARTERS){
                    preciseTarget = bot.getLocation(); 
                	hqSpotted = true;
                    break;
                }
        	}
        }
        
      //Run from nearest enemy bot (threatening)
        RobotInfo[] enemies = rc.senseNearbyRobots(rc.getType().visionRadiusSquared, friendly.opponent());
        if(enemies.length > 0) {
        	MapLocation nearestEnemy = new MapLocation(61,61);
        	for(RobotInfo bot : enemies){
                if (bot.type != RobotType.CARRIER && bot.type != RobotType.AMPLIFIER){
                	MapLocation botLoc = bot.getLocation(); 
                	if (me.distanceSquaredTo(botLoc) < me.distanceSquaredTo(nearestEnemy)) {
                		nearestEnemy = botLoc;
                	}
                }
        	}
        	if (rc.onTheMap(nearestEnemy)) {
        		flee(rc, nearestEnemy);
        		rc.setIndicatorString("AHHHHHHHHHHHHHHHHHHHHH");
        	}
        }
        
        if (total > 0) {
        	//if full of resource, scan for nearest HQ and move there.
        	if (rc.onTheMap(preciseTarget)) {
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
                } else if (total >= desiredResourceAmount) {
		        	if(friends.length > 0) {
		            	for(RobotInfo bot : friends){
		                    if (bot.type == RobotType.HEADQUARTERS){
		                    	preciseTarget = bot.getLocation();
		                        //Direction dir = me.directionTo(preciseTarget);
		                        mooTwo(rc, preciseTarget);
		                        rc.setIndicatorString("Returning to HQ!" + preciseTarget.x + " " + preciseTarget.y);
		                        break;
		                    }
		                }
		            }
                }
        	}
        	if (total >= desiredResourceAmount) {	
	        	if(hqSpotted == false) {
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
	    	        //Direction nearestHQ = me.directionTo(hqs[nearHQidx]);
	                //MapLocation targetHQ = hqs[nearHQidx];
	                rc.setIndicatorString("Going to " + hqs[nearHQidx].x + " " + hqs[nearHQidx].y);
	                mooTwo(rc, hqs[nearHQidx]);
	        	}
    		}
        	//If not carrying an anchor, take one if the HQ has one
        } else if (rc.getAnchor() == null) {
        	if (rc.canSenseLocation(preciseTarget)) {
        		if (rc.senseRobotAtLocation(preciseTarget).getTotalAnchors() > 0) {
        			if (rc.canTakeAnchor(preciseTarget, Anchor.ACCELERATING)) {
        				rc.takeAnchor(preciseTarget, Anchor.ACCELERATING);
        				rc.setIndicatorString("Taking an anchor!");
        			} else if (rc.canTakeAnchor(preciseTarget, Anchor.STANDARD)) {
        				rc.takeAnchor(preciseTarget, Anchor.STANDARD);
        				rc.setIndicatorString("Taking an anchor!");
        			} else {
        				mooTwo(rc, preciseTarget);
        				rc.setIndicatorString("Moving to get an anchor!");
        			}
        		}
        	}
        }
        //If carrying an anchor and standing on a valid island, plant the anchor
        if (rc.getAnchor() != null) {
        	if(rc.canPlaceAnchor() == true) {
            	if (rc.senseTeamOccupyingIsland(rc.senseIsland(me)) != rc.getTeam()) {
            		rc.placeAnchor();
            		rc.setIndicatorString("Planting an anchor!");
            	}
            	//Spread out, avoid other bots with anchors
        	} else {
        		if(friends.length > 0) {
        			MapLocation nearestAnchorBot = new MapLocation(61,61);
                	for(RobotInfo bot : friends){
                		if(bot.getTotalAnchors() > 0) {
                			if (me.distanceSquaredTo(bot.getLocation()) < me.distanceSquaredTo(nearestAnchorBot)){
                            	nearestAnchorBot = bot.getLocation();
                            }
                		}
                	}
                	if (rc.onTheMap(nearestAnchorBot)) {
                		flee(rc, nearestAnchorBot);
                	}
                }
        		//Scan for visible islands, determine which is closest, and move to it
        		int[] nearbyIslands = rc.senseNearbyIslands();
        		if (nearbyIslands.length > 0) {
        			List<Integer> plantableIslands = new ArrayList<Integer>();
            		for (int i : nearbyIslands) {
            			if (rc.senseTeamOccupyingIsland(i) != rc.getTeam()) {
            				plantableIslands.add(i);
            			}
            		}
            		//plantableIslands.removeIf(Objects::isNull);
            		MapLocation[] nearestIslandLoc = new MapLocation[plantableIslands.size()];
            		int idx = 0;
            		for (int j : plantableIslands) {
            			MapLocation[] islandLocs = rc.senseNearbyIslandLocations(j);
            			MapLocation nearestLoc = new MapLocation(61,61);
            			for (MapLocation k : islandLocs) {
            				if (me.distanceSquaredTo(k) < me.distanceSquaredTo(nearestLoc)) {
            					nearestLoc = k;
            				}
            			}
            			nearestIslandLoc[idx] = nearestLoc;
            			idx++;
            		}
            		MapLocation nearestIsland = new MapLocation(61,61);
            		for (MapLocation l : nearestIslandLoc) {
            			if (me.distanceSquaredTo(l) < me.distanceSquaredTo(nearestIsland)) {
            				nearestIsland = l;
            			}
            		}
            		mooTwo(rc, nearestIsland);
            		rc.setIndicatorString("Heading to an island!" + nearestIsland.x + " " + nearestIsland.y);
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

        //if wells nearby and not crowded, move to them
        if(nearWell.length >= 1 && total < desiredResourceAmount){
    		boolean wellFound = false;
        	for (WellInfo aWell : nearWell) {
        		MapLocation wellLoc = aWell.getMapLocation();
        		RobotInfo[] atWell = rc.senseNearbyRobots(wellLoc, 2, friendly);
        		int crowdSize = 0;
        		for (RobotInfo robot : atWell) {
        			if (robot.getType() == RobotType.CARRIER) {
        				crowdSize++;
        			}
        		}
        		if (crowdSize < 5) {
        			wellFound = true;
        			mooTwo(rc, wellLoc);
        			rc.setIndicatorString("Moving to well!" + wellLoc.x + " " + wellLoc.y);
        			break;
        		}
        	}
        	if (wellFound == false) {
        		flee(rc, nearWell[0].getMapLocation());
        		rc.setIndicatorString("Ew. People. Looking for somewhere less crowded!");
        	}
        } else {
        	RobotInfo[] nearbyBots = rc.senseNearbyRobots(rc.getType().visionRadiusSquared);
        	MapLocation nearestBot = new MapLocation(61,61);
        	for (RobotInfo aBot : nearbyBots) {
        		if (me.distanceSquaredTo(aBot.getLocation()) < me.distanceSquaredTo(nearestBot)) {
        			nearestBot = aBot.getLocation();
        		}
        	}
        	if (rc.onTheMap(nearestBot)) {
        		flee(rc, nearestBot);
        	} else {
        		mooTwo(rc, new MapLocation(Math.round(rc.getMapWidth()/2),Math.round(rc.getMapHeight()/2)));
        	}
        }

        
    }
    /**
     * Run a single turn for a Launcher.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
     static void runLauncher(RobotController rc) throws GameActionException {
        int width = rc.getMapWidth();
        int height = rc.getMapHeight();
        MapLocation me = rc.getLocation();
        //move to center
        //int centerWidth = Math.round(width/2);
        //MapLocation centerOfMap = new MapLocation(centerWidth, centerWidth);
        //Direction launcherDir = me.directionTo(centerOfMap);
        //Enemy HQ location calculation

        int nearHQidx = 0;
        int nearHQdist = 7201;
        int dist = 7202;
        MapLocation[] enemyHQs = new MapLocation[4];
        for(int i = 0; i < 4; ++i) {
            MapLocation friendlyHQ = buttToDec(rc.readSharedArray(63-i), width, height);
            enemyHQs[i] = findEnemyHQ(friendlyHQ, width, height);
            dist = me.distanceSquaredTo(enemyHQs[i]);
            if(dist < nearHQdist) {
                nearHQidx = i;
                nearHQdist = dist;
            }
        }
        //if see enemy, shoot
        Team opponent = rc.getTeam().opponent();
        RobotInfo[] enemies = rc.senseNearbyRobots(rc.getType().visionRadiusSquared, opponent);
        if (enemies.length > 0){
            rc.setIndicatorString("I see an enemy!");
            for(RobotInfo bot : enemies){
                if(bot.type == RobotType.LAUNCHER){
                    if(rc.canAttack(bot.location)){
                        rc.attack(bot.location);
                    } else {
                        mooTwo(rc, bot.location);
                    }
                } else{
                    if(rc.canAttack(bot.location)){
                        rc.attack(bot.location);
                    } else {
                        mooTwo(rc, bot.location);
                    }
                }
            }
        }
        mooTwo(rc, enemyHQs[nearHQidx]);
        /*rc.setIndicatorString("Moving to center of map - " + centerOfMap.toString());
        if(me.equals(centerOfMap) || me.isAdjacentTo(centerOfMap)){
            mooTwo(rc, enemyHQs[nearHQidx]);
            rc.setIndicatorString("Moving to enemy HQ - " + enemyHQs[nearHQidx]);
        }*/
    }


        //Otherwise, move towards the middle of the map
        //then, move towards enemy HQ if nothing 

    public static MapLocation findEnemyHQ(MapLocation map, int width, int height){
        int x = map.x;
        int y = map.y;
        int halfwidth = (int)Math.round(width*.5);
        int halfheight = (int)Math.round(height*.5);

        x = x - halfwidth;
        y = y - halfheight;
        x = x * -1;
        y = y * -1;
        x = x + halfwidth;
        y = y + halfheight;
        return new MapLocation(x,y);

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

    public static void mooTwo(RobotController rc, MapLocation loc) throws GameActionException {
        Direction dir = rc.getLocation().directionTo(loc);
        Direction secDir = dirSecDir(rc.getLocation(), loc);
        if (rc.canMove(dir)) {
            rc.move(dir);
        } else if (rc.canMove(secDir)) {
            rc.move(secDir);
        } else if (dir.rotateLeft() == secDir) {
        	if (rc.canMove(dir.rotateRight())) {
                rc.move(dir.rotateRight());
        	} else if (rc.canMove(dir.rotateLeft())) {
        		rc.move(dir.rotateLeft());
        	} else if (rc.canMove(dir.rotateRight().rotateRight())) {
                rc.move(dir.rotateRight().rotateRight());
        	} else if (rc.canMove(dir.rotateLeft().rotateLeft())) {
        		rc.move(dir.rotateLeft().rotateLeft());
        	} else if (rc.canMove(dir.rotateRight().rotateRight().rotateRight())) {
                rc.move(dir.rotateRight().rotateRight().rotateRight());
        	} else if (rc.canMove(dir.rotateLeft().rotateLeft().rotateLeft())) {
        		rc.move(dir.rotateLeft().rotateLeft().rotateLeft());
        	}
        } else if (rc.canMove(dir.rotateRight())) {
    		rc.move(dir.rotateRight());
    	} else if (rc.canMove(dir.rotateLeft().rotateLeft())) {
            rc.move(dir.rotateLeft().rotateLeft());
    	} else if (rc.canMove(dir.rotateRight().rotateRight())) {
    		rc.move(dir.rotateRight().rotateRight());
    	} else if (rc.canMove(dir.rotateLeft().rotateLeft().rotateLeft())) {
            rc.move(dir.rotateLeft().rotateLeft().rotateLeft());
    	} else if (rc.canMove(dir.rotateRight().rotateRight().rotateRight())) {
    		rc.move(dir.rotateRight().rotateRight().rotateRight());
    	}
    }
    
    public static void flee(RobotController rc, MapLocation loc) throws GameActionException {
        Direction dir = rc.getLocation().directionTo(loc).opposite();
        Direction secDir = dirSecDir(rc.getLocation(), loc).opposite();
        if (rc.canMove(dir)) {
            rc.move(dir);
        } else if (rc.canMove(secDir)) {
            rc.move(secDir);
        } else if (dir.rotateLeft() == secDir) {
        	if (rc.canMove(dir.rotateRight())) {
                rc.move(dir.rotateRight());
        	} else if (rc.canMove(dir.rotateLeft())) {
        		rc.move(dir.rotateLeft());
        	} else if (rc.canMove(dir.rotateRight().rotateRight())) {
                rc.move(dir.rotateRight().rotateRight());
        	} else if (rc.canMove(dir.rotateLeft().rotateLeft())) {
        		rc.move(dir.rotateLeft().rotateLeft());
        	} else if (rc.canMove(dir.rotateRight().rotateRight().rotateRight())) {
                rc.move(dir.rotateRight().rotateRight().rotateRight());
        	} else if (rc.canMove(dir.rotateLeft().rotateLeft().rotateLeft())) {
        		rc.move(dir.rotateLeft().rotateLeft().rotateLeft());
        	}
        } else if (rc.canMove(dir.rotateRight())) {
    		rc.move(dir.rotateRight());
    	} else if (rc.canMove(dir.rotateLeft().rotateLeft())) {
            rc.move(dir.rotateLeft().rotateLeft());
    	} else if (rc.canMove(dir.rotateRight().rotateRight())) {
    		rc.move(dir.rotateRight().rotateRight());
    	} else if (rc.canMove(dir.rotateLeft().rotateLeft().rotateLeft())) {
            rc.move(dir.rotateLeft().rotateLeft().rotateLeft());
    	} else if (rc.canMove(dir.rotateRight().rotateRight().rotateRight())) {
    		rc.move(dir.rotateRight().rotateRight().rotateRight());
    	}
    }
} 