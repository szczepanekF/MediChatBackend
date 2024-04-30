package pl.logic.site.model.predictions.parser;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class SymptomParserTest {
    private SymptomParser symptomParser;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {;
        this.symptomParser = new SymptomParser(this.jdbcTemplate);
    }

    @Test
    void searchForSymptoms() {
        // given
        int id_chart = 1;

        // when
        HashMap<String, String> result = symptomParser.searchForSymptoms(id_chart);

        // then
        System.out.println(result);
        System.out.println(new ArrayList<>(result.values()).get(0));

    }

    @Test
    void connectSymptoms() {
        // given
        int id_chart = 1;
        ArrayList<Symptom> symptoms = new ArrayList<>();
        symptoms.add(new Symptom(1, "headache"));
        symptoms.add(new Symptom(2, "sore throat"));
        symptoms.add(new Symptom(3, "abdominal pain"));
        symptoms.add(new Symptom(4, "nausea"));
        symptoms.add(new Symptom(5, "vomiting"));

        // when
        HashMap<String, String> result = symptomParser.connectSymptoms(id_chart, symptoms);

        // then
        System.out.println(result);
        System.out.println(new ArrayList<>(result.values()).get(0));
    }
}