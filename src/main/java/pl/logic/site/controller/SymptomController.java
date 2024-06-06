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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.logic.site.aspects.AuthorizationHeaderHolder;
import pl.logic.site.aspects.ControllerUtils;
import pl.logic.site.facade.ObjectFacade;
import pl.logic.site.model.dao.SymptomDAO;
import pl.logic.site.model.enums.LogType;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.mysql.Symptom;
import pl.logic.site.model.response.Response;
import pl.logic.site.service.LoggingService;
import pl.logic.site.utils.Consts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("symptomController")
@Scope("request")

public class SymptomController {
    @Autowired
    ObjectFacade objectFacade;
    @Autowired
    LoggingService loggingService;
    @Autowired
    HttpServletRequest request;

    /**
     * An endpoint for getting symptom by ID
     *
     * @param symptomId - id of the symptom
     * @return HTTP response
     */
    @GetMapping(value = "/symptoms/{symptomId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get symptom from the database", description = "Get symptom from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Response> getSymptom(@Parameter(description = "id of symptom to be searched") @PathVariable int symptomId) {
        Symptom symptom = new Symptom();
        try {
            symptom = (Symptom) objectFacade.getObject(new SymptomDAO(new Symptom()), symptomId);
            
            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", symptom));
        } catch (EntityNotFound e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), symptom));
        } catch (Exception e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    /**
     * An endpoint for getting all symptom entities
     *
     * @return HTTP response
     */
    @GetMapping(value = "/symptoms", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all symptoms from the database", description = "Get all symptoms from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Response> getAllSymptoms() {
        List<Symptom> symptoms = new ArrayList<>();
        try {
            symptoms = (List<Symptom>) objectFacade.getObjects(new SymptomDAO(new Symptom()), -1);
            

            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", symptoms));
        } catch (EntityNotFound e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), symptoms));
        } catch (Exception e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }
}
