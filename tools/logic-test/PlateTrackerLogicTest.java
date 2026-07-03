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
        check(!PlateTracker.isValid("T1L-7815"), "first three positions require letters");
        check(!PlateTracker.isValid("ABC-DEFG"), "last four positions require digits");
        check(PlateTracker.totalSlotsForPosition(2) == 26, "first three positions track letters only");
        check(PlateTracker.totalSlotsForPosition(3) == 10, "last four positions track digits only");

        List<Set<String>> progress = new ArrayList<>();
        for (int i = 0; i < 7; i++) progress.add(new HashSet<>());
        progress.get(0).add("T");
        List<PlateTracker.Discovery> discoveries = PlateTracker.findNewDiscoveries("TGL-7815", progress);
        check(discoveries.size() == 6, "new discoveries exclude existing position character");
        check(discoveries.get(0).positionIndex == 1 && "G".equals(discoveries.get(0).character), "position logic");
        check(PlateTracker.percentComplete(118) == 100, "completion percentage");
        System.out.println("All PlateTracker logic tests passed.");
    }

    private static void check(boolean condition, String name) {
        if (!condition) throw new AssertionError("Failed: " + name);
    }
}
