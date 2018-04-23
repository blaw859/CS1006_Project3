import java.util.*;

//TODO make constructing buildings more accurate
//TODO increase accuracy of mineral mining eg. first second third probe
//TODO move probe to assimilator
public class GameSimulator {
  private int currentSupply;
  private int maxSupply;

  private int time;

  private double currentMinerals;
  private double currentGas;
  private boolean goalComplete;
  private BuildQueue buildQueue;
  //Ticks per second
  private final int TICKRATE = 1;
  private final int STANDARD_MINERALS_PER_MIN = 41;
  private final int THIRD_PROBE_MINERALS_PER_MIN = 20;
  public static List<Unit> unitList = new ArrayList<>();
  public static List<String> unitNameList = new ArrayList<>();
  public static List<Building> buildingList = new ArrayList<>();
  public static List<String> buildingNameList = new ArrayList<>();
  public static List<Building> buildingBeingConstructed = new ArrayList<>();
  public static List<Integer> buildingFinishTime = new ArrayList<>();
  public static List<Unit> unitBeingConstructed = new ArrayList<>();
  public static List<Integer> unitFinishTime = new ArrayList<>();
  public static HashMap<String, Building> buildingNameToBuilding = new HashMap<>();
  public static HashMap<String, Unit> unitNameToUnit = new HashMap<>();
  //public static List<buildingToBuildQueue> buildingsWithQueues= new ArrayList<>();
  //This maps each building to its respective buildqueue
  //public HashMap<Building, BuildQueue> buildingToBuildQueue = new HashMap<>();
  //For the moment we will say that the zealot can still be working at a building whilst building another building
  private static HashMap<Unit,Integer> goalUnits;
  public List<Building> activeBuildingList = new ArrayList<>();
  //to decide on whether list or hashmap is better for availableProbes
  public HashMap<Unit,Integer> numberOfActiveUnits = new HashMap<>();
  public HashMap<Building,Integer> numberOfActiveBuildings = new HashMap<>();
  public HashMap<String,Integer> numberOfProbesAtLocation = new HashMap<>();

  private void printUnits() {
    System.out.println("Units currently active:");
    for (int i = 0; i < unitList.size(); i++) {
      if (numberOfActiveUnits.get(unitList.get(i)) != null) {
        System.out.println(numberOfActiveUnits.get(unitList.get(i))+" "+ unitList.get(i).getType());
      }
    }
  }

  private void printBuildings() {
    if (activeBuildingList.size() > 0) {
      System.out.println("Buildings currently active:");
      for (int i = 0; i < activeBuildingList.size(); i++) {
        System.out.println(numberOfActiveBuildings.get(activeBuildingList.get(i))+" "+activeBuildingList.get(i).getType());
      }
    }
  }

  public GameSimulator(InstructionList instructions) {
    startGame();
    int maxLoops = 20;
    time = 0;
    boolean nextInstruction = false;


    while (!checkGoalUnitsBuilt() && time < maxLoops*60) {
      System.out.println("_______NEW TURN_______");
      //BuildQueue.printAllUnitsInBuildQueues();
      System.out.println(instructions.currentInstructionIndex);
      System.out.println("Current Elapsed Time: " + getTimeStamp());
      System.out.println("Current Mineral Supply: " + currentMinerals);
      System.out.println("Current Gas Supply: " + currentGas);
      printUnits();
      printBuildings();
      /*System.out.println("Active building list size: "+activeBuildingList.size());

      for (int k = 0; k < activeBuildingList.size(); k++) {
        System.out.println(activeBuildingList.get(k).getType());
      }*/
      if (instructions.getCurrentInstruction().method.getName().equals("constructUnit")) {
        System.out.println("Current instruction: Constructing "+instructions.getCurrentInstruction().unit.getType());
      } else if (instructions.getCurrentInstruction().method.getName().equals("constructBuilding")) {
        System.out.println("Current instruction: Constructing "+instructions.getCurrentInstruction().building.getType());
      }
      try {
        if (instructions.getCurrentInstruction().getArgType().equals("unit")) {
          boolean canMoveOn = (boolean) instructions.getCurrentInstruction().method.invoke(this, instructions.getCurrentInstruction().unit);
          System.out.println("Invocation returned: "+canMoveOn);
          if(canMoveOn) {
            instructions.moveToNextInstruction();
          } else {
            //Add to some kind of actually useful instruction list?
          }
        } else if (instructions.getCurrentInstruction().getArgType().equals("building")) {
          boolean canMoveOn = (boolean) instructions.getCurrentInstruction().method.invoke(this, instructions.getCurrentInstruction().building);
          System.out.println("Invocation returned: "+canMoveOn);
          if(canMoveOn) {
            instructions.moveToNextInstruction();
          } else {
            //Add to some kind of actually useful instruction list?
          }
        }
      } catch (Exception e) {
        //e.printStackTrace();
        System.out.println("Invocation Exception");
      }
      updateAllResources();
      time++;
    }
  }

