import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Unit {
  private String type;
  private int mineralCost;
  private int gasCost;
  private int buildTime;
  private int warpGateCooldown;
  private int supplyNeeded;
  private String dependentOn;
  private String builtFrom;

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
    Game.addToUnitList(this);
  }

  public static void createUnits() {
    File csvFile = new File("/Users/benlawrence859/Documents/University/First Year/CS1006_Project3/datasheets/units.csv");
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
    } catch (Exception e) {
      e.printStackTrace();
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

  public String getBuiltFrom() {
    return builtFrom;
  }
}


