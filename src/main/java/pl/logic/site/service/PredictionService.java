package pl.logic.site.service;

import pl.logic.site.model.mysql.Disease;

public interface PredictionService {
    Object getStatisticDisease();
    double getPredictionAccuracy();
    Disease getPatientDisease(int patientId);
}
