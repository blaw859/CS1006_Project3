import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Building {
  private String type;
  private int mineralCost;
  private int gasCost;
  private int buildTime;
  private int supplyProvided;
  private List<String> dependentOn;

  public Building(String[] buildingInfo) {
    dependentOn = new ArrayList<>();
    type = buildingInfo[0];
    mineralCost = Integer.parseInt(buildingInfo[1]);
    gasCost = Integer.parseInt(buildingInfo[2]);
    buildTime = Integer.parseInt(buildingInfo[3]);
    if (!buildingInfo[4].equals("null")) {
      dependentOn.add(buildingInfo[4]);
    }
    if (!buildingInfo[5].equals("null")) {
      dependentOn.add(buildingInfo[5]);
    }
    supplyProvided = Integer.parseInt(buildingInfo[6]);
    Game.buildingList.add(this);
    Game.buildingNameList.add(type);
  }

  public static void createBuildings() {
    File csvFile = new File("/Users/benlawrence859/Documents/University/First Year/CS1006_Project3/datasheets/buildings.csv");
    String currentLine = "";
    BufferedReader reader;
    try {
      reader = new BufferedReader(new FileReader(csvFile));
      while ((currentLine = reader.readLine()) != null) {
        String[] buildingInfo = currentLine.split(",");
        for (int j = 0; j < buildingInfo.length; j++) {
          buildingInfo[j] = buildingInfo[j].replace("\uFEFF", "");
        }
        new Building(buildingInfo);
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

  public int getSupplyProvided() {
    return supplyProvided;
  }

  public List<String> getDependentOn() {
    return dependentOn;
  }
}
