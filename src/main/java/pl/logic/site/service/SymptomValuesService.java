package pl.logic.site.service;

import pl.logic.site.model.mysql.Symptom;
import pl.logic.site.model.mysql.SymptomValues;

import java.util.List;

public interface SymptomValuesService {
    List<SymptomValues> getSymptomsValues();
}
