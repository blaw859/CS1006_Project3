import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

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
    for (int i = 0; i < buildingsToConstruct.size(); i++) {
      System.out.println("Building to build: "+buildingsToConstruct.get(i).getType());
      //orderedInstructionList.add(possibleInstructions.get(buildingsToConstruct.get(i)));
    }
    for (int j = 0; j < unitsToConstruct.size(); j++) {
      System.out.println("Unit to build: "+unitsToConstruct.get(j).getType());
      //orderedInstructionList.add(possibleInstructions.get(unitsToConstruct.get(j)));
    }
    //System.out.println(possibleInstructions.get(GameSimulator.unitNameToUnit.get("zealot")).method.getName());
    //orderedInstructionList.add(possibleInstructions.get(buildingsToConstruct.get(0)));
    //orderedInstructionList.add(possibleInstructions.get(unitsToConstruct.get(0)));
  }

  private void initializeInstructions() {
    for (int i = 0; i < unitsToConstruct.size(); i++) {
      possibleInstructions.put(unitsToConstruct.get(i), new Instruction(unitsToConstruct.get(i)));
    }
    for (int j = 0; j < buildingsToConstruct.size(); j++) {
      possibleInstructions.put(buildingsToConstruct.get(j), new Instruction(buildingsToConstruct.get(j)));
    }
    possibleInstructions.put(GameSimulator.unitNameToUnit.get("probe"), new Instruction(GameSimulator.unitNameToUnit.get("probe")));
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
      getDependencies(unitsToConstruct.get(i));
      System.out.println("looping");
      /*Unit thisUnit = unitsToConstruct.get(i);
      if ((GameSimulator.buildingNameToBuilding.get(thisUnit.getDependentOn()) != null)&&(!buildingsToConstruct.contains(GameSimulator.buildingNameToBuilding.get(thisUnit.getDependentOn())))) {
        buildingsToConstruct.add(GameSimulator.buildingNameToBuilding.get(thisUnit.getDependentOn()));
      }
      if (!buildingsToConstruct.contains(GameSimulator.buildingNameToBuilding.get(thisUnit.getBuiltFrom()))) {
        buildingsToConstruct.add(GameSimulator.buildingNameToBuilding.get(thisUnit.getBuiltFrom()));
      }*/
    }
  }

  /**
   * Recursive method that when given a unit will get all of the necessary dependencies for a unit and add them to
   * @param unit
   */
  private void getDependencies (Unit unit) {
    System.out.println(unit.getType());
    System.out.println("first"+GameSimulator.buildingNameToBuilding.get(unit.getDependentOn()));
    System.out.println("second"+buildingsToConstruct.contains(GameSimulator.buildingNameToBuilding.get(unit.getDependentOn())));
    System.out.println((GameSimulator.buildingNameToBuilding.get(unit.getDependentOn()) != null)&&(!buildingsToConstruct.contains(GameSimulator.buildingNameToBuilding.get(unit.getDependentOn()))));
    if ((GameSimulator.buildingNameToBuilding.get(unit.getDependentOn()) != null)&&(!buildingsToConstruct.contains(GameSimulator.buildingNameToBuilding.get(unit.getDependentOn())))) {
        buildingsToConstruct.add(GameSimulator.buildingNameToBuilding.get(unit.getDependentOn()));
        getDependencies(GameSimulator.buildingNameToBuilding.get(unit.getDependentOn()));

    }
    if ((GameSimulator.buildingNameToBuilding.get(unit.getBuiltFrom()) != null)&&(!buildingsToConstruct.contains(GameSimulator.buildingNameToBuilding.get(unit.getBuiltFrom())))) {
      buildingsToConstruct.add(GameSimulator.buildingNameToBuilding.get(unit.getBuiltFrom()));
      getDependencies(GameSimulator.buildingNameToBuilding.get(unit.getBuiltFrom()));
    }
    if (unit.getGasCost() > 0) {
      buildingsToConstruct.add(GameSimulator.buildingNameToBuilding.get("assimilator"));
    }
  }

  private void getDependencies (Building building) {
    System.out.println(building.getType());
    for (int i = 0; i < building.getDependentOn().size(); i++) {
      if ((GameSimulator.buildingNameToBuilding.get(building.getDependentOn().get(i)) != null)&&(!buildingsToConstruct.contains(GameSimulator.buildingNameToBuilding.get(building.getDependentOn().get(i))))) {
        buildingsToConstruct.add(GameSimulator.buildingNameToBuilding.get(building.getDependentOn().get(i)));
        getDependencies(GameSimulator.buildingNameToBuilding.get(building.getDependentOn().get(i)));
        System.out.println("building called");
      }
      if (building.getGasCost() > 0) {
        buildingsToConstruct.add(GameSimulator.buildingNameToBuilding.get("assimilator"));
      }
    }
  }
//Ideas:
  //Only try to build units when their dependant buildings are built

  /*private void generateInstructionList() {
    double randomNum = Math.random();
    int instructionNumber = 0;
    boolean instructionSetComplete = false;
    while ()
    if (randomNum < 40)

  }*/

  public void moveToNextInstruction() {
    currentInstructionIndex++;
  }

  public Instruction getCurrentInstruction() {
    return orderedInstructionList.get(currentInstructionIndex);
  }


}
//Instruction list inputted
//Game outputs all actions with timestamps including those that do not need to be written in instructions
