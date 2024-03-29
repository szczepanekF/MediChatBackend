package pl.logic.site.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.logic.site.model.dao.DoctorDAO;
import pl.logic.site.model.exception.UnknownUserType;
import pl.logic.site.service.DoctorService;
import pl.logic.site.utils.Consts;

@Component
public class UserFacade {
    private final DoctorService doctorService;

    @Autowired
    public UserFacade(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    public Object createUser(Object user) {
        if (user instanceof DoctorDAO) {
            return doctorService.createDoctor((DoctorDAO) user);
        } else {
            throw new UnknownUserType(Consts.C452_UKNOWN_USER_TYPE);
        }
    }

    public Object getUser(Object user, int id) {
        if (user instanceof DoctorDAO) {
            return doctorService.getDoctor(id);
        } else {
            throw new UnknownUserType(Consts.C452_UKNOWN_USER_TYPE);
        }
    }


    public Object getUsers(Object user) {
        if (user instanceof DoctorDAO) {
            return doctorService.getDoctors();
        } else {
            throw new UnknownUserType(Consts.C452_UKNOWN_USER_TYPE);
        }
    }

    public Object updateUser(Object user, int id) {
        if (user instanceof DoctorDAO) {
            return doctorService.updateDoctor((DoctorDAO) user, id);
        } else {
            throw new UnknownUserType(Consts.C452_UKNOWN_USER_TYPE);
        }
    }

    public void deleteUser(Object user, int id) {
        if (user instanceof DoctorDAO) {
             doctorService.deleteDoctor(id);
        } else {
            throw new UnknownUserType(Consts.C452_UKNOWN_USER_TYPE);
        }
    }
}