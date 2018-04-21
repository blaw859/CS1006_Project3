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
  int currentInstructionIndex = 0;

  InstructionList() {
    getThingsWorthBuilding();
    initializeInstructions();
    //System.out.println(possibleInstructions.get(GameSimulator.unitNameToUnit.get("zealot")).method.getName());

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
      if ((GameSimulator.buildingNameToBuilding.get(thisUnit.getDependentOn()) != null)&&(!buildingsToConstruct.contains(GameSimulator.buildingNameToBuilding.get(thisUnit.getDependentOn())))) {
        buildingsToConstruct.add(GameSimulator.buildingNameToBuilding.get(thisUnit.getDependentOn()));
      }
      if (!buildingsToConstruct.contains(GameSimulator.buildingNameToBuilding.get(thisUnit.getBuiltFrom()))) {
        buildingsToConstruct.add(GameSimulator.buildingNameToBuilding.get(thisUnit.getBuiltFrom()));
      }
    }
  }

  public void moveToNextInstruction() {
    currentInstructionIndex++;
  }

  public Instruction getCurrentInstruction() {
    return orderedInstructionList.get(currentInstructionIndex);
  }


}
