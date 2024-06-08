package pl.logic.site.model.predictions.features;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.logic.site.facade.ObjectFacade;
import pl.logic.site.model.dao.PatientDAO;
import pl.logic.site.model.mysql.Patient;
import pl.logic.site.service.PatientService;
import pl.logic.site.service.impl.PatientServiceImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class FeatureVectorTest {
    private FeatureVector featureVector;

    @BeforeEach
    void setUp() throws ParseException {
        double height = 180.;
        double weight = 80.6;
        String gender = "male";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date birth_date = formatter.parse("2023-01-01 00:00:00");
        HashMap<String, String> symptoms = new HashMap<String, String>();
        symptoms.put("headache", "strong");
        symptoms.put("sore throat", "mild");
        symptoms.put("cough", "moderate");
        symptoms.put("vomiting", "null");

        this.featureVector = new FeatureVector(height, weight, gender, birth_date, symptoms);
    }

    @Test
    void getPersonalInfoFeatures() {
        System.out.println(Arrays.toString(this.featureVector.getPersonalInfoFeatures()));
        assertEquals(3, this.featureVector.getPersonalInfoFeatures().length);
        assertEquals(0.72, this.featureVector.getPersonalInfoFeatures()[0], 0.001);
        assertEquals(0.5373, this.featureVector.getPersonalInfoFeatures()[1], 0.001);
        assertEquals(0.0, this.featureVector.getPersonalInfoFeatures()[2], 0.001);

    }

    @Test
    void getDateFeatures() throws ParseException {
        System.out.println(Arrays.toString(this.featureVector.getDateFeatures()));
//        assertEquals("Sun Jan 01 00:00:00 CET 2023", this.featureVector.getDateFeatures()[0].toString());
    }

    @Test
    void getSymptomFeatures() {
        System.out.println(Arrays.toString(this.featureVector.getSymptomFeatures()));
        assertEquals(0.2, this.featureVector.getSymptomFeatures()[0], 0.001);
        assertEquals(0.8, this.featureVector.getSymptomFeatures()[1], 0.001);
        assertEquals(0.5, this.featureVector.getSymptomFeatures()[2], 0.001);
        assertEquals(0.0, this.featureVector.getSymptomFeatures()[3], 0.001);
    }
}