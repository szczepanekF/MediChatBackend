package pl.logic.site.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.mysql.Symptom;
import pl.logic.site.repository.SymptomRepository;
import pl.logic.site.service.SymptomService;
import pl.logic.site.utils.Consts;

import java.util.List;
@Slf4j
@Service
public class SymptomServiceImpl implements SymptomService {
    @Autowired
    private SymptomRepository symptomRepository;

    /**
     * Get symptom with given id
     * @param id - id of symptom
     * @return symptom with given id.
     */
    @Override
    public Symptom getSymptom(int id) {
        return symptomRepository.findById(id).orElseThrow(() -> {
            EntityNotFound err = new EntityNotFound(Consts.C404 + " ID: " + id + " Type: " + this.getClass());
            log.error(err.getMessage());
            return err;
        });
    }

    /**
     * Get all symptoms
     * @return list of all symptoms
     */
    @Override
    public List<Symptom> getSymptoms() {
        List<Symptom> symptoms = symptomRepository.findAll();
        if (symptoms.isEmpty()) {
            EntityNotFound err = new EntityNotFound(Consts.C404);
            log.error(err.getMessage());
            throw err;
        }
        log.info("All symptoms were successfully retrieved");
        return symptoms;
    }
}
