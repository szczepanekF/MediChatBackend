package pl.logic.site.model.predictions.statictic;

import pl.logic.site.model.predictions.quality.Result;

import java.util.List;

/**
 * Interface for making predictions (statistics) based on a list of results.
 *
 * @author Kacper
 */
public interface Prediction {
    /**
     * Makes a prediction (statistic) based on a list of results.
     *
     * @param results a list of results
     * @return the prediction
     */
    public Object getPrediction(List<Result> results);
}
