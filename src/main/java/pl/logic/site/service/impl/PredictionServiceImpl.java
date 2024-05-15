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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static pl.logic.site.model.predictions.statictic.StatisticPrediction.getDiagnosisRequestsByDaysInterval;
import static pl.logic.site.utils.predictions.PredictionConsts.K;
import static pl.logic.site.utils.predictions.PredictionConsts.MAX_DEEP_OF_PREDICTIONS;

/**
 * This service implementation class is responsible for making predictions about diseases based on symptoms.
 *
 * @author Kacper
 */
@Slf4j
@Service
public class PredictionServiceImpl implements PredictionService {
    //TODO Dostosuj klasę pod to że dany pacjent może mieć wiele kart
    @Autowired
    private DiagnosisRequestService diagnosisRequestService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private DiseaseService diseaseService;
    @Autowired
    private SymptomService symptomService;
    @Autowired
    private ChartService chartService;
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
                patientDiagnosisRequests = diagnosisRequestService.getDiagnosisRequests(symptomParser.searchChartIdByPatientId(patients.get(i).getId()));
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
                DiseaseParser diseaseParser = new DiseaseParser(patientDiagnosisRequests.get(j).getDiagnosis(), diseases);
                List<Disease> patientDiseases = diseaseParser.getDiseases();
                if (!patientDiseases.isEmpty()) {
                    for (Disease disease : patientDiseases) {
                        this.dataset.add(new DiseaseVector(disease, patients.get(i), patientSymptoms));
                        this.numberOfCompleteDiseaseVectors++;
                    }
                }
            }
        }
        log.info("Dataset initialized");
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
        log.info("Testing set prepared");

        List<Result> results = knn.classifyVectors(testingSet, K, euclideanMetric, diseases);
        Prediction diseasePrediction = new DiseasePrediction();
        log.info("Prediction made successfully");

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
        log.info("Successfully divided dataset on learning set and testing set by given proportion");

        KNN knn = new KNN(learningSet);
        List<Result> results = knn.classifyVectors(testingSet, K, euclideanMetric, diseases);
        log.info("Compute accuracy successfully");

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
        log.info("Prediction made successfully");

        return result.getResult();
    }

    /**
     * Gets the number of future diagnosis requests in the next daysInterval.
     * The maximum number of intervals considered is MAX_DEEP_OF_PREDICTIONS.
     * From the current time it subtracts the interval as many times as it is
     * in MAX_DEEP_OF_PREDICTIONS.
     *
     * @param daysInterval how many days have the single interval
     * @return the number of future diagnosis requests in the next daysInterval
     */
    @Override
    public double getFutureDiagnosisRequest(int daysInterval) {
        List<Integer> results = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        for (int i = 1; i <= MAX_DEEP_OF_PREDICTIONS; i++) {
            results.add(getDiagnosisRequestsByDaysInterval(
                    this.jdbcTemplate, daysInterval * i, currentDate));
            currentDate = currentDate.minusDays(daysInterval);
        }
        return results.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }
}
