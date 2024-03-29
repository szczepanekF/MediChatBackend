package pl.logic.site.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.service.DoctorService;
import pl.logic.site.model.dao.DoctorDAO;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.mysql.Doctor;
import pl.logic.site.repository.DoctorRepository;
import pl.logic.site.utils.Consts;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;


    @Override
    @Transactional
    public Doctor createDoctor(final DoctorDAO doctor) {
        Doctor doctorEntity = new Doctor(doctor.doctor().getId(),
                                         doctor.doctor().getName(),
                                         doctor.doctor().getSurname(),
                                         doctor.doctor().getBirth_date(),
                                         doctor.doctor().getSpecialisation_id()) ;
        if(doctorEntity.getId() != 0)
            throw new SaveError(Consts.C453_SAVING_ERROR + " Explicitly stated entity ID, entity: " + doctorEntity);

        Doctor returned;
        try {
            returned = doctorRepository.saveAndFlush(doctorEntity);
        }
        catch (Exception e){
            throw new SaveError(Consts.C453_SAVING_ERROR + " " + doctorEntity);
        }
        return returned;
    }

    @Override
    @Transactional
    public void deleteDoctor(int id) {
        Optional<Doctor> doctor = doctorRepository.findById(id);
        if(doctor.isEmpty())
            throw new EntityNotFound(Consts.C404 + " ID: "+id + " Type: "+this.getClass());

        try {
            doctorRepository.deleteById(doctor.get().getId());
        } catch (Exception e) {
            throw new DeleteError(Consts.C455_DELETING_ERROR + " " + doctor);
        }
    }

    @Override
    @Transactional
    public Doctor updateDoctor(final DoctorDAO doctor, int id) {
        Doctor doctorEntity = new Doctor(doctor.doctor().getId(),
                doctor.doctor().getName(),
                doctor.doctor().getSurname(),
                doctor.doctor().getBirth_date(),
                doctor.doctor().getSpecialisation_id());
        doctorEntity.setId(id);

        Optional<Doctor> doctorFromDatabse = doctorRepository.findById(id);
        if(doctorFromDatabse.isEmpty())
            throw new EntityNotFound(Consts.C404 + " " + doctorEntity);

        Doctor returned;
        try {
            returned = doctorRepository.saveAndFlush(doctorEntity);
        }
        catch (Exception e){
            throw new SaveError(Consts.C454_UPDATING_ERROR + " " + doctorEntity);
        }
        return returned;
    }

    @Override
    public Doctor getDoctor(final int id) {
        return doctorRepository.findById(id).orElseThrow(() -> new EntityNotFound(Consts.C404 + " ID: "+id + " Type: "+this.getClass()));
    }

    @Override
    public List<Doctor> getDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        if(doctors.isEmpty())
            throw new EntityNotFound(Consts.C404);
        return doctors;
    }
}
