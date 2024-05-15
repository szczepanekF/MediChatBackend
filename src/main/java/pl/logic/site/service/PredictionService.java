package pl.logic.site.service;

import pl.logic.site.model.mysql.Disease;
import pl.logic.site.model.mysql.Doctor;

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
     * @param chartId the patient's chart id (if patient does not have a chart, then give 0 as charId).
     * @return the predicted disease of the patient
     */
    Disease getPatientDisease(int chartId);

    /**
     * Returns the number of future diagnosis requests in the next daysInterval.
     * Max number of how many interval will be considered is in MAX_DEEP_OF_PREDICTIONS.
     *
     * @param daysInterval - how many days have the single interval
     * @return the number of future diagnosis requests in next daysInterval
     */
    double getFutureDiagnosisRequest(int daysInterval);

    /**
     * Returns the doctor who is most wanted by patients in the next daysInterval.
     * Max number of how many interval will be considered is in MAX_DEEP_OF_PREDICTIONS.
     *
     * @param daysInterval - how many days have the single interval
     * @return the doctor who is most wanted by patients in the next daysInterval
     */
    Doctor getMostWantedDoctor(int daysInterval);
}
