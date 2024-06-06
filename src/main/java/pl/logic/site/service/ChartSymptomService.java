package pl.logic.site.service;

import pl.logic.site.model.dao.ChartSymptomDAO;
import pl.logic.site.model.mysql.ChartSymptom;

import java.util.List;

public interface ChartSymptomService {
    ChartSymptom createChartSymptom(ChartSymptomDAO recognition);

    void deleteChartSymptom(int id);

    ChartSymptom updateChartSymptom(ChartSymptomDAO recognition, int id);

    ChartSymptom getChartSymptom(int id);

    List<ChartSymptom> getChartSymptoms(int id);

}
