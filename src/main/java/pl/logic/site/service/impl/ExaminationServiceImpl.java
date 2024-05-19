package pl.logic.site.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.logic.site.model.dao.ExaminationDAO;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.Examination;
import pl.logic.site.repository.ExaminationRepository;
import pl.logic.site.service.ExaminationService;
import pl.logic.site.utils.Consts;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ExaminationServiceImpl implements ExaminationService {
    @Autowired
    private ExaminationRepository examinationRepository;

    /**
     * Create examination based on given data access object
     *
     * @param examination - data access object
     * @return created examination
     */
    @Override
    @Transactional
    public Examination createExamination(ExaminationDAO examination) {
        Examination examinationEntity = new Examination(examination.examination().getId(),
                examination.examination().getIdPatient(),
                examination.examination().getExamination(),
                examination.examination().getExaminationValue());
        if (examinationEntity.getId() != 0) {
            SaveError err = new SaveError(Consts.C453_SAVING_ERROR + " Explicitly stated entity ID, entity: " + examinationEntity);
            log.error(err.getMessage());
            throw err;
        }
        Examination returned;
        try {
            returned = examinationRepository.saveAndFlush(examinationEntity);
        } catch (Exception e) {
            SaveError err = new SaveError(Consts.C453_SAVING_ERROR + " " + examinationEntity);
            log.error(err.getMessage());
            throw err;
        }
        log.info("Examination was successfully created: {}", returned);
        return returned;
    }

    /**
     * Delete examination with given id
     *
     * @param examinationId - id of the examination
     */
    @Override
    public void deleteExamination(int examinationId) {
        Optional<Examination> examination = examinationRepository.findById(examinationId);
        if (examination.isEmpty()) {
            EntityNotFound err = new EntityNotFound(Consts.C404 + " ID: " + examinationId + " Type: " + this.getClass());
            log.error(err.getMessage());
            throw err;
        }
        try {
            examinationRepository.deleteById(examination.get().getId());
        } catch (Exception e) {
            DeleteError err = new DeleteError(Consts.C455_DELETING_ERROR + " " + examination);
            log.error(err.getMessage());
            throw err;
        }
        log.info("Examination with id: {} was successfully deleted", examinationId);
    }

    /**
     * Update examination based on examination data access object and examinations id
     *
     * @param examination   - data access object
     * @param examinationId - id of the examination
     * @return updated examination
     */
    @Override
    public Examination updateExamination(ExaminationDAO examination, int examinationId) {
        Examination examinationEntity = new Examination(examination.examination().getId(),
                examination.examination().getIdPatient(),
                examination.examination().getExamination(),
                examination.examination().getExaminationValue());
        Optional<Examination> examinationFromDatabase = examinationRepository.findById(examinationId);
        if (examinationFromDatabase.isEmpty()) {
            EntityNotFound err = new EntityNotFound(Consts.C404 + " " + examinationEntity);
            log.error(err.getMessage());
            throw err;
        }
        Examination returned;
        try {
            returned = examinationRepository.saveAndFlush(examinationEntity);
        } catch (Exception e) {
            SaveError err = new SaveError(Consts.C454_UPDATING_ERROR + " " + examinationEntity);
            log.error(err.getMessage());
            throw err;
        }
        log.info("Examination with id: {} was successfully updated: {}", examinationId, returned);
        return returned;
    }

    /**
     * Get examination entity by id
     *
     * @param examinationId - id of the examination
     * @return examination entity with given id
     */
    @Override
    public Examination getExamination(int examinationId) {
        return examinationRepository.findById(examinationId).orElseThrow(() -> {
            EntityNotFound err = new EntityNotFound(Consts.C404 + " ID: " + examinationId + " Type: " + this.getClass());
            log.error(err.getMessage());
            return err;
        });
    }

    /**
     * Get all examinations for given patient id
     *
     * @param patientId - id of the patient
     * @return list of all examinations
     */
    @Override
    public List<Examination> getExaminations(int patientId) {
        List<Examination> examinations = examinationRepository.findAllByIdPatient(patientId);
        if (examinations.isEmpty()) {
            EntityNotFound err = new EntityNotFound(Consts.C404);
            log.error(err.getMessage());
            throw err;
        }
        log.info("All examinations were successfully retrieved");
        return examinations;
    }
}
