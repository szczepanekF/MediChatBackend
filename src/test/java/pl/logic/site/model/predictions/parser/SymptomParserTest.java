package pl.logic.site.model.predictions.parser;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.junit4.SpringRunner;
import pl.logic.site.model.mysql.Symptom;
import pl.logic.site.repository.RecognitionRepository;
import pl.logic.site.service.ChartService;
import pl.logic.site.service.SymptomService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class SymptomParserTest {
    private SymptomParser symptomParser;

    @Autowired
    private ChartService chartService;

    @Autowired
    private RecognitionRepository recognitionRepository;

    @Autowired
    private SymptomService symptomService;

    @BeforeEach
    void setUp() {;
        this.symptomParser = new SymptomParser(chartService, recognitionRepository, symptomService);
    }


    @ParameterizedTest
    @ValueSource(ints = {1, 32})
    void searchForSymptoms(int id_chart) {
//        // when
//        HashMap<String, String> result = symptomParser.searchForSymptoms(id_chart);
//
//        // then
//        System.out.println(result);
//        System.out.println(new ArrayList<>(result.values()).get(0));
    }

    @Test
    void connectSymptoms() {
        // given
//        int id_chart = 1;
//        ArrayList<Symptom> symptoms = new ArrayList<>();
//        symptoms.add(new Symptom(1, "headache"));
//        symptoms.add(new Symptom(2, "sore throat"));
//        symptoms.add(new Symptom(3, "abdominal pain"));
//        symptoms.add(new Symptom(4, "nausea"));
//        symptoms.add(new Symptom(5, "vomiting"));
//
//        // when
//        HashMap<String, String> result = symptomParser.connectSymptoms(id_chart, symptoms);
//
//        // then
//        System.out.println(result);
//        System.out.println(new ArrayList<>(result.values()).get(0));
    }
}