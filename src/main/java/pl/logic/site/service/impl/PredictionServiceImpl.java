package pl.logic.site.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import pl.logic.site.model.mysql.DiagnosisRequest;
import pl.logic.site.model.mysql.Disease;
import pl.logic.site.model.mysql.Patient;
import pl.logic.site.model.mysql.Symptom;
import pl.logic.site.model.predictions.features.DiseaseVector;
import pl.logic.site.model.predictions.knn.KNN;
import pl.logic.site.model.predictions.metric.EuclideanMetric;
import pl.logic.site.model.predictions.parser.DiseaseParser;
import pl.logic.site.model.predictions.parser.SymptomParser;
import pl.logic.site.model.predictions.quality.Result;
import pl.logic.site.service.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static pl.logic.site.utils.predictions.PredictionConsts.K;

@Slf4j
@Service
public class PredictionServiceImpl implements PredictionService {
    @Autowired
    private DiagnosisRequestService diagnosisRequestService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private DiseaseService diseaseService;
    @Autowired
    private SymptomService symptomService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private List<DiseaseVector> learningSet;
    private List<DiseaseVector> testingSet;
    private EuclideanMetric euclideanMetric;

    @Override
    public Object getStatisticDisease() {
        return null;
    }

    @Override
    public double getPredictionAccuracy() {
        return 0;
    }

    // ciekawej jest jeśli pacjent którego podamy nie miał wcześniej żadnej choroby
    @Override
    public Disease getPatientDisease(int patientId) {
        SymptomParser symptomParser = new SymptomParser(this.jdbcTemplate);
        List<Disease> diseases = diseaseService.getDiseases();
        List<Patient> patients = patientService.getPatients();
        List<Symptom> symptoms = symptomService.getSymptoms();
        this.learningSet = new ArrayList<>();
        this.euclideanMetric = new EuclideanMetric();

        for (int i = 0; i < patients.size(); i++) {
            List<DiagnosisRequest> patientDiagnosisRequests = diagnosisRequestService.getDiagnosisRequests();//TODO change method to better
            Integer chartId = symptomParser.searchChartIdByPatientId(patients.get(i).getId());
            if (chartId == null) {
                continue;
            }
            HashMap<String, String> patientSymptoms = symptomParser.connectSymptoms(chartId, symptoms);
            for (int j = 0; j < patientDiagnosisRequests.size(); j++) {
                DiseaseParser diseaseParser = new DiseaseParser(patientDiagnosisRequests.get(j).getDaignosis(), diseases);
                List<Disease> patientDiseases = diseaseParser.getDiseases();
                if (!patientDiseases.isEmpty()) {
                    for (Disease disease : patientDiseases) {
                        this.learningSet.add(new DiseaseVector(disease, patients.get(i), patientSymptoms));
                    }
                }
            }
        }

        Patient patient = patientService.getPatient(patientId);
        Integer patientChartId = symptomParser.searchChartIdByPatientId(patientId);
        HashMap<String, String> patientSymptom;
        if (patientChartId == null) {
            patientSymptom = symptomParser.madeZeroSymptoms(symptoms);
        } else {
            patientSymptom = symptomParser.connectSymptoms(patientChartId, symptoms);
        }

        DiseaseVector patientDiseaseVector = new DiseaseVector(null, patient, patientSymptom);

        KNN knn = new KNN(learningSet);
        Result result = knn.classifyVector(patientDiseaseVector, K, euclideanMetric, diseases);

        return result.getResult();
    }
}
