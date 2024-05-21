package pl.logic.site.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.logic.site.aspects.AuthorizationHeaderHolder;
import pl.logic.site.aspects.ControllerUtils;
import pl.logic.site.facade.ObjectFacade;
import pl.logic.site.model.dao.PatientDAO;
import pl.logic.site.model.enums.LogType;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.Patient;
import pl.logic.site.model.response.Response;
import pl.logic.site.service.LoggingService;
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
    ObjectFacade objectFacade;
    @Autowired
    LoggingService loggingService;
    @Autowired
    HttpServletRequest request;

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
            patient = (Patient) objectFacade.createObject(patientDao);
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_SUCCESFULLY_CREATED + "Patient ", patient,
                    LogType.create, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>(Consts.C201, 201, "", patient));
        } catch (SaveError e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(453).body(new Response<>(e.getMessage(), 453, Arrays.toString(e.getStackTrace()), patient));
        } catch (Exception e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
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
            patients = (List<Patient>) objectFacade.getObjects(new PatientDAO(new Patient()), -1);
            loggingService.createLog(ControllerUtils.combinePaths(request) + "Patients ",
                    Consts.LOG_SUCCESFULLY_RETRIEVED, LogType.info, AuthorizationHeaderHolder.getAuthorizationHeader());

            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", patients));
        } catch (EntityNotFound e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), patients));
        } catch (Exception e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }


    /**
     * An endpoint for getting patient by ID
     *
     * @param patientsFilter - id of the doctor which patients (chat between doctor and patient exists) will be returned
     * @return HTTP response
     */
    @GetMapping(value = "/patientsByDoctor/{patientsFilter}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get patients from the database based on existing chat rooms with given doctor", description = "Get patients from the database based on existing chat rooms with given doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Response> getPatientsByDoctor(@Parameter(description = "id of doctor which patients (with existing chat) will be returned") @PathVariable int patientsFilter) {
        List<Patient> patients = new ArrayList<>();
        try {
            patients = (List<Patient>) objectFacade.getObjects(new PatientDAO(new Patient()), patientsFilter);
            loggingService.createLog(ControllerUtils.combinePaths(request) + "Patients by doctor id: " + patientsFilter,
                    Consts.LOG_SUCCESFULLY_RETRIEVED, LogType.info, AuthorizationHeaderHolder.getAuthorizationHeader());

            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", patients));
        } catch (EntityNotFound e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), patients));
        } catch (Exception e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }


    /**
     * An endpoint for getting patient by ID
     *
     * @param patientId id of the patient
     * @return HTTP response
     */

    @GetMapping(value = "/patients/{patientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get patient from the database", description = "Get patient from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Response> getPatient(@Parameter(description = "id of patient to be searched") @PathVariable int patientId) {
        Patient patient = new Patient();
        try {
            patient = (Patient) objectFacade.getObject(new PatientDAO(new Patient()), patientId);
            loggingService.createLog(ControllerUtils.combinePaths(request) + "Patient ",
                    Consts.LOG_SUCCESFULLY_RETRIEVED, LogType.info, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", patient));
        } catch (EntityNotFound e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), patient));
        } catch (Exception e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }


    /**
     * An endpoint for updating specific patient entity
     *
     * @param patientId  - id of the patient
     * @param patientDAO
     * @return HTTP response
     */
    @ResponseBody
    @PutMapping(value = "/patients/{patientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update specific patient from the database", description = "Update specific patient from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "209", description = "Successfully updated"),
            @ApiResponse(responseCode = "404", description = "Entity not found"),
            @ApiResponse(responseCode = "454", description = "Error during update")
    })
    public ResponseEntity<Response> updatePatient(@Parameter(description = "id of patient to be searched") @PathVariable int patientId, @RequestBody PatientDAO patientDAO) {
        Patient patient = new Patient();
        try {
            patient = (Patient) objectFacade.updateObject(patientDAO, patientId);
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_SUCCESFULLY_UPDATED + "Patient ", patient,
                    LogType.update, AuthorizationHeaderHolder.getAuthorizationHeader());
            // Update patient logic here
            return ResponseEntity.status(209).body(new Response<>(Consts.C209, 209, "", patient));
        } catch (EntityNotFound e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), patient));
        } catch (SaveError e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(454).body(new Response<>(e.getMessage(), 454, Arrays.toString(e.getStackTrace()), patient));
        } catch (Exception e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    /**
     * An endpoint for specific patient deletion
     *
     * @param patientId - id of the patient
     * @return HTTP response
     */
    @ResponseBody
    @DeleteMapping(value = "/patients/{patientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete specific patient from the database", description = "Delete specific patient from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Entity not found"),
            @ApiResponse(responseCode = "455", description = "Error during deletion")
    })
    public ResponseEntity<Response> deletePatient(@Parameter(description = "id of patient to be searched") @PathVariable int patientId) {
        Patient patient = new Patient();
        try {
            objectFacade.deleteObject(new PatientDAO(new Patient()), patientId);
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_SUCCESFULLY_DELETED + "Patient ", patient,
                    LogType.delete, AuthorizationHeaderHolder.getAuthorizationHeader());
            // Update patient logic here
            return ResponseEntity.status(210).body(new Response<>(Consts.C210, 210, "", patient));
        } catch (EntityNotFound e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), patient));
        } catch (DeleteError e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(455).body(new Response<>(e.getMessage(), 455, Arrays.toString(e.getStackTrace()), patient));
        } catch (Exception e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

}
