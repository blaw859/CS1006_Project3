import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Game {
  private int currentSupply;
  private int maxSupply;
  private int currentMinerals;
  private int currentGas;
  private int buildTime;
  public static List<Unit> unitList = new ArrayList<>();
  public static List<String> unitNameList = new ArrayList<>();
  public static List<Building> buildingList = new ArrayList<>();
  public static List<String> buildingNameList = new ArrayList<>();
  public static List<buildingToBuildQueue> buildingsWithQueues= new ArrayList<>();
  private static HashMap<String,Integer> goalUnits;
  public HashMap<String,Integer> numberOfActiveUnits;
  public HashMap<String,Integer> numberOfActiveBuildings;

  public Game() {

  }

  public boolean buildUnit(Unit unitToBeBuilt, int quantity) {
    boolean hasResources = currentGas >= unitToBeBuilt.getGasCost()*quantity && currentMinerals >= unitToBeBuilt.getMineralCost()*quantity && maxSupply >= currentSupply+(unitToBeBuilt.getSupplyNeeded()*quantity);
    boolean buildingsExist = numberOfActiveUnits.containsKey(unitToBeBuilt.getType());
    boolean buildingsAbleToBuild = false;
    buildTime = unitToBeBuilt.getBuildTime();
    /*if (buildingsExist) {
      buildingsAbleToBuild = true;
    } else {
      return false;
    }*/
    if (hasResources) {
      if (buildingsExist) {
        unitList.add(unitToBeBuilt);
        numberOfActiveUnits.put(unitToBeBuilt.getType(), quantity);
        currentGas -= unitToBeBuilt.getGasCost() * quantity;
        currentMinerals -= unitToBeBuilt.getMineralCost() * quantity;
      }
    } else if (currentMinerals < unitToBeBuilt.getMineralCost() * quantity) {
        System.out.println("Insufficient minerals available!");
    } else if (currentGas < unitToBeBuilt.getGasCost() * quantity) {
        System.out.println("Insufficient gas available!");
    } else {
        return false;
    }
    for (int i = 0; i < quantity; i++) {

    }
    return true;
  }

  Timer timer = new Timer();
  TimerTask task = new TimerTask() {
      @Override
      public void run() {
          buildUnit(unitToBeBuilt, quantity);
      }
  };

  //timer method which runs the buildUnit task
    //schedueleAtFixedRate(task, delay before task is executed, time between each task execution)
  public void buildQueue() {
      timer.scheduleAtFixedRate(task, unitToBeBuilt.getBuildTime(), 0);
  }



  private void updateSupply(int supplyToAdd) {
    currentSupply =+ supplyToAdd;
  }

  public static void setGoalUnits(HashMap<String,Integer> goalUnits) {
    Game.goalUnits = goalUnits;
  }

  public static void addToUnitList(Unit unitToAdd) {
    unitList.add(unitToAdd);
  }

  public static void addToBuildingList(Building buildingToAdd) {
    buildingList.add(buildingToAdd);
  }

  private class buildingToBuildQueue {
    String buildingType;
    BuildQueue buildQueue;

    public buildingToBuildQueue(String buildingType, BuildQueue buildQueue) {
      this.buildingType = buildingType;
      this.buildQueue = buildQueue;
    }
  }
}
