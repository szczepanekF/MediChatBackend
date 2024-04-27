package pl.logic.site.model.predictions.features;

import lombok.Getter;
import pl.logic.site.model.mysql.Disease;
import pl.logic.site.model.mysql.Patient;

import java.io.Serializable;
import java.util.HashMap;

@Getter
public class DiseaseVector implements Serializable {
    Disease disease;
    FeatureVector featureVector;

    public DiseaseVector(Disease disease, Patient patient, HashMap<String, String> symptoms) {
        this.disease = disease;
        this.featureVector = new FeatureVector(patient.getHeight(), patient.getWeight(), patient.getGender(), patient.getBirth_date(), symptoms);
    }
}
