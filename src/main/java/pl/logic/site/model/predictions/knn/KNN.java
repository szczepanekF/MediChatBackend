package pl.logic.site.model.predictions.knn;

import pl.logic.site.model.mysql.Disease;
import pl.logic.site.model.predictions.features.DiseaseVector;
import pl.logic.site.model.predictions.metric.Metric;
import pl.logic.site.model.predictions.quality.Result;

import java.util.*;
import java.util.stream.Collectors;

/**
 * KNN (k-nearest neighbors) algorithm implementation.
 * A class that uses its methods to find the most probable disease for a given patient based on the learning set.
 *
 * @author Kacper
 */
public class KNN {
    private List<DiseaseVector> learningSet;

    /**
     * Creates a new instance of the KNN algorithm with the specified learning set.
     *
     * @param learningSet the set of DiseaseVectors used for learning.
     *                    Important: disease in DiseaseVector in learningSet can't be null!
     */
    public KNN(List<DiseaseVector> learningSet) {
        this.learningSet = learningSet;
    }

    /**
     * Classifies a list of disease vectors (testingSet) using the KNN algorithm with the specified parameters.
     * A method to predict probable diseases in the entire test set.
     * Typically used to calculate the accuracy of a prediction or statistics system.
     *
     * @param diseaseVectors the list of disease vectors (testingSet) to classify
     * @param neighbours     the number of nearest neighbors to use for classification
     * @param m              the kind of distance metric to use for classification
     * @param diseases       the list of all available diseases (from database) to classify the vectors against
     * @return the list of classification results
     */
    public List<Result> classifyVectors(List<DiseaseVector> diseaseVectors, int neighbours, Metric m,
                                        List<Disease> diseases) {
        List<Result> results = new LinkedList<>();
        for (int i = 0; i < diseaseVectors.size(); i++) {
            results.add(i, classifyVector(diseaseVectors.get(i), neighbours, m, diseases));
        }
        return results;
    }

    /**
     * Classifies a single disease vector (with trainingSet) using the KNN algorithm with the specified parameters.
     * Used to count a predicate for a single person or as part of the classifyVectors method
     *
     * @param diseaseVector the disease vector (with trainingSet) to classify
     * @param neighbours    the number of nearest neighbors to use for classification
     * @param m             the kind of distance metric to use for classification
     * @param diseases      the list of all available diseases (from database) to classify the vector against
     * @return the classification result
     */
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
                .map(Map.Entry::getKey).toList().getFirst();

        return new Result(diseaseVector, classifiedLabel);
    }
}
