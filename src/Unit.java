import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Unit {
  private String type;
  private int mineralCost;
  private int gasCost;
  private int buildTime;
  private int warpGateCooldown;
  private int supplyNeeded;
  private String dependentOn;
  private Building dependentOnBuilding;
  private String builtFrom;
  private Building builtFromBuilding;
  private static List<Unit> allUnits = new ArrayList<>();

  //The String being the building type and the int being the number of them currently built;
  //private static HashMap<String,Integer> typeCount;

  public Unit(String[] unitInfo) {
    type = unitInfo[0];
    mineralCost = Integer.parseInt(unitInfo[1]);
    gasCost = Integer.parseInt(unitInfo[2]);
    buildTime = Integer.parseInt(unitInfo[3]);
    warpGateCooldown = Integer.parseInt(unitInfo[4]);
    supplyNeeded = Integer.parseInt(unitInfo[5]);
    dependentOn = unitInfo[6];
    builtFrom = unitInfo[7];
    GameSimulator.unitList.add(this);
    GameSimulator.unitNameList.add(type);
    GameSimulator.unitNameToUnit.put(type,this);
    allUnits.add(this);
  }

  public static void createUnits(File csvFile) {
    String currentLine = "";
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(csvFile));
      int i = 0;
      while ((currentLine = reader.readLine()) != null) {
        String[] unitInfo = currentLine.split(",");
        for (int j = 0; j < unitInfo.length; j++) {
          unitInfo[j] = unitInfo[j].replace("\uFEFF","");
        }
        new Unit(unitInfo);
        i++;
      }
      setDependencies();
      getBuildingsWithBuildQueues();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void getBuildingsWithBuildQueues() {
    List<Building> buildingsWithBuildQueues = new ArrayList<>();
    for (int i = 0; i < allUnits.size(); i++) {
      if (!buildingsWithBuildQueues.contains(allUnits.get(i).getBuiltFromBuilding()) && allUnits.get(i).getBuiltFromBuilding() != null) {
        buildingsWithBuildQueues.add(allUnits.get(i).getBuiltFromBuilding());
        System.out.println(allUnits.get(i).getBuiltFromBuilding().getType());
      }
    }
    Building.setBuildingsWithBuildQueues(buildingsWithBuildQueues);
  }

  private static void setDependencies() {
    for (int i = 0; i < allUnits.size(); i++) {
      Unit currentUnit = allUnits.get(i);
      currentUnit.dependentOnBuilding = GameSimulator.buildingNameToBuilding.get(currentUnit.dependentOn);
      currentUnit.builtFromBuilding = GameSimulator.buildingNameToBuilding.get(currentUnit.builtFrom);
    }

  }

  public String getType() {
    return type;
  }

  public int getMineralCost() {
    return mineralCost;
  }

  public int getGasCost() {
    return gasCost;
  }

  public int getBuildTime() {
    return buildTime;
  }

  public int getWarpGateCooldown() {
    return warpGateCooldown;
  }

  public int getSupplyNeeded() {
    return supplyNeeded;
  }

  public String getDependentOn() {
    return dependentOn;
  }

  public Building getDependentOnBuilding() {
    return dependentOnBuilding;
  }

  public Building getBuiltFromBuilding() {
    return builtFromBuilding;
  }

  public String getBuiltFrom() {
    return builtFrom;
  }
}


