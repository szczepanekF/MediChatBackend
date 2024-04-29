package pl.logic.site.service;

import pl.logic.site.model.dao.ExaminationDAO;
import pl.logic.site.model.mysql.Examination;

import java.util.List;

public interface ExaminationService {
    Examination createExamination(ExaminationDAO examination);

    void deleteExamination(int id);

    Examination updateExamination(ExaminationDAO examination, int id);

    Examination getExamination(int id);

    List<Examination> getExaminations(int examinationFilter);
}
