import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InstructionList {
  List<Instruction> orderedInstructionList = new ArrayList<>();
  //List<Instruction> possibleInstructionList = new ArrayList<>();
  HashMap<Object,Instruction> possibleInstructions = new HashMap<>();
  Class gameClass = GameSimulator.class;
  GameSimulator currentGame;
  List<Unit> unitsToConstruct = new ArrayList<>();
  List<Building> buildingsToConstruct = new ArrayList<>();
  List<Unit> unitDependencies = new ArrayList<>();
  List<Building> buildingDependences = new ArrayList<>();
  int currentInstructionIndex = 0;

  InstructionList() {
    getThingsWorthBuilding();
    initializeInstructions();
    //System.out.println(possibleInstructions.get(GameSimulator.unitNameToUnit.get("zealot")).method.getName());
    orderedInstructionList.add(possibleInstructions.get(buildingsToConstruct.get(0)));
    orderedInstructionList.add(possibleInstructions.get(unitsToConstruct.get(0)));

    /*for (int i = 0; i < unitsToConstruct.size(); i++) {
      System.out.println(unitsToConstruct.get(i).getType());
    }
    for (int j = 0; j < buildingsToConstruct.size(); j++) {
      System.out.println(buildingsToConstruct.get(j).getType());
    }*/

  }

  private void initializeInstructions() {
    for (int i = 0; i < unitsToConstruct.size(); i++) {
      possibleInstructions.put(unitsToConstruct.get(i), new Instruction(unitsToConstruct.get(i)));
    }
    for (int j = 0; j < buildingsToConstruct.size(); j++) {
      possibleInstructions.put(buildingsToConstruct.get(j), new Instruction(unitsToConstruct.get(j)));
    }
  }

  /*private void initializeMethods() {
    try {
      Method constructUnit = gameClass.getMethod("constructUnit", Unit.class);
      Method constructBuilding = gameClass.getMethod("constructBuilding", Building.class);
    } catch (Exception e) {
      System.out.println("These methods cannot be found");
    }
  }*/

  //Probably rename this before submission
  private void getThingsWorthBuilding() {
    HashMap<Unit,Integer> goalUnits = GameSimulator.getGoalUnits();
    unitsToConstruct.addAll(goalUnits.keySet());
    for (int i = 0; i < unitsToConstruct.size(); i++) {
      Unit thisUnit = unitsToConstruct.get(i);
      if ((GameSimulator.buildingNameToBuilding.get(thisUnit.getDependentOnString()) != null)&&(!buildingsToConstruct.contains(GameSimulator.buildingNameToBuilding.get(thisUnit.getDependentOnString())))) {
        buildingsToConstruct.add(GameSimulator.buildingNameToBuilding.get(thisUnit.getDependentOnString()));
      }
      if (!buildingsToConstruct.contains(GameSimulator.buildingNameToBuilding.get(thisUnit.getBuiltFrom()))) {
        buildingsToConstruct.add(GameSimulator.buildingNameToBuilding.get(thisUnit.getBuiltFrom()));
      }
    }
  }

  /**
   * Recursive method that when given a unit will get all of the necessary dependencies for a unit and add them to
   * @param unit
   */
  private void getDependencies (Unit unit) {
    if ((GameSimulator.buildingNameToBuilding.get(unit.getDependentOnString()) != null)&&(!buildingsToConstruct.contains(GameSimulator.buildingNameToBuilding.get(unit.getDependentOnString())))) {
        buildingsToConstruct.add(GameSimulator.buildingNameToBuilding.get(unit.getDependentOnString()));
        getDependencies(GameSimulator.buildingNameToBuilding.get(unit.getDependentOnString()));
    }
  }

  /**
   * Recursive method that when given a building it will return/get all the necessary dependencies for a building and add them to... insert_type_here
   * Should "return" object instead of string.
   * @param building
   */
  private void getDependencies (Building building) {
    if ((GameSimulator.buildingNameToBuilding.get(building.getDependentOnString()) != null) && (!buildingsToConstruct.contains(GameSimulator.buildingNameToBuilding.get(building.getDependentOnString())))) {
      buildingDependences.add(GameSimulator.buildingNameToBuilding.get(building.getDependentOnString()));
    }
  }

  //alternative method that does a return instead of adding them to a list.
  /*private Building getDependencies (Building building) {
    if ((GameSimulator.buildingNameToBuilding.get(building.getDependentOnString()) != null) && (!buildingsToConstruct.contains(GameSimulator.buildingNameToBuilding.get(building.getDependentOnString())))) {
      return GameSimulator.buildingNameToBuilding.get(building.getDependentOnString());
    }
    return GameSimulator.buildingNameToBuilding.get(building.getDependentOnString());
  }*/

  public void moveToNextInstruction() {
    currentInstructionIndex++;
  }

  public Instruction getCurrentInstruction() {
    return orderedInstructionList.get(currentInstructionIndex);
  }


}
