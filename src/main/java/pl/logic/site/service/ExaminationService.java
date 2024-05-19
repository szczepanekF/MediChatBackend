package pl.logic.site.service;

import pl.logic.site.model.dao.ExaminationDAO;
import pl.logic.site.model.mysql.Examination;

import java.util.List;

public interface ExaminationService {
    Examination createExamination(ExaminationDAO examination);

    void deleteExamination(int examinationID);

    Examination updateExamination(ExaminationDAO examination, int examinationID);

    Examination getExamination(int examinationID);

    List<Examination> getExaminations(int patientId);
}
