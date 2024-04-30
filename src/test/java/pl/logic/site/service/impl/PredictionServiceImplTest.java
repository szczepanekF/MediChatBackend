package pl.logic.site.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.logic.site.model.mysql.Disease;
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
        Object statisticDisease = this.predictionService.getStatisticDisease();
        System.out.println(statisticDisease.toString());
    }

    @Test
    void getPredictionAccuracy() {
        double accuracy = this.predictionService.getPredictionAccuracy(new String[]{"1", "1"});
        System.out.println(accuracy);
    }

    @Test
    void getPatientDisease() {
        Disease predictedDisease = this.predictionService.getPatientDisease(1);
        System.out.println(predictedDisease.getName());
    }
}