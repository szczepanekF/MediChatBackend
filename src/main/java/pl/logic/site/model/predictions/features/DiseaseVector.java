package pl.logic.site.model.predictions.features;

import lombok.Getter;
import pl.logic.site.model.mysql.Disease;
import pl.logic.site.model.mysql.Patient;

import java.io.Serializable;
import java.util.HashMap;

/**
 * A class that represents a vector of features for a given patient and takes into account the label (disease).
 *
 * @Getter means that all fields will be private and there will be no setters.
 *
 * @author Kacper
 */
@Getter
public class DiseaseVector implements Serializable {
    private final Disease disease;
    private final FeatureVector featureVector;

    /**
     * Creates a new DiseaseVector instance.
     *
     * @param disease The disease that the patient has
     * @param patient The patient for whom the vector is being created
     * @param symptoms A map of all symptoms, including those occurring in the patient
     */
    public DiseaseVector(Disease disease, Patient patient, HashMap<String, String> symptoms) {
        this.disease = disease;
        this.featureVector = new FeatureVector(patient.getHeight(), patient.getWeight(), patient.getGender(), patient.getBirth_date(), symptoms);
    }
}
