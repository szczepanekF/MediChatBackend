package pl.logic.site.model.predictions.quality;

import java.util.ArrayList;
import java.util.List;

public class Quality {
    public static Double calculateAccuracy(List<Result> results) {
        List<Result> filteredResults = deleteNullExpectedResults(results);
        double accumulator = 0.0;
        for (Label result : filteredResults) {
            if (result.getExpected().getName().equals(result.getResult().getName())) accumulator++;
        }
        if (filteredResults.isEmpty()) return 0.0;
        else return (accumulator / filteredResults.size());
    }

    private static List<Result> deleteNullExpectedResults(List<Result> results) {
        return results.stream().filter(r -> r.getExpected() != null).toList();
    }
}
