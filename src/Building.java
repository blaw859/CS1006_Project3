import java.util.ArrayList;
import java.util.HashMap;

public class Building {
    private String type;
    private int mineralCost;
    private int gasCost;
    private int buildTime;
    private int supplyProvided;
    private ArrayList<String> dependentOn;

    //The String being the building type and the int being the number of them currently built;
    private HashMap<String,Integer> typeCount;
}
