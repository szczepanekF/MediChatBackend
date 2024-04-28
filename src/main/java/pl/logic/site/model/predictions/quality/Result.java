package pl.logic.site.model.predictions.quality;

import pl.logic.site.model.mysql.Disease;
import pl.logic.site.model.predictions.features.DiseaseVector;
import pl.logic.site.model.predictions.quality.Label;

/**
 * A class that represents the result of a prediction.
 * It contains the predicted disease and the expected disease, along with the vector of features used for prediction.
 *
 * @author Kacper
 */
public class Result implements Label {

    private Disease result;
    private Disease expected;
    private DiseaseVector diseaseVector;

    /**
     * Constructs a new Result object.
     *
     * @param diseaseVector the vector of features used for prediction
     * @param result        the predicted disease
     */
    public Result(DiseaseVector diseaseVector, Disease result) {
        this.diseaseVector = diseaseVector;
        this.result = result;
        this.expected = diseaseVector.getDisease();
    }

    /**
     * Returns the expected disease.
     *
     * @return the expected disease
     */
    @Override
    public Disease getExpected() {
        return this.expected;
    }

    /**
     * Returns the predicted disease.
     *
     * @return the result disease
     */
    @Override
    public Disease getResult() {
        return this.result;
    }
}
