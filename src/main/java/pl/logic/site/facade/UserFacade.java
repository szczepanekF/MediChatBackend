package pl.logic.site.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.logic.site.model.dao.DoctorDAO;
import pl.logic.site.model.dao.PatientDAO;
import pl.logic.site.model.exception.UnknownUserType;
import pl.logic.site.service.DoctorService;
import pl.logic.site.service.PatientService;
import pl.logic.site.utils.Consts;

@Component
public class UserFacade {
    private final DoctorService doctorService;
    private final PatientService patientService;


    @Autowired
    public UserFacade(DoctorService doctorService, PatientService patientService) {
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    /**
     * Create user of given data access object class using suitable service
     *
     * @param user - data access object providing data for user creation
     * @return created user
     */
    public Object createUser(Object user) {
        return switch (user) {
            case DoctorDAO doctor -> doctorService.createDoctor(doctor);
            case PatientDAO patient -> patientService.createPatient(patient);
            default -> throw new UnknownUserType(Consts.C452_UKNOWN_USER_TYPE);
        };
    }

    /**
     * Get user based on data access object class and user ID
     *
     * @param user - data access object
     * @param id   - id of the user
     * @return retreived user
     */
    public Object getUser(Object user, int id) {
        return switch (user) {
            case DoctorDAO doctor -> doctorService.getDoctor(id);
            case PatientDAO patient -> patientService.getPatient(id);
            default -> throw new UnknownUserType(Consts.C452_UKNOWN_USER_TYPE);
        };
    }

    /**
     * Get all users of class represented by given data access object
     *
     * @param user - data access object
     * @return all users of class represented by user param
     */
    public Object getUsers(Object user) {
        return switch (user) {
            case DoctorDAO doctor -> doctorService.getDoctors();
            case PatientDAO patient -> patientService.getPatients();
            default -> throw new UnknownUserType(Consts.C452_UKNOWN_USER_TYPE);
        };
    }

    /**
     * Get all users of class represented by given data access object
     *
     * @param user - data access object
     * @param isBot - integer specifying if doctor is bot (1) or not (0)
     * @return all users of class represented by user param
     */
    public Object getUsersByDoctorType(Object user, boolean isBot) {
        return switch (user) {
            case DoctorDAO doctor -> {
                if (isBot) {
                    yield doctorService.getDoctorsBot();
                } else {
                    yield doctorService.getDoctorsNonBot();
                }
            }
            default -> throw new UnknownUserType(Consts.C452_UKNOWN_USER_TYPE);
        };
    }


    /**
     * Update user based on data access object class and user ID
     *
     * @param user - data access object
     * @param id   - id of the user
     * @return updated user
     */
    public Object updateUser(Object user, int id) {
        return switch (user) {
            case DoctorDAO doctor -> doctorService.updateDoctor(doctor, id);
            case PatientDAO patient -> patientService.updatePatient(patient, id);
            default -> throw new UnknownUserType(Consts.C452_UKNOWN_USER_TYPE);
        };
    }

    /**
     * Delete user based on data access object class and user ID
     *
     * @param user - data access object
     * @param id   - id of the user
     */
    public void deleteUser(Object user, int id) {
        switch (user) {
            case DoctorDAO doctor -> doctorService.deleteDoctor(id);
            case PatientDAO patient -> patientService.deletePatient(id);
            default -> throw new UnknownUserType(Consts.C452_UKNOWN_USER_TYPE);
        }
        ;
    }
}