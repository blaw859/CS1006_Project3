import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class InstructionList {
  List<Instruction> orderedInstructionList = new ArrayList<>();
  HashMap<Object,Integer> unitInstructionCalls = new HashMap<>();
  HashMap<Object,Instruction> possibleInstructions = new HashMap<>();
  WeightedInstructionMap weightedInstructions = new WeightedInstructionMap();
  Class gameClass = GameSimulator.class;
  GameSimulator currentGame;
  List<Unit> unitsToConstruct = new ArrayList<>();
  List<Building> buildingsToConstruct = new ArrayList<>();
  public int currentInstructionIndex = 0;

  private void printInstructionList() {
    //System.out.println(orderedInstructionList.size());
    for (int i = 0; i < orderedInstructionList.size(); i++) {
      if (orderedInstructionList.get(i).method.getName().equals("constructUnit")) {
        System.out.println("Constructing Unit: "+orderedInstructionList.get(i).unit.getType());
      } else if (orderedInstructionList.get(i).method.getName().equals("constructBuilding")) {
        System.out.println("Constructing Building: "+orderedInstructionList.get(i).building.getType());
      }
    }
  }

  InstructionList(int testInstructionList) {
    if (testInstructionList == 1) {
      orderedInstructionList.add(new Instruction(GameSimulator.buildingNameToBuilding.get("pylon")));
      orderedInstructionList.add(new Instruction(GameSimulator.buildingNameToBuilding.get("gateway")));
      orderedInstructionList.add(new Instruction(GameSimulator.unitNameToUnit.get("zealot")));
      orderedInstructionList.add(new Instruction(GameSimulator.unitNameToUnit.get("zealot")));
      orderedInstructionList.add(new Instruction(GameSimulator.unitNameToUnit.get("zealot")));
      orderedInstructionList.add(new Instruction(GameSimulator.unitNameToUnit.get("zealot")));
      orderedInstructionList.add(new Instruction(GameSimulator.unitNameToUnit.get("zealot")));
      orderedInstructionList.add(new Instruction(GameSimulator.unitNameToUnit.get("zealot")));

    }
  }

  InstructionList() {
    getThingsWorthBuilding();
    initializeInstructions();
    giveInstructionsWeights();
    generateInstructionList();
    printInstructionList();

    for (int i = 0; i < buildingsToConstruct.size(); i++) {
      System.out.println("Building to build: "+buildingsToConstruct.get(i).getType());
    }
    for (int j = 0; j < unitsToConstruct.size(); j++) {
      System.out.println("Unit to build: "+unitsToConstruct.get(j).getType());
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
      System.out.println("looping");
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
        System.out.println("building called");
      }
      if (building.getGasCost() > 0) {
        buildingsToConstruct.add(GameSimulator.buildingNameToBuilding.get("assimilator"));
      }
    }
  }
//Ideas:
  //Only try to build units when their dependant buildings are built

  private void generateInstructionList() {
    int unitConstructionNumber = ThreadLocalRandom.current().nextInt(unitsToConstruct.size());
    int instructionNumber = 0;
    boolean instructionSetComplete = false;
    while (!checkAllIfInstructionsComplete() && instructionNumber < 1000) {
      orderedInstructionList.add(weightedInstructions.next());
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
    if (currentInstructionIndex + 1 >= orderedInstructionList.size()) {
      GameSimulator.stopSimulation = true;
    } else {
      currentInstructionIndex++;
    }

  }

  public Instruction getCurrentInstruction() {
    return orderedInstructionList.get(currentInstructionIndex);
  }

  //Possibly make this more efficient
  private class WeightedInstructionMap {
    private final NavigableMap<Double,Instruction> weightedInstructions = new TreeMap<>();
    private final Random random;
    private double total;

    public WeightedInstructionMap() {
      this(new Random());
    }

    public WeightedInstructionMap(Random random) {
      this.random = random;
    }

    public WeightedInstructionMap add (double weight, Instruction instruction) {
      if (weight <= 0) return this;
      total += weight;
      weightedInstructions.put(total, instruction);
      return this;
    }

    public Instruction next() {
      double value = random.nextDouble()*total;
      return weightedInstructions.higherEntry(value).getValue();
    }

  }


}
//Instruction list inputted
//Game outputs all actions with timestamps including those that do not need to be written in instructions
