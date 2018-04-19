import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InstructionList {
  List<Method> methodList = new ArrayList<>();
  Class gameClass = GameSimulator.class;
  GameSimulator currentGame;

  InstructionList(GameSimulator game) {
    currentGame = game;
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
  private ArrayList<Object> getThingsWorthBuilding(G) {
    List<Object> toConstruct = new ArrayList<>();
    HashMap<Unit,Integer> goalUnits = GameSimulator.getGoalUnits();

  }
}
