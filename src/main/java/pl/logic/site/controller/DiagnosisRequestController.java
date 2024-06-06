package pl.logic.site.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
import pl.logic.site.model.dao.ChartDAO;
import pl.logic.site.model.dao.DiagnosisRequestDAO;
import pl.logic.site.model.dao.PatientDAO;
import pl.logic.site.model.enums.EmailType;
import pl.logic.site.model.enums.LogType;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.DiagnosisRequest;
import pl.logic.site.model.mysql.Message;
import pl.logic.site.model.mysql.SpringUser;
import pl.logic.site.model.response.Response;
import pl.logic.site.service.ChartService;
import pl.logic.site.service.LoggingService;
import pl.logic.site.service.UserService;
import pl.logic.site.service.impl.MessageServiceImpl;
import pl.logic.site.service.impl.UserServiceImpl;

import pl.logic.site.model.mysql.*;
import pl.logic.site.model.response.Response;
import pl.logic.site.service.impl.EmailServiceImpl;

import pl.logic.site.utils.Consts;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("diagnosisRequestController")
@Scope("request")
public class DiagnosisRequestController {
    private final ObjectFacade objectFacade;
    private final LoggingService loggingService;
    private final HttpServletRequest request;
    private final EmailServiceImpl emailService;


    /**
     * An endpoint for creating diagnosis request entity
     *
     * @param diagnosisRequestDao - diagnosis request data access object
     * @return HTTP Response
     */
    @PostMapping(value = "/diagnosisRequest", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create diagnosis request entity and push it to database", description = "Create diagnosis request entity and push it to database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created"),
            @ApiResponse(responseCode = "453", description = "Error during saving an entity")
    })
    public ResponseEntity<Response> createDiagnosisRequest(@RequestBody DiagnosisRequestDAO diagnosisRequestDao) {
        DiagnosisRequest diagnosisRequest = new DiagnosisRequest();
        try {
            diagnosisRequest = (DiagnosisRequest) objectFacade.createObject(diagnosisRequestDao);
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_SUCCESFULLY_CREATED + "DiagnosisRequest ", diagnosisRequest,
                    LogType.create, AuthorizationHeaderHolder.getAuthorizationHeader());
            Doctor doctor = (Doctor) objectFacade.getDoctorByDiagnosisRequest(diagnosisRequest.getId());
            Chart chart = (Chart) objectFacade.getObject(new ChartDAO(new Chart()), diagnosisRequest.getIdChart());
            Patient patient = (Patient) objectFacade.getObject(new PatientDAO(new Patient()), chart.getIdPatient());
            String dateString = diagnosisRequest.getCreationDate().toString();
            String diagnosis = diagnosisRequest.getDiagnosis();
            SpringUser springUser = objectFacade.getUserIdByDoctorOrPatientId(doctor.getId(), false).orElseThrow();
            if(doctor.getIsBot() == 0) {
                Map<String, String> emailParameters = new HashMap<>() {{
                    put("requestUserFullName", patient.getName() + " " + patient.getSurname());
                    put("emailAddress", springUser.getEmail());
                    put("name", doctor.getName());
                    put("date", dateString);
                    put("requestContent", diagnosis);
                    put("thisUserId", String.valueOf(springUser.getId()));
                    put("patientUserId", String.valueOf(patient.getId()));
                    put("subject", "REQUEST DIAGNOSIS");
                }};
                emailService.sendEmail(EmailType.DIAGNOSIS_REQUEST, emailParameters);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>(Consts.C201, 201, "", diagnosisRequest));
        } catch (SaveError e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(453).body(new Response<>(e.getMessage(), 453, Arrays.toString(e.getStackTrace()), diagnosisRequest));
        } catch (Exception e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    /**
     * An endpoint for getting diagnosis request entity with given chartId
     *
     * @param chartId - id of the chart
     * @return HTTP response
     */
    @GetMapping(value = "/diagnosisRequestByChart/{chartId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all diagnosis requests for chart id from the database", description = "Get all diagnosis requests for chart id from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Response> getDiagnosisRequestByChart(@Parameter(description = "id of the chart for which diagnosis requests will be returned") @PathVariable int chartId) {
        DiagnosisRequest diagnosisRequest = new DiagnosisRequest();
        try {
            diagnosisRequest = (DiagnosisRequest) objectFacade.getDiagnosisRequestByChartId(chartId);

            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", diagnosisRequest));
        } catch (EntityNotFound e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), null));
        } catch (Exception e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    /**
     * An endpoint for getting diagnosis request by ID
     *
     * @param diagnosisRequestId - id of the diagnosis request
     * @return HTTP response
     */
    @GetMapping(value = "/diagnosisRequest/{diagnosisRequestId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get diagnosis request from the database", description = "Get diagnosis request from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Response> getDiagnosisRequest(@Parameter(description = "id of diagnosis request to be searched") @PathVariable int diagnosisRequestId) {
        DiagnosisRequest diagnosisRequest = new DiagnosisRequest();
        try {
            diagnosisRequest = (DiagnosisRequest) objectFacade.getObject(new DiagnosisRequestDAO(new DiagnosisRequest()), diagnosisRequestId);

            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", diagnosisRequest));
        } catch (EntityNotFound e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), diagnosisRequest));
        } catch (Exception e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    /**
     * An endpoint for updating specific diagnosis request entity
     *
     * @param diagnosisRequestId  - id of the diagnosis request
     * @param diagnosisRequestDAO - diagnosis request data access object
     * @return HTTP response
     */
    @ResponseBody
    @PutMapping(value = "/diagnosisRequest/{diagnosisRequestId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update specific diagnosis request from the database", description = "Update specific diagnosis request from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "209", description = "Successfully updated"),
            @ApiResponse(responseCode = "404", description = "Entity not found"),
            @ApiResponse(responseCode = "454", description = "Error during update")
    })
    public ResponseEntity<Response> updateDiagnosisRequest(@Parameter(description = "id of diagnosis request to be searched") @PathVariable int diagnosisRequestId, @RequestBody DiagnosisRequestDAO diagnosisRequestDAO) {
        DiagnosisRequest diagnosisRequest = new DiagnosisRequest();
        try {
            diagnosisRequest = (DiagnosisRequest) objectFacade.updateObject(diagnosisRequestDAO, diagnosisRequestId);
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_SUCCESFULLY_UPDATED + "DiagnosisRequest ", diagnosisRequest,
                    LogType.update, AuthorizationHeaderHolder.getAuthorizationHeader());
            // Update diagnosisRequest logic here
            return ResponseEntity.status(209).body(new Response<>(Consts.C209, 209, "", diagnosisRequest));
        } catch (EntityNotFound e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), diagnosisRequest));
        } catch (SaveError e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(454).body(new Response<>(e.getMessage(), 454, Arrays.toString(e.getStackTrace()), diagnosisRequest));
        } catch (Exception e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    /**
     * An endpoint for specific diagnosis request deletion
     *
     * @param diagnosisRequestId - id of the diagnosis request
     * @return HTTP response
     */
    @ResponseBody
    @DeleteMapping(value = "/diagnosisRequest/{diagnosisRequestId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete specific diagnosis request from the database", description = "Delete specific diagnosis request from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Entity not found"),
            @ApiResponse(responseCode = "455", description = "Error during deletion")
    })
    public ResponseEntity<Response> deleteDiagnosisRequest(@Parameter(description = "id of diagnosis request to be searched") @PathVariable int diagnosisRequestId) {
        DiagnosisRequest diagnosisRequest = new DiagnosisRequest();
        try {
            objectFacade.deleteObject(new DiagnosisRequestDAO(new DiagnosisRequest()), diagnosisRequestId);
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_SUCCESFULLY_DELETED + "DiagnosisRequest ", diagnosisRequest,
                    LogType.delete, AuthorizationHeaderHolder.getAuthorizationHeader());
            // Update diagnosisRequest logic here
            return ResponseEntity.status(210).body(new Response<>(Consts.C210, 210, "", diagnosisRequest));
        } catch (EntityNotFound e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), diagnosisRequest));
        } catch (DeleteError e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(455).body(new Response<>(e.getMessage(), 455, Arrays.toString(e.getStackTrace()), diagnosisRequest));
        } catch (Exception e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }
}
