package pl.logic.site.model.predictions.parser;

import pl.logic.site.facade.ObjectFacade;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import pl.logic.site.model.dao.SymptomDAO;
import pl.logic.site.model.mysql.Chart;
import pl.logic.site.model.mysql.Recognition;
import pl.logic.site.model.mysql.Symptom;
import pl.logic.site.repository.RecognitionRepository;
import pl.logic.site.service.ChartService;
import pl.logic.site.service.SymptomService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is responsible for parsing symptoms from the database.
 * This class associates symptom names with their severity value,
 * and associates a patient's symptoms from his chart with the given symptoms.
 *
 * @author Kacper
 */
public class SymptomParser {
    private HashMap<String, String> patientSymptoms;
    private List<String> allSymptoms;
    private ChartService chartService;
    private RecognitionRepository recognitionRepository;
    private SymptomService symptomService;


    /**
     * Constructs a new SymptomParser instance.
     *
     * @param
     */
    public SymptomParser(ChartService chartService, RecognitionRepository recognitionRepository, SymptomService symptomService) {
        this.chartService = chartService;
        this.recognitionRepository = recognitionRepository;
        this.symptomService = symptomService;
    }

    /**
     * Connects the given symptoms to the patient's symptoms with his chart,
     * returning a map of the symptoms' name and symptoms severity level (as String).
     * If the patient does not have a symptom, the value will be "null".
     *
     * @param id_chart the ID of the patient's chart
     * @param symptoms the symptoms to connect to the patient's symptoms
     * @return a map of the connected symptoms
     */
    public HashMap<String, String> connectSymptoms(int id_chart, List<Symptom> symptoms) {
        this.patientSymptoms = searchForSymptoms(id_chart);
        this.allSymptoms = new ArrayList<>();
        for (Symptom symptom : symptoms) {
            allSymptoms.add(symptom.getName());
        }

        HashMap<String, String> result = new HashMap<>();
        for (String symptom : allSymptoms) {
            if (patientSymptoms.containsKey(symptom)) {
                result.put(symptom, patientSymptoms.get(symptom));
            } else {
                result.put(symptom, "null");
            }
        }
        return result;
    }

    /**
     * Searches the database for the patient's symptoms with the given ID in his chart.
     *
     * @param id_chart the ID of the patient's chart
     * @return a map of the patient's symptoms (symptoms name, symptoms severity level)
     */
    public HashMap<String, String> searchForSymptoms(int id_chart) {
        HashMap<String, String> results = new HashMap<String, String>();
        List<Recognition> recognitions = recognitionRepository.findByIdChart(id_chart);
        for (Recognition recognition : recognitions) {
            Symptom symptom = symptomService.getSymptom(recognition.getIdSymptom());
            results.put(symptom.getName(), recognition.getSymptomValueLevel());
        }

        return results;
    }

    public List<Integer> searchChartIdByPatientId(int patientId) {
        List<Integer> results = chartService.getChartsForPatient(patientId).stream().map(Chart::getId).toList();
        return results.isEmpty() ? null : results;
    }

    public HashMap<String, String> madeZeroSymptoms(List<Symptom> symptoms) {
        HashMap<String, String> result = new HashMap<>();
        for (Symptom symptom : symptoms) {
            result.put(symptom.getName(), "null");
        }
        return result;
    }
}
