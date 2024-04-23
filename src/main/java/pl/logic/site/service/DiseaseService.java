package pl.logic.site.service;

import pl.logic.site.model.mysql.Disease;

import java.util.List;

public interface DiseaseService {
    Disease getDisease(int id);
    List<Disease> getDiseases();
}
