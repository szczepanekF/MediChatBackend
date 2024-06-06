package pl.logic.site.model.predictions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.logic.site.model.mysql.Recognition;
import pl.logic.site.repository.RecognitionRepository;

@SpringBootTest
public class RecognitionTest {
    @Autowired
    private RecognitionRepository recognitionRepository;

    @Test
    public void testRecognition() {
        Recognition recognition = new Recognition();
        recognition.setId(1);
        recognition.setId_chart(2);
        recognition.setId_symptom(3);
        recognition.setSymptom_value_level("High");

        assertEquals(1, recognition.getId());
        assertEquals(2, recognition.getId_chart());
        assertEquals(3, recognition.getId_symptom());
        assertEquals("High", recognition.getSymptom_value_level());
    }

    @Test
    public void testRecognitionRepository() {
        System.out.println(recognitionRepository.findAll());
    }
}