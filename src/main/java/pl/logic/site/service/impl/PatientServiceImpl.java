package pl.logic.site.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.logic.site.model.dao.PatientDAO;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.Patient;
import pl.logic.site.model.mysql.SpringUser;
import pl.logic.site.model.response.Response;
import pl.logic.site.repository.ChatRoomRepository;
import pl.logic.site.repository.PatientRepository;
import pl.logic.site.service.ChatRoomService;
import pl.logic.site.service.PatientService;
import pl.logic.site.utils.Consts;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;
    private final ChatRoomService chatRoomService;
    private final UserServiceImpl userService;


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
     * @param patientId - id of the patient
     */
    @Override
    public void deletePatient(int patientId) {
        Optional<Patient> patient = patientRepository.findById(patientId);
        if (patient.isEmpty()) {
            EntityNotFound err = new EntityNotFound(Consts.C404 + " ID: " + patientId + " Type: " + this.getClass());
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
        log.info("Patient with id: {} was successfully deleted", patientId);
    }

    /**
     * Update patient based on patient data access object and patients id
     *
     * @param patient   - data access object
     * @param patientId - id of the patient
     * @return updated patient
     */
    @Override
    public Patient updatePatient(PatientDAO patient, int patientId) {
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

        Optional<Patient> patientFromDatabase = patientRepository.findById(patientId);
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
        log.info("Patient with id: {} was successfully updated: {}", patientId, returned);
        return returned;
    }

    /**
     * Get patient entity by id
     *
     * @param patientId - id of the patient
     * @return patient entity with given id
     */
    @Override
    public Patient getPatient(int patientId) {
        return patientRepository.findById(patientId).orElseThrow(() -> {
            EntityNotFound err = new EntityNotFound(Consts.C404 + " ID: " + patientId + " Type: " + this.getClass());
            log.error(err.getMessage());
            return err;
        });
    }

    /**
     * Get all patients by filter
     *
     * @param patientsFilter - filter deciding whether to return all patients or patients of given doctor
     * @return list of all patients
     */
    @Override
    public List<Patient> getPatients(int patientsFilter) {
        List<Patient> patients = patientRepository.findAll();

        if (patientsFilter > 0) {
            Optional<SpringUser> doctorUser = userService.findSpringUser(patientsFilter, false);
            if (doctorUser.isPresent()) {
                int doctorSpringUserId = doctorUser.get().getId();
                patients = patients.stream().filter(patient -> {
                    Optional<SpringUser> patientUser = userService.findSpringUser(patient.getId(), true);
                    return patientUser.isPresent() &&
                            chatRoomService.getChatRoomId(patientUser.get().getId(), doctorSpringUserId, false).isPresent();
                }).collect(Collectors.toList());
            } else {
                patients = List.of();
            }

        }
        if (patients.isEmpty()) {
            EntityNotFound err = new EntityNotFound(Consts.C404);
            log.error(err.getMessage());
            throw err;
        }
        log.info("All patients were successfully retrieved");
        return patients;
    }


    /**
     * Get all patients
     *
     * @return list of all patients
     */
    @Override
    public List<Patient> getPatients() {
        return getPatients(0);
    }

    @Override
    public int getAge(int patientId) {
        Patient patient = getPatient(patientId);
        LocalDate birthLocalDate = patient.getBirth_date().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        LocalDate currentDate = LocalDate.now();

        // Check if the birthdate is not in the future
        if (birthLocalDate.isAfter(currentDate)) {
            throw new IllegalArgumentException("Birth date cannot be in the future");
        }
        Period age = Period.between(birthLocalDate, currentDate);

        return age.getYears();
    }


//public int calculateAgeOnDate(Date onDate) {
//        LocalDate birthLocalDate = this.birth_date.toInstant()
//                .atZone(ZoneId.systemDefault())
//                .toLocalDate();
//        LocalDate onLocalDate = onDate.toInstant()
//                .atZone(ZoneId.systemDefault())
//                .toLocalDate();
//
//        // Check if the birthdate is not after the given date
//        if (birthLocalDate.isAfter(onLocalDate)) {
//            throw new IllegalArgumentException("Birth date cannot be after the date on which age is calculated");
//        }
//        Period age = Period.between(birthLocalDate, onLocalDate);
//
//        return age.getYears();
//    }
}
