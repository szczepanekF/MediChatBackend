package pl.logic.site.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.mysql.Disease;
import pl.logic.site.repository.DiseaseRepository;
import pl.logic.site.service.DiseaseService;
import pl.logic.site.utils.Consts;

import java.util.List;

@Slf4j
@Service
public class DiseaseServiceImpl implements DiseaseService {
    @Autowired
    private DiseaseRepository diseaseRepository;

    /**
     * Get disease with given id
     *
     * @param id - id of disease
     * @return disease with given id.
     */
    @Override
    public Disease getDisease(int id) {
        return diseaseRepository.findById(id).orElseThrow(() -> {
            EntityNotFound err = new EntityNotFound(Consts.C404 + " ID: " + id + " Type: " + this.getClass());
            log.error(err.getMessage());
            return err;
        });
    }

    /**
     * Get all diseases
     *
     * @return list of all diseases
     */
    @Override
    public List<Disease> getDiseases() {
        List<Disease> diseases = diseaseRepository.findAll();
        if (diseases.isEmpty()) {
            EntityNotFound err = new EntityNotFound(Consts.C404);
            log.error(err.getMessage());
            throw err;
        }
        log.info("All diseases were successfully retrieved");
        return diseases;
    }

}
