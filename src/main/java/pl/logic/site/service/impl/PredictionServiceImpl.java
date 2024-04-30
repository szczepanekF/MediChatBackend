package pl.logic.site.service.impl;

import jakarta.annotation.PostConstruct;
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
import pl.logic.site.model.predictions.quality.Quality;
import pl.logic.site.model.predictions.quality.Result;
import pl.logic.site.model.predictions.statictic.DiseasePrediction;
import pl.logic.site.model.predictions.statictic.Prediction;
import pl.logic.site.service.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static pl.logic.site.utils.predictions.PredictionConsts.K;

/**
 * This service implementation class is responsible for making predictions about diseases based on symptoms.
 *
 * @author Kacper
 */
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

    private List<DiseaseVector> dataset;
    private List<DiseaseVector> learningSet;
    private List<DiseaseVector> testingSet;
    private EuclideanMetric euclideanMetric;
    private int numberOfCompleteDiseaseVectors;
    private SymptomParser symptomParser;

    private List<Disease> diseases;
    private List<Patient> patients;
    private List<Symptom> symptoms;

    /**
     * Creates a new prediction service.
     * This method initializes first part of necessary parameters.
     */
    public PredictionServiceImpl() {
        this.dataset = new ArrayList<>();
        this.learningSet = new ArrayList<>();
        this.testingSet = new ArrayList<>();
        this.euclideanMetric = new EuclideanMetric();
        this.numberOfCompleteDiseaseVectors = 0;
    }

    /**
     * This method initializes rest of necessary parameters.
     * DiseaseVector calculates for every patient who has a patient card and has previously suffered
     * from at least one disease.
     * Such DiseaseVectors are added to the dataset
     */
    @PostConstruct
    public void init() {
        this.symptomParser = new SymptomParser(this.jdbcTemplate);
        this.diseases = diseaseService.getDiseases();
        this.patients = patientService.getPatients();
        this.symptoms = symptomService.getSymptoms();
        log.info("Prediction service initialized");
        for (int i = 0; i < patients.size(); i++) {
            List<DiagnosisRequest> patientDiagnosisRequests;
            try {
                patientDiagnosisRequests = diagnosisRequestService.getDiagnosisRequests(patients.get(i).getId());//TODO change method for that one which will return diagnosis for patient by his ID
            } catch (Exception e) {
                log.error("Error while getting diagnosis requests for patient with ID: " + patients.get(i).getId());
                continue;
            }
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
                        this.dataset.add(new DiseaseVector(disease, patients.get(i), patientSymptoms));
                        this.numberOfCompleteDiseaseVectors++;
                    }
                }
            }
        }
    }


    /**
     * Gets the statistic disease for a set of patients without a health card or a previously diagnosed
     * disease (or both) and based on these patients what is the most popular disease.
     *
     * @return the statistic disease
     */
    @Override
    public Object getStatisticDisease() {
        this.learningSet = this.dataset;
        KNN knn = new KNN(learningSet);

        for (int i = 0; i < patients.size(); i++) {
            Integer chartId = symptomParser.searchChartIdByPatientId(patients.get(i).getId());
            HashMap<String, String> patientSymptom;
            if (chartId == null) {
                patientSymptom = symptomParser.madeZeroSymptoms(symptoms);
            } else {
                patientSymptom = symptomParser.connectSymptoms(chartId, symptoms);
            }
            this.testingSet.add(new DiseaseVector(null, patients.get(i), patientSymptom));
        }

        List<Result> results = knn.classifyVectors(testingSet, K, euclideanMetric, diseases);
        Prediction diseasePrediction = new DiseasePrediction();

        return (String) diseasePrediction.getPrediction(results);
    }

    /**
     * Gets the prediction accuracy.
     * Only patients with a patient card and at least one disease diagnosed are taken into account.
     *
     * @param proportions the proportions between learningSet and testingSet
     * @return the prediction accuracy
     */
    @Override
    public double getPredictionAccuracy(String[] proportions) {
        int[] proportionsInts = Quality.countProportions(proportions, numberOfCompleteDiseaseVectors);
        this.learningSet = dataset.subList(0, proportionsInts[0]);
        this.testingSet = dataset.subList(proportionsInts[0], numberOfCompleteDiseaseVectors);

        KNN knn = new KNN(learningSet);
        List<Result> results = knn.classifyVectors(testingSet, K, euclideanMetric, diseases);
        return Quality.calculateAccuracy(results);
    }

    /**
     * Gets a likely diagnosis for a specific patient.
     * The method counts the most likely disease for the patient.
     * They are most interesting for patients who have not previously been diagnosed with the disease.
     *
     * @param patientId the patient id
     * @return the patient disease
     */
    @Override
    public Disease getPatientDisease(int patientId) {
        Patient patient = patientService.getPatient(patientId);
        Integer patientChartId = symptomParser.searchChartIdByPatientId(patientId);
        HashMap<String, String> patientSymptom;
        if (patientChartId == null) {
            patientSymptom = symptomParser.madeZeroSymptoms(symptoms);
        } else {
            patientSymptom = symptomParser.connectSymptoms(patientChartId, symptoms);
        }

        DiseaseVector patientDiseaseVector = new DiseaseVector(null, patient, patientSymptom);
        this.learningSet = this.dataset;
        KNN knn = new KNN(learningSet);
        Result result = knn.classifyVector(patientDiseaseVector, K, euclideanMetric, diseases);

        return result.getResult();
    }
}
