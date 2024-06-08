package pl.logic.site.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.logic.site.model.mysql.Disease;
import pl.logic.site.model.mysql.Doctor;
import pl.logic.site.service.PredictionService;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@SpringBootTest
class PredictionServiceImplTest {
    @Autowired
    private PredictionService predictionService;


    @BeforeEach
    void setUp() {

    }

    @Test
    void getStatisticDisease() {
        try {
            Object statisticDisease = this.predictionService.getStatisticDisease();
            System.out.println(statisticDisease.toString());
        } catch (Exception e) {
            System.out.println("No diseases found");
        }
    }

    @Test
    void getPredictionAccuracy() {
        try {
            double accuracy = this.predictionService.getPredictionAccuracy(new String[]{"1", "1"});
            System.out.println(accuracy);
        } catch (Exception e) {
            System.out.println("No prediction found");
        }
    }

    @Test
    void getPatientDisease() {
        try {
            Disease predictedDisease = this.predictionService.getPatientDisease(1);
            System.out.println(predictedDisease.getName());
        } catch (Exception e) {
            System.out.println("No disease found");
        }
    }

    @Test
    void getFutureDiagnosisRequest() {
        try {
            double futureDiagnosisRequest = this.predictionService.getFutureDiagnosisRequest(31);
            System.out.println(futureDiagnosisRequest);
        } catch (Exception e) {
            System.out.println("No diagnosis request found");
        }
    }

    @Test
    void getMostWantedDoctor() {
        try {
            Doctor doctor = this.predictionService.getMostWantedDoctor(31);
            System.out.println(doctor.getName() + " " + doctor.getSurname() + " id: " + doctor.getId());
        } catch (NullPointerException e) {
            System.out.println("No doctor found");
        }
    }


    @ParameterizedTest
    @ValueSource(ints = {1})
    void getSymptomCountInInterval(int symptomId) {
        try {
            LocalDate startDate = LocalDate.of(2024, 6, 9); // przykładowa data początkowa
            LocalDate endDate = LocalDate.of(2024, 6, 15); // przykładowa data końcowa

            List<Double> results = this.predictionService.getSymptomCountInIntervals(startDate, endDate, symptomId);
            System.out.println(results);
        } catch (Exception e) {
            System.out.println("No symptoms found");
        }
    }

    @Test
    void getSymptomsCountInIntervals() {
        try {
            LocalDate startDate = LocalDate.of(2024, 6, 9); // przykładowa data początkowa
            LocalDate endDate = LocalDate.of(2024, 6, 20); // przykładowa data końcowa

            List<List<Double>> results = this.predictionService.getSymptomsCountInIntervals(startDate, endDate);
            System.out.println(results);
        } catch (Exception e) {
            System.out.println("No symptoms found");
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {8})
    void getDiseaseCountInIntervals(int diseaseId) {
        try {
            LocalDate startDate = LocalDate.of(2024, 6, 9); // przykładowa data początkowa
            LocalDate endDate = LocalDate.of(2024, 6, 20); // przykładowa data końcowa

            List<Double> results = this.predictionService.getDiseaseCountInIntervals(startDate, endDate, diseaseId);
            System.out.println(results);
        } catch (Exception e) {
            System.out.println("No diseases found");
        }
    }

    @Test
    void getDiseasesCountInIntervals() {
        try {
            LocalDate startDate = LocalDate.of(2024, 6, 9); // przykładowa data początkowa
            LocalDate endDate = LocalDate.of(2024, 6, 20); // przykładowa data końcowa

            List<List<Double>> results = this.predictionService.getDiseasesCountInIntervals(startDate, endDate);
            System.out.println(results);
        } catch (Exception e) {
            System.out.println("No diseases found");
        }
//        System.out.println(results.size());
    }

    @Test
    void getDatesInIntervals() {
        try {
            LocalDate startDate = LocalDate.of(2024, 6, 9); // przykładowa data początkowa
            LocalDate endDate = LocalDate.of(2024, 6, 20); // przykładowa data końcowa

            List<LocalDate> results = this.predictionService.getDatesInIntervals(startDate, endDate);
            System.out.println(results);
        } catch (Exception e) {
            System.out.println("No dates found");
        }
    }

    @Test
    void getSymptomsNames() {
        try {
            List<String> symptomsNames = this.predictionService.getSymptomsNames();
            System.out.println(symptomsNames);
        } catch (Exception e) {
            System.out.println("No symptoms found");
        }
    }

    @Test
    void getDiseasesNames() {
        try {
            List<String> diseasesNames = this.predictionService.getDiseasesNames();
            System.out.println(diseasesNames);
        } catch (Exception e) {
            System.out.println("No diseases found");
        }
//        System.out.println(diseasesNames.size());
    }

    @Test
    void getSymptomsPredictionInInterval() {
        try {
            Calendar calendar = Calendar.getInstance();

            calendar.set(2024, Calendar.JUNE, 9); // ustawiamy datę na 1 lipca 2024
            Date fromDate = calendar.getTime();

            calendar.set(2024, Calendar.JUNE, 20); // ustawiamy datę na 1 listopada 2024
            Date toDate = calendar.getTime();

            List<Object> symptomsPredictionInInterval = this.predictionService.getSymptomsPredictionInInterval(fromDate, toDate);
            for (int i = 0; i < 3; i++) {
                System.out.println(symptomsPredictionInInterval.get(i));
            }
        } catch (Exception e) {
            System.out.println("No symptoms found");
        }
    }

    @Test
    void getDiseasesPredictionInInterval() {
        try {
            Calendar calendar = Calendar.getInstance();

            calendar.set(2024, Calendar.JUNE, 9); // ustawiamy datę na 1 lipca 2024
            Date fromDate = calendar.getTime();

            calendar.set(2024, Calendar.JUNE, 20); // ustawiamy datę na 1 listopada 2024
            Date toDate = calendar.getTime();

            List<Object> diseasesPredictionInInterval = this.predictionService.getDiseasesPredictionInInterval(fromDate, toDate);
            for (int i = 0; i < 3; i++) {
                System.out.println(diseasesPredictionInInterval.get(i));
            }
        } catch (Exception e) {
            System.out.println("No diseases found");
        }
    }

}