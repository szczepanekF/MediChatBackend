package pl.logic.site.service;


import pl.logic.site.model.dao.DoctorDAO;
import pl.logic.site.model.mysql.Doctor;

import java.util.List;

/**
 * A service used for manipulating doctor entity.
 */
public interface DoctorService {
    Doctor createDoctor(DoctorDAO doctor);
    void deleteDoctor(int id);
    Doctor updateDoctor(DoctorDAO doctor, int id);
    Doctor getDoctor(int id);
    List<Doctor> getDoctors(int doctorFilter);
    Doctor getDoctorByDiagnosisRequest(int diagnosisRequestId);
}
