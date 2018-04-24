import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class SC_Build_Order_Optimizer {
  static List<GameSimulator> allGames = new ArrayList<>();

  public static void main(String[] args) {
    initializeUnits();
    GameSimulator.setGoalUnits(getGoalUnits());
    int shortestTime = 1000000;
    GameSimulator fastestGame;
  /*  InstructionList instructionList1 = new InstructionList();
    GameSimulator game1 = new GameSimulator(instructionList1);*/
    for (int i = 0; i < 10; i++) {
      allGames.add(new GameSimulator(new InstructionList(),i));
      if (allGames.get(i).timeTakenToComplete < shortestTime) {
        shortestTime = allGames.get(i).timeTakenToComplete;
        fastestGame = allGames.get(i);
      }
      System.out.println("Game " + i + " took " + getTimeStamp(allGames.get(i).timeTakenToComplete));
      //Move this internal
      BuildQueue.clearBuildQueues();
    }
    System.out.println("The fastest game took "+ getTimeStamp(shortestTime));
    /*InstructionList instructionList1 = new InstructionList();
    GameSimulator game1 = new GameSimulator(instructionList1);
    System.out.println("Now the second game");
    InstructionList instructionList2 = new InstructionList();
    GameSimulator game2 = new GameSimulator(instructionList2);*/
  }

  public static String getTimeStamp(int time) {
    int mins = time / 60;
    int secs = time % 60;
    return ( mins < 10 ? "0" + mins : mins ) + ":" + ( secs < 10 ? "0" + secs : secs);
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
