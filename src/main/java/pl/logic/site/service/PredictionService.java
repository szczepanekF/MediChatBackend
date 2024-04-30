package pl.logic.site.service;

import pl.logic.site.model.mysql.Disease;

/**
 * This interface provides methods for predicting the disease of a patient based on various factors.
 *
 * @author Kacper
 */
public interface PredictionService {
    /**
     * Returns the predicted object by system.
     *
     * @return an object that is the result of the system's predictions
     */
    Object getStatisticDisease();

    /**
     * Returns the accuracy of the prediction model based on a given set of proportions.
     *
     * @param proportions the proportions between learningSet and testingSet
     * @return the accuracy of the prediction model
     */
    double getPredictionAccuracy(String[] proportions);

    /**
     * Returns the predicted disease of a specific patient (based on their ID).
     *
     * @param patientId the ID of the patient
     * @return the predicted disease of the patient
     */
    Disease getPatientDisease(int patientId);
}
