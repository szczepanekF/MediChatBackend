package pl.logic.site.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.mysql.DictionaryExamination;
import pl.logic.site.repository.DictionaryExaminationRepository;
import pl.logic.site.service.DictionaryExaminationService;
import pl.logic.site.utils.Consts;

import java.util.List;

@Slf4j
@Service
public class DictionaryExaminationServiceImpl implements DictionaryExaminationService {
    @Autowired
    private DictionaryExaminationRepository dictionaryExaminationRepository;

    /**
     * Get dictionary examination with given id
     *
     * @param dictionaryExaminationId - id of dictionary examination
     * @return dictionary examination with given id.
     */
    @Override
    public DictionaryExamination getDictionaryExamination(int dictionaryExaminationId) {
        return dictionaryExaminationRepository.findById(dictionaryExaminationId).orElseThrow(() -> {
            EntityNotFound err = new EntityNotFound(Consts.C404 + " ID: " + dictionaryExaminationId + " Type: " + this.getClass());
            log.error(err.getMessage());
            return err;
        });
    }

    /**
     * Get all dictionary examinations
     *
     * @return list of all dictionary examinations
     */
    @Override
    public List<DictionaryExamination> getDictionaryExaminations() {
        List<DictionaryExamination> dictionaryExaminations = dictionaryExaminationRepository.findAll();
        if (dictionaryExaminations.isEmpty()) {
            EntityNotFound err = new EntityNotFound(Consts.C404);
            log.error(err.getMessage());
            throw err;
        }
        log.info("All dictionary examinations were successfully retrieved");
        return dictionaryExaminations;
    }
}
