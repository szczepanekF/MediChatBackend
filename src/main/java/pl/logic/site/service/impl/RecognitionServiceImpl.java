package pl.logic.site.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.logic.site.model.dao.RecognitionDAO;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.Recognition;
import pl.logic.site.repository.RecognitionRepository;
import pl.logic.site.service.RecognitionService;
import pl.logic.site.utils.Consts;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class RecognitionServiceImpl implements RecognitionService {
    @Autowired
    private RecognitionRepository recognitionRepository;

    /**
     * Create recognition based on given data access object
     *
     * @param recognition - data access object
     * @return created recognition
     */
    @Override
    @Transactional
    public Recognition createRecognition(RecognitionDAO recognition) {
        Recognition recognitionEntity = new Recognition(recognition.recognition().getId(),
                recognition.recognition().getIdChart(),
                recognition.recognition().getIdSymptom(),
                recognition.recognition().getSymptomValue());

        if (recognitionEntity.getId() != 0) {
            SaveError err = new SaveError(Consts.C453_SAVING_ERROR + " Explicitly stated entity ID, entity: " + recognitionEntity);
            log.error(err.getMessage());
            throw err;
        }
        Recognition returned;
        try {
            returned = recognitionRepository.saveAndFlush(recognitionEntity);
        } catch (Exception e) {
            SaveError err = new SaveError(Consts.C453_SAVING_ERROR + " " + recognitionEntity);
            log.error(err.getMessage());
            throw err;
        }
        log.info("Recognition was successfully created: {}", returned);
        return returned;
    }

    /**
     * Delete recognition with given id
     *
     * @param id - id of the recognition
     */
    @Override
    public void deleteRecognition(int id) {
        Optional<Recognition> recognition = recognitionRepository.findById(id);
        if (recognition.isEmpty()) {
            EntityNotFound err = new EntityNotFound(Consts.C404 + " ID: " + id + " Type: " + this.getClass());
            log.error(err.getMessage());
            throw err;
        }
        try {
            recognitionRepository.deleteById(recognition.get().getId());
        } catch (Exception e) {
            DeleteError err = new DeleteError(Consts.C455_DELETING_ERROR + " " + recognition);
            log.error(err.getMessage());
            throw err;
        }
        log.info("Recognition with id: {} was successfully deleted", id);
    }

    /**
     * Update recognition based on recognition data access object and recognitions id
     *
     * @param recognition - data access object
     * @param id          - id of the recognition
     * @return updated recognition
     */
    @Override
    public Recognition updateRecognition(RecognitionDAO recognition, int id) {
        Recognition recognitionEntity = new Recognition(recognition.recognition().getId(),
                recognition.recognition().getIdChart(),
                recognition.recognition().getIdSymptom(),
                recognition.recognition().getSymptomValue());

        Optional<Recognition> recognitionFromDatabase = recognitionRepository.findById(id);
        if (recognitionFromDatabase.isEmpty()) {
            EntityNotFound err = new EntityNotFound(Consts.C404 + " " + recognitionEntity);
            log.error(err.getMessage());
            throw err;
        }
        Recognition returned;
        try {
            returned = recognitionRepository.saveAndFlush(recognitionEntity);
        } catch (Exception e) {
            SaveError err = new SaveError(Consts.C454_UPDATING_ERROR + " " + recognitionEntity);
            log.error(err.getMessage());
            throw err;
        }
        log.info("Recognition with id: {} was successfully updated: {}", id, returned);
        return returned;
    }

    /**
     * Get recognition entity by id
     *
     * @param id - id of the recognition
     * @return recognition entity with given id
     */
    @Override
    public Recognition getRecognition(int id) {
        return recognitionRepository.findById(id).orElseThrow(() -> {
            EntityNotFound err = new EntityNotFound(Consts.C404 + " ID: " + id + " Type: " + this.getClass());
            log.error(err.getMessage());
            return err;
        });
    }

    /**
     * Get all recognitions
     *
     * @return list of all recognitions
     */
    @Override
    public List<Recognition> getRecognitions() {
        List<Recognition> recognitions = recognitionRepository.findAll();
        if (recognitions.isEmpty()) {
            EntityNotFound err = new EntityNotFound(Consts.C404);
            log.error(err.getMessage());
            throw err;
        }
        log.info("All recognitions were successfully retrieved");
        return recognitions;
    }
}
