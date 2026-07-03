import com.platequest.app.PlateTracker;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlateTrackerLogicTest {
    public static void main(String[] args) {
        check("TGL7815".equals(PlateTracker.normalize("tgl-7815")), "normalize");
        check("TGL-7815".equals(PlateTracker.formatForDisplay("TGL7815")), "format");
        check(PlateTracker.isValid("TGL-7815"), "valid plate");
        check(!PlateTracker.isValid("ABC-12"), "invalid plate");

        List<Set<String>> progress = new ArrayList<>();
        for (int i = 0; i < 7; i++) progress.add(new HashSet<>());
        progress.get(0).add("T");
        List<PlateTracker.Discovery> discoveries = PlateTracker.findNewDiscoveries("TGL-7815", progress);
        check(discoveries.size() == 6, "new discoveries exclude existing position character");
        check(discoveries.get(0).positionIndex == 1 && "G".equals(discoveries.get(0).character), "position logic");
        check(PlateTracker.percentComplete(252) == 100, "completion percentage");
        System.out.println("All PlateTracker logic tests passed.");
    }

    private static void check(boolean condition, String name) {
        if (!condition) throw new AssertionError("Failed: " + name);
    }
}
