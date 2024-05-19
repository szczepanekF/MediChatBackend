package pl.logic.site.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.logic.site.model.dao.ChartSymptomDAO;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.ChartSymptom;
import pl.logic.site.repository.ChartSymptomRepository;
import pl.logic.site.service.ChartSymptomService;
import pl.logic.site.utils.Consts;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ChartSymptomServiceImpl implements ChartSymptomService {
    @Autowired
    private ChartSymptomRepository chartSymptomRepository;

    /**
     * Create chartSymptom based on given data access object
     *
     * @param chartSymptomDAO - data access object
     * @return created chartSymptom
     */
    @Override
    @Transactional
    public ChartSymptom createChartSymptom(ChartSymptomDAO chartSymptomDAO) {
        ChartSymptom chartSymptomEntity = new ChartSymptom(chartSymptomDAO.chartSymptom().getId(),
                chartSymptomDAO.chartSymptom().getIdChart(),
                chartSymptomDAO.chartSymptom().getIdSymptom(),
                chartSymptomDAO.chartSymptom().getSymptomValueLevel());

        ChartSymptom returned;
        try {
            returned = chartSymptomRepository.saveAndFlush(chartSymptomEntity);
        } catch (Exception e) {
            SaveError err = new SaveError(Consts.C453_SAVING_ERROR + " " + chartSymptomEntity);
            log.error(err.getMessage());
            throw err;
        }
        log.info("ChartSymptom was successfully created: {}", returned);
        return returned;
    }

    /**
     * Delete chartSymptom with given id
     *
     * @param id - id of the chartSymptom
     */
    @Override
    public void deleteChartSymptom(int id) {
        Optional<ChartSymptom> recognition = chartSymptomRepository.findById(id);
        if (recognition.isEmpty()) {
            EntityNotFound err = new EntityNotFound(Consts.C404 + " ID: " + id + " Type: " + this.getClass());
            log.error(err.getMessage());
            throw err;
        }
        try {
            chartSymptomRepository.deleteById(recognition.get().getId());
        } catch (Exception e) {
            DeleteError err = new DeleteError(Consts.C455_DELETING_ERROR + " " + recognition);
            log.error(err.getMessage());
            throw err;
        }
        log.info("ChartSymptom with id: {} was successfully deleted", id);
    }

    /**
     * Update chartSymptom based on chartSymptom data access object and recognitions id
     *
     * @param chartSymptomDAO - data access object
     * @param id          - id of the chartSymptom
     * @return updated chartSymptom
     */
    @Override
    public ChartSymptom updateChartSymptom(ChartSymptomDAO chartSymptomDAO, int id) {
        ChartSymptom chartSymptomEntity = new ChartSymptom(chartSymptomDAO.chartSymptom().getId(),
                chartSymptomDAO.chartSymptom().getIdChart(),
                chartSymptomDAO.chartSymptom().getIdSymptom(),
                chartSymptomDAO.chartSymptom().getSymptomValueLevel());

        Optional<ChartSymptom> recognitionFromDatabase = chartSymptomRepository.findById(id);
        if (recognitionFromDatabase.isEmpty()) {
            EntityNotFound err = new EntityNotFound(Consts.C404 + " " + chartSymptomEntity);
            log.error(err.getMessage());
            throw err;
        }
        ChartSymptom returned;
        try {
            returned = chartSymptomRepository.saveAndFlush(chartSymptomEntity);
        } catch (Exception e) {
            SaveError err = new SaveError(Consts.C454_UPDATING_ERROR + " " + chartSymptomEntity);
            log.error(err.getMessage());
            throw err;
        }
        log.info("ChartSymptom with id: {} was successfully updated: {}", id, returned);
        return returned;
    }

    /**
     * Get chartSymptom entity by id
     *
     * @param id - id of the chartSymptom
     * @return chartSymptom entity with given id
     */
    @Override
    public ChartSymptom getChartSymptom(int id) {
        return chartSymptomRepository.findById(id).orElseThrow(() -> {
            EntityNotFound err = new EntityNotFound(Consts.C404 + " ID: " + id + " Type: " + this.getClass());
            log.error(err.getMessage());
            return err;
        });
    }

    /**
     * Get all chartSymptoms
     *
     * @return list of all chartSymptoms
     */
    @Override
    public List<ChartSymptom> getChartSymptoms(int id) {
        List<ChartSymptom> chartSymptoms = chartSymptomRepository.findAllByIdChart(id);
        if (chartSymptoms.isEmpty()) {
            EntityNotFound err = new EntityNotFound(Consts.C404);
            log.error(err.getMessage());
            throw err;
        }
        log.info("All chartSymptoms were successfully retrieved");
        return chartSymptoms;
    }
}
