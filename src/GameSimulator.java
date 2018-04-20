import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
//TODO make constructing buildings more accurate
//TODO increase accuracy of mineral mining eg. first second third probe
//TODO make method that assigns probes to mineral patches and assimilators to gas geysers
//TODO FURTHURMORE update buildBuilding method with method that assigns/deassigns probes
public class GameSimulator {
  private int currentSupply;
  private int maxSupply;
  private int currentMinerals;
  private int currentGas;
  private boolean goalComplete;
  private BuildQueue buildQueue;
  //Max game time in minutes
  private static int maxTime = 20;
  private final int SECONDS_IN_MINUTES = 60;
  //Ticks per second
  private final int TICKRATE = 1;
  private final int STANDARD_MINERALS_PER_MIN = 41;
  private final int THIRD_PROBE_MINERALS_PER_MIN = 20;

  public static List<Unit> unitList = new ArrayList<>();
  public static List<String> unitNameList = new ArrayList<>();
  public static List<Building> buildingList = new ArrayList<>();
  public static List<String> buildingNameList = new ArrayList<>();
  public static HashMap<String, Building> buildingNameToBuilding = new HashMap<>();
  public static HashMap<String, Unit> unitNameToUnit = new HashMap<>();
  //public static List<buildingToBuildQueue> buildingsWithQueues= new ArrayList<>();
  //This maps each building to its respective buildqueue
  //public HashMap<Building, BuildQueue> buildingToBuildQueue = new HashMap<>();
  //For the moment we will say that the zealot can still be working at a building whilst building another building
  public HashMap<Building, Integer> zealotsWorkingOnBuilding;
  private static HashMap<String,Integer> goalUnits;
  public List<Building> activeBuildingList = new ArrayList<>();
  //to decide on whether list or hashmap is better for availableProbes
  public HashMap<Unit,Integer> numberOfActiveUnits = new HashMap<>();
  public HashMap<Building,Integer> numberOfActiveBuildings = new HashMap<>();
  public HashMap<String,Integer> numberOfProbesAtLocation = new HashMap<>();

  public GameSimulator() {
    startGame();
    buildBuilding(buildingNameToBuilding.get("gateway"));
    System.out.println("Number of probes: "+numberOfActiveUnits.get(unitNameToUnit.get("probe")));
  }

  public void gameClock() {
    while (maxTime > 0) {
      if () {
        //Check all possible moves (methods)
        buildBuilding();
        buildUnit();
        getShortestBuildQueue();
        if () {
          //If possible move (method) meets a goal, print out the timeStamp at which it reached that goal
          getTimeStamp();
        }
      } else {
        maxTime--;
      }
    }
  }

  public String getTimeStamp() {
    int timeInSeconds = maxTime * SECONDS_IN_MINUTES;
    int mins = timeInSeconds / 60;
    int secs = timeInSeconds % 60;

    return ( mins < 10 ? "0" + mins : mins ) + ":" + ( secs < 10 ? "0" + secs : secs);
  }

