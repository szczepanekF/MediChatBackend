package pl.logic.site.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.logic.site.model.dao.SpecialisationDAO;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.Specialisation;
import pl.logic.site.repository.SpecialisationRepository;
import pl.logic.site.service.SpecialisationService;
import pl.logic.site.utils.Consts;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SpecialisationServiceImpl implements SpecialisationService {
    @Autowired
    private SpecialisationRepository specialisationRepository;

    /**
     * Create specialisation based on given data access object
     *
     * @param specialisation - data access object
     * @return created specialisation
     */
    @Override
    @Transactional
    public Specialisation createSpecialisation(SpecialisationDAO specialisation) {
        Specialisation specialisationEntity = new Specialisation(specialisation.specialisation().getId(),
                specialisation.specialisation().getSpecialisation());

        if (specialisationEntity.getId() != 0) {
            SaveError err = new SaveError(Consts.C453_SAVING_ERROR + " Explicitly stated entity ID, entity: " + specialisationEntity);
            log.error(err.getMessage());
            throw err;
        }
        Specialisation returned;
        try {
            returned = specialisationRepository.saveAndFlush(specialisationEntity);
        } catch (Exception e) {
            SaveError err = new SaveError(Consts.C453_SAVING_ERROR + " " + specialisationEntity);
            log.error(err.getMessage());
            throw err;
        }
        log.info("Specialisation was successfully created: {}", returned);
        return returned;
    }

    /**
     * Delete specialisation with given id
     *
     * @param id - id of the specialisation
     */
    @Override
    public void deleteSpecialisation(int id) {
        Optional<Specialisation> specialisation = specialisationRepository.findById(id);
        if (specialisation.isEmpty()) {
            EntityNotFound err = new EntityNotFound(Consts.C404 + " ID: " + id + " Type: " + this.getClass());
            log.error(err.getMessage());
            throw err;
        }
        try {
            specialisationRepository.deleteById(specialisation.get().getId());
        } catch (Exception e) {
            DeleteError err = new DeleteError(Consts.C455_DELETING_ERROR + " " + specialisation);
            log.error(err.getMessage());
            throw err;
        }
        log.info("Specialisation with id: {} was successfully deleted", id);
    }

    /**
     * Update specialisation based on specialisation data access object and specialisations id
     *
     * @param specialisation - data access object
     * @param id             - id of the specialisation
     * @return updated specialisation
     */
    @Override
    public Specialisation updateSpecialisation(SpecialisationDAO specialisation, int id) {
        Specialisation specialisationEntity = new Specialisation(specialisation.specialisation().getId(),
                specialisation.specialisation().getSpecialisation());

        Optional<Specialisation> specialisationFromDatabase = specialisationRepository.findById(id);
        if (specialisationFromDatabase.isEmpty()) {
            EntityNotFound err = new EntityNotFound(Consts.C404 + " " + specialisationEntity);
            log.error(err.getMessage());
            throw err;
        }
        Specialisation returned;
        try {
            returned = specialisationRepository.saveAndFlush(specialisationEntity);
        } catch (Exception e) {
            SaveError err = new SaveError(Consts.C454_UPDATING_ERROR + " " + specialisationEntity);
            log.error(err.getMessage());
            throw err;
        }
        log.info("Specialisation with id: {} was successfully updated: {}", id, returned);
        return returned;
    }

    /**
     * Get specialisation entity by id
     *
     * @param id - id of the specialisation
     * @return specialisation entity with given id
     */
    @Override
    public Specialisation getSpecialisation(int id) {
        return specialisationRepository.findById(id).orElseThrow(() -> {
            EntityNotFound err = new EntityNotFound(Consts.C404 + " ID: " + id + " Type: " + this.getClass());
            log.error(err.getMessage());
            return err;
        });
    }

    /**
     * Get all specialisations
     *
     * @return list of all specialisations
     */
    @Override
    public List<Specialisation> getSpecialisations() {
        List<Specialisation> specialisations = specialisationRepository.findAll();
        if (specialisations.isEmpty()) {
            EntityNotFound err = new EntityNotFound(Consts.C404);
            log.error(err.getMessage());
            throw err;
        }
        log.info("All specialisations were successfully retrieved");
        return specialisations;
    }
}
