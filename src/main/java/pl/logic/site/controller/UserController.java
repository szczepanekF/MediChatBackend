package pl.logic.site.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.logic.site.model.mysql.Patient;
import pl.logic.site.service.impl.UserServiceImpl;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;

    /**
     * Test endpoint for adding new user. Do not use.
     * @param patient
     * @return
     */
    @Deprecated
    @MessageMapping("/user.addUser")
    @SendTo("/user/public")
    public Patient addUser(
            @Payload Patient patient
    ) {
        log.info("Saving user:"+patient);
        Patient patient1 = userService.saveUser(patient);
        log.info("Returned user:"+patient1);
        return patient1;
    }

    /**
     * Endpoint used for disconnecting a specific patient (currently, in fact, it should also detect doctors) by changing
     * their STATUS to 1 (OFFLINE).
     * @param patient
     * @return
     */
    @MessageMapping("/user.disconnectUser")
    @SendTo("/patient/public")
    public Patient disconnectUser(
            @Payload Patient patient
    ) {
        log.info("Disconnecting user:"+patient);
        userService.disconnect(patient);
        return patient;
    }

    /**
     * Endpoint used for finding all patients (currently, in fact, it should also detect doctors).
     * @return
     */
    @GetMapping("/users")
    public ResponseEntity<List<Patient>> findConnectedUsers() {
        log.info("Finding connected users");
        return ResponseEntity.ok(userService.findConnectedUsers());
    }

    /**
     * Endpoint used for finding a specific patient (currently, in fact, it should also detect doctors) by his name and surname.
     * @param name
     * @param surname
     * @return
     */
    @GetMapping("/finduser/{name}/{surname}")
    public ResponseEntity<Patient> findPatient(
            @PathVariable String name, @PathVariable String surname
    ) {
        Patient patient = userService.findUser(name, surname);
        log.info("User found: "+patient);
        return ResponseEntity.ok(patient);
    }
}
