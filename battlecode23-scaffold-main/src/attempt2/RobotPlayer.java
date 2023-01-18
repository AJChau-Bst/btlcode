package attempt2;

import battlecode.common.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;
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
        int width = rc.getMapWidth();
        int height = rc.getMapHeight();
        int centerWidth = Math.round(width/2);
        int centerHeight = Math.round(height/2);
        MapLocation centerOfMap = new MapLocation(centerWidth, centerHeight);
        Team friendly = rc.getTeam();

        
        //Find Nearby Wells
        RobotInfo[] enemies = rc.senseNearbyRobots(rc.getType().visionRadiusSquared, friendly.opponent());
        ArrayList<AdvMapLoc> spawnLocs = new ArrayList<AdvMapLoc>();
        if(enemies.length > 0) {
        	spawnLocs = locationsAround(rc, me, centerOfMap, rc.getType().actionRadiusSquared);
        	Collections.reverse(spawnLocs);
        } else {
        	spawnLocs = locationsAround(rc, me, centerOfMap, rc.getType().actionRadiusSquared);
        }
        
      //Spawn Launcher Code
        //ArrayList<AdvMapLoc> launcherSpawnLocs = locationsAround(rc, me, centerOfMap, rc.getType().actionRadiusSquared);
        if(rc.getResourceAmount(ResourceType.MANA) >= 100 && rng.nextInt(2000) > turnCount) {
        	for (AdvMapLoc advLoc : spawnLocs) {
        		if (rc.canBuildRobot(RobotType.LAUNCHER, advLoc.loc)){
                    rc.buildRobot(RobotType.LAUNCHER, advLoc.loc);
                    break;
                }
        	}
        }
        
        //Direction targetAdWell = rc.getLocation().directionTo(nearestAdWell);
        //Build Carriers, if we can build carriers -- NEED TO CHANGE THIS LOGIC/ADD AND CONDITION
        //Get Array of Nearby Robots, Count the NUmber of Carriers
        int carrierCounter = 0;
        RobotInfo[] friends = rc.senseNearbyRobots(rc.getType().visionRadiusSquared, friendly);
        if(friends.length > 0) {
            for(RobotInfo bot : friends){
                if (bot.type == RobotType.CARRIER){
                	if (bot.getTotalAnchors() == 0) {
                        carrierCounter++;
                	}
                }
            }
        }
        ///ADJUST NUMBER OF CARRIERS HERE///
        double mapSizeAdjust = Math.sqrt(rc.getMapWidth()*rc.getMapHeight());
        if(carrierCounter <= mapSizeAdjust){
            //System.out.println("Printing Carrier");
            //System.out.println("Printing Carrier, + " + carrierCounter);
        	for (AdvMapLoc advLoc : spawnLocs) {
        		if(rc.canBuildRobot(RobotType.CARRIER, advLoc.loc)){
                    rc.buildRobot(RobotType.CARRIER, advLoc.loc);
                    rc.setIndicatorString("Building a carrier!");
                    break;
                }
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
        MapLocation hqOne = buttToDec(rc.readSharedArray(lookingForIndex), width, height);
        MapLocation hqTwo = buttToDec(rc.readSharedArray(lookingForIndex-1), width, height);
        MapLocation hqThree = buttToDec(rc.readSharedArray(lookingForIndex-2), width, height);
        MapLocation hqFour = buttToDec(rc.readSharedArray(lookingForIndex-3), width, height);
        rc.setIndicatorString(hqOne.x + " " + hqOne.y + " " + hqTwo.x + " " + hqTwo.y + " " + hqThree.x + " " + hqThree.y + " " + hqFour.x + " " + hqFour.y + " ");

        //When threshold for resources, create an anchor
        if (rc.senseRobotAtLocation(me).getTotalAnchors() == 0) {
        	if (rc.getResourceAmount(ResourceType.ELIXIR) >= 300) {
        		if (rc.canBuildAnchor(Anchor.ACCELERATING)) {
        			rc.buildAnchor(Anchor.ACCELERATING);
        			rc.setIndicatorString("Building an achor!");
        		}
        	} else if (rc.getResourceAmount(ResourceType.ADAMANTIUM) >= 190 && rc.getResourceAmount(ResourceType.MANA) >= 100) {
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
        int width = rc.getMapWidth();
        int height = rc.getMapHeight();
        MapLocation me = rc.getLocation();
        int desiredResourceAmount = 40;
    	boolean hqSpotted = false;
    	ArrayList<MapLocation> edges = senseMapEdges(rc, width, height);
        
        //If Robot is full, go to the closest HQ to deposit.
        int elAmt = rc.getResourceAmount(ResourceType.ELIXIR);
        int adAmt = rc.getResourceAmount(ResourceType.ADAMANTIUM);
        int maAmt = rc.getResourceAmount(ResourceType.MANA);
        int anchors = rc.getNumAnchors(Anchor.STANDARD);
        int accAnchors = rc.getNumAnchors(Anchor.ACCELERATING);
        int total = elAmt + adAmt + maAmt;
        int weight = elAmt + adAmt + maAmt + anchors*40 + accAnchors*40;
        
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
                if (bot.type != RobotType.CARRIER && bot.type != RobotType.AMPLIFIER && bot.type != RobotType.HEADQUARTERS){
                	MapLocation botLoc = bot.getLocation(); 
                	if (me.distanceSquaredTo(botLoc) < me.distanceSquaredTo(nearestEnemy)) {
                		nearestEnemy = botLoc;
                	}
                } else if (rc.getHealth() < rc.getType().getMaxHealth() && bot.type != RobotType.HEADQUARTERS) {
                	MapLocation botLoc = bot.getLocation(); 
                	if (me.distanceSquaredTo(botLoc) < me.distanceSquaredTo(nearestEnemy)) {
                		nearestEnemy = botLoc;
                	}
                }
        	}
        	if (rc.onTheMap(nearestEnemy)) {
        		if (total >= 5) {
        			if(rc.canAttack(nearestEnemy)) {
        				rc.attack(nearestEnemy);
                		rc.setIndicatorString("Projectile poop!");
        			} else {
                		rc.setIndicatorString("AHHHHHHHHHHHHHHHHHHHHH");
        			}
        		}
        		flee(rc, nearestEnemy);
        	}
        }
        
        //Approximate nearest HQ
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
        
        if (rc.getAnchor() != null) {
        	
    		//Scan for visible islands, determine which is closest, and move to it
    		int[] nearbyIslands = rc.senseNearbyIslands();
    		if (nearbyIslands.length > 0) {
    			ArrayList<Integer> plantableIslands = new ArrayList<Integer>();
        		for (int i : nearbyIslands) {
        			if (rc.senseTeamOccupyingIsland(i) != rc.getTeam()) {
        				plantableIslands.add(i);
        			}
        		}
        		if (plantableIslands.size() > 0) {
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
            		//If carrying an anchor and standing on a valid island, plant the anchor
            		if(rc.canPlaceAnchor() == true) {
                		int islandHere = rc.senseIsland(nearestIsland);
                		int islandOn = rc.senseIsland(me);
                		if (islandHere == islandOn) {
                			rc.placeAnchor();
                    		rc.setIndicatorString("Planting an anchor! " + islandHere);
                		}
                    	//Spread out, avoid other bots with anchors
                	} else if (rc.onTheMap(nearestIsland)) {
                		mooTwo(rc, nearestIsland);
                		rc.setIndicatorString("Heading to an island!" + nearestIsland.x + " " + nearestIsland.y);
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
                        	for (MapLocation aLoc : edges) {
                        		if (me.distanceSquaredTo(aLoc) < me.distanceSquaredTo(nearestAnchorBot)){
                                	nearestAnchorBot = aLoc;
                                }
                        	}
                        	if (rc.onTheMap(nearestAnchorBot)) {
                        		flee(rc, nearestAnchorBot);
                        	}
                        }
            		}
        		}
        	}
        }
        //Find Wells
        WellInfo[] nearWell = rc.senseNearbyWells();
        
        //if adjacent to well, start fucking collecting
        boolean collecting = false;
        boolean collectHere = true;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                MapLocation wellLocation = new MapLocation(me.x + dx, me.y + dy);
                if (rc.canCollectResource(wellLocation, -1)){
                	if (hqSpotted) {
            			if(rc.canSenseLocation(preciseTarget)) {
            				if (rc.senseRobotAtLocation(preciseTarget).getResourceAmount(ResourceType.ADAMANTIUM) > 190) {
                				if (rc.senseWell(wellLocation).getResourceType() == ResourceType.ADAMANTIUM) {
                					collectHere = false;
                				}
                			}
            			}
            		}
                	if (collectHere) {
                        rc.collectResource(wellLocation, -1);
                        rc.setIndicatorString("Collecting, now have, AD:" + 
                        rc.getResourceAmount(ResourceType.ADAMANTIUM) + 
                        " MN: " + rc.getResourceAmount(ResourceType.MANA) + 
                        " EX: " + rc.getResourceAmount(ResourceType.ELIXIR));
                        collecting = true;
                	}
                }
            }
        }

        //if wells nearby and not crowded, move to them
        if(nearWell.length >= 1 && weight < desiredResourceAmount && !collecting){
    		MapLocation desiredWell = new MapLocation(61,61);
    		int bestWellScore = 200000;
        	for (WellInfo aWell : nearWell) {
        		MapLocation wellLoc = aWell.getMapLocation();
        		RobotInfo[] atWell = rc.senseNearbyRobots(wellLoc, 4, friendly);
        		int crowdSize = atWell.length;
        		int carrierCount = 0;
        		for (RobotInfo aBot : atWell) {
        			if (aBot.getType() == RobotType.CARRIER) {
        				carrierCount++;
        			}
        		}
        		int wellScore = ((wellLoc.distanceSquaredTo(hqs[nearHQidx]) + me.distanceSquaredTo(wellLoc)) * Math.max((int) Math.floor(rc.getRoundNum()/1000) + 1, (int) Math.floor((crowdSize / 2))) ) ;
        		if (carrierCount > 8) {
        			if (aWell.getResourceType() == ResourceType.ADAMANTIUM) {
        				wellScore = Math.round(wellScore*2);
        			}
        		}
        		if (hqSpotted) {
        			if(rc.canSenseLocation(preciseTarget)) {
        				if (rc.senseRobotAtLocation(preciseTarget).getResourceAmount(ResourceType.ADAMANTIUM) > 190) {
            				wellScore = 200001;
            			}
        			}
        		}
        		if (wellScore < bestWellScore) {
        			desiredWell = wellLoc;
        			bestWellScore = wellScore;
        		}
        	}
        	if (rc.onTheMap(desiredWell)) {
    			mooTwo(rc, desiredWell);
    			rc.setIndicatorString("Moving to well!" + desiredWell.x + " " + desiredWell.y + " " + bestWellScore);
        	} else {
        		rc.setIndicatorString("Looking for another well!");
        		carrierDispersion(rc, edges);
        	}
        } else if (!collecting) {
        	rc.setIndicatorString("Looking for another well!");
        	carrierDispersion(rc, edges);
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
        int centerWidth = Math.round(width/2);
        int centerHeight = Math.round(height/2);
        MapLocation centerOfMap = new MapLocation(centerWidth, centerHeight);
        //Direction launcherDir = me.directionTo(centerOfMap);
        
        
        //Find friendly HQs
        MapLocation base = new MapLocation(61,61);
        int baseDist = 7201;
        boolean hqSpotted = false;
        Team friendly = rc.getTeam();
        int friendCount = 0; //oof
        RobotInfo[] friends = rc.senseNearbyRobots(rc.getType().visionRadiusSquared, friendly);
        if(friends.length > 0) {
        	for(RobotInfo bot : friends){
                if (bot.type == RobotType.HEADQUARTERS){
                    base = bot.getLocation(); 
                    baseDist = me.distanceSquaredTo(base);
                	hqSpotted = true;
                } else if (bot.type == RobotType.LAUNCHER) { //Count friendly army
                	friendCount++;
                } else if (bot.type == RobotType.BOOSTER) {
                	friendCount+=3;
                } else if (bot.type == RobotType.DESTABILIZER) {
                	friendCount+=4;
                }
        	}
        }

        int wallSize = 0;
        for (Direction dir : directions) {
        	if (!rc.canMove(dir)) {
        		wallSize++;
        	}
        }
        
        //Scan for enemy HQ crowding
        MapLocation enemyHQ = new MapLocation(61,61);
        Team enemy = rc.getTeam().opponent();
        int crowd = 0;
        RobotInfo[] enemies = rc.senseNearbyRobots(rc.getType().visionRadiusSquared, enemy);
        if(enemies.length > 0) {
        	for(RobotInfo bot : enemies){
                if (bot.type == RobotType.HEADQUARTERS){
                	enemyHQ = bot.getLocation(); 
                }
        	}
    		crowd = rc.senseNearbyRobots(enemyHQ, 4, friendly).length;
        }
        
        //Friendly and enemy HQ location calculation
        int nearHQidx = 0;
        int nearHQdist = 7201;
        int dist = 7202;
        int nearEnemyHQidx = 0;
        int farEnemyHQidx = 0;
        int nearEnemyHQdist = 7201;
        int distEnemy = 7202;
        MapLocation[] hqs = new MapLocation[4];
        MapLocation[] enemyHQs = new MapLocation[4];
        for(int i = 0; i < 4; ++i) {
        	hqs[i] = buttToDec(rc.readSharedArray(63-i), width, height);
        	dist = me.distanceSquaredTo(hqs[i]);
            if(dist < nearHQdist) {
            	nearHQidx = i;
            	nearHQdist = dist;
            }
            enemyHQs[i] = findSymmetric(hqs[i], width, height);
            distEnemy = me.distanceSquaredTo(enemyHQs[i]);
            if(distEnemy < nearEnemyHQdist) {
            	farEnemyHQidx = nearEnemyHQidx;
            	nearEnemyHQidx = i;
            	nearEnemyHQdist = distEnemy;
            }
        }
        
        //Find nearest well (for avoidance)
        WellInfo[] nearWell = rc.senseNearbyWells(4);
        int wellDist = 7201;
        MapLocation nearestWell = new MapLocation(-1,-1);
        for (WellInfo aWell : nearWell) {
        	if (me.distanceSquaredTo(aWell.getMapLocation()) < wellDist) {
        		wellDist = me.distanceSquaredTo(aWell.getMapLocation());
        		nearestWell = aWell.getMapLocation();
        	}
        }
        
        //if see enemy, shoot
        ArrayList<ScoredBot> scoredEnemies = new ArrayList<ScoredBot>();
        boolean attacking = false;
        if (enemies.length > 0){
            for(RobotInfo bot : enemies) {
                if(bot.type == RobotType.DESTABILIZER){
                	int botDist = me.distanceSquaredTo(bot.location);
                	scoredEnemies.add(new ScoredBot(bot, botDist));
                } else if(bot.type == RobotType.BOOSTER){
                	int botDist = me.distanceSquaredTo(bot.location);
                	scoredEnemies.add(new ScoredBot(bot, botDist * 2));
                } else if(bot.type == RobotType.LAUNCHER){
                	int botDist = me.distanceSquaredTo(bot.location);
                	scoredEnemies.add(new ScoredBot(bot, botDist * 3));
                } else if(bot.type == RobotType.HEADQUARTERS){
                	//haha no
                } else {
                	int botDist = me.distanceSquaredTo(bot.location);
                	scoredEnemies.add(new ScoredBot(bot, botDist * 4));
                }
            }
            scoredEnemies.sort(Comparator.comparing(ScoredBot::getScore));
            for (ScoredBot aBot : scoredEnemies) {
            	if(rc.canAttack(aBot.getLocation())){
                    rc.attack(aBot.getLocation());
                    flee(rc, aBot.getLocation());
                    attacking = true;
                    break;
                } 
            }
            if (!attacking) {
            	for (ScoredBot aBot : scoredEnemies) {
            		if (rc.canActLocation(aBot.getLocation())) {
                        flee(rc, aBot.getLocation());
                        break;
                    } else {
                    	//mooTwo(rc, aBot.getLocation());
                    }
            	}
            }
        }
        if (scoredEnemies.size() < 1) {
        	if (hqSpotted) {
            	if (me.isAdjacentTo(base)) {
            		mooTwo(rc, enemyHQs[nearEnemyHQidx]);
            	} else if(wallSize >= 3) {
            		mooTwo(rc, enemyHQs[nearEnemyHQidx]);
            	} else if(nearWell.length > 0) {
            		flee(rc, nearestWell);
            	} else if (friendCount > 5) {
            		if (turnCount < 100) {
            			mooTwo(rc, centerOfMap);
            			rc.setIndicatorString("Pressuring center! " + centerOfMap.x + " " + centerOfMap.y);
            		} else {
                        mooTwo(rc, enemyHQs[nearEnemyHQidx]);
                        rc.setIndicatorString("Marching to enemy HQ! " + enemyHQs[nearEnemyHQidx].x + " " + enemyHQs[nearEnemyHQidx].y);
            		}
            	}
            } else {
            	if (friendCount >= 2) {
            		if (crowd <= 2) {
            			if (turnCount < 100) {
                			mooTwo(rc, centerOfMap);
                			rc.setIndicatorString("Pressuring center! " + centerOfMap.x + " " + centerOfMap.y);
                		} else {
    	            		mooTwo(rc, enemyHQs[nearEnemyHQidx]);
    	            		rc.setIndicatorString("Marching to enemy HQ! " + enemyHQs[nearEnemyHQidx].x + " " + enemyHQs[nearEnemyHQidx].y);
                		}
            		} else if(nearEnemyHQidx == farEnemyHQidx) { 
            			mooTwo(rc, hqs[nearHQidx]);
                		rc.setIndicatorString("Returning to HQ! " + hqs[nearHQidx].x + " " + hqs[nearHQidx].y);
            		} else {
            			mooTwo(rc, enemyHQs[farEnemyHQidx]);
                		rc.setIndicatorString("Marching to new enemy HQ! " + enemyHQs[farEnemyHQidx].x + " " + enemyHQs[farEnemyHQidx].y);
            		}
            	} else {
            		mooTwo(rc, hqs[nearHQidx]);
            		rc.setIndicatorString("Returning to HQ! " + hqs[nearHQidx].x + " " + hqs[nearHQidx].y);
            	}
            }
        }
        if (rc.getActionCooldownTurns() <= GameConstants.COOLDOWN_LIMIT) {
        	enemies = rc.senseNearbyRobots(rc.getType().visionRadiusSquared, enemy);
        	scoredEnemies = new ArrayList<ScoredBot>();
            attacking = false;
            if (enemies.length > 0){
                for(RobotInfo bot : enemies) {
                    if(bot.type == RobotType.DESTABILIZER){
                    	int botDist = me.distanceSquaredTo(bot.location);
                    	scoredEnemies.add(new ScoredBot(bot, botDist));
                    } else if(bot.type == RobotType.BOOSTER){
                    	int botDist = me.distanceSquaredTo(bot.location);
                    	scoredEnemies.add(new ScoredBot(bot, botDist * 2));
                    } else if(bot.type == RobotType.LAUNCHER){
                    	int botDist = me.distanceSquaredTo(bot.location);
                    	scoredEnemies.add(new ScoredBot(bot, botDist * 3));
                    } else if(bot.type == RobotType.HEADQUARTERS){
                    	//haha no
                    } else {
                    	int botDist = me.distanceSquaredTo(bot.location);
                    	scoredEnemies.add(new ScoredBot(bot, botDist * 4));
                    }
                }
                scoredEnemies.sort(Comparator.comparing(ScoredBot::getScore));
                for (ScoredBot aBot : scoredEnemies) {
                	if(rc.canAttack(aBot.getLocation())){
                        rc.attack(aBot.getLocation());
                        flee(rc, aBot.getLocation());
                        attacking = true;
                        break;
                    } 
                }
                if (!attacking) {
                	for (ScoredBot aBot : scoredEnemies) {
                		if (rc.canActLocation(aBot.getLocation())) {
                            flee(rc, aBot.getLocation());
                            break;
                        } else {
                        	//mooTwo(rc, aBot.getLocation());
                        }
                	}
                }
            }
        }
        
        //stick together if doing nothing else
        if (rc.isMovementReady()) {
        	ArrayList<RobotInfo> fellows = new ArrayList<RobotInfo>();
            for (RobotInfo aBot : friends) {
            	if (aBot.getType() == RobotType.LAUNCHER && aBot.location.distanceSquaredTo(me) > 4) {
            		fellows.add(aBot);
            		if (fellows.size() > 5) {
            			break;
            		}
            	}
            }
            MapLocation nearestFriend = new MapLocation(-1,-1);
            int friendDist = 7201;
            if (fellows.size() > 0) {
            	for (RobotInfo aBot : fellows) {
            		dist = me.distanceSquaredTo(aBot.location);
            		if (dist < friendDist) {
            			friendDist = dist;
            			nearestFriend = aBot.location;
            		}
            	}
            }
            if (rc.onTheMap(nearestFriend)) {
            	mooTwo(rc, nearestFriend);
            }
        }
        
        
        /*rc.setIndicatorString("Moving to center of map - " + centerOfMap.toString());
        if(me.equals(centerOfMap) || me.isAdjacentTo(centerOfMap)){
            mooTwo(rc, enemyHQs[nearHQidx]);
            rc.setIndicatorString("Moving to enemy HQ - " + enemyHQs[nearHQidx]);
        }*/
    }

    public static MapLocation findSymmetric(MapLocation map, int width, int height){
        int x = map.x;
        int y = map.y;
        float halfwidth = width/2;
        float halfheight = height/2;

        float tx = x - halfwidth;
        float ty = y - halfheight;
        tx = tx * -1;
        ty = ty * -1;
        x = Math.round(tx + halfwidth);
        y = Math.round(ty + halfheight);
        return new MapLocation(x,y);

    }
    public static int decToButt(MapLocation loc, int width, int height){
        int x = loc.x;
        int y = loc.y;
        float buttWidth = width/9;
        float buttHeight = height/9;
        int ten = ((int) Math.floor(x / buttWidth)) * 10;
        int one = ((int) Math.floor(y / buttHeight));
        return ten + one + 100;
    }

    public static MapLocation buttToDec(int buttNum, int width, int height){
        float buttWidth = width/9;
        float buttHeight = height/9;
        buttNum = buttNum - 100;
        float dx = (buttNum * buttWidth) / 10;
        int x = (int) Math.floor(dx);
        int y = (int) Math.floor((buttNum % 10) * buttHeight);
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
        if (dir == Direction.CENTER) {
        	int width = rc.getMapWidth();
            int height = rc.getMapHeight();
        	int centerWidth = Math.round(width/2);
            int centerHeight = Math.round(height/2);
            MapLocation centerOfMap = new MapLocation(centerWidth, centerHeight);
        	dir = rc.getLocation().directionTo(centerOfMap);
        }
        Direction secDir = dirSecDir(rc.getLocation(), loc);
        scoot(rc, dir, secDir);
    }
    
    public static void flee(RobotController rc, MapLocation loc) throws GameActionException {
        Direction dir = rc.getLocation().directionTo(loc).opposite();
        if (dir == Direction.CENTER) {
        	int width = rc.getMapWidth();
            int height = rc.getMapHeight();
        	int centerWidth = Math.round(width/2);
            int centerHeight = Math.round(height/2);
            MapLocation centerOfMap = new MapLocation(centerWidth, centerHeight);
        	dir = rc.getLocation().directionTo(centerOfMap);
        }
        Direction secDir = dirSecDir(rc.getLocation(), loc).opposite();
        scoot(rc, dir, secDir);
    }
    
    public static void scoot(RobotController rc, Direction dir, Direction secDir) throws GameActionException {
    	if (rc.canMove(dir)) {
            rc.move(dir);
        } else if (rc.canMove(secDir)) {
            rc.move(secDir);
        } else if (dir.rotateLeft() == secDir) {
        	if (rc.canMove(dir.rotateRight())) {
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
    }
    
    public static ArrayList<AdvMapLoc> locationsAround(RobotController rc, MapLocation me, MapLocation loc, int radiusSquared) throws GameActionException {
    	MapLocation[] visibleLoc = 	rc.getAllLocationsWithinRadiusSquared(me, radiusSquared);
    	ArrayList<AdvMapLoc> scoredLocations = new ArrayList<AdvMapLoc>();
    	for (MapLocation aLoc : visibleLoc) {
    		int dist = aLoc.distanceSquaredTo(loc);
    		if (rc.senseMapInfo(aLoc).isPassable() && !rc.canSenseRobotAtLocation(aLoc)) {
        		scoredLocations.add(new AdvMapLoc(aLoc, dist));
    		}
    	}
    	scoredLocations.sort(Comparator.comparing(AdvMapLoc::getDist));
    	return scoredLocations;
    }
    
    public static ArrayList<MapLocation> senseMapEdges(RobotController rc, int width, int height) {
    	MapLocation me = rc.getLocation();
    	int x = me.x;
    	int y = me.y;
    	MapLocation north = new MapLocation(x, height - 1);
    	MapLocation south = new MapLocation(x, 0);
    	MapLocation east = new MapLocation(width - 1, y);
    	MapLocation west = new MapLocation(0, y);
    	ArrayList<MapLocation> locs = new ArrayList<MapLocation>();
    	if (rc.canSenseLocation(north)) {
    		locs.add(north);
    	}
    	if (rc.canSenseLocation(south)) {
    		locs.add(south);
    	}
    	if (rc.canSenseLocation(east)) {
    		locs.add(east);
    	}
    	if (rc.canSenseLocation(west)) {
    		locs.add(west);
    	}
    	return locs;
    }
    
    public static void carrierDispersion(RobotController rc, ArrayList<MapLocation> edges) throws GameActionException {
    	MapLocation me = rc.getLocation();
    	RobotInfo[] nearbyBots = rc.senseNearbyRobots(rc.getType().visionRadiusSquared);
    	MapLocation nearestBot = new MapLocation(61,61);
    	for (RobotInfo aBot : nearbyBots) {
    		if (aBot.getType() == RobotType.CARRIER) {
        		if (me.distanceSquaredTo(aBot.getLocation()) < me.distanceSquaredTo(nearestBot)) {
        			nearestBot = aBot.getLocation();
        		}
    		}
    	}
    	for (MapLocation aLoc : edges) {
    		if (me.distanceSquaredTo(aLoc) < me.distanceSquaredTo(nearestBot)){
    			nearestBot = aLoc;
            }
    	}
    	if (rc.onTheMap(nearestBot)) {
    		flee(rc, nearestBot);
    	} else {
    		mooTwo(rc, new MapLocation(Math.round(rc.getMapWidth()/2),Math.round(rc.getMapHeight()/2)));
    	}
    }
} 

class AdvMapLoc {
	MapLocation loc;
	int dist;
	
	public AdvMapLoc(MapLocation loc, int dist) {
		this.loc = loc;
		this.dist = dist;
	}
	
	public int getDist() {
		return this.dist;
	}
}

class ScoredBot {
	RobotInfo info;
	int score;
	
	public ScoredBot(RobotInfo info, int score) {
		this.info = info;
		this.score = score;
	}
	
	public RobotInfo getInfo() {
		return this.info;
	}
	public int getScore() {
		return this.score;
	}
	public MapLocation getLocation() {
		return this.info.getLocation();
	}
}