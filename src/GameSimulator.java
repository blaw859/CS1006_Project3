import java.util.*;

//TODO make constructing buildings more accurate
//TODO increase accuracy of mineral mining eg. first second third probe
//TODO move probe to assimilator
public class GameSimulator {
  private int currentSupply;
  private int maxSupply;
  private int gasGeyserNumber = 2;
  private int mineralPatchNumber = 8;
  public int timeTakenToComplete = 100000;
  public boolean stopSimulation = false;
  private int time;
  public List<InstructionTime> instructionsToPrint = new ArrayList<>();
  private InstructionList instructionList;
  private double currentMinerals;
  private double currentGas;
  private final int TICKRATE = 1;
  public static List<Unit> unitList = new ArrayList<>();
  public static List<String> unitNameList = new ArrayList<>();
  public static List<Building> buildingList = new ArrayList<>();
  public static List<String> buildingNameList = new ArrayList<>();
  public static List<Integer> buildingFinishTime = new ArrayList<>();
  public static HashMap<String, Building> buildingNameToBuilding = new HashMap<>();
  public static HashMap<String, Unit> unitNameToUnit = new HashMap<>();
  public static ArrayList<Building> buildingBeingConstructed = new ArrayList<>();
  private int finalInstructionListLength = 0;
  private static HashMap<Unit,Integer> goalUnits;
  public List<Building> activeBuildingList = new ArrayList<>();
  //to decide on whether list or hashmap is better for availableProbes
  public HashMap<Unit,Integer> numberOfActiveUnits = new HashMap<>();
  public HashMap<Building,Integer> numberOfActiveBuildings = new HashMap<>();
  public HashMap<String,Integer> numberOfProbesAtLocation = new HashMap<>();