  public String getTimeStamp() {
    int mins = time / 60;
    int secs = time % 60;
    return ( mins < 10 ? "0" + mins : mins ) + ":" + ( secs < 10 ? "0" + secs : secs);
  }

  /**
   * 1 - Checks if there is sufficient resources to build
   * 2 - Checks if it depends on another building(s) to build
   * 3 - Checks if there is an available probe to initiate building, if not, it would deassign from mineral gathering
   * to start construction then reassign the probe back to mining the mineral
   * 4 - Checks and gets the build time. Will add the building to numberOfActiveBuildings once timer has elapsed
   * Adds a building to a
   * @param buildingToBeConstructed
   * @return
   */
  public boolean constructBuilding(Building buildingToBeConstructed) {
    numberOfActiveBuildings.putIfAbsent(buildingToBeConstructed, 0);
    boolean hasResources = currentGas >= buildingToBeConstructed.getGasCost() && currentMinerals >= buildingToBeConstructed.getMineralCost();
    boolean hasNeededBuildings = activeBuildingList.contains(buildingToBeConstructed.getDependentOnBuildings()) || !(buildingToBeConstructed.getDependentOnString() == null) || !(buildingToBeConstructed.getDependentOnString().isEmpty());
    System.out.println("Has needed buildings = "+hasNeededBuildings);
    boolean hasAvailableProbes = numberOfActiveUnits.get(unitNameToUnit.get("probe")) > 0;
    System.out.println("Has available probes = "+hasAvailableProbes);

    if(buildingFinishTime.contains(time)) {
      addToActiveBuildingList(buildingToBeConstructed);
        if (numberOfActiveBuildings.get(buildingToBeConstructed) == null) {
            numberOfActiveBuildings.put(buildingToBeConstructed, 1);
        } else {
            numberOfActiveBuildings.put(buildingToBeConstructed, numberOfActiveBuildings.get(buildingToBeConstructed) + 1);
        }
    }


    if(!hasNeededBuildings) {
      return true;
    } else if (!(hasResources && hasAvailableProbes)) {
      return false;
    } else {
      if (buildingToBeConstructed.getType().equals("assimilator") && numberOfActiveBuildings.get(buildingNameToBuilding.get("assimilator")) >= 2) {
        return true;
      } else {
          buildingFinishTime.add(time + buildingToBeConstructed.getBuildTime());
          currentGas = currentGas - buildingToBeConstructed.getGasCost();
          currentMinerals = currentMinerals - buildingToBeConstructed.getMineralCost();
          buildingBeingConstructed.add(buildingToBeConstructed);
          createBuildQueue(buildingToBeConstructed);
      }
    }
    return true;
  }
//TODO make it so that a possible method is building an assimilator if gas is needed
  private void addToActiveBuildingList(Building building) {
    if (!activeBuildingList.contains(building)) {
      activeBuildingList.add(building);
    }
  }

  /**
   * Adds the unit that needs to be built to a build queue if it can be according to 3 conditions:
   * 1 - The buildings are there and so are the resources so the unit can be immediately added to a build queue
   * 2 - The buildings are there but the resources are not, so the program will keep trying this method until it has the necessary resources
   * 3 - The buildings are not there so no amount of waiting will mean the unit can be built and the method returns false
   * @param unitToBeConstructed
   * @return True if the unit has been built, false otherwise
   */
  public boolean constructUnit(Unit unitToBeConstructed) {
      numberOfActiveUnits.putIfAbsent(unitToBeConstructed,0);
    //Indicates if the user has the resources to build the unit
    boolean hasResources = currentGas >= unitToBeConstructed.getGasCost() && currentMinerals >= unitToBeConstructed.getMineralCost(); //&& maxSupply >= currentSupply+(unitToBeConstructed.getSupplyNeeded());
    //System.out.println("Has resources = "+hasResources);
    //Indicates if the buildings exist that are required to build the unit gets dependant on strings and then uses strings to get buildings
    boolean buildingsExist = (activeBuildingList.contains(unitToBeConstructed.getDependentOnBuilding()) || unitToBeConstructed.getDependentOnBuilding() == null) && activeBuildingList.contains(unitToBeConstructed.getBuiltFromBuilding());
    boolean buildingsAbleToBuild = false;
    if (unitFinishTime.contains(time)) {
        addToUnitList(unitToBeConstructed);
        if (numberOfActiveUnits.get(unitToBeConstructed) == null) {
            numberOfActiveUnits.put(unitToBeConstructed,1);
        } else {
            numberOfActiveUnits.put(unitToBeConstructed,numberOfActiveUnits.get(unitToBeConstructed)+1);
        }
    }
    if (!(hasResources)) {
      return false;
    } else if (!buildingsExist){
      return true;
    } else {
        unitFinishTime.add(time + unitToBeConstructed.getBuildTime());
        currentGas -= unitToBeConstructed.getGasCost();
        currentMinerals -= unitToBeConstructed.getMineralCost();
        currentSupply += unitToBeConstructed.getSupplyNeeded();
        //unitBeingConstructed.add(unitToBeConstructed);

      //getShortestBuildQueue(buildingNameToBuilding.get(unitToBeConstructed.getDependentOn())).addUnitToBuildQueue(unitToBeConstructed);
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
    return shortestBuildQueue;
  }

  private void updateAllResources() {
    //System.out.println(calculateMineralsInPerTick());
    currentMinerals = currentMinerals + calculateMineralsInPerTick();
    currentGas =+ calculateGasInPerTick();
    BuildQueue.updateAllBuildLists(this);
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

  private void createBuildQueue(Building building) {
    if (Building.buildingsWithBuildQueues.contains(building)) {
      building.buildQueues.add(new BuildQueue(5,this));
    }
  }

  private double calculateGasInPerTick() {
    double gasIn;
    int numberOfGasProbes = numberOfProbesAtLocation.get("gas");
    gasIn = 38D*numberOfGasProbes;
    return gasIn/(60*TICKRATE);
  }

  private boolean checkGoalUnitsBuilt() {
    List<Unit> goalUnitList = new ArrayList<>();
    goalUnitList.addAll(goalUnits.keySet());
    for (int i = 0; i < goalUnitList.size(); i++) {
      if (numberOfActiveUnits.get(goalUnitList.get(i)) != null) {
        if (numberOfActiveUnits.get(goalUnitList.get(i)) < goalUnits.get(goalUnitList.get(i))) {
          return false;
        }
      } else {
        return false;
      }
    }
    return true;
  }

  private void updateSupply(int supplyToAdd) {
    currentSupply =+ supplyToAdd;
  }

  public static void setGoalUnits(HashMap<Unit,Integer> goalUnits) {
    GameSimulator.goalUnits = goalUnits;
  }

  public static HashMap<Unit, Integer> getGoalUnits() { return GameSimulator.goalUnits; }

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

  public boolean moveProbeToGas() {
    if (numberOfActiveBuildings.get(buildingNameToBuilding.get("assimilator")) > 0) {
      if (numberOfProbesAtLocation.get("gas") > 0 && numberOfProbesAtLocation.get("gas") < 6) {
        numberOfProbesAtLocation.put("minerals", numberOfProbesAtLocation.get("minerals") - 1);
        numberOfProbesAtLocation.put("gas", numberOfProbesAtLocation.get("gas") + 1);
        return true;
      }
    }
    return false;
  }

  public List<Building> getActiveBuildingList() {
    return activeBuildingList;
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
