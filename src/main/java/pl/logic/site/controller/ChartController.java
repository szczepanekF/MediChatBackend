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
import pl.logic.site.model.dao.ChartDAO;
import pl.logic.site.model.enums.LogType;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.Chart;
import pl.logic.site.model.response.Response;
import pl.logic.site.service.LoggingService;
import pl.logic.site.utils.Consts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("chartController")
@Scope("request")
public class ChartController {
    @Autowired
    ObjectFacade objectFacade;
    @Autowired
    LoggingService loggingService;
    @Autowired
    HttpServletRequest request;


    /**
     * An endpoint for creating chart entity
     *
     * @param chartDao - chart data access object
     * @return HTTP Response
     */
    @PostMapping(value = "/chart", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create chart entity and push it to database", description = "Create chart entity and push it to database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created"),
            @ApiResponse(responseCode = "453", description = "Error during saving an entity")
    })
    public ResponseEntity<Response> createChart(@RequestBody ChartDAO chartDao) {


        Chart chart = new Chart();
        try {
            chart = (Chart) objectFacade.createObject(chartDao);
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_SUCCESFULLY_CREATED + "Chart ", chart,
                    LogType.create, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>(Consts.C201, 201, "", chart));
        } catch (SaveError e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(453).body(new Response<>(e.getMessage(), 453, Arrays.toString(e.getStackTrace()), chart));
        } catch (Exception e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    /**
     * An endpoint for getting all charts for specified patient
     *
     * @param patientId - id of the patient
     * @return HTTP response
     */
    @GetMapping(value = "/charts/{patientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all charts for specific patient from the database", description = "Get all charts for specific patient from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Response> getAllCharts(@Parameter(description = "patient id") @PathVariable int patientId) {
        List<Chart> charts = new ArrayList<>();
        try {
            charts = (List<Chart>) objectFacade.getObjects(new ChartDAO(new Chart()), patientId);

            loggingService.createLog(ControllerUtils.combinePaths(request) + "Charts for patient id: " + patientId,
                    Consts.LOG_SUCCESFULLY_RETRIEVED, LogType.info, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", charts));
        } catch (EntityNotFound e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), charts));
        } catch (Exception e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }


    /**
     * An endpoint for getting chart entities for specified patient and state.
     *
     * @param state     - state which specifies if the returned charts are with diagnosis requested (1) or not (0)
     * @param patientId - id of the patient
     * @return HTTP response
     */
    @GetMapping(value = "/chartsByState/{state}/{patientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all charts with specific state for patient from the database",
            description = "Get all charts with specific state for patient from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Response> getChartsByDiagnosis(@Parameter(description = "1 for charts with diagnosis request, 0 otherwise") @PathVariable int state, @Parameter(description = "id of patient for which charts are retrieved") @PathVariable int patientId) {
        List<Chart> charts = new ArrayList<>();
        try {
            charts = objectFacade.getChartsByStateAndPatientId(state, patientId);
            loggingService.createLog(ControllerUtils.combinePaths(request) + "Charts for patient id: " + patientId + " and state: " + state + " ",
                    Consts.LOG_SUCCESFULLY_RETRIEVED, LogType.info, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", charts));
        } catch (EntityNotFound e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), charts));
        } catch (Exception e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }


    /**
     * An endpoint for getting chart by ID
     *
     * @param chartId - id of the chart
     * @return HTTP response
     */
    @GetMapping(value = "/chart/{chartId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get chart from the database", description = "Get chart from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Response> getChart(@Parameter(description = "id of the chart to be retreived") @PathVariable int chartId) {
        Chart chart = new Chart();
        try {
            chart = (Chart) objectFacade.getObject(new ChartDAO(new Chart()), chartId);
            loggingService.createLog(ControllerUtils.combinePaths(request) + "Chart ",
                    Consts.LOG_SUCCESFULLY_RETRIEVED, LogType.info, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", chart));
        } catch (EntityNotFound e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), chart));
        } catch (Exception e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    /**
     * An endpoint for updating specific chart entity
     *
     * @param chartId  - id of the chart
     * @param chartDAO - chart data access object
     * @return HTTP response
     */
    @ResponseBody
    @PutMapping(value = "/chart/{chartId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update specific chart from the database", description = "Update specific chart from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "209", description = "Successfully updated"),
            @ApiResponse(responseCode = "404", description = "Entity not found"),
            @ApiResponse(responseCode = "454", description = "Error during update")
    })
    public ResponseEntity<Response> updateChart(@Parameter(description = "id of chart to be updated") @PathVariable int chartId, @RequestBody ChartDAO chartDAO) {
        Chart chart = new Chart();
        try {
            chart = (Chart) objectFacade.updateObject(chartDAO, chartId);

            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_SUCCESFULLY_UPDATED + "Chart ", chart,
                    LogType.update, AuthorizationHeaderHolder.getAuthorizationHeader());
            // Update chart logic here
            return ResponseEntity.status(209).body(new Response<>(Consts.C209, 209, "", chart));
        } catch (EntityNotFound e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), chart));
        } catch (SaveError e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(454).body(new Response<>(e.getMessage(), 454, Arrays.toString(e.getStackTrace()), chart));
        } catch (Exception e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    /**
     * An endpoint for specific chart deletion
     *
     * @param chartId - id of the chart
     * @return HTTP response
     */
    @ResponseBody
    @DeleteMapping(value = "/chart/{chartId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete specific chart from the database", description = "Delete specific chart from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Entity not found"),
            @ApiResponse(responseCode = "455", description = "Error during deletion")
    })
    public ResponseEntity<Response> deleteChart(@Parameter(description = "id of chart to be searched") @PathVariable int chartId) {
        Chart chart = new Chart();
        try {
            objectFacade.deleteObject(new ChartDAO(new Chart()), chartId);
            // Update chart logic here
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_SUCCESFULLY_DELETED + "Chart ", chart,
                    LogType.delete, AuthorizationHeaderHolder.getAuthorizationHeader());

            return ResponseEntity.status(210).body(new Response<>(Consts.C210, 210, "", chart));
        } catch (EntityNotFound e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), chart));
        } catch (DeleteError e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(455).body(new Response<>(e.getMessage(), 455, Arrays.toString(e.getStackTrace()), chart));
        } catch (Exception e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

}
