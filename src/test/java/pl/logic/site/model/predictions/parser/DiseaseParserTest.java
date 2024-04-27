package pl.logic.site.model.predictions.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.logic.site.model.mysql.Disease;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DiseaseParserTest {
    List<Disease> diseases;
    String diagnosis;

    @BeforeEach
    void setUp() {
        diseases = new ArrayList<>();
        diseases.add(new Disease(1, "pneumonia"));
        diseases.add(new Disease(2, "motion sickness"));
        diseases.add(new Disease(3, "irritable bowel syndrome"));
        diseases.add(new Disease(4, "chickenpox"));
        diseases.add(new Disease(5, "diabetes"));
        diseases.add(new Disease(6, "hypertension"));
        diseases.add(new Disease(7, "gastroesophageal reflux disease"));
        diseases.add(new Disease(8, "otitis"));
        diseases.add(new Disease(9, "measles"));
        diseases.add(new Disease(10, "sunburn"));
        diseases.add(new Disease(11, "celiac disease"));
        diseases.add(new Disease(12, "lyme disease"));
        diseases.add(new Disease(13, "hyperthyroidism"));
        diseases.add(new Disease(14, "myocardial infarction"));
        diseases.add(new Disease(15, "arthritis"));
        diseases.add(new Disease(16, "peptic ulcer disease"));
        diseases.add(new Disease(17, "restless legs syndrome"));
        diseases.add(new Disease(18, "colitis"));

        diagnosis = "Jan Kowalski: Based on your current symptoms of headache, sore throat, and mild cough,\n" +
                "it could potentially be otitis media or sinusitis. However,\n" +
                "it's important to note that these symptoms can also overlap with other\n" +
                "conditions such as hypertension or even the common cold. To make an accurate diagnosis,\n" +
                "I would recommend visiting your local healthcare provider for a thorough examination\n" +
                "and possibly some diagnostic tests. In the meantime, try to stay hydrated, get plenty of rest,\n" +
                "and avoid irritants like smoking or strong perfumes. If your symptoms worsen or you develop\n" +
                "additional symptoms such as fever, chest pain, or difficulty breathing,\n" +
                "please seek medical attention immediately.";
    }

    @Test
    void parseDiseases() {
        DiseaseParser diseaseParser = new DiseaseParser(diagnosis, diseases);
        List<Disease> result = diseaseParser.getDiseases();
        for (Disease disease : result) {
            System.out.println(disease.getName());
        }
    }
}