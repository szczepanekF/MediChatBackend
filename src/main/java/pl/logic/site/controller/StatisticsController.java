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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.logic.site.aspects.AuthorizationHeaderHolder;
import pl.logic.site.aspects.ControllerUtils;
import pl.logic.site.facade.ObjectFacade;
import pl.logic.site.model.dao.ChartDAO;
import pl.logic.site.model.dao.ReportDAO;
import pl.logic.site.model.dao.SpecialisationDAO;
import pl.logic.site.model.enums.LogType;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.Chart;
import pl.logic.site.model.mysql.Report;
import pl.logic.site.model.mysql.Specialisation;
import pl.logic.site.model.reportsForms.ReportCreateForm;
import pl.logic.site.model.response.Response;
import pl.logic.site.service.LoggingService;
import pl.logic.site.utils.Consts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("statisticsController")
@Scope("request")
public class StatisticsController  {
    @Autowired
    ObjectFacade objectFacade;
    @Autowired
    LoggingService loggingService;
    @Autowired
    HttpServletRequest request;

    //getDoctorReports
    //createNewReport

    @PostMapping(value = "/report", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create report based on the form, generate file and push it to database", description = "Create report based on the form, generate file and push it to database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created"),
            @ApiResponse(responseCode = "453", description = "Error during saving an entity")
    })
    public ResponseEntity<Response> createSpecialisation(@RequestBody ReportCreateForm reportCreateForm) {
        Report report = new Report();
        try {
            report = objectFacade.createReport(reportCreateForm);
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_SUCCESFULLY_CREATED + "Specialisation ", report,
                    LogType.create, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>(Consts.C201, 201, "", report));
        } catch (SaveError e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(453).body(new Response<>(e.getMessage(), 453, Arrays.toString(e.getStackTrace()), report));
        } catch (Exception e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    /**
     * An endpoint for getting all reports by doctor id
     *
     * @param doctorId - id of the doctor
     * @return HTTP response
     */
    @GetMapping(value = "/reports/{doctorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all diagnosis request for specific doctorId from the database", description = "Get all reports for specific doctorId from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Response> getAllCharts(@Parameter(description = "doctorId") @PathVariable int doctorId) {
        List<Report> reports = new ArrayList<>();
        try {
            reports = (List<Report>) objectFacade.getObjects(new ReportDAO(new Report()), doctorId);


            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", reports));
        } catch (EntityNotFound e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), reports));
        } catch (Exception e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }
}