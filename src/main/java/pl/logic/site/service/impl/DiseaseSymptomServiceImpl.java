package pl.logic.site.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.mysql.DiseaseSymptom;
import pl.logic.site.repository.DiseaseSymptomRepository;
import pl.logic.site.service.DiseaseSymptomService;
import pl.logic.site.utils.Consts;

import java.util.List;

@Slf4j
@Service
public class DiseaseSymptomServiceImpl implements DiseaseSymptomService {
    @Autowired
    private DiseaseSymptomRepository diseaseSymptomRepository;

    /**
     * Get disease-symptom record by it's id
     *
     * @param diseaseSymptomId - id of disease-symptom record
     * @return disease-symptom record
     */
    @Override
    public DiseaseSymptom getDiseaseSymptom(int diseaseSymptomId) {
        return diseaseSymptomRepository.findById(diseaseSymptomId).orElseThrow(() -> {
            EntityNotFound err = new EntityNotFound(Consts.C404 + " ID: " + diseaseSymptomId + " Type: " + this.getClass());
            log.error(err.getMessage());
            return err;
        });
    }

    /**
     * Get all disease-symptom records wtih given symptomId
     *
     * @param symptomId - symptom id of required disease-symptom records
     * @return list of disease-symptom records with given symptom id
     */
    @Override
    public List<DiseaseSymptom> getDiseaseSymptomsForSymptom(int symptomId) {
        List<DiseaseSymptom> diseaseSymptoms = diseaseSymptomRepository.findByIdSymtpom(symptomId);
        if (diseaseSymptoms.isEmpty()) {
            EntityNotFound err = new EntityNotFound(Consts.C404);
            log.error(err.getMessage());
            throw err;
        }
        log.info("All disease-symptom records matching symptom ID: {} requests were successfully retrieved", symptomId);
        return diseaseSymptoms;
    }

    /**
     * Get all disease-symptom records with given diseaseId
     *
     * @param diseaseId - disease id of required disease-symptom records
     * @return list of disease-symptom records with given disease id
     */
    @Override
    public List<DiseaseSymptom> getDiseaseSymptomsForDisease(int diseaseId) {
        List<DiseaseSymptom> diseaseSymptoms = diseaseSymptomRepository.findByIdDisease(diseaseId);
        if (diseaseSymptoms.isEmpty()) {
            EntityNotFound err = new EntityNotFound(Consts.C404);
            log.error(err.getMessage());
            throw err;
        }
        log.info("All disease-symptom records matching disease ID: {} requests were successfully retrieved", diseaseId);
        return diseaseSymptoms;
    }

    /**
     * Get all disease-symptom records
     *
     * @return list of all disease-symptom records.
     */
    @Override
    public List<DiseaseSymptom> getDiseaseSymptoms() {
        List<DiseaseSymptom> diseaseSymptoms = diseaseSymptomRepository.findAll();
        if (diseaseSymptoms.isEmpty()) {
            EntityNotFound err = new EntityNotFound(Consts.C404);
            log.error(err.getMessage());
            throw err;
        }
        log.info("All disease-symptom records requests were successfully retrieved");
        return diseaseSymptoms;
    }
}
