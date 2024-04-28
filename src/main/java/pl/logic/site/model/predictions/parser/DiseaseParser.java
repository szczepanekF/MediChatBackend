package pl.logic.site.model.predictions.parser;

import pl.logic.site.model.mysql.Disease;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for parsing a diagnosis string and finding the corresponding diseases.
 * Search the diagnosis and add the diseases found to the variable list of diseases.
 * Important: If the patient has not had a diagnosis or no potential diseases have been detected,
 * the diseases list will be empty.
 *
 * @author Kacper
 */
public class DiseaseParser {
    private String diagnosis;
    private List<Disease> diseases;

    /**
     * The diagnosis string that is being parsed.
     */
    public String getDiagnosis() {
        return diagnosis;
    }

    /**
     * The list of diseases that is parsed with the diagnosis string.
     */
    public List<Disease> getDiseases() {
        return diseases;
    }

    /**
     * Constructs a DiseaseParser object with the given diagnosis and a list of all diseases.
     *
     * @param diagnosis   the diagnosis (String)
     * @param allDiseases the list of all diseases (from database)
     */
    public DiseaseParser(String diagnosis, List<Disease> allDiseases) {
        this.diagnosis = diagnosis;
        this.diseases = new ArrayList<Disease>();
        parseDiseases(allDiseases);
    }

    /**
     * Parses the given list of diseases and adds any that match the diagnosis string to the list of matched diseases.
     *
     * @param allDiseases the list of all diseases
     */
    private void parseDiseases(List<Disease> allDiseases) {
        for (Disease disease : allDiseases) {
            if (this.diagnosis.contains(disease.getName())) {
                diseases.add(disease);
            }
        }
    }
}
