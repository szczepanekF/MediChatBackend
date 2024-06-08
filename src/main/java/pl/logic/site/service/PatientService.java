package pl.logic.site.service;

import pl.logic.site.model.dao.PatientDAO;
import pl.logic.site.model.mysql.Patient;

import java.util.List;

/**
 * A service used for manipulating patient entity.
 */
public interface PatientService {
    Patient createPatient(PatientDAO patient);

    void deletePatient(int id);

    Patient updatePatient(PatientDAO patient, int patientId);

    Patient getPatient(int patientId);

    List<Patient> getPatients();
    List<Patient> getPatients(int patientsFilter);
    int getAge(int id);

}
