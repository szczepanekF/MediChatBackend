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
import pl.logic.site.model.dao.ChartSymptomDAO;
import pl.logic.site.model.enums.LogType;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.ChartSymptom;
import pl.logic.site.model.response.Response;
import pl.logic.site.service.LoggingService;
import pl.logic.site.utils.Consts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("chartSymptomController")
@Scope("request")
public class ChartSymptomController {
    @Autowired
    ObjectFacade objectFacade;
    @Autowired
    LoggingService loggingService;
    @Autowired
    HttpServletRequest request;

    /**
     * An endpoint for creating chartSymptom entity
     *
     * @param chartSymptomDao
     * @return HTTP Response
     */
    @PostMapping(value = "/chartSymptom", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create chartSymptom entity and push it to database", description = "Create chartSymptom entity and push it to database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created"),
            @ApiResponse(responseCode = "453", description = "Error during saving an entity")
    })
    public ResponseEntity<Response> createChartSymptom(@RequestBody ChartSymptomDAO chartSymptomDao) {
        ChartSymptom chartSymptom = new ChartSymptom();
        try {
            chartSymptom = (ChartSymptom) objectFacade.createObject(chartSymptomDao);

            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_SUCCESFULLY_DELETED + "ChartSymptom ", chartSymptom,
                    LogType.create, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>(Consts.C201, 201, "", chartSymptom));
        } catch (SaveError e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(453).body(new Response<>(e.getMessage(), 453, Arrays.toString(e.getStackTrace()), chartSymptom));
        } catch (Exception e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    /**
     * An endpoint for getting all chartSymptom entities
     *
     * @return HTTP response
     */
    @GetMapping(value = "/chartSymptoms/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all chartSymptoms from the database for chart", description = "Get all chartSymptoms for chart id from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Response> getAllChartSymptoms(@Parameter(description = "chart id") @PathVariable int id) {
        List<ChartSymptom> chartSymptoms = new ArrayList<>();
        try {
            chartSymptoms = (List<ChartSymptom>) objectFacade.getObjects(new ChartSymptomDAO(new ChartSymptom()), id);
            
            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", chartSymptoms));
        } catch (EntityNotFound e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), chartSymptoms));
        } catch (Exception e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }


    /**
     * An endpoint for updating specific chartSymptom entity
     *
     * @param id              - id of the chartSymptom
     * @param chartSymptomDAO
     * @return HTTP response
     */
    @ResponseBody
    @PutMapping(value = "/chartSymptoms/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update specific chartSymptom from the database", description = "Update specific chartSymptom from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "209", description = "Successfully updated"),
            @ApiResponse(responseCode = "404", description = "Entity not found"),
            @ApiResponse(responseCode = "454", description = "Error during update")
    })
    public ResponseEntity<Response> updateChartSymptom(@Parameter(description = "id of chartSymptom to be searched") @PathVariable int id, @RequestBody ChartSymptomDAO chartSymptomDAO) {
        ChartSymptom chartSymptom = new ChartSymptom();
        try {
            chartSymptom = (ChartSymptom) objectFacade.updateObject(chartSymptomDAO, id);
            // Update chartSymptom logic here
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_SUCCESFULLY_UPDATED + "ChartSymptom ", chartSymptom,
                    LogType.update, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(209).body(new Response<>(Consts.C209, 209, "", chartSymptom));
        } catch (EntityNotFound e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), chartSymptom));
        } catch (SaveError e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(454).body(new Response<>(e.getMessage(), 454, Arrays.toString(e.getStackTrace()), chartSymptom));
        } catch (Exception e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    /**
     * An endpoint for specific chartSymptom deletion
     *
     * @param id - id of the chartSymptom
     * @return HTTP response
     */
    @ResponseBody
    @DeleteMapping(value = "/chartSymptoms/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete specific chartSymptom from the database", description = "Delete specific chartSymptom from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Entity not found"),
            @ApiResponse(responseCode = "455", description = "Error during deletion")
    })
    public ResponseEntity<Response> deleteChartSymptom(@Parameter(description = "id of chartSymptom to be searched") @PathVariable int id) {
        ChartSymptom chartSymptom = new ChartSymptom();
        try {
            objectFacade.deleteObject(new ChartSymptomDAO(new ChartSymptom()), id);
            // Update chartSymptom logic here
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_SUCCESFULLY_DELETED + "ChartSymptom ", chartSymptom,
                    LogType.delete, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(210).body(new Response<>(Consts.C210, 210, "", chartSymptom));
        } catch (EntityNotFound e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), chartSymptom));
        } catch (DeleteError e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(455).body(new Response<>(e.getMessage(), 455, Arrays.toString(e.getStackTrace()), chartSymptom));
        } catch (Exception e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }
}
