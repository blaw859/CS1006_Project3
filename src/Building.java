import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Building {
  static List<Building> allBuildings = new ArrayList<>();
  static List<Building> buildingsWithBuildQueues = new ArrayList<>();
  private String type;
  private int mineralCost;
  private int gasCost;
  private int buildTime;
  private int supplyProvided;
  private List<String> dependentOn = new ArrayList<>();
  private List<Building> dependentOnBuildings = new ArrayList<>();
  public List<BuildQueue> buildQueues = new ArrayList<>();

  /**
   * This is only a reference! if you want to create a new building do not create a new building object because the objects
   * just define the properties of each type of unit
   * @param buildingInfo
   */
  public Building(String[] buildingInfo) throws Exception {
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
    buildQueues = new ArrayList<>();
    GameSimulator.buildingList.add(this);
    GameSimulator.buildingNameList.add(type);
    GameSimulator.buildingNameToBuilding.put(type,this);
    allBuildings.add(this);
    //System.out.println("Creating building "+type);

  }

  public void clearBuildQueues() {
    buildQueues.clear();
  }

  public void createNewBuildQueue (GameSimulator currentGame) {
    buildQueues.add(new BuildQueue(5,currentGame));
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
      setDependencies();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void setDependencies() {
    for (int i = 0; i < allBuildings.size(); i++) {
      System.out.println("Getting dependencies for unit "+allBuildings.get(i).type);
      List<String> nameStringList = allBuildings.get(i).getDependentOnString();
      Building currentBuilding = allBuildings.get(i);
      for (int j = 0; j < allBuildings.get(i).dependentOn.size(); j++) {
        System.out.println("The unit "+allBuildings.get(i).type+" has a dependency on "+ nameStringList.get(j));
        currentBuilding.dependentOnBuildings.add(GameSimulator.buildingNameToBuilding.get(nameStringList.get(j)));
      }
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

  public List<Building> getDependentOnBuildings() {
    return dependentOnBuildings;
  }

  public List<String> getDependentOnString() {
    return dependentOn;
  }

  public static void setBuildingsWithBuildQueues(List<Building> buildingsWithBuildQueues) {
    Building.buildingsWithBuildQueues = buildingsWithBuildQueues;
  }
}
