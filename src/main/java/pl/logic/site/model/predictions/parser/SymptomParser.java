package pl.logic.site.model.predictions.parser;

import pl.logic.site.facade.ObjectFacade;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import pl.logic.site.model.dao.SymptomDAO;
import pl.logic.site.model.mysql.Symptom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SymptomParser {
    private final JdbcTemplate jdbcTemplate;
    private HashMap<String, String> patientSymptoms;
    private List<String> allSymptoms;

    public SymptomParser(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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

    public HashMap<String, String> searchForSymptoms(int id_chart) {
        String sql = "SELECT s.name, r.symptom_value FROM recognition r INNER JOIN symptom s ON r.id_symptom = s.id WHERE r.id_chart = ?";

        RowMapper<HashMap<String, String>> mapper = (rs, rowNum) -> {
            HashMap<String, String> results = new HashMap<>();
            results.put(rs.getString("name"), rs.getString("symptom_value"));
            return results;
        };

        Map<String, String> result = jdbcTemplate.query(sql, new Object[]{id_chart}, mapper).stream().flatMap(map -> map.entrySet().
                stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1));
        return new HashMap<>(result);
    }
}
