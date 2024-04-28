package pl.logic.site.model.predictions.metric;

import pl.logic.site.model.predictions.features.Vector;

/**
 * Interface for metrics used to calculate the distance between two vectors.
 *
 * @author Kacper
 */
public interface Metric {
    /**
     * Calculates the distance between two vectors.
     *
     * @param v1 the first vector
     * @param v2 the second vector
     * @return the distance between the two vectors
     */
    double calculateMetric(Vector v1, Vector v2);
}
