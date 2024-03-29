package pl.logic.site.service;


import pl.logic.site.model.dao.DoctorDAO;
import pl.logic.site.model.mysql.Doctor;

import java.util.List;

public interface DoctorService {
    Doctor createDoctor(DoctorDAO doctor);
    void deleteDoctor(int id);
    Doctor updateDoctor(DoctorDAO doctor, int id);
    Doctor getDoctor(int id);
    List<Doctor> getDoctors();
}
