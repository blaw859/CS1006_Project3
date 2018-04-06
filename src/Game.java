import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Game {
  private int currentSupply;
  private int maxSupply;
  private int currentMinerals;
  private int currentGas;
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

  public boolean buildUnit(Unit unitToBeBuilt,int quantity) {
    boolean hasResources = currentGas >= unitToBeBuilt.getGasCost()*quantity && currentMinerals >= unitToBeBuilt.getMineralCost()*quantity && maxSupply >= currentSupply+(unitToBeBuilt.getSupplyNeeded()*quantity);
    boolean buildingsExist = numberOfActiveUnits.containsKey(unitToBeBuilt.getType());
    boolean buildingsAbleToBuild = false;
    if (buildingsExist) {
      buildingsAbleToBuild = 
    } else {
      return false;
    }
    if () {

    }
    for (int i = 0; i < quantity; i++) {

    }
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
