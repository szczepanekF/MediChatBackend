package pl.logic.site.service;

import pl.logic.site.model.dao.DiagnosisRequestDAO;
import pl.logic.site.model.mysql.DiagnosisRequest;


import java.util.List;

public interface DiagnosisRequestService {
    DiagnosisRequest createDiagnosisRequest(DiagnosisRequestDAO diagnosisRequest);

    void deleteDiagnosisRequest(int id);

    DiagnosisRequest updateDiagnosisRequest(DiagnosisRequestDAO diagnosisRequest, int id);

    DiagnosisRequest getDiagnosisRequest(int id);

    DiagnosisRequest getDiagnosisRequestByChart(int id);

    List<DiagnosisRequest> getAllDiagnosisRequestsByChart(int filter);
}
