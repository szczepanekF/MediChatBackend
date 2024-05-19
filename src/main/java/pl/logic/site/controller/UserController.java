package pl.logic.site.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.logic.site.facade.ObjectFacade;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.mysql.Patient;
import pl.logic.site.model.mysql.SpringUser;
import pl.logic.site.model.response.Response;
import pl.logic.site.service.impl.UserServiceImpl;
import pl.logic.site.utils.Consts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("userController")
@Scope("request")
public class UserController {

    private final UserServiceImpl userService;
    @Autowired
    ObjectFacade objectFacade;

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
//
//    /**
//     * Endpoint used for finding a specific patient (currently, in fact, it should also detect doctors) by his name and surname.
//     * @param name
//     * @param surname
//     * @return
//     */
//    @GetMapping("/finduser/{name}/{surname}")
//    public ResponseEntity<Patient> findPatient(
//            @PathVariable String name, @PathVariable String surname
//    ) {
//        Patient patient = userService.findUser(name, surname);
//        log.info("User found: "+patient);
//        return ResponseEntity.ok(patient);
//    }


    @GetMapping(value = "/findUser/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Find user", description = "Find user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Response> findUser(@Parameter(description = "spring user id") @PathVariable int id){
        Object user;
        try{
            user = userService.findUser(id);
            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", user));
        } catch (EntityNotFound e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    @GetMapping(value = "/findSpringUserById/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Find spring user", description = "Find spring user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Response> findSpringUserById(
            @PathVariable int id
    ) {
        Optional<SpringUser> springUser;
        try {
            springUser = userService.findSpringUserById(id);
            if (springUser.isPresent()) {
                return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", springUser.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>("SpringUser not found", 404, "", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    @GetMapping(value = "/findSpringUser/{id}/{isPatient}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Find spring user", description = "Find spring user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Response> findSpringUser(
            @PathVariable int id,
            @PathVariable boolean isPatient
    ) {
        Optional<SpringUser> springUser;
        try {
            springUser = userService.findSpringUser(id, isPatient);
            if (springUser.isPresent()) {
                return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", springUser.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>("SpringUser not found", 404, "", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }


    @GetMapping(value = "/users/{userFilter}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all users from the database", description = "Get all users from the database for 0, all patients for 1, all doctors for 2")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Response> getAllUsers(@Parameter(description = "filter value, 0 all users, 1 patients, 2 doctors") @PathVariable int userFilter){
        List<SpringUser> users = new ArrayList<>();
        try{
            users = (List<SpringUser>) objectFacade.getObjects(new SpringUser(), userFilter);
            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", users));
        } catch (EntityNotFound e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), userFilter));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), userFilter));
        }
    }

}
