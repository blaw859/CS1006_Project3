import java.util.HashMap;
import java.util.Scanner;

public class SC_Build_Order_Optimizer {
  public static void main(String[] args) {
    initializeUnits();
    GameSimulator.setGoalUnits(getGoalUnits());
    InstructionList instructionList1 = new InstructionList(1);
    GameSimulator game1 = new GameSimulator(instructionList1);
  }


  private static HashMap<Unit, Integer> getGoalUnits() {
    HashMap<Unit,Integer> goalUnits = new HashMap<>();
    Scanner userIn = new Scanner(System.in);
    String inputString = "";
    int i = 0;
    while (!inputString.equals("done")) {
      System.out.println("Input a unit you want to build press enter then type the quantity (type done when finished)");
      inputString = userIn.nextLine();
      if (!inputString.equals("done")) {
        int quantity = Integer.parseInt(userIn.nextLine());
        if (GameSimulator.unitNameList.contains(inputString)) {
          goalUnits.put(GameSimulator.unitNameToUnit.get(inputString), quantity);
        } else {
          System.out.println("Invalid unit please try again");
        }
      }
    }
    return goalUnits;
  }

  private static void initializeUnits() {
    Building.createBuildings();
    Unit.createUnits();
  }
}
