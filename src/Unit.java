import java.util.HashMap;
import java.util.List;

public class Unit {
    private String type;
    private int mineralCost;
    private int gasCost;
    private int buildTime;
    private int warpGateCooldown;
    private int supplyNeeded;
    private String dependentOn;
    private String builtFrom;

    //The String being the building type and the int being the number of them currently built;
    private static HashMap<String,Integer> typeCount;
}

