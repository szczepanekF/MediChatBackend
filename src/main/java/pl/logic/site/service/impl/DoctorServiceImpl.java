package pl.logic.site.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.mysql.DiagnosisRequest;
import pl.logic.site.repository.DiagnosisRequestRepository;
import pl.logic.site.service.DoctorService;
import pl.logic.site.model.dao.DoctorDAO;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.mysql.Doctor;
import pl.logic.site.repository.DoctorRepository;
import pl.logic.site.utils.Consts;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private DiagnosisRequestRepository diagnosisRequestRepository;


    @Override
    @Transactional
    public Doctor createDoctor(final DoctorDAO doctor) {
        Doctor doctorEntity = new Doctor(doctor.doctor().getId(),
                doctor.doctor().getName(),
                doctor.doctor().getSurname(),
                doctor.doctor().getBirth_date(),
                doctor.doctor().getSpecialisation_id(),
                doctor.doctor().getIsBot());
        if (doctorEntity.getId() != 0)
            throw new SaveError(Consts.C453_SAVING_ERROR + " Explicitly stated entity ID, entity: " + doctorEntity);

        Doctor returned;
        try {
            returned = doctorRepository.saveAndFlush(doctorEntity);
        } catch (Exception e) {
            throw new SaveError(Consts.C453_SAVING_ERROR + " " + doctorEntity);
        }
        return returned;
    }

    @Override
    @Transactional
    public void deleteDoctor(int doctorId) {
        Optional<Doctor> doctor = doctorRepository.findById(doctorId);
        if (doctor.isEmpty())
            throw new EntityNotFound(Consts.C404 + " ID: " + doctorId + " Type: " + this.getClass());

        try {
            doctorRepository.deleteById(doctor.get().getId());
        } catch (Exception e) {
            throw new DeleteError(Consts.C455_DELETING_ERROR + " " + doctor);
        }
    }

    @Override
    @Transactional
    public Doctor updateDoctor(final DoctorDAO doctor, int doctorId) {
        Doctor doctorEntity = new Doctor(doctor.doctor().getId(),
                doctor.doctor().getName(),
                doctor.doctor().getSurname(),
                doctor.doctor().getBirth_date(),
                doctor.doctor().getSpecialisation_id(),
                doctor.doctor().getIsBot());
        doctorEntity.setId(doctorId);

        Optional<Doctor> doctorFromDatabse = doctorRepository.findById(doctorId);
        if (doctorFromDatabse.isEmpty())
            throw new EntityNotFound(Consts.C404 + " " + doctorEntity);

        Doctor returned;
        try {
            returned = doctorRepository.saveAndFlush(doctorEntity);
        } catch (Exception e) {
            throw new SaveError(Consts.C454_UPDATING_ERROR + " " + doctorEntity);
        }
        return returned;
    }

    @Override
    public Doctor getDoctor(final int doctorId) {
        return doctorRepository.findById(doctorId).orElseThrow(() -> new EntityNotFound(Consts.C404 + " ID: " + doctorId + " Type: " + this.getClass()));
    }

    @Override
    public List<Doctor> getDoctors(int doctorFilter) {
        List<Doctor> doctors;
        if (doctorFilter == 2)
            doctors = doctorRepository.findAll();
        else
            doctors = doctorRepository.retrieveDoctorsByType(doctorFilter);
        if (doctors.isEmpty())
            throw new EntityNotFound(Consts.C404);
        return doctors;
    }

    @Override
    public Doctor getDoctorByDiagnosisRequest(int diagnosisRequestId) {
        return doctorRepository.findAllById(diagnosisRequestRepository.findById(diagnosisRequestId).get().getIdDoctor());

    }

}