  /**
   * 1 - Checks if there is sufficient resources to build
   * 2 - Checks if it depends on another building(s) to build
   * 3 - Checks if there is an available probe to initiate building, if not, it would deassign from mineral gathering
   * to start construction then reassign the probe back to mining the mineral
   * 4 - Checks and gets the build time. Will add the building to numberOfActiveBuildings once timer has elapsed
   * Adds a building to a
   * @param buildingToBeBuilt
   * @return
   */
  public boolean buildBuilding(Building buildingToBeBuilt) {
    String buildingName = buildingToBeBuilt.toString();
    int numOfBuilding = numberOfActiveBuildings.get(buildingToBeBuilt);
    boolean hasResources = currentGas >= buildingToBeBuilt.getGasCost() && currentMinerals >= buildingToBeBuilt.getMineralCost();
    boolean hasNeededBuildings = activeBuildingList.contains(buildingToBeBuilt.getDependentOn());
    boolean hasAvailableProbes = numberOfActiveUnits.get(unitNameToUnit.get("probe")) > 0;
    long buildTime = buildingToBeBuilt.getBuildTime();
    if(!(hasResources && hasNeededBuildings && hasAvailableProbes)) {
      return false;
    } else {
      try {
        //Thread.sleep(buildTime) should act as a delay so that the building is added to the activeBuildings once it has finished "sleeping"
        Thread.sleep(buildTime * 1000);
        currentGas -= currentGas - buildingToBeBuilt.getGasCost();
        currentMinerals -= currentMinerals - buildingToBeBuilt.getMineralCost();
        numberOfActiveBuildings.merge(buildingToBeBuilt, 1, (a, b) -> a + b);
        buildingToBeBuilt.createNewBuildQueue(this);
        System.out.println("Total number of " + numberOfActiveBuildings.get(buildingNameToBuilding.get(buildingName)) + ": " + numOfBuilding);
        maxTime--;
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    return true;
  }

  /**
   * Adds the unit that needs to be built to a build queue if it can be according to 3 conditions:
   * 1 - The buildings are there and so are the resources so the unit can be immediately added to a build queue
   * 2 - The buildings are there but the resources are not, so the program will keep trying this method until it has the necessary resources
   * 3 - The buildings are not there so no amount of waiting will mean the unit can be built and the method returns false
   * @param unitToBeBuilt
   * @return True if the unit has been built, false otherwise
   */
  public boolean buildUnit(Unit unitToBeBuilt) {
    //Indicates if the user has the resources to build the unit
    boolean hasResources = currentGas >= unitToBeBuilt.getGasCost() && currentMinerals >= unitToBeBuilt.getMineralCost() && maxSupply >= currentSupply+(unitToBeBuilt.getSupplyNeeded());
    //Indicates if the buildings exist that are required to build the unit gets dependant on strings and then uses strings to get buildings
    boolean buildingsExist = numberOfActiveBuildings.containsKey(buildingNameToBuilding.get(unitToBeBuilt.getDependentOn()));
    //Indicates if the buildings are able to build the unit
    boolean buildingsAbleToBuild = false;
    if (!(buildingsExist && hasResources)) {
     return false;
    } else {
      currentGas =- unitToBeBuilt.getGasCost();
      currentMinerals =- unitToBeBuilt.getMineralCost();
      currentSupply =+ unitToBeBuilt.getSupplyNeeded();
      getShortestBuildQueue(buildingNameToBuilding.get(unitToBeBuilt.getDependentOn())).addUnitToBuildQueue(unitToBeBuilt);
      maxTime--;
    }
    return true;
  }

  private BuildQueue getShortestBuildQueue(Building building) {
    BuildQueue shortestBuildQueue = building.buildQueues.get(0);
    int shortestQueueUnits = Integer.MAX_VALUE;
    for (int i = 0; i < numberOfActiveBuildings.get(building); i++) {
      if (building.buildQueues.get(i).getNumberOfUnitsInQueue() < shortestQueueUnits) {
        shortestBuildQueue = building.buildQueues.get(i);
      }
    }
    maxTime--;
    return shortestBuildQueue;
  }

  private void updateAllResources() {
    currentMinerals =+ (int) Math.round(calculateMineralsInPerTick());
  }

  private double calculateMineralsInPerTick() {
    double mineralsIn;
    int numberOfMineralProbes = numberOfProbesAtLocation.get("minerals");
    if (numberOfMineralProbes <= 16) {
      mineralsIn = 41D*numberOfMineralProbes;
    } else {
      mineralsIn = (41D*16)+(20D*(numberOfMineralProbes-16));
    }
    return mineralsIn/(60*TICKRATE);
  }

  private double calculateGasInPerTick() {
    double gasIn;
    int numberOfGasProbes = numberOfProbesAtLocation.get("gas");
    gasIn = 38D*numberOfGasProbes;
    return gasIn/(60*TICKRATE);
  }

  private void updateSupply(int supplyToAdd) {
    currentSupply =+ supplyToAdd;
  }

  public static void setGoalUnits(HashMap<String,Integer> goalUnits) {
    GameSimulator.goalUnits = goalUnits;
  }

  public static void addToUnitList(Unit unitToAdd) {
    unitList.add(unitToAdd);
  }

  public static void addToBuildingList(Building buildingToAdd) {
    buildingList.add(buildingToAdd);
  }
  /**
   * Added some starting values for nexus, probes and the number of minerals and gas
   * goalComplete initially set to false
   */
  private void startGame() {
    numberOfActiveBuildings.put(buildingNameToBuilding.get("nexus"),1);
    numberOfActiveUnits.put(unitNameToUnit.get("probe"),6);
    currentMinerals = 50;
    currentGas = 0;
    goalComplete = false;
    numberOfProbesAtLocation.put("minerals",6);
    numberOfProbesAtLocation.put("gas",0);
    numberOfProbesAtLocation.put("building",0);
  }

  private class buildingToBuildQueue {
    String buildingType;
    BuildQueue buildQueue;

    public buildingToBuildQueue(String buildingType, BuildQueue buildQueue) {
      this.buildingType = buildingType;
      this.buildQueue = buildQueue;
    }
  }

  private class unitToBuildQueue {
    String unitType;
    BuildQueue buildQueue;

    public unitToBuildQueue(String unitType, BuildQueue buildQueue) {
      this.unitType = unitType;
      this.buildQueue = buildQueue;
    }
  }
}
