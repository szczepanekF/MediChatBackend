package pl.logic.site.model.predictions.statictic;

import pl.logic.site.model.predictions.quality.Result;

import java.util.List;

public interface Prediction {
    public Object getPrediction(List<Result> results);
}
