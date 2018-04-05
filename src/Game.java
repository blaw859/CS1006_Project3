import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Game {
  private int currentSupply;
  private int maxSupply;
  private int currentMinerals;
  private int currentGas;
  private static List<Unit> unitTypeList = new ArrayList<>();
  private static List<Building> buildingTypeList = new ArrayList<>();
  private HashMap<String,Integer> numberOfUnits;

  public Game() {
    Unit.createUnits();
    Building.createBuildings();
    for (int i = 0; i < buildingTypeList.size(); i++) {
      System.out.println(buildingTypeList.get(i).getType());
    }
  }

  public static void addToUnitList(Unit unitToAdd) {
    unitTypeList.add(unitToAdd);
  }

  public static void addToBuildingList(Building buildingToAdd) {
    buildingTypeList.add(buildingToAdd);
  }
}
