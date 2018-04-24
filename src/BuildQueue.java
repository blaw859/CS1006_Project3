import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BuildQueue {
  private int maxQueueLength;
  private int numberOfUnitsInQueue;
  private GameSimulator currentGame;
  private LinkedList<UnitBuildTime> buildQueue = new LinkedList<>();
  public static List<BuildQueue> allBuildQueues= new ArrayList<>();

  /**
   * Empties all the buildqueues so a new game can start fresh
   */
  public static void clearBuildQueues() {
    allBuildQueues.clear();
  }

  /**
   * Creates a new buildqueue for a building
   * @param queueLength The number of units that can be held in the queue at any one time
   * @param game the current game
   */
  public BuildQueue(int queueLength, GameSimulator game) {
    this.maxQueueLength = queueLength;
    numberOfUnitsInQueue = 0;
    allBuildQueues.add(this);
  }

  /**
   * Gets the number of units in the buildqueue
   * @return the number of units in this builqueue
   */
  public int getNumberOfUnitsInQueue() {
    return numberOfUnitsInQueue;
  }

  /**
   * Adds a unit to
   * @param unit
   * @return
   */
  public boolean addUnitToBuildQueue(Unit unit) {
    if (numberOfUnitsInQueue < maxQueueLength){
      buildQueue.add(new UnitBuildTime(unit, unit.getBuildTime()));
      numberOfUnitsInQueue++;
      return true;
    } else {
      return false;
    }
  }

  /**
   * This updates all of the buildLists that are currently active. If a unit is currently being built the time it has
   * until it is built is decremented and then when the unit is built it is made active and the next unit in the build
   * queue starts being built
   * @param currentGame The current game the buildqueues are for
   */
  public static void updateAllBuildLists(GameSimulator currentGame) {
    for (int i = 0; i < allBuildQueues.size(); i++) {
      if (!allBuildQueues.get(i).buildQueue.isEmpty()) {
        Unit unit = allBuildQueues.get(i).buildQueue.peek().unit;
        if (allBuildQueues.get(i).numberOfUnitsInQueue > 0) {
          allBuildQueues.get(i).buildQueue.peek().buildTime--;
          if (allBuildQueues.get(i).buildQueue.peek().buildTime == 0) {
            currentGame.numberOfActiveUnits.merge(unit, 1, (a, b) -> a + b);
            allBuildQueues.get(i).buildQueue.pop();
          }
        }
      }
    }
  }

  /**
   * This class stores a unit and the amount of time left until it is built
   */
  private class UnitBuildTime{
    Unit unit;
    int buildTime;

    public UnitBuildTime (Unit unit, int buildTime) {
      this.unit = unit;
      this.buildTime = buildTime;
    }
  }
}

