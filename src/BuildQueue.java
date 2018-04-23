import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BuildQueue {
  private int maxQueueLength;
  private int numberOfUnitsInQueue;
  private GameSimulator currentGame;
  private LinkedList<UnitBuildTime> buildQueue = new LinkedList<>();
  public static List<BuildQueue> allBuildQueues= new ArrayList<>();

  public BuildQueue(int queueLength, GameSimulator game) {
    System.out.println("+++++++++++++++++++++++++++++++++++New build queue created++++++++++++++++++++++++++++++++++++++");
    this.maxQueueLength = queueLength;
    numberOfUnitsInQueue = 0;
    allBuildQueues.add(this);
  }

  public int getNumberOfUnitsInQueue() {
    return numberOfUnitsInQueue;
  }

  public boolean addUnitToBuildQueue(Unit unit) {
    if (numberOfUnitsInQueue < maxQueueLength){
      buildQueue.add(new UnitBuildTime(unit, unit.getBuildTime()));
      numberOfUnitsInQueue++;
      return true;
    } else {
      return false;
    }
  }
//Test
  public static void updateAllBuildLists(GameSimulator currentGame) {
    for (int i = 0; i < allBuildQueues.size(); i++) {
      if (!allBuildQueues.get(i).buildQueue.isEmpty()) {
        Unit unit = allBuildQueues.get(i).buildQueue.peek().unit;
        if (allBuildQueues.get(i).numberOfUnitsInQueue > 0) {
          allBuildQueues.get(i).buildQueue.peek().buildTime--;
          if (allBuildQueues.get(i).buildQueue.peek().buildTime == 0) {
            System.out.println("Updating buildqueue");
            //I think this line essentially does this
            currentGame.numberOfActiveUnits.merge(unit, 1, (a, b) -> a + b);
            //currentGame.numberOfActiveUnits.merge(unit, 1, (a, b) -> a + b);
          }
        }
      }
    }
  }

  private class UnitBuildTime{
    Unit unit;
    int buildTime;

    public UnitBuildTime (Unit unit, int buildTime) {
      this.unit = unit;
      this.buildTime = buildTime;
    }
  }
}

