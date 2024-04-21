package pl.logic.site.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.logic.site.facade.UserFacade;
import pl.logic.site.model.dao.PatientDAO;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.Patient;
import pl.logic.site.model.response.Response;
import pl.logic.site.utils.Consts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("patientController")
@Scope("request")
public class PatientController {
    @Autowired
    UserFacade userFacade;

    /**
     * An endpoint for creating patient entity
     *
     * @param patientDao
     * @return HTTP Response
     */
    @PostMapping(value = "/patient", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create patient entity and push it to database", description = "Create patient entity and push it to database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created"),
            @ApiResponse(responseCode = "453", description = "Error during saving an entity")
    })
    public ResponseEntity<Response> createPatient(@RequestBody PatientDAO patientDao) {
        Patient patient = new Patient();
        try {
            patient = (Patient) userFacade.createUser(patientDao);
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>(Consts.C201, 201, "", patient));
        } catch (SaveError e) {
            return ResponseEntity.status(453).body(new Response<>(e.getMessage(), 453, Arrays.toString(e.getStackTrace()), patient));
        }
    }

    /**
     * An endpoint for getting all patient entities
     *
     * @return HTTP response
     */
    @GetMapping(value = "/patients", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all patients from the database", description = "Get all patients from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Response> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        try {
            patients = (List<Patient>) userFacade.getUsers(new PatientDAO(new Patient()), -1);
            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", patients));
        } catch (EntityNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), patients));
        }
    }

    /**
     * An endpoint for getting patient by ID
     *
     * @param id - id of the patient
     * @return HTTP response
     */
    @GetMapping(value = "/patients/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get patient from the database", description = "Get patient from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Response> getPatient(@Parameter(description = "id of patient to be searched") @PathVariable int id) {
        Patient patient = new Patient();
        try {
            patient = (Patient) userFacade.getUser(new PatientDAO(new Patient()), id);
            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", patient));
        } catch (EntityNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), patient));
        }
    }

    /**
     * An endpoint for updating specific patient entity
     *
     * @param id         - id of the patient
     * @param patientDAO
     * @return HTTP response
     */
    @ResponseBody
    @PutMapping(value = "/patients/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update specific patient from the database", description = "Update specific patient from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "209", description = "Successfully updated"),
            @ApiResponse(responseCode = "404", description = "Entity not found"),
            @ApiResponse(responseCode = "454", description = "Error during update")
    })
    public ResponseEntity<Response> updatePatient(@Parameter(description = "id of patient to be searched") @PathVariable int id, @RequestBody PatientDAO patientDAO) {
        Patient patient = new Patient();
        try {
            patient = (Patient) userFacade.updateUser(patientDAO, id);
            // Update patient logic here
            return ResponseEntity.status(209).body(new Response<>(Consts.C209, 209, "", patient));
        } catch (EntityNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), patient));
        } catch (SaveError e) {
            return ResponseEntity.status(454).body(new Response<>(e.getMessage(), 454, Arrays.toString(e.getStackTrace()), patient));
        }
    }

    /**
     * An endpoint for specific patient deletion
     *
     * @param id - id of the patient
     * @return HTTP response
     */
    @ResponseBody
    @DeleteMapping(value = "/patients/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete specific patient from the database", description = "Delete specific patient from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "2010", description = "Successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Entity not found"),
            @ApiResponse(responseCode = "455", description = "Error during deletion")
    })
    public ResponseEntity<Response> deletePatient(@Parameter(description = "id of patient to be searched") @PathVariable int id) {
        Patient patient = new Patient();
        try {
            userFacade.deleteUser(new PatientDAO(new Patient()), id);
            // Update patient logic here
            return ResponseEntity.status(210).body(new Response<>(Consts.C210, 210, "", patient));
        } catch (EntityNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), patient));
        } catch (DeleteError e) {
            return ResponseEntity.status(455).body(new Response<>(e.getMessage(), 455, Arrays.toString(e.getStackTrace()), patient));
        }
    }
}
