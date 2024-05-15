package pl.logic.site.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.logic.site.model.dao.ChartDAO;
import pl.logic.site.model.dao.DiagnosisRequestDAO;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.Chart;
import pl.logic.site.model.mysql.DiagnosisRequest;
import pl.logic.site.repository.ChartRepository;
import pl.logic.site.repository.DiagnosisRequestRepository;
import pl.logic.site.service.ChartService;
import pl.logic.site.utils.Consts;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ChartServiceImpl implements ChartService {
    @Autowired
    private ChartRepository chartRepository;
    @Autowired
    private DiagnosisRequestRepository diagnosisRequestRepository;


    /**
     * Create chart based on given data access object
     *
     * @param chart - data access object
     * @return created chart
     */
    @Override
    @Transactional
    public Chart createChart(ChartDAO chart) {
        Chart chartEntity = new Chart(chart.chart().getId(), chart.chart().getIdPatient(), chart.chart().getDate());

        if (chartEntity.getId() != 0) {
            SaveError err = new SaveError(Consts.C453_SAVING_ERROR + " Explicitly stated entity ID, entity: " + chartEntity);
            log.error(err.getMessage());
            throw err;
        }
        Chart returned;
        try {
            returned = chartRepository.saveAndFlush(chartEntity);
        } catch (Exception e) {
            SaveError err = new SaveError(Consts.C453_SAVING_ERROR + " " + chartEntity);
            log.error(err.getMessage());
            throw err;
        }
        log.info("Chart was successfully created: {}", returned);
        return returned;
    }

    /**
     * Delete chart with given id
     *
     * @param id - id of the chart
     */
    @Override
    public void deleteChart(int id) {
        Optional<Chart> chart = chartRepository.findById(id);
        if (chart.isEmpty()) {
            EntityNotFound err = new EntityNotFound(Consts.C404 + " ID: " + id + " Type: " + this.getClass());
            log.error(err.getMessage());
            throw err;
        }
        try {
            chartRepository.deleteById(chart.get().getId());
        } catch (Exception e) {
            DeleteError err = new DeleteError(Consts.C455_DELETING_ERROR + " " + chart);
            log.error(err.getMessage());
            throw err;
        }
        log.info("Chart with id: {} was successfully deleted", id);

    }

    /**
     * Update chart based on chart data access object and chart id
     *
     * @param chart - data access object
     * @param id - id of the chart
     * @return updated chart
     */

    @Override
    public Chart updateChart(ChartDAO chart, int id) {
        Chart chartEntity = new Chart(chart.chart().getId(), chart.chart().getIdPatient(), chart.chart().getDate());
        Optional<Chart> diagnosisRequestFromDatabase = chartRepository.findById(id);
        if (diagnosisRequestFromDatabase.isEmpty()) {
            EntityNotFound err = new EntityNotFound(Consts.C404 + " " + chartEntity);
            log.error(err.getMessage());
            throw err;
        }
        Chart returned;
        try {
            returned = chartRepository.saveAndFlush(chartEntity);
        } catch (Exception e) {
            SaveError err = new SaveError(Consts.C454_UPDATING_ERROR + " " + chartEntity);
            log.error(err.getMessage());
            throw err;
        }
        log.info("Chart with id: {} was successfully updated: {}", id, returned);
        return returned;
    }

    /**
     * Get chart entity by id
     *
     * @param id - id of the chart
     * @return chart with given id
     */
    @Override
    public Chart getChart(int id) {
        return chartRepository.findById(id).orElseThrow(() -> {
            EntityNotFound err = new EntityNotFound(Consts.C404 + " ID: " + id + " Type: " + this.getClass());
            log.error(err.getMessage());
            return err;
        });
    }

    /**
     * Get all charts for patient with given id
     * @param id - id of patient
     * @return list of all charts for given patientId
     */

    @Override
    public List<Chart> getChartsForPatient(int id) {
        List<Chart> charts = chartRepository.findByIdPatient(id);
        if (charts.isEmpty()) {
            EntityNotFound err = new EntityNotFound(Consts.C404);
            log.error(err.getMessage());
            throw err;
        }
        log.info("All charts for patient with ID: {} were successfully retrieved", id);
        return charts;
    }

    /**
     * Get all charts
     *
     * @return list of all charts
     */
    @Override
    public List<Chart> getCharts() {
        List<Chart> charts = chartRepository.findAll();
        if (charts.isEmpty()) {
            EntityNotFound err = new EntityNotFound(Consts.C404);
            log.error(err.getMessage());
            throw err;
        }
        log.info("All charts were successfully retrieved");
        return charts;
    }

    @Override
    public List<Chart> getChartsByState(int state) {
        List<Chart> charts = new ArrayList<>(chartRepository.findAll());

        List<Chart> chartsToRemove = new ArrayList<>();

        for (Chart chart : charts) {
            List<DiagnosisRequest> diagnosisRequestList = diagnosisRequestRepository.findAllByIdChart(chart.getId());
            if (state == 1) {
                if (!diagnosisRequestList.isEmpty())
                    chartsToRemove.add(chart);
            } else {
                if (diagnosisRequestList.isEmpty())
                    chartsToRemove.add(chart);
            }
        }

        log.info("All charts were successfully retrieved");
        return chartsToRemove;
    }

}
