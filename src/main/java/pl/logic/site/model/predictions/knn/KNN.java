package pl.logic.site.model.predictions.knn;

import pl.logic.site.model.mysql.Disease;
import pl.logic.site.model.predictions.features.DiseaseVector;
import pl.logic.site.model.predictions.metric.Metric;
import pl.logic.site.model.predictions.quality.Result;

import java.util.*;
import java.util.stream.Collectors;

public class KNN {
    private List<DiseaseVector> learningSet;

    public KNN(List<DiseaseVector> learningSet) {
        this.learningSet = learningSet;
    }

    public List<Result> classifyVectors(List<DiseaseVector> diseaseVectors, int neighbours, Metric m,
                                        List<Disease> diseases) {
        List<Result> results = new LinkedList<>();
        for (int i = 0; i < diseaseVectors.size(); i++) {
            results.add(i, classifyVector(diseaseVectors.get(i), neighbours, m, diseases));
        }
        return results;
    }

    public Result classifyVector(DiseaseVector diseaseVector, int neighbours, Metric m, List<Disease> diseases) {
        HashMap<DiseaseVector, Double> results = new HashMap<>();
        for (DiseaseVector feature : learningSet) {
            results.put(feature, m.calculateMetric(feature.getFeatureVector(), diseaseVector.getFeatureVector()));
        }
        Map<DiseaseVector, Double> nBest = results
                .entrySet().stream()
                .sorted(Map.Entry.<DiseaseVector, Double>comparingByValue())
                .limit(neighbours).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        HashMap<Disease, Integer> labels = new HashMap<>();
        for (Disease disease : diseases) {
            labels.put(disease, 0);
        }
        for (Map.Entry<DiseaseVector, Double> entry : nBest.entrySet()) {
            labels.computeIfPresent(entry.getKey().getDisease(), (k, v) -> v + 1);
        }

        int max = Collections.max(labels.values());
        Disease classifiedLabel = labels.entrySet().stream().filter(l -> l.getValue() == max).limit(1)
                .map(Map.Entry::getKey).collect(Collectors.toList()).get(0);

        return new Result(diseaseVector, classifiedLabel);
    }
}
