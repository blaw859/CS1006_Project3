import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
//This class is used for giving weights both to instructions and games

/**
 * Class to used to assign weights to instructions or games
 * I came across this source code online and it was the best way of doing it
 * Title: Weighted Map
 * Author: Peter Lawrey
 * Date: 2017
 * Availability: https://stackoverflow.com/a/6409791
 * @param <T>
 */
public class WeightedMap<T> {
  private final NavigableMap<Double,T> weightedMap = new TreeMap<>();
  private final Random random;
  private double total;

  public WeightedMap() {
    this(new Random());
  }

  public WeightedMap(Random random) {
    this.random = random;
  }

  public WeightedMap<T> add (double weight, T value) {
    if (weight <= 0) return this;
    total += weight;
    weightedMap.put(total, value);
    return this;
  }

  public T next() {
    double value = random.nextDouble()*total;
    return weightedMap.higherEntry(value).getValue();
  }

}