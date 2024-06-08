package pl.logic.site.service;


import pl.logic.site.model.dao.DoctorDAO;
import pl.logic.site.model.mysql.Chart;
import pl.logic.site.model.mysql.DiagnosisRequest;
import pl.logic.site.model.mysql.Doctor;
import pl.logic.site.model.mysql.Patient;
import pl.logic.site.model.views.DoctorPatientsWithData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A service used for manipulating doctor entity.
 */
public interface DoctorService {
    Doctor createDoctor(DoctorDAO doctor);

    void deleteDoctor(int doctorId);

    Doctor updateDoctor(DoctorDAO doctor, int doctorId);

    Doctor getDoctor(int doctorId);

    List<Doctor> getDoctors(int doctorFilter);

    Doctor getDoctorByDiagnosisRequest(int diagnosisRequestId);

    public List<Patient> getMyPatients(int doctorId);

    public List<DoctorPatientsWithData> getMyPatientsByDate(final int doctorId, Date fromDate, Date toDate);

    public List<DiagnosisRequest> getMyDiagnosisRequests(int doctorId, Date from, Date to);

    public List<Chart> getMyCharts(int doctorId, Date from, Date to);
}