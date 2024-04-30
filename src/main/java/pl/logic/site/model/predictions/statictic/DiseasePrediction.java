package pl.logic.site.model.predictions.statictic;

import pl.logic.site.model.mysql.Disease;
import pl.logic.site.model.predictions.quality.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class implements the prediction algorithm for find the most common disease.
 *
 * @author Kacper
 */
public class DiseasePrediction implements Prediction {
    /**
     * Calculates the prediction based on the given list of results.
     * This prediction involves finding the most common disease predicted by the algorithm
     * as the most popular disease among patients from the test set.
     *
     * @param results the list of results
     * @return the most common disease
     */
    @Override
    public Object getPrediction(List<Result> results) {
        Map<String, Integer> diseaseCounts = new HashMap<>();
        for (Result result : results) {
            String disease = result.getResult().getName();
            diseaseCounts.put(disease, diseaseCounts.getOrDefault(disease, 0) + 1);
        }

        String mostCommonDiseaseName = null;
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : diseaseCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                mostCommonDiseaseName = entry.getKey();
                maxCount = entry.getValue();
            }
        }
        return mostCommonDiseaseName;
    }
}
