package pl.logic.site.model.predictions.quality;

import pl.logic.site.model.mysql.Disease;
import pl.logic.site.model.predictions.quality.Label;

public class Result implements Label {
    // TODO remember to check that HashMap after DiseaseParser might be empty then this patient can't be in training set. (After DiseaseParser, before DiseaseVector)

    @Override
    public Disease getExpected() {
        return null;
    }

    @Override
    public Disease getResult() {
        return null;
    }
}