  public GameSimulator(InstructionList instructions) {
    startGame();
    this.instructionList = instructions;
    int maxLoops = 20;
    time = 0;
    boolean nextInstruction = false;
    while (!checkGoalUnitsBuilt() && time < maxLoops*600 && !stopSimulation) {
      updateAllResources();
      printStuff(instructions);
      try {
        if (instructions.getCurrentInstruction().getArgType().equals("unit")) {
          boolean canMoveOn = (boolean) instructions.getCurrentInstruction().method.invoke(this, instructions.getCurrentInstruction().unit);
          if(canMoveOn) {
            checkFinished();
            instructions.moveToNextInstruction();
          }
        } else if (instructions.getCurrentInstruction().getArgType().equals("building")) {
          boolean canMoveOn = (boolean) instructions.getCurrentInstruction().method.invoke(this, instructions.getCurrentInstruction().building);

          //System.out.println("Invocation returned: "+canMoveOn);
          if(canMoveOn) {
            checkFinished();
            instructions.moveToNextInstruction();
          } else {
            //Add to some kind of actually useful instruction list?
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
        System.out.println(instructions.getCurrentInstruction().method.getName());
        System.out.println(instructions.getCurrentInstruction().unit.getType());
        System.out.println("Invocation Exception");
      }
    }
    if (stopSimulation) {
      //System.out.println("Stop simulation called");
      BuildQueue.clearBuildQueues();
      timeTakenToComplete = time;
    }
  }

  public String getTimeStamp(int time) {
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
    boolean hasNeededBuildings = (activeBuildingList.containsAll(buildingToBeConstructed.getDependentOnBuildings()) || buildingToBeConstructed.getDependentOnBuildings().isEmpty());
    //System.out.println("Has needed buildings = " + hasNeededBuildings);
    boolean hasAvailableProbes = numberOfActiveUnits.get(unitNameToUnit.get("probe")) > 0;
    //System.out.println("Has available probes = " + hasAvailableProbes);
    for (Integer e : buildingFinishTime) {
        if (e == (time)) {
            addToActiveBuildingList(buildingBeingConstructed.get(0));
            System.out.println("This building is now active: " + e);
            if (numberOfActiveBuildings.get(buildingBeingConstructed.get(0)) == null) {
                numberOfActiveBuildings.put(buildingBeingConstructed.get(0), 1);
            } else {
                numberOfActiveBuildings.put(buildingBeingConstructed.get(0), numberOfActiveBuildings.get(buildingBeingConstructed.get(0)) + 1);
            }
            buildingBeingConstructed.remove(0);
        }
    }

    if (!hasNeededBuildings) {
      return true;
    } else if (!(hasResources && hasAvailableProbes)) {
      return false;
    } else {
      if (buildingToBeConstructed.getType().equals("assimilator") && numberOfActiveBuildings.get(buildingNameToBuilding.get("assimilator")) >= 2) {
        return true;
      } else {
          if (buildingToBeConstructed.getType().equals(("assimilator")) && gasGeyserNumber < 2) {
              System.out.println("Building assimilator");
              assignProbeToGas();
              gasGeyserNumber--;
          }
              buildingFinishTime.add(time + buildingToBeConstructed.getBuildTime());
              currentGas = currentGas - buildingToBeConstructed.getGasCost();
              currentMinerals = currentMinerals - buildingToBeConstructed.getMineralCost();
              buildingBeingConstructed.add(buildingToBeConstructed);
              createBuildQueue(buildingToBeConstructed);
          }
    }
    addInstructionToList("Construct "+ buildingToBeConstructed.getType());
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
    boolean hasResources = currentGas >= unitToBeConstructed.getGasCost() && currentMinerals >= unitToBeConstructed.getMineralCost(); //&& maxSupply >= currentSupply+(unitToBeConstructed.getSupplyNeeded());
    //System.out.println("Has resources: "+hasResources);
    boolean buildingsExist = (activeBuildingList.contains(unitToBeConstructed.getDependentOnBuilding()) || unitToBeConstructed.getDependentOnBuilding() == null) && activeBuildingList.contains(unitToBeConstructed.getBuiltFromBuilding());
    //System.out.println("Buildings exist: "+buildingsExist);
    if (!buildingsExist) {
      return true;
    } else if (!(hasResources)){
      return false;
    } else {
      currentGas -= unitToBeConstructed.getGasCost();
      currentMinerals -= unitToBeConstructed.getMineralCost();
      BuildQueue buildQueue = getShortestBuildQueue(unitToBeConstructed.getBuiltFromBuilding());
      buildQueue.addUnitToBuildQueue(unitToBeConstructed);
    }
    addInstructionToList("Construct "+ unitToBeConstructed.getType());
    //updateAllResources();
    return true;
  }

  private void addInstructionToList(String instruction) {
    instructionsToPrint.add(new InstructionTime(time, instruction));
  }

  public void printInstructions() {
    for (int i = 0; i < instructionsToPrint.size(); i++) {
      System.out.println(getTimeStamp(instructionsToPrint.get(i).time)+"        "+instructionsToPrint.get(i).instruction);
    }
  }

  private void printStuff(InstructionList instructions) {
    System.out.println("_______NEW TURN_______");
    System.out.println("Start Build Queue");
    //BuildQueue.printAllUnitsInBuildQueues();
    System.out.println("End build queue");
    if (instructions.getCurrentInstruction().method.getName().equals("constructBuilding")) {
      System.out.println("Instruction: " +instructions.getCurrentInstruction().method.getName() +" "+ instructions.getCurrentInstruction().building.getType());
    } else if (instructions.getCurrentInstruction().method.getName().equals("constructUnit")) {
      System.out.println("Instruction: " +instructions.getCurrentInstruction().method.getName() +" "+ instructions.getCurrentInstruction().unit.getType());
    }
    System.out.println("Current time: " + getTimeStamp(time));
    System.out.println("Current minerals: "+currentMinerals);
    System.out.println("Curent gas: "+currentGas);
    printUnits();
    printBuildings();
    assignProbeToMinerals();
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
    currentGas += calculateGasInPerTick();
    BuildQueue.updateAllBuildLists(this);
    time++;
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

  /**
   * 1 - Checks if there is an assimilator in the number of active buildings
   * 2 - Checks if there are enough "free" probes
   * 3 - Checjs if the number of probes on gas geyser is less than 3 and there are still available gas geysers
   */
  private boolean assignProbeToGas() {
      if (!(numberOfActiveBuildings.get(buildingNameToBuilding.get("assimilator")) > 0)) {
        return true;
      } else {
          if (!(numberOfActiveUnits.get(unitNameToUnit.get("probe")) == 0 ) && numberOfProbesAtLocation.get("gas") < 3 && gasGeyserNumber > 0) {
              numberOfProbesAtLocation.put("gas", numberOfActiveUnits.get(unitNameToUnit.get("probe")) + 1);
          }
      }
      return true;
  }

  private boolean assignProbeToMinerals() {
    if (!(numberOfActiveUnits.get(unitNameToUnit.get("probe")) == 0 ) && numberOfProbesAtLocation.get("minerals") < 3) {
      numberOfProbesAtLocation.put("gas", numberOfActiveUnits.get(unitNameToUnit.get("probe")) + 1);
      if (numberOfProbesAtLocation.get("minerals") == 3) {
          mineralPatchNumber--;
      }
    }
    return true;
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
    System.out.println("Goal units built");
    stopSimulation();
    return true;
  }

  /*private void updateSupply(int supplyToAdd) {
    currentSupply =+ supplyToAdd;
  }*/

  public static void setGoalUnits(HashMap<Unit,Integer> goalUnits) {
    GameSimulator.goalUnits = goalUnits;
  }

  public static HashMap<Unit, Integer> getGoalUnits() { return GameSimulator.goalUnits; }

  public static void addToUnitList(Unit unitToAdd) {
    unitList.add(unitToAdd);
  }

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
    numberOfProbesAtLocation.put("minerals",6);
    numberOfProbesAtLocation.put("gas",0);
    numberOfProbesAtLocation.put("building",0);
    for (int i = 0; i < buildingList.size(); i++) {
      buildingList.get(i).clearBuildQueues();
    }
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

  private void stopSimulation() {
    System.out.println("Stop simulation called");
    stopSimulation = true;
    instructionList.clearListFrom(instructionList.getCurrentInstructionIndex());
    //finalInstructionListLength = instructionList.getCurrentInstructionIndex();
    //System.out.println(getTimeStamp());
  }

  private void checkFinished() {
    if (instructionList.getCurrentInstructionIndex() + 1 >= instructionList.orderedInstructionList.size()) {
      System.out.println("Stopped because instructions");
      stopSimulation();
    }
  }
  public List<Building> getActiveBuildingList() {
    return activeBuildingList;
  }

  public int getFinalInstructionListLength() {
    return finalInstructionListLength;
  }

  public InstructionList getInstructionList() {
    return instructionList;
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

  private class InstructionTime {
    int time;
    String instruction;

    public InstructionTime(int time, String instruction) {
      this.time = time;
      this.instruction = instruction;
    }
  }
}
