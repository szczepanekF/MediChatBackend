package pl.logic.site.model.predictions.statictic;

import pl.logic.site.model.mysql.Disease;
import pl.logic.site.model.predictions.quality.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiseasePrediction implements Prediction {
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
