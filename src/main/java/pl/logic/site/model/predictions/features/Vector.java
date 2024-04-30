package pl.logic.site.model.predictions.features;

import java.util.Date;

/**
 * Interface for a vector of features used in KNN algorithm.
 *
 * @author Kacper
 */
public interface Vector {
    /**
     * Returns the information from patient table.
     *
     * @return the personal information features
     */
    Double[] getPersonalInfoFeatures();

    /**
     * Returns the dates of patient.
     * It could be the dates from different tables.
     *
     * @return the date features
     */
    Date[] getDateFeatures();

    /**
     * Returns the symptoms based on the patient's card.
     * The list will be as large as all symptoms but with particular emphasis on those of a specific patient.
     *
     * @return the symptom features
     */
    Double[] getSymptomFeatures();
}
