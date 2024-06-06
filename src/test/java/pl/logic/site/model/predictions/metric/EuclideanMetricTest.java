package pl.logic.site.model.predictions.metric;

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
import pl.logic.site.model.predictions.parser.SymptomParser;
import pl.logic.site.service.ChartService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static pl.logic.site.utils.predictions.PredictionConsts.MAX_DATE_DIFF;

@SpringBootTest
class EuclideanMetricTest {
    private DiseaseVector diseaseVector_1;
    private DiseaseVector diseaseVector_2;
    private EuclideanMetric euclideanMetric;

    @Autowired
    private ChartService chartService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() throws ParseException {
        SymptomParser symptomParser = new SymptomParser(this.jdbcTemplate, chartService);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date birth_date_1 = formatter.parse("2002-03-01 02:00:00");
        Date birth_date_2 = formatter.parse("1998-11-25 11:30:01");

        Status status = Status.ONLINE;
        Disease disease_1 = new Disease(1, "pneumonia");
        Disease disease_2 = new Disease(8, "otitis");

        Patient patient_1 = new Patient(1, "John", "Smith", birth_date_1, 180, 80, "male", status, "cm", "kg");
        Patient patient_2 = new Patient(2, "Mary", "Hill", birth_date_2, 165, 58, "female", status, "cm", "kg");

        int id_chart_1 = 1;
        int id_chart_2 = 2;
        ArrayList<Symptom> symptoms = new ArrayList<>();
        symptoms.add(new Symptom(1, "headache"));
        symptoms.add(new Symptom(2, "sore throat"));
        symptoms.add(new Symptom(3, "abdominal pain"));
        symptoms.add(new Symptom(4, "nausea"));
        symptoms.add(new Symptom(5, "vomiting"));
        HashMap<String, String> result_1 = symptomParser.connectSymptoms(id_chart_1, symptoms);
        HashMap<String, String> result_2 = symptomParser.connectSymptoms(id_chart_2, symptoms);

        this.diseaseVector_1 = new DiseaseVector(disease_1, patient_1, result_1);
        this.diseaseVector_2 = new DiseaseVector(disease_2, patient_2, result_2);

        this.euclideanMetric = new EuclideanMetric();
    }

    @Test
    void calculateMetric() {
        System.out.println(Arrays.toString(diseaseVector_1.getFeatureVector().getPersonalInfoFeatures()));
        System.out.println(Arrays.toString(diseaseVector_2.getFeatureVector().getPersonalInfoFeatures()));

        System.out.println();
        System.out.println(Arrays.toString(diseaseVector_1.getFeatureVector().getDateFeatures()));
        System.out.println(Arrays.toString(diseaseVector_2.getFeatureVector().getDateFeatures()));

        System.out.println();
        System.out.println(Arrays.toString(diseaseVector_1.getFeatureVector().getSymptomFeatures()));
        System.out.println(Arrays.toString(diseaseVector_2.getFeatureVector().getSymptomFeatures()));

        long diffInMillies = Math.abs(diseaseVector_1.getFeatureVector().getDateFeatures()[0].getTime() - diseaseVector_2.getFeatureVector().getDateFeatures()[0].getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        double normalization = (double) diff / MAX_DATE_DIFF;
        normalization = Math.pow(normalization, 2);
        System.out.println(normalization);

        double result = euclideanMetric.calculateMetric(diseaseVector_1.getFeatureVector(), diseaseVector_2.getFeatureVector());
        System.out.println();

        System.out.println(result);
        assertEquals(1.0145, result, 0.001);
    }
}