package pl.logic.site.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.logic.site.model.dao.SpringUserDAO;
import pl.logic.site.model.enums.Status;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.Patient;
import pl.logic.site.model.mysql.Specialisation;
import pl.logic.site.model.mysql.SpringUser;
import pl.logic.site.repository.DoctorRepository;
import pl.logic.site.repository.PatientRepository;
import pl.logic.site.repository.SpringUserRepository;
import pl.logic.site.service.UserService;
import pl.logic.site.utils.Consts;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final PatientRepository repository;
    private final DoctorRepository doctorRepository;
    private final SpringUserRepository springUserRepository;

    public Patient saveUser(Patient patient) {
        patient.setStatus(Status.ONLINE);
        Patient patient1 = repository.save(patient);
        log.info("Saved user: "+ patient1);
        return patient1;
    }

    public void disconnect(Patient patient) {
//        var storedUser = repository.findByName(patient.getName());
//        log.info("Disconnected user: "+ storedUser);
//        if (storedUser != null) {
//            storedUser.setStatus(Status.OFFLINE);
//            repository.save(storedUser);
//        }
    }

    public List<Patient> findConnectedUsers() {
        log.info("Connected users: "+repository.findAllByStatus(Status.ONLINE));
        log.info("All users: "+repository.findAll());
        return repository.findAllByStatus(Status.ONLINE);
    }

    public Patient findUser(String name, String surname) {
        return repository.findByNameAndAndSurname(name, surname);
    }

    public Object findUser(int id) {
        Optional<SpringUser> springUser = springUserRepository.findById(id);
        if(springUser.get().getPatientId() != null)
            return repository.findById(springUser.get().getPatientId());
        else
            return doctorRepository.findById(springUser.get().getDoctorId());
    }

    public Optional<SpringUser> findSpringUser(int id, boolean patient) {
        if(patient)
            return  springUserRepository.findByPatientId(id);
        else
            return  springUserRepository.findByDoctorId(id);

    }

    public Optional<SpringUser> findSpringUserById(int id) {
            return  springUserRepository.findById(id);

    }

    @Override
    public Optional<String> getChatRoomId(final int senderId, final int recipientId, final boolean createNewRoomIfNotExists) {
        return Optional.empty();
    }

    @Override
    public String createChatId(final int senderId, final int recipientId) {
        return null;
    }

    public List<Optional<SpringUser>> getAllUsers(int userFilter){
        return switch (userFilter) {
            case 0 -> springUserRepository.retrieveAll();
            case 1 -> springUserRepository.findAllByPatientIdNotNull();
            default -> springUserRepository.findAllByDoctorIdNotNull();
        };
    }


    @Override
    public SpringUser updateSpringUser(SpringUserDAO springUserDAO, int springUserId) {
        SpringUser springUser = new SpringUser(springUserDAO.springUser().getId(),
                springUserDAO.springUser().getUsername(),
                springUserDAO.springUser().getEmail(),
                springUserDAO.springUser().getPassword(),
                springUserDAO.springUser().getPatientId(),
                springUserDAO.springUser().getDoctorId(),
                springUserDAO.springUser().getCreationDate(),
                springUserDAO.springUser().getRole()
        );

        Optional<SpringUser> springUserOptional = springUserRepository.findById(springUserId);
        if (springUserOptional.isEmpty()) {
            EntityNotFound err = new EntityNotFound(Consts.C404 + " " + springUser);
            log.error(err.getMessage());
            throw err;
        }
        SpringUser returned;
        try {
            returned = springUserRepository.saveAndFlush(springUser);
        } catch (Exception e) {
            SaveError err = new SaveError(Consts.C454_UPDATING_ERROR + " " + springUser);
            log.error(err.getMessage());
            throw err;
        }
        log.info("Specialisation with id: {} was successfully updated: {}", springUserId, returned);
        return returned;
    }

}
