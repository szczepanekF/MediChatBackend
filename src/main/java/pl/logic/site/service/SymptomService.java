package pl.logic.site.service;

import pl.logic.site.model.mysql.Symptom;

import java.util.List;

public interface SymptomService {
    Symptom getSymptom(int id);
    List<Symptom> getSymptoms();
}
