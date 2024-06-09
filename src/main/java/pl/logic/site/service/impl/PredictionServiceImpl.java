package pl.logic.site.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.mysql.*;
import pl.logic.site.model.predictions.features.DiseaseVector;
import pl.logic.site.model.predictions.knn.KNN;
import pl.logic.site.model.predictions.metric.EuclideanMetric;
import pl.logic.site.model.predictions.parser.DiseaseParser;
import pl.logic.site.model.predictions.parser.SymptomParser;
import pl.logic.site.model.predictions.quality.Quality;
import pl.logic.site.model.predictions.quality.Result;
import pl.logic.site.model.predictions.statictic.DiseasePrediction;
import pl.logic.site.model.predictions.statictic.Prediction;
import pl.logic.site.repository.RecognitionRepository;
import pl.logic.site.service.*;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static pl.logic.site.model.predictions.statictic.StatisticPrediction.*;
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
    @Autowired
    private DiagnosisRequestService diagnosisRequestService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private DiseaseService diseaseService;
    @Autowired
    private SymptomService symptomService;
    @Autowired
    private ChartService chartService;
    @Autowired
    private ChartSymptomService chartSymptomService;
    @Autowired
    private RecognitionRepository recognitionRepository;
    @Autowired
    private StatisticsService statisticsService;


    private List<DiseaseVector> dataset;
    private List<DiseaseVector> learningSet;
    private List<DiseaseVector> testingSet;
    private EuclideanMetric euclideanMetric;
    private int numberOfCompleteDiseaseVectors;
    private SymptomParser symptomParser;

    private List<Disease> diseases;
    private List<Patient> patients;
    private List<Chart> charts;
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
        this.symptomParser = new SymptomParser(chartService, recognitionRepository, symptomService);
        this.diseases = diseaseService.getDiseases();
        this.patients = patientService.getPatients();
        this.charts = chartService.getAllCharts();
        this.symptoms = symptomService.getSymptoms();
        log.info("Prediction service initialized");
        for (int i = 0; i < patients.size(); i++) {
            List<DiagnosisRequest> patientDiagnosisRequests = new ArrayList<>();
            try {
                List<Integer> chartIds = symptomParser.searchChartIdByPatientId(patients.get(i).getId());
                for (Integer chartId : chartIds) {
                    List<DiagnosisRequest> newDiagnosisRequests = diagnosisRequestService.getAllDiagnosisRequestsByChart(chartId);
                    patientDiagnosisRequests.addAll(newDiagnosisRequests);
                }
//                patientDiagnosisRequests = diagnosisRequestService.getDiagnosisRequests(symptomParser.searchChartIdByPatientId(patients.get(i).getId()));
            } catch (Exception e) {
                log.error("Error while getting diagnosis requests for patient with ID: " + patients.get(i).getId());
                continue;
            }
            List<Integer> chartIds = symptomParser.searchChartIdByPatientId(patients.get(i).getId());
            if (chartIds.isEmpty()) {
                continue;
            }
            for (Integer chartId : chartIds) {
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
            List<Integer> chartIds = symptomParser.searchChartIdByPatientId(patients.get(i).getId());
            if (chartIds == null || chartIds.isEmpty()) {
                HashMap<String, String> patientSymptom;
                patientSymptom = symptomParser.madeZeroSymptoms(symptoms);
                this.testingSet.add(new DiseaseVector(null, patients.get(i), patientSymptom));
                continue;
            }
            for (Integer chartId : chartIds) {
                HashMap<String, String> patientSymptom;
                if (chartId == null) {
                    patientSymptom = symptomParser.madeZeroSymptoms(symptoms);
                } else {
                    patientSymptom = symptomParser.connectSymptoms(chartId, symptoms);
                }
                this.testingSet.add(new DiseaseVector(null, patients.get(i), patientSymptom));
            }
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
     * @param chartId the patient's chart id (if patient does not have a chart, then give 0 as charId).
     * @return the patient disease
     */
    @Override
    public Disease getPatientDisease(int chartId) {
        int patientId = chartService.getChart(chartId).getIdPatient();
        Patient patient = patientService.getPatient(patientId);

        HashMap<String, String> patientSymptom;

        if (chartId == 0) {
            patientSymptom = symptomParser.madeZeroSymptoms(symptoms);
        } else {
            patientSymptom = symptomParser.connectSymptoms(chartId, symptoms);
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
     * A weighted average is calculated to increase the impact of the final intervals
     *
     * @param daysInterval how many days have the single interval
     * @return the number of future diagnosis requests in the next daysInterval
     */
    @Override
    public double getFutureDiagnosisRequest(int daysInterval) {
        List<Integer> results = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        for (int i = 1; i <= MAX_DEEP_OF_PREDICTIONS; i++) {
            results.add(getDiagnosisRequestsSizeByDaysInterval(
                    diagnosisRequestService, daysInterval, currentDate) * (MAX_DEEP_OF_PREDICTIONS - i + 1));
            currentDate = currentDate.minusDays(daysInterval);
        }
        return calculateFraction(results);
    }

    /**
     * Gets the most wanted doctor in the next daysInterval.
     * The maximum number of intervals considered is MAX_DEEP_OF_PREDICTIONS.
     * In the case of several doctors with the same result, the one found first is taken
     *
     * @param daysInterval - how many days have the single interval
     * @return - the most wanted doctor in the next daysInterval
     */
    @Override
    public Doctor getMostWantedDoctor(int daysInterval) {
        List<HashMap<Integer, Integer>> doctorsCounter = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        List<Doctor> doctors = this.doctorService.getDoctors(2); // because filter == 2 returns all doctors
        for (int i = 1; i <= MAX_DEEP_OF_PREDICTIONS; i++) {
            doctorsCounter.add(getDoctorsInInterval(diagnosisRequestService, doctors, daysInterval, currentDate));
            currentDate = currentDate.minusDays(daysInterval);
        }

        HashMap<Integer, Double> doctorsWeightAverage = getIntegerDoubleHashMap(doctorsCounter);

        Optional<Map.Entry<Integer, Double>> maxEntry = doctorsWeightAverage.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue());

        if (maxEntry.isPresent()) {
            Integer mostWantedDoctorId = maxEntry.get().getKey();
            return doctorService.getDoctor(mostWantedDoctorId);
        } else {
            throw new EntityNotFound("No doctor found");
        }
    }


    @Override
    public List<String> getTopNDiseases(int N) throws IllegalArgumentException {
        List<String> diseasesNames = new ArrayList<String>();
        List<Disease> diseases = getTopNDiseasesToDiseases(N);
        for (Disease disease : diseases) {
            diseasesNames.add(disease.getName());
        }
        return diseasesNames;
    }


    @Override
    public List<Object> getSymptomsPredictionInInterval(Date fromDate, Date toDate) {
        List<Object> results = new ArrayList<>();
        LocalDate startDate = statisticsService.convertToLocalDate(fromDate);
        LocalDate endDate = statisticsService.convertToLocalDate(toDate);
        List<DiagnosisRequest> allDiagnosisRequests = diagnosisRequestService.getAllDiagnosisRequests();

//        List<Integer> intervalList = getIntervalList(startDate, endDate);
//        System.out.println(intervalList);
//        System.out.println(getDatesInIntervals(startDate, endDate));
//        System.out.println(statisticsService.generateDateRange(fromDate, toDate));

        List<String> symptomsNames = getSymptomsNames();
        List<String> dates = statisticsService.generateDateRange(fromDate, toDate);
        List<List<Double>> symptomsCountInIntervals = getSymptomsCountInIntervals(startDate, endDate, allDiagnosisRequests);

        results.add(symptomsNames);
        results.add(dates);
        results.add(symptomsCountInIntervals);

        return results;
    }

    @Override
    public List<Object> getDiseasesPredictionInInterval(Date fromDate, Date toDate) {
        List<Object> results = new ArrayList<>();
        LocalDate startDate = statisticsService.convertToLocalDate(fromDate);
        LocalDate endDate = statisticsService.convertToLocalDate(toDate);
        List<DiagnosisRequest> allDiagnosisRequests = diagnosisRequestService.getAllDiagnosisRequests();

        List<String> diseasesNames = getDiseasesNames();
        List<String> dates = statisticsService.generateDateRange(fromDate, toDate);
        List<List<Double>> diseasesCountInIntervals = getDiseasesCountInIntervals(startDate, endDate, allDiagnosisRequests);

        results.add(diseasesNames);
        results.add(dates);
        results.add(diseasesCountInIntervals);

        return results;
    }

    @Override
    public List<Object> getAgeGroupSymptomsPredictionInInterval(Date fromDate, Date toDate, String ageGroup) {
        List<Object> results = new ArrayList<>();
        LocalDate startDate = statisticsService.convertToLocalDate(fromDate);
        LocalDate endDate = statisticsService.convertToLocalDate(toDate);
        List<DiagnosisRequest> allDiagnosisRequests = diagnosisRequestService.getAllDiagnosisRequests();

        int[] ageGroupInt = convertRangeStringToArray(ageGroup);

        allDiagnosisRequests = getDiagnosisRequestsByAgeGroups(allDiagnosisRequests, ageGroupInt);

        List<String> symptomsNames = getSymptomsNames();
        List<String> dates = statisticsService.generateDateRange(fromDate, toDate);
        List<List<Double>> symptomsCountInIntervals = getSymptomsCountInIntervals(startDate, endDate, allDiagnosisRequests);

        results.add(symptomsNames);
        results.add(dates);
        results.add(symptomsCountInIntervals);

        return results;
    }

    @Override
    public List<Object> getAgeGroupDiseasesPredictionInInterval(Date fromDate, Date toDate, String ageGroup) {
        List<Object> results = new ArrayList<>();
        LocalDate startDate = statisticsService.convertToLocalDate(fromDate);
        LocalDate endDate = statisticsService.convertToLocalDate(toDate);
        List<DiagnosisRequest> allDiagnosisRequests = diagnosisRequestService.getAllDiagnosisRequests();

        int[] ageGroupInt = convertRangeStringToArray(ageGroup);

        allDiagnosisRequests = getDiagnosisRequestsByAgeGroups(allDiagnosisRequests, ageGroupInt);

        List<String> diseasesNames = getDiseasesNames();
        List<String> dates = statisticsService.generateDateRange(fromDate, toDate);
        List<List<Double>> diseasesCountInIntervals = getDiseasesCountInIntervals(startDate, endDate, allDiagnosisRequests);

        results.add(diseasesNames);
        results.add(dates);
        results.add(diseasesCountInIntervals);

        return results;
    }


    @Override
    public List<String> getSymptomsNames() {
        List<String> symptomsNames = new ArrayList<>();
        for (Symptom symptom : symptoms) {
            symptomsNames.add(symptom.getName());
        }
        return symptomsNames;
    }

    @Override
    public List<String> getDiseasesNames() {
        List<String> diseasesNames = new ArrayList<>();
        for (Disease disease : diseases) {
            diseasesNames.add(disease.getName());
        }
        return diseasesNames;
    }

    @Override
    public List<LocalDate> getDatesInIntervals(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dates = new ArrayList<>();
        List<Integer> intervalList = getIntervalList(startDate, endDate);
//        System.out.println(intervalList);
//        System.out.println(symptoms);

        dates.add(startDate);
        for (int i = 0; i < intervalList.size(); i++) {
            startDate = startDate.plusDays(intervalList.get(i));
            dates.add(startDate);
        }
        return dates;
    }

    @Override
    public List<List<Double>> getSymptomsCountInIntervals(LocalDate startDate, LocalDate endDate, List<DiagnosisRequest> allDiagnosisRequests) {
        List<List<Double>> results = new ArrayList<>();
        for (Symptom symptom : symptoms) {
            log.info("Symptom: " + symptom.getName() + " id: " + symptom.getId() + " is being processed");
            List<Double> symptomCountInIntervals = getSymptomCountInIntervals(startDate, endDate, symptom.getId(), allDiagnosisRequests);
            results.add(symptomCountInIntervals);
        }
        return results;
    }


    @Override
    public List<Double> getSymptomCountInIntervals(LocalDate startDate, LocalDate endDate, int symptomId, List<DiagnosisRequest> allDiagnosisRequests) {
//        List<DiagnosisRequest> allDiagnosisRequests = diagnosisRequestService.getAllDiagnosisRequests();
        List<ChartSymptom> chartSymptoms = chartSymptomService.getAllChartSymptoms();
        List<Integer> intervalList = getIntervalList(startDate, endDate);
        List<Double> results = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();

        for (int i = 0; i < intervalList.size(); i++) {
            int intervalSum = (int) ChronoUnit.DAYS.between(currentDate, startDate);
            ;
            for (int j = 0; j <= i; j++) {
                intervalSum += intervalList.get(j);
            }
            results.add(getSymptomCountInInterval(allDiagnosisRequests, chartSymptoms, intervalSum, symptomId));
        }
        return results;
    }

    @Override
    public List<List<Double>> getDiseasesCountInIntervals(LocalDate startDate, LocalDate endDate, List<DiagnosisRequest> allDiagnosisRequests) {
        List<List<Double>> results = new ArrayList<>();
        for (Disease disease : diseases) {
            log.info("Disease: " + disease.getName() + " id: " + disease.getId() + " is being processed");
            List<Double> diseaseCountInIntervals = getDiseaseCountInIntervals(startDate, endDate, disease.getId(), allDiagnosisRequests);
            results.add(diseaseCountInIntervals);
        }
        return results;
    }

    @Override
    public List<Double> getDiseaseCountInIntervals(LocalDate startDate, LocalDate endDate, int diseaseId, List<DiagnosisRequest> allDiagnosisRequests) {
//        List<DiagnosisRequest> allDiagnosisRequests = diagnosisRequestService.getAllDiagnosisRequests();
        List<Disease> diseases = this.diseases;
        List<Integer> intervalList = getIntervalList(startDate, endDate);
        List<Double> results = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();

        for (int i = 0; i < intervalList.size(); i++) {
            int intervalSum = (int) ChronoUnit.DAYS.between(currentDate, startDate);
            ;
            for (int j = 0; j <= i; j++) {
                intervalSum += intervalList.get(j);
            }
            results.add(getDiseaseCountInInterval(allDiagnosisRequests, diseases, intervalSum, diseaseId));
        }
        return results;
    }

    private Double getDiseaseCountInInterval(List<DiagnosisRequest> allDiagnosisRequests, List<Disease> diseases, int daysInterval, int diseaseId) {
        List<Integer> diseaseCounter = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();

        for (int i = 1; i <= MAX_DEEP_OF_PREDICTIONS; i++) {
            diseaseCounter.add(getDiseaseCountInDaysInterval(allDiagnosisRequests, diseases, daysInterval, currentDate, diseaseId) * (MAX_DEEP_OF_PREDICTIONS - i + 1));
            currentDate = currentDate.minusDays(daysInterval);
        }
        return calculateFraction(diseaseCounter);
    }

    private Double getSymptomCountInInterval(List<DiagnosisRequest> allDiagnosisRequests, List<ChartSymptom> chartSymptoms, int daysInterval, int symptomId) {
        List<Integer> symptomCounter = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();

        for (int i = 1; i <= MAX_DEEP_OF_PREDICTIONS; i++) {
            symptomCounter.add(getSymptomCountInDaysInterval(allDiagnosisRequests, chartSymptoms, daysInterval, currentDate, symptomId) * (MAX_DEEP_OF_PREDICTIONS - i + 1));
            currentDate = currentDate.minusDays(daysInterval);
        }
        return calculateFraction(symptomCounter);
    }

    private List<Disease> getTopNDiseasesToDiseases(int N) {
        if (N < 0) {
            throw new IllegalArgumentException("N must be greater than 0");
        }

        Map<Disease, Integer> diseaseCount = new HashMap<>();
        for (Chart chart : this.charts) {
            Disease disease = getPatientDisease(chart.getId());
            diseaseCount.put(disease, diseaseCount.getOrDefault(disease, 0) + 1);
        }

        List<Map.Entry<Disease, Integer>> diseaseCountList = new ArrayList<>(diseaseCount.entrySet());
        diseaseCountList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        if (N > diseaseCountList.size()) {
//            throw new IllegalArgumentException("N must be less than or equal to " + diseaseCountList.size());
            log.warn("N should be less than or equal to " + diseaseCountList.size());
//            System.out.println("N should be less than or equal to " + diseaseCountList.size());
        }

        List<Disease> topNDiseases = new ArrayList<>();
        for (int i = 0; i < N && i < diseaseCountList.size(); i++) {
            topNDiseases.add(diseaseCountList.get(i).getKey());
        }
//        for (int i = 0; i < N; i++) {
//            topNDiseases.add(diseaseCountList.get(i).getKey());
//        }

        return topNDiseases;
    }


    private List<Integer> getIntervalList(LocalDate startDate, LocalDate endDate) {
        List<Integer> intervalList = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(startDate);

        long days = ChronoUnit.DAYS.between(startDate, endDate);
        int interval = 1;
        if (days >= 31 * 2 + 1) {
            interval = yearMonth.lengthOfMonth();
            intervalList.add(interval);

            long months = ChronoUnit.MONTHS.between(startDate, endDate);

            for (int i = 1; i < months+1; i++) {
                yearMonth = yearMonth.plusMonths(1);
                interval = yearMonth.lengthOfMonth();
                intervalList.add(interval);
            }
        } else {
            for (int i = 0; i < days+1; i++) {
                intervalList.add(interval);
            }
        }

//        System.out.println(intervalList);
        return intervalList;
    }

    private Double calculateFraction(List<Integer> counter) {
        int denominator = 0;
        for (int i = 1; i <= MAX_DEEP_OF_PREDICTIONS; i++) {
            denominator += i;
        }
        int meter = counter.stream().mapToInt(Integer::intValue).sum();

        return roundToTwoDecimalPlaces((double) meter / denominator);
    }

    private List<DiagnosisRequest> getDiagnosisRequestsByAgeGroups(List<DiagnosisRequest> allDiagnosisRequests, int[] ageGroup) {
        List<DiagnosisRequest> allDiagnosisRequestsCopy = new ArrayList<>();
        for (DiagnosisRequest request : allDiagnosisRequests) {
            for (Chart chart : this.charts) {
                if (chart.getId() == request.getIdChart()) {
                    int patientId = chart.getIdPatient();
                    Patient patient = patientService.getPatient(patientId);
                    int age = patient.getAge();
                    if (age >= ageGroup[0] && age <= ageGroup[1]) {
                        allDiagnosisRequestsCopy.add(request);
                    }
                }
            }
        }

        return allDiagnosisRequestsCopy;
    }

    private int[] convertRangeStringToArray(String range) {
        int start;
        int end;

        if (range.endsWith("+")) {
            String[] parts = range.split("\\+");
            start = Integer.parseInt(parts[0]);
            end = 150;
        } else {
            String[] parts = range.split("-");
            start = Integer.parseInt(parts[0]);
            end = Integer.parseInt(parts[1]);
        }

        return new int[]{start, end};
    }
}
