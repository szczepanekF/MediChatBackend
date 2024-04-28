package pl.logic.site.model.predictions.quality;

import pl.logic.site.model.mysql.Disease;
import pl.logic.site.model.predictions.features.DiseaseVector;
import pl.logic.site.model.predictions.quality.Label;

public class Result implements Label {
    // TODO remember to check that HashMap after DiseaseParser might be empty then this patient can't be in training set. (After DiseaseParser, before DiseaseVector)
    private Disease result;
    private Disease expected;
    private DiseaseVector diseaseVector;

    public Result(DiseaseVector diseaseVector, Disease result) {
        this.diseaseVector = diseaseVector;
        this.result = result;
        this.expected = diseaseVector.getDisease();
    }

    @Override
    public Disease getExpected() {
        return this.expected;
    }

    @Override
    public Disease getResult() {
        return this.result;
    }
}
