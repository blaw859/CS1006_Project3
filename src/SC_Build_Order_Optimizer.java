import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class SC_Build_Order_Optimizer {
  static int numberOfPools = 10;
  static List<GameSimulator> allGames = new ArrayList<>();
  static List<List<GameSimulator>> allGamePools = new ArrayList<>();
  private static WeightedMap weightedGames = new WeightedMap();
  static List<GameSimulator> fastestTen = new ArrayList<>();
  static GameSimulator fastestGame = null;


  public static void main(String[] args) {
    initializeUnits();
    GameSimulator.setGoalUnits(getGoalUnits());
    /*GameSimulator first = new GameSimulator(new InstructionList(1));
    first.printInstructions();*/
    int shortestTime = 1000000;
    for (int i = 0; i < 100; i++) {
      allGames.add(new GameSimulator(new InstructionList()));
      weightedGames.add(((double) 1) / allGames.get(i).timeTakenToComplete, allGames.get(i));
      if (allGames.get(i).timeTakenToComplete < shortestTime) {
        shortestTime = allGames.get(i).timeTakenToComplete;
        fastestGame = allGames.get(i);
      }
      System.out.println("Initial Game " + i + " took " + allGames.get(i).timeTakenToComplete);
      //System.out.println("The instructionList was " + allGames.get(i).getFinalInstructionListLength());
      //Move this internal
      BuildQueue.clearBuildQueues();
    }
    for (int k = 0; k < numberOfPools; k++) {
      int poolSum = 0;
      List<GameSimulator> currentPool = new ArrayList<>();
      System.out.println("New Pool");
      for (int i = 0; i < 100; i++) {
        currentPool.add(new GameSimulator(new InstructionList(((GameSimulator) weightedGames.next()),((GameSimulator) weightedGames.next()))));
        poolSum = poolSum + currentPool.get(i).timeTakenToComplete;
        if (currentPool.get(i).timeTakenToComplete < shortestTime) {
          shortestTime = allGames.get(i).timeTakenToComplete;
          fastestGame = allGames.get(i);
        }
        System.out.println("Pool "+ k +" Game " + i + " took " + currentPool.get(i).timeTakenToComplete);
      }
      System.out.println("The pool average was"+ (poolSum/100));
      System.out.println("The fastest game took " + shortestTime);
      allGamePools.add(currentPool);
      currentPool.clear();
    }

    System.out.println("The fastest game took "+ getTimeStamp(shortestTime));
    fastestGame.printInstructions();
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
