package pl.logic.site.model.predictions.parser;

import pl.logic.site.model.mysql.Disease;

import java.util.ArrayList;
import java.util.List;

public class DiseaseParser {
    private String diagnosis;
    private List<Disease> diseases;

    public String getDiagnosis() {
        return diagnosis;
    }

    public List<Disease> getDiseases() {
        return diseases;
    }

    public DiseaseParser(String diagnosis, List<Disease> allDiseases) {
        this.diagnosis = diagnosis;
        this.diseases = new ArrayList<Disease>();
        parseDiseases(allDiseases);
    }

    private void parseDiseases(List<Disease> allDiseases) {
        for (Disease disease : allDiseases) {
            if (this.diagnosis.contains(disease.getName())) {
                diseases.add(disease);
            }
        }
    }
}
