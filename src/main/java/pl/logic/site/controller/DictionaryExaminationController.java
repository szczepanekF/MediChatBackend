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
import pl.logic.site.model.dao.DictionaryExaminationDAO;
import pl.logic.site.model.enums.LogType;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.mysql.DictionaryExamination;
import pl.logic.site.model.response.Response;
import pl.logic.site.service.LoggingService;
import pl.logic.site.utils.Consts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("dictionaryExaminationController")
@Scope("request")

public class DictionaryExaminationController {
    @Autowired
    ObjectFacade objectFacade;
    @Autowired
    LoggingService loggingService;
    @Autowired
    HttpServletRequest request;

    /**
     * An endpoint for getting dictionary examination by ID
     *
     * @param dictionaryExaminationId - id of the dictionary examination
     * @return HTTP response
     */
    @GetMapping(value = "/dictionaryExamination/{dictionaryExaminationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get dictionary examination from the database", description = "Get dictionary examination from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Response> getDictionaryExamination(@Parameter(description = "id of dictionary examination to be searched") @PathVariable int dictionaryExaminationId) {
        DictionaryExamination dictionaryExamination = new DictionaryExamination();
        try {
            dictionaryExamination = (DictionaryExamination) objectFacade.getObject(new DictionaryExaminationDAO(new DictionaryExamination()), dictionaryExaminationId);

            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", dictionaryExamination));
        } catch (EntityNotFound e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), dictionaryExamination));
        } catch (Exception e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    /**
     * An endpoint for getting all dictionary examination entities
     *
     * @return HTTP response
     */
    @GetMapping(value = "/dictionaryExaminations", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all dictionary examinations from the database", description = "Get all dictionary examinations from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Response> getAllDictionaryExaminations() {
        List<DictionaryExamination> dictionaryExaminations = new ArrayList<>();
        try {
            dictionaryExaminations = (List<DictionaryExamination>) objectFacade.getObjects(new DictionaryExaminationDAO(new DictionaryExamination()), -1);

            ;
            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", dictionaryExaminations));
        } catch (EntityNotFound e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), dictionaryExaminations));
        } catch (Exception e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }
}
