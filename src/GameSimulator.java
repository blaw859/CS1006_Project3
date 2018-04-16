import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class GameSimulator {
  private int currentSupply;
  private int maxSupply;
  private int currentMinerals;
  private int currentGas;
  private int probesOnGas;
  private boolean goalComplete;
  private BuildQueue buildQueue;

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
  public HashMap<Unit,Integer> numberOfActiveUnits;
  public HashMap<Building,Integer> numberOfActiveBuildings;

  public GameSimulator() {

  }

  /**
   * !Need to add a way that the program will take an amount of time to build the units and also will use a probe to do it
   * Adds a building to a
   * @param buildingToBeBuilt
   * @return
   */
  public boolean buildBuilding(Building buildingToBeBuilt, HashMap numberOfAvailableProbes) {
    boolean hasResources = currentGas >= buildingToBeBuilt.getGasCost() && currentMinerals >= buildingToBeBuilt.getMineralCost();
    boolean hasNeededBuildings = activeBuildingList.contains(buildingToBeBuilt.getDependentOn());
    boolean hasAvailableProbes = numberOfActiveUnits.get(unitNameToUnit.get("probe")) > 0;
    if(!(hasResources && hasNeededBuildings && hasAvailableProbes)) {
      return false;
    } else {
      numberOfActiveBuildings.merge(buildingToBeBuilt, 1, (a, b) -> a + b);
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
    //Indicates if the buildings exist that are required to build the unit
    boolean buildingsExist = numberOfActiveUnits.containsKey(unitToBeBuilt);
    //Indicates if the buildings are able to build the unit
    boolean buildingsAbleToBuild = false;
    if (!(buildingsExist && hasResources)) {
     return false;
    } else {

    }
    for (int i = 0; i < quantity; i++) {

    }
    return true;
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

    this.currentMinerals = 50;
    this.currentGas = 0;
    this.goalComplete = false;
    this.probesOnGas = 0;
  }

  private class buildingToBuildQueue {
    String buildingType;

    public buildingToBuildQueue(String buildingType, BuildQueue buildQueue) {
      this.buildingType = buildingType;
      this.buildQueue = buildQueue;
    }
  }

  private class unitToBuildQueue {
    String unitType;

    public unitToBuildQueue(String unitType, BuildQueue buildQueue) {
      this.unitType = unitType;
      this.buildQueue = buildQueue;
    }
  }
}
