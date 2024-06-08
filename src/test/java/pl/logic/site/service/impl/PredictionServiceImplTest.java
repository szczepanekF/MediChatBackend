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
        double accuracy = this.predictionService.getPredictionAccuracy(new String[]{"1", "1"});
        System.out.println(accuracy);
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
        double futureDiagnosisRequest = this.predictionService.getFutureDiagnosisRequest(31);
        System.out.println(futureDiagnosisRequest);
    }

    @Test
    void getMostWantedDoctor() {
        Doctor doctor = this.predictionService.getMostWantedDoctor(31);
        try {
            System.out.println(doctor.getName() + " " + doctor.getSurname() + " id: " + doctor.getId());
        } catch (NullPointerException e) {
            System.out.println("No doctor found");
        }
    }


    @ParameterizedTest
    @ValueSource(ints = {1})
    void getSymptomCountInInterval(int symptomId) {
        LocalDate startDate = LocalDate.of(2024, 7, 1); // przykładowa data początkowa
        LocalDate endDate = LocalDate.of(2024, 7, 30); // przykładowa data końcowa

        List<Double> results = this.predictionService.getSymptomCountInIntervals(startDate, endDate, symptomId);
        System.out.println(results);
    }

    @Test
    void getSymptomsCountInIntervals() {
        LocalDate startDate = LocalDate.of(2024, 6, 9); // przykładowa data początkowa
        LocalDate endDate = LocalDate.of(2024, 6, 20); // przykładowa data końcowa

        List<List<Double>> results = this.predictionService.getSymptomsCountInIntervals(startDate, endDate);
        System.out.println(results);
    }

    @Test
    void getDiseaseCountInIntervals() {
        LocalDate startDate = LocalDate.of(2024, 6, 10); // przykładowa data początkowa
        LocalDate endDate = LocalDate.of(2024, 6, 12); // przykładowa data końcowa

        List<Double> results = this.predictionService.getDiseaseCountInIntervals(startDate, endDate, 8);
        System.out.println(results);
    }
}