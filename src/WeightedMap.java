import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
//This class is used for giving weights both to instructions and games
public class WeightedMap<T> {
  private final NavigableMap<Double,T> weightedInstructions = new TreeMap<>();
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
    weightedInstructions.put(total, value);
    return this;
  }

  public T next() {
    double value = random.nextDouble()*total;
    return weightedInstructions.higherEntry(value).getValue();
  }

}