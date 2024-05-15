package pl.logic.site.model.predictions.parser;

import pl.logic.site.facade.ObjectFacade;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import pl.logic.site.model.dao.SymptomDAO;
import pl.logic.site.model.mysql.Symptom;

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
    private final JdbcTemplate jdbcTemplate;
    private HashMap<String, String> patientSymptoms;
    private List<String> allSymptoms;

    /**
     * Constructs a new SymptomParser instance.
     *
     * @param jdbcTemplate the JDBC template used to query the database
     */
    public SymptomParser(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
//        String sql = "SELECT s.name, r.symptom_value FROM recognition r INNER JOIN symptom s ON r.id_symptom = s.id WHERE r.id_chart = ?";
        String sql = "SELECT s.name, r.symptom_value_level FROM recognition r INNER JOIN symptom s ON r.id_symptom = s.id WHERE r.id_chart = ? and r.symptom_value_level is not null";

        RowMapper<HashMap<String, String>> mapper = (rs, rowNum) -> {
            HashMap<String, String> results = new HashMap<>();
            results.put(rs.getString("name"), rs.getString("symptom_value_level"));
            return results;
        };

        Map<String, String> result = jdbcTemplate.query(sql, new Object[]{id_chart}, mapper).stream().
                flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1));
        return new HashMap<>(result);
    }

    public List<Integer> searchChartIdByPatientId(int patientId) {
        String sql = "SELECT id FROM chart WHERE id_patient = ?";
        List<Integer> results = jdbcTemplate.query(sql, new Object[]{patientId}, (rs, rowNum) -> rs.getInt("id"));
        return results.isEmpty() ? null : results;
    }

    public  HashMap<String, String> madeZeroSymptoms(List<Symptom> symptoms) {
        HashMap<String, String> result = new HashMap<>();
        for (Symptom symptom : symptoms) {
            result.put(symptom.getName(), "null");
        }
        return result;
    }
}
