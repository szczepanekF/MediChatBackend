package pl.logic.site.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.logic.site.model.dao.PatientDAO;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.Patient;
import pl.logic.site.repository.PatientRepository;
import pl.logic.site.service.PatientService;
import pl.logic.site.utils.Consts;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PatientServiceImpl implements PatientService {
    @Autowired
    private PatientRepository patientRepository;

    /**
     * Create patient based on given data access object
     *
     * @param patient - data access object
     * @return created patient
     */
    @Override
    @Transactional
    public Patient createPatient(PatientDAO patient) {
        Patient patientEntity = new Patient(patient.patient().getId(),
                                            patient.patient().getName(),
                                            patient.patient().getSurname(),
                                            patient.patient().getBirth_date(),
                                            patient.patient().getHeight(),
                                            patient.patient().getWeight(),
                                            patient.patient().getGender(),
                                            patient.patient().getStatus(),
                                            patient.patient().getHeightUnit(),
                                            patient.patient().getWeightUnit()
                );
        if (patientEntity.getId() != 0) {
            SaveError err = new SaveError(Consts.C453_SAVING_ERROR + " Explicitly stated entity ID, entity: " + patientEntity);
            log.error(err.getMessage());
            throw err;
        }
        Patient returned;
        try {
            returned = patientRepository.saveAndFlush(patientEntity);
        } catch (Exception e) {
            SaveError err = new SaveError(Consts.C453_SAVING_ERROR + " " + patientEntity);
            log.error(err.getMessage());
            throw err;
        }
        log.info("Patient was successfully created: {}", returned);
        return returned;
    }

    /**
     * Delete patient with given id
     *
     * @param id - id of the patient
     */
    @Override
    public void deletePatient(int id) {
        Optional<Patient> patient = patientRepository.findById(id);
        if (patient.isEmpty()) {
            EntityNotFound err = new EntityNotFound(Consts.C404 + " ID: " + id + " Type: " + this.getClass());
            log.error(err.getMessage());
            throw err;
        }
        try {
            patientRepository.deleteById(patient.get().getId());
        } catch (Exception e) {
            DeleteError err = new DeleteError(Consts.C455_DELETING_ERROR + " " + patient);
            log.error(err.getMessage());
            throw err;
        }
        log.info("Patient with id: {} was successfully deleted", id);
    }

    /**
     * Update patient based on patient data access object and patients id
     *
     * @param patient - data access object
     * @param id      - id of the patient
     * @return updated patient
     */
    @Override
    public Patient updatePatient(PatientDAO patient, int id) {
        Patient patientEntity = new Patient(patient.patient().getId(),
                                            patient.patient().getName(),
                                            patient.patient().getSurname(),
                                            patient.patient().getBirth_date(),
                                            patient.patient().getHeight(),
                                            patient.patient().getWeight(),
                                            patient.patient().getGender(),
                                            patient.patient().getStatus(),
                                            patient.patient().getHeightUnit(),
                                            patient.patient().getWeightUnit()
                );
        Optional<Patient> patientFromDatabase = patientRepository.findById(id);
        if (patientFromDatabase.isEmpty()) {
            EntityNotFound err = new EntityNotFound(Consts.C404 + " " + patientEntity);
            log.error(err.getMessage());
            throw err;
        }
        Patient returned;
        try {
            returned = patientRepository.saveAndFlush(patientEntity);
        } catch (Exception e) {
            SaveError err = new SaveError(Consts.C454_UPDATING_ERROR + " " + patientEntity);
            log.error(err.getMessage());
            throw err;
        }
        log.info("Patient with id: {} was successfully updated: {}", id, returned);
        return returned;
    }

    /**
     * Get patient entity by id
     *
     * @param id - id of the patient
     * @return patient entity with given id
     */
    @Override
    public Patient getPatient(int id) {
        return patientRepository.findById(id).orElseThrow(() -> {
            EntityNotFound err = new EntityNotFound(Consts.C404 + " ID: " + id + " Type: " + this.getClass());
            log.error(err.getMessage());
            return err;
        });
    }

    /**
     * Get all patients
     *
     * @return list of all patients
     */
    @Override
    public List<Patient> getPatients() {
        List<Patient> patients = patientRepository.findAll();
        if (patients.isEmpty()) {
            EntityNotFound err = new EntityNotFound(Consts.C404);
            log.error(err.getMessage());
            throw err;
        }
        log.info("All patients were successfully retrieved");
        return patients;
    }
}
