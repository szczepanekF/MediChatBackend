package pl.logic.site.model.predictions.metric;

import pl.logic.site.model.predictions.features.IFeatureVector;

public interface Metric {
    double calculateMetric(IFeatureVector v1, IFeatureVector v2);
}
