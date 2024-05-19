package pl.logic.site.service;

import pl.logic.site.model.mysql.DiseaseSymptom;

import java.util.List;

public interface DiseaseSymptomService {
    DiseaseSymptom getDiseaseSymptom(int id);

    List<DiseaseSymptom> getDiseaseSymptomsForSymptom(int symptomId);

    List<DiseaseSymptom> getDiseaseSymptomsForDisease(int diseaseId);

    List<DiseaseSymptom> getDiseaseSymptoms();
}
