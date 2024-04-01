package pl.logic.site.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.logic.site.model.enums.Status;
import pl.logic.site.model.mysql.Patient;
import pl.logic.site.repository.PatientRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl {

    private final PatientRepository repository;

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
}
