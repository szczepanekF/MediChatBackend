package pl.logic.site.model.predictions.metric;

import pl.logic.site.model.predictions.features.Vector;

public interface Metric {
    double calculateMetric(Vector v1, Vector v2);
}
