package pl.logic.site.model.predictions.quality;

import java.util.ArrayList;
import java.util.List;

/**
 * A class with static methods used to assess the quality of classification for an algorithm.
 *
 * @author Kacper
 */
public class Quality {

    /**
     * Calculates the accuracy of a list of results.
     * That is, simply calculating the accuracy for the prediction made on the test set,
     * excluding the results for patients who have null in the disease
     * because there is nothing to refer to and calculating the accuracy would be pointless.
     *
     * @param results a list of results (from predictions made on the testing set)
     * @return the accuracy as a double value between 0 and 1
     */
    public static Double calculateAccuracy(List<Result> results) {
        List<Result> filteredResults = deleteNullExpectedResults(results);
        double accumulator = 0.0;
        for (Label result : filteredResults) {
            if (result.getExpected().getName().equals(result.getResult().getName())) accumulator++;
        }
        if (filteredResults.isEmpty()) return 0.0;
        else return (accumulator / filteredResults.size());
    }

    /**
     * Deletes all results from a list that have a null expected value.
     * For null expected values, it would be impossible to calculate precision
     *
     * @param results a list of results (from predictions made on the testing set)
     * @return a list of results without null expected values
     */
    private static List<Result> deleteNullExpectedResults(List<Result> results) {
        return results.stream().filter(r -> r.getExpected() != null).toList();
    }
}
