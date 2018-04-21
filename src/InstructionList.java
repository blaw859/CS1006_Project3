import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InstructionList {
  List<Method> methodList = new ArrayList<>();
  Class gameClass = GameSimulator.class;
  GameSimulator currentGame;
  List<Unit> unitsToConstruct = new ArrayList<>();
  List<Building> buildingsToConstruct = new ArrayList<>();

  InstructionList() {
    //currentGame = game;
    getThingsWorthBuilding();
  }

  private void initializeMethods() {
    try {
      Method constructUnit = gameClass.getMethod("constructUnit", Unit.class);
      Method constructBuilding = gameClass.getMethod("constructBuilding", Building.class);
    } catch (Exception e) {
      System.out.println("These methods cannot be found");
    }
  }

  //Probably rename this before submission
  private void getThingsWorthBuilding() {
    List<Unit> unitsToConstruct = new ArrayList<>();
    HashMap<Unit,Integer> goalUnits = GameSimulator.getGoalUnits();
    unitsToConstruct.addAll(goalUnits.keySet());
    for (int i = 0; i < unitsToConstruct.size(); i++) {
      Unit thisUnit = unitsToConstruct.get(i);
      if ((GameSimulator.buildingNameToBuilding.get(thisUnit.getDependentOn()) != null)&&(!buildingsToConstruct.contains(GameSimulator.buildingNameToBuilding.get(thisUnit.getDependentOn())))) {
        buildingsToConstruct.add(GameSimulator.buildingNameToBuilding.get(thisUnit.getDependentOn()));
      }
      if (!buildingsToConstruct.contains(GameSimulator.buildingNameToBuilding.get(thisUnit.getBuiltFrom()))) {
        buildingsToConstruct.add(GameSimulator.buildingNameToBuilding.get(thisUnit.getBuiltFrom()));
      }
    }
  }
}
