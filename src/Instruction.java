import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Instruction {
  Unit unit;
  Building building;
  Method method;
  String argType;
  static List<Method> possibleMethods = new ArrayList<>();

  public Instruction(Unit unit) {
    argType = "unit";
    this.unit = unit;
    try {
      method = GameSimulator.class.getMethod("constructUnit", Unit.class);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
  }

  public Instruction(Building building) {
    argType = "building";
    this.building = building;
    try {
      method = GameSimulator.class.getMethod("constructBuilding", Building.class);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
  }

  public Instruction(String specialCase) {
    argType = null;
    if (specialCase.equals("move probe to gas")) {
      try {
        method = GameSimulator.class.getMethod("moveProbeToGas");
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      }
    }
  }

  public String getArgType() {
    return argType;
  }

  /*private static void initalizePossibleMethods() {
    try {
      possibleMethods.add(GameSimulator.class.getMethod("constructUnit", Unit.class));
      possibleMethods.add(GameSimulator.class.getMethod("constructBuilding", Building.class));
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
  }*/
}
