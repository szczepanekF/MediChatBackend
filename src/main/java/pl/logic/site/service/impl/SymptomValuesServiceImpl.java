package pl.logic.site.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.mysql.Symptom;
import pl.logic.site.model.mysql.SymptomValues;
import pl.logic.site.repository.SymptomRepository;
import pl.logic.site.repository.SymptomValuesRepository;
import pl.logic.site.service.SymptomService;
import pl.logic.site.service.SymptomValuesService;
import pl.logic.site.utils.Consts;

import java.util.List;

@Slf4j
@Service
public class SymptomValuesServiceImpl implements SymptomValuesService {
    @Autowired
    private SymptomValuesRepository symptomValuesRepository;

    /**
     * Get all symptoms
     * @return list of all symptoms
     */
    @Override
    public List<SymptomValues> getSymptomsValues() {
        List<SymptomValues> symptomValues = symptomValuesRepository.findAll();
        if (symptomValues.isEmpty()) {
            EntityNotFound err = new EntityNotFound(Consts.C404);
            log.error(err.getMessage());
            throw err;
        }
        log.info("All symptoms values were successfully retrieved");
        return symptomValues;
    }
}
