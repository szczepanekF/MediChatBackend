package pl.logic.site.model.predictions.knn;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.logic.site.model.enums.Status;
import pl.logic.site.model.mysql.Disease;
import pl.logic.site.model.mysql.Patient;
import pl.logic.site.model.mysql.Symptom;
import pl.logic.site.model.predictions.features.DiseaseVector;
import pl.logic.site.model.predictions.metric.EuclideanMetric;
import pl.logic.site.model.predictions.parser.SymptomParser;
import pl.logic.site.model.predictions.quality.Result;
import pl.logic.site.service.ChartService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class KNNTest {
    private List<Disease> diseases;
    private List<DiseaseVector> learningSet;
    private List<DiseaseVector> testingSet;
    private EuclideanMetric euclideanMetric;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ChartService chartService;

    @BeforeEach
    void setUp() throws ParseException {
        diseases = new ArrayList<>();
        diseases.add(new Disease(1, "pneumonia"));
        diseases.add(new Disease(2, "motion sickness"));
        diseases.add(new Disease(3, "irritable bowel syndrome"));
        diseases.add(new Disease(4, "chickenpox"));
        diseases.add(new Disease(5, "diabetes"));
        diseases.add(new Disease(6, "hypertension"));
        diseases.add(new Disease(7, "gastroesophageal reflux disease"));
        diseases.add(new Disease(8, "otitis"));
        diseases.add(new Disease(9, "measles"));
        diseases.add(new Disease(10, "sunburn"));
        diseases.add(new Disease(11, "celiac disease"));
        diseases.add(new Disease(12, "lyme disease"));
        diseases.add(new Disease(13, "hyperthyroidism"));
        diseases.add(new Disease(14, "myocardial infarction"));
        diseases.add(new Disease(15, "arthritis"));
        diseases.add(new Disease(16, "peptic ulcer disease"));
        diseases.add(new Disease(17, "restless legs syndrome"));
        diseases.add(new Disease(18, "colitis"));

        SymptomParser symptomParser = new SymptomParser(this.jdbcTemplate, chartService);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date birth_date_1 = formatter.parse("2002-03-01 02:00:00");
        Date birth_date_2 = formatter.parse("1998-11-25 11:30:01");
        Date birth_date_3 = formatter.parse("1974-06-11 11:30:01");
        Date birth_date_4 = formatter.parse("2007-11-05 11:30:01");
        Date birth_date_5 = formatter.parse("1986-09-17 11:30:01");

        Status status = Status.ONLINE;
        Disease disease_1 = new Disease(1, "pneumonia");
        Disease disease_2 = new Disease(8, "otitis");
        Disease disease_3 = new Disease(10, "sunburn");
        Disease disease_5 = new Disease(8, "sunburn");

        Patient patient_1 = new Patient(1, "John", "Smith", birth_date_1, 180, 80, "male", status, "cm", "kg");
        Patient patient_2 = new Patient(2, "Mary", "Hill", birth_date_2, 165, 58, "female", status, "cm", "kg");
        Patient patient_3 = new Patient(3, "Peter", "Watson", birth_date_3, 179, 88, "male", status, "cm", "kg");
        Patient patient_4 = new Patient(4, "Mary", "Hill", birth_date_2, 165, 58, "female", status, "cm", "kg");
        Patient patient_5 = new Patient(5, "Wiktor", "Orkczan", birth_date_5, 169, 65, "male", status, "cm", "kg");

        int id_chart_1 = 1;
        int id_chart_2 = 2;
        int id_chart_3 = 3;
        int id_chart_4 = 2;
        int id_chart_5 = 3;
        ArrayList<Symptom> symptoms = new ArrayList<>();
        symptoms.add(new Symptom(1, "headache"));
        symptoms.add(new Symptom(2, "sore throat"));
        symptoms.add(new Symptom(3, "abdominal pain"));
        symptoms.add(new Symptom(4, "nausea"));
        symptoms.add(new Symptom(5, "vomiting"));
        HashMap<String, String> result_1 = symptomParser.connectSymptoms(id_chart_1, symptoms);
        HashMap<String, String> result_2 = symptomParser.connectSymptoms(id_chart_2, symptoms);
        HashMap<String, String> result_3 = symptomParser.connectSymptoms(id_chart_3, symptoms);
        HashMap<String, String> result_4 = symptomParser.connectSymptoms(id_chart_4, symptoms);
        HashMap<String, String> result_5 = symptomParser.connectSymptoms(id_chart_5, symptoms);

        this.learningSet = new ArrayList<>();
        this.learningSet.add(new DiseaseVector(disease_1, patient_1, result_1));
        this.learningSet.add(new DiseaseVector(disease_2, patient_2, result_2));
        this.learningSet.add(new DiseaseVector(disease_3, patient_3, result_3));

        this.testingSet = new ArrayList<>();
        this.testingSet.add(new DiseaseVector(null, patient_4, result_4));
        this.testingSet.add(new DiseaseVector(disease_5, patient_5, result_5));

        this.euclideanMetric = new EuclideanMetric();
    }

    @Test
    void classifyVectors() {
        KNN knn = new KNN(learningSet);
        List<Result> results = knn.classifyVectors(testingSet, 1, euclideanMetric, diseases);
        for (Result result : results) {
            System.out.println("Result: " + result.getResult() + "\texpected: " + result.getExpected());
        }
        assertEquals("otitis", results.get(0).getResult().getName());
        assertEquals("sunburn", results.get(1).getResult().getName());
    }

    @Test
    void classifyVector() {
        KNN knn = new KNN(learningSet);
        Result result = knn.classifyVector(testingSet.get(0), 1, euclideanMetric, diseases);

        System.out.println("Result: " + result.getResult() + "\texpected: " + result.getExpected());
        assertEquals("otitis", result.getResult().getName());
    }
}