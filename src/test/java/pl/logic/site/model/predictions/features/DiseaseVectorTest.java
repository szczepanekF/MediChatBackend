package pl.logic.site.model.predictions.features;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.logic.site.model.enums.Status;
import pl.logic.site.model.mysql.Disease;
import pl.logic.site.model.mysql.Patient;
import pl.logic.site.model.mysql.Symptom;
import pl.logic.site.model.predictions.parser.SymptomParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DiseaseVectorTest {
    private DiseaseVector diseaseVector;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date birth_date = formatter.parse("2020-03-01 02:00:00");
        Status status = Status.ONLINE;
        Disease disease = new Disease(1, "pneumonia");
        Patient patient = new Patient(1, "John", "Smith", birth_date, 180, 80, "male", status, "cm", "kg");

        SymptomParser symptomParser = new SymptomParser(this.jdbcTemplate);
        int id_chart = 1;
        ArrayList<Symptom> symptoms = new ArrayList<>();
        symptoms.add(new Symptom(1, "headache"));
        symptoms.add(new Symptom(2, "sore throat"));
        symptoms.add(new Symptom(3, "abdominal pain"));
        symptoms.add(new Symptom(4, "nausea"));
        symptoms.add(new Symptom(5, "vomiting"));
        HashMap<String, String> result = symptomParser.connectSymptoms(id_chart, symptoms);

        this.diseaseVector = new DiseaseVector(disease, patient, result);
    }

    @Test
    void getDisease() {
        System.out.println(this.diseaseVector.getDisease().getName());
        assertEquals("pneumonia", this.diseaseVector.getDisease().getName());
    }

    @Test
    void getFeatureVector() {
        System.out.println(Arrays.toString(this.diseaseVector.getFeatureVector().getPersonalInfoFeatures()));
        System.out.println(Arrays.toString(this.diseaseVector.getFeatureVector().getDateFeatures()));
        System.out.println(Arrays.toString(this.diseaseVector.getFeatureVector().getSymptomFeatures()));
    }
}