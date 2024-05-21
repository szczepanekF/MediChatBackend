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
import pl.logic.site.model.dao.SpecialisationDAO;
import pl.logic.site.model.dao.SymptomValuesDAO;
import pl.logic.site.model.enums.LogType;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.mysql.Specialisation;
import pl.logic.site.model.mysql.SymptomValues;
import pl.logic.site.model.response.Response;
import pl.logic.site.service.LoggingService;
import pl.logic.site.utils.Consts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("symptomValuesController")
@Scope("request")
public class SyptomValuesController {
    @Autowired
    ObjectFacade objectFacade;

    @Autowired
    LoggingService loggingService;
    @Autowired
    HttpServletRequest request;

    /**
     * An endpoint for getting all specialisation entities
     *
     * @return HTTP response
     */
    @GetMapping(value = "/symptomValue", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all syptoms values from the database", description = "Get all syptoms values from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Response> getAllSymptomValues() {
        List<SymptomValues> symptomValues = new ArrayList<>();
        try {
            symptomValues = (List<SymptomValues>) objectFacade.getObjects(new SymptomValuesDAO(new SymptomValues()), -1);
            loggingService.createLog(ControllerUtils.combinePaths(request) + "SymptomValues ",
                    Consts.LOG_SUCCESFULLY_RETRIEVED, LogType.info, AuthorizationHeaderHolder.getAuthorizationHeader());

            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", symptomValues));
        } catch (EntityNotFound e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), symptomValues));
        } catch (Exception e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }


}
