package pl.logic.site.model.predictions.features;

import java.util.Date;

public interface Vector {
    Double[] getPersonalInfoFeatures();

    Date[] getDateFeatures();

    Double[] getSymptomFeatures();
}
