package pl.logic.site.service;

import pl.logic.site.model.dao.ChartDAO;
import pl.logic.site.model.dao.DiagnosisRequestDAO;
import pl.logic.site.model.mysql.Chart;
import pl.logic.site.model.mysql.DiagnosisRequest;

import java.util.List;

public interface ChartService {
    Chart createChart(ChartDAO chart);

    void deleteChart(int id);

    Chart updateChart(ChartDAO chart, int id);

    Chart getChart(int id);

    List<Chart> getChartsForPatient(int id);

    List<Chart> getCharts();

    List<Chart> getChartsByState(int state);
}
