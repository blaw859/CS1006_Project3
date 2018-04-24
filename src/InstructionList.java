import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class InstructionList {
  List<Instruction> orderedInstructionList = new ArrayList<>();
  HashMap<Object,Integer> unitInstructionCalls = new HashMap<>();
  HashMap<Object,Instruction> possibleInstructions = new HashMap<>();
  WeightedMap weightedInstructions = new WeightedMap();
  Class gameClass = GameSimulator.class;
  GameSimulator currentGame;
  List<Unit> unitsToConstruct = new ArrayList<>();
  List<Building> buildingsToConstruct = new ArrayList<>();
  private int currentInstructionIndex = 0;

  public void printInstructionList() {
    //System.out.println(orderedInstructionList.size());
    for (int i = 0; i < orderedInstructionList.size(); i++) {
      if (orderedInstructionList.get(i).method.getName().equals("constructUnit")) {
        System.out.println("Constructing Unit: "+orderedInstructionList.get(i).unit.getType());
      } else if (orderedInstructionList.get(i).method.getName().equals("constructBuilding")) {
        System.out.println("Constructing Building: "+orderedInstructionList.get(i).building.getType());
      }
    }
  }

  //Crossbreeds instructionlist
  public InstructionList(GameSimulator game1, GameSimulator game2) {
    InstructionList instructionList1 = game1.getInstructionList();
    InstructionList instructionList2 = game2.getInstructionList();
    possibleInstructions = instructionList1.getPossibleInstructions();
    int listLength1 = instructionList1.orderedInstructionList.size();
    int listLength2 = instructionList2.orderedInstructionList.size();
    int longestList = 0;
    int shortestList = 0;
    if (listLength1 >= listLength2) {
      longestList = listLength1;
      shortestList = listLength2;
    } else {
      longestList = listLength2;
      shortestList = listLength1;
    }
    //System.out.println("Longest List:"+ longestList);
    for (int i = 0; i < longestList; i++) {
      //System.out.println("Shortest List: "+shortestList);
      if (ThreadLocalRandom.current().nextInt(10) != 10 && i < shortestList) {
        addInstructionToNewList(instructionList1,instructionList2,i);
      } else {
        orderedInstructionList.add(getRandomPossibleInstruction());
      }
    }
  }

  private void addInstructionToNewList(InstructionList instructionList1, InstructionList instructionList2, int currentIndex) {
    //This switch works such that if one instruction is null then the other will be used
    switch (ThreadLocalRandom.current().nextInt(1)) {
      case 0:
        if (instructionList1.getOrderedInstructionList().get(currentIndex) != null) {
          orderedInstructionList.add(instructionList1.getOrderedInstructionList().get(currentIndex));
          break;
        }
      case 1:
        if (instructionList2.getOrderedInstructionList().get(currentIndex) != null) {
          orderedInstructionList.add(instructionList2.getOrderedInstructionList().get(currentIndex));
          break;
        }
      default:
        orderedInstructionList.add(instructionList1.getOrderedInstructionList().get(currentIndex));
        break;
    }
  }


  InstructionList(int testInstructionList) {
    if (testInstructionList == 1) {
      //orderedInstructionList.add(new Instruction(GameSimulator.unitNameToUnit.get("gateway")));
      orderedInstructionList.add(new Instruction(GameSimulator.buildingNameToBuilding.get("gateway")));
      orderedInstructionList.add(new Instruction(GameSimulator.buildingNameToBuilding.get("pylon")));
      orderedInstructionList.add(new Instruction(GameSimulator.buildingNameToBuilding.get("gateway")));
      orderedInstructionList.add(new Instruction(GameSimulator.buildingNameToBuilding.get("cybernetics core")));
      orderedInstructionList.add(new Instruction(GameSimulator.unitNameToUnit.get("zealot")));
      orderedInstructionList.add(new Instruction(GameSimulator.unitNameToUnit.get("stalker")));
      orderedInstructionList.add(new Instruction(GameSimulator.unitNameToUnit.get("zealot")));
      orderedInstructionList.add(new Instruction(GameSimulator.unitNameToUnit.get("zealot")));
      orderedInstructionList.add(new Instruction(GameSimulator.unitNameToUnit.get("zealot")));
      orderedInstructionList.add(new Instruction(GameSimulator.unitNameToUnit.get("zealot")));
      orderedInstructionList.add(new Instruction(GameSimulator.unitNameToUnit.get("zealot")));
      orderedInstructionList.add(new Instruction(GameSimulator.unitNameToUnit.get("zealot")));
      orderedInstructionList.add(new Instruction(GameSimulator.unitNameToUnit.get("zealot")));
      orderedInstructionList.add(new Instruction(GameSimulator.unitNameToUnit.get("zealot")));
      orderedInstructionList.add(new Instruction(GameSimulator.buildingNameToBuilding.get("pylon")));
      orderedInstructionList.add(new Instruction(GameSimulator.buildingNameToBuilding.get("assimilator")));
      orderedInstructionList.add(new Instruction(GameSimulator.buildingNameToBuilding.get("assimilator")));
      orderedInstructionList.add(new Instruction(GameSimulator.buildingNameToBuilding.get("assimilator")));
      orderedInstructionList.add(new Instruction(GameSimulator.buildingNameToBuilding.get("assimilator")));
      orderedInstructionList.add(new Instruction(GameSimulator.buildingNameToBuilding.get("assimilator")));
      orderedInstructionList.add(new Instruction(GameSimulator.buildingNameToBuilding.get("gateway")));
      orderedInstructionList.add(new Instruction(GameSimulator.buildingNameToBuilding.get("gateway")));
      orderedInstructionList.add(new Instruction(GameSimulator.buildingNameToBuilding.get("gateway")));
      orderedInstructionList.add(new Instruction(GameSimulator.buildingNameToBuilding.get("gateway")));
      orderedInstructionList.add(new Instruction(GameSimulator.buildingNameToBuilding.get("gateway")));


    }
  }

  InstructionList() {
    getThingsWorthBuilding();
    initializeInstructions();
    giveInstructionsWeights();
    generateInstructionList();
    //printInstructionList();

    for (int i = 0; i < buildingsToConstruct.size(); i++) {
      //System.out.println("Building to build: "+buildingsToConstruct.get(i).getType());
    }
    for (int j = 0; j < unitsToConstruct.size(); j++) {
      //System.out.println("Unit to build: "+unitsToConstruct.get(j).getType());
    }
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

  private void giveInstructionsWeights() {
    for (int i = 0; i < unitsToConstruct.size(); i++) {
      //Maybe change doing this if performance is really bad
      weightedInstructions.add(((double)35)/unitsToConstruct.size(),new Instruction(unitsToConstruct.get(i)));
    }
    for (int j = 0; j < buildingsToConstruct.size(); j++) {
      weightedInstructions.add(((double)35)/buildingsToConstruct.size(),new Instruction(buildingsToConstruct.get(j)));
    }
    weightedInstructions.add(((double)30),new Instruction(GameSimulator.unitNameToUnit.get("probe")));
    //Old code that might be useful, probably keep this until you are sure this method works well
    /*if (hasBuildingBeenCreated(GameSimulator.buildingNameToBuilding.get(unitsToConstruct.get(unitConstructionNumber).getDependentOn()))) {
        orderedInstructionList.add(new Instruction(unitsToConstruct.get(unitConstructionNumber)));
      }*/
  }

  //Probably rename this before submission
  private void getThingsWorthBuilding() {
    HashMap<Unit,Integer> goalUnits = GameSimulator.getGoalUnits();
    unitsToConstruct.addAll(goalUnits.keySet());
    for (int i = 0; i < unitsToConstruct.size(); i++) {
      getDependencies(unitsToConstruct.get(i));
      //.out.println("looping");
    }
  }

  /**
   * Recursive method that when given a unit will get all of the necessary dependencies for a unit and add them to
   * @param unit
   */
  private void getDependencies (Unit unit) {
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
    for (int i = 0; i < building.getDependentOnString().size(); i++) {
      if ((GameSimulator.buildingNameToBuilding.get(building.getDependentOnString().get(i)) != null)&&(!buildingsToConstruct.contains(GameSimulator.buildingNameToBuilding.get(building.getDependentOnString().get(i))))) {
        buildingsToConstruct.add(GameSimulator.buildingNameToBuilding.get(building.getDependentOnString().get(i)));
        getDependencies(GameSimulator.buildingNameToBuilding.get(building.getDependentOnString().get(i)));
        //System.out.println("building called");
      }
      if (building.getGasCost() > 0) {
        buildingsToConstruct.add(GameSimulator.buildingNameToBuilding.get("assimilator"));
      }
    }
  }

  private Instruction getRandomPossibleInstruction() {
    return possibleInstructions.get(ThreadLocalRandom.current().nextInt(possibleInstructions.size()));
  }
//Ideas:
  //Only try to build units when their dependant buildings are built

  private void generateInstructionList() {
    int unitConstructionNumber = ThreadLocalRandom.current().nextInt(unitsToConstruct.size());
    int instructionNumber = 0;
    boolean instructionSetComplete = false;
    while (!checkAllIfInstructionsComplete() && instructionNumber < 1000) {
      orderedInstructionList.add((Instruction) weightedInstructions.next());
      instructionNumber++;
    }
  }

  private boolean checkAllIfInstructionsComplete() {
    for (int i = 0; i < unitsToConstruct.size(); i++) {
      if (!(unitInstructionCalls.get(unitsToConstruct.get(i)) == GameSimulator.getGoalUnits().get(unitsToConstruct.get(i)))) {
        return false;
      }
    }
    return true;
  }

  private boolean hasBuildingBeenCreated(Building building) {
    for (int i = 0; i < orderedInstructionList.size(); i++) {
      if (orderedInstructionList.get(i).building == building) {
        return true;
      }
    }
    return false;
  }

  /*private boolean waitUntilUnitBuilt(Unit unit) {

  }*/

  public void moveToNextInstruction() {
      currentInstructionIndex++;
  }

  public Instruction getCurrentInstruction() {
    return orderedInstructionList.get(currentInstructionIndex);
  }

  public int getCurrentInstructionIndex() {
    return currentInstructionIndex;
  }

  public List<Instruction> getOrderedInstructionList() {
    return orderedInstructionList;
  }

  public void clearListFrom(int finishIndex) {
    //orderedInstructionList.subList(finishIndex+1, orderedInstructionList.size()).clear();
  }

  public HashMap<Object, Instruction> getPossibleInstructions() {
    return possibleInstructions;
  }

  //Possibly make this more efficient


}
//Instruction list inputted
//Game outputs all actions with timestamps including those that do not need to be written in instructions
