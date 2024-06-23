package pl.logic.site.service;

import pl.logic.site.model.mysql.DictionaryExamination;

import java.util.List;

public interface DictionaryExaminationService {
    DictionaryExamination getDictionaryExamination(int dictionaryExaminationId);

    List<DictionaryExamination> getDictionaryExaminations();
}
