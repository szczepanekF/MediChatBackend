package pl.logic.site.model.predictions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
        recognition.setIdChart(2);
        recognition.setIdSymptom(3);
        recognition.setSymptomValueLevel("High");

        assertEquals(1, recognition.getId());
        assertEquals(2, recognition.getIdChart());
        assertEquals(3, recognition.getIdSymptom());
        assertEquals("High", recognition.getSymptomValueLevel());
    }

    @Test
    public void testRecognitionRepository() {
//        System.out.println(recognitionRepository.findAll());
        System.out.println(recognitionRepository.findByIdChart(31));
    }
}