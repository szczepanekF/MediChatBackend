package pl.logic.site.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.logic.site.model.mysql.Disease;
import pl.logic.site.model.mysql.Doctor;
import pl.logic.site.service.PredictionService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PredictionServiceImplTest {
    @Autowired
    private PredictionService predictionService;


    @BeforeEach
    void setUp() {

    }

    @Test
    void getStatisticDisease() {
//        Object statisticDisease = this.predictionService.getStatisticDisease();
//        System.out.println(statisticDisease.toString());
    }

    @Test
    void getPredictionAccuracy() {
        double accuracy = this.predictionService.getPredictionAccuracy(new String[]{"1", "1"});
        System.out.println(accuracy);
    }

    @Test
    void getPatientDisease() {
//        Disease predictedDisease = this.predictionService.getPatientDisease(1);
//        System.out.println(predictedDisease.getName());
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
}