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
import pl.logic.site.facade.ObjectFacade;
import pl.logic.site.model.dao.DiagnosisRequestDAO;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.DiagnosisRequest;
import pl.logic.site.model.response.Response;
import pl.logic.site.utils.Consts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("diagnosisRequestController")
@Scope("request")
public class DiagnosisRequestController {
    @Autowired
    ObjectFacade objectFacade;

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
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>(Consts.C201, 201, "", diagnosisRequest));
        } catch (SaveError e) {
            return ResponseEntity.status(453).body(new Response<>(e.getMessage(), 453, Arrays.toString(e.getStackTrace()), diagnosisRequest));
        } catch (Exception e) {
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), null));
        } catch (Exception e) {
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), diagnosisRequest));
        } catch (Exception e) {
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
            // Update diagnosisRequest logic here
            return ResponseEntity.status(209).body(new Response<>(Consts.C209, 209, "", diagnosisRequest));
        } catch (EntityNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), diagnosisRequest));
        } catch (SaveError e) {
            return ResponseEntity.status(454).body(new Response<>(e.getMessage(), 454, Arrays.toString(e.getStackTrace()), diagnosisRequest));
        } catch (Exception e) {
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
            // Update diagnosisRequest logic here
            return ResponseEntity.status(210).body(new Response<>(Consts.C210, 210, "", diagnosisRequest));
        } catch (EntityNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), diagnosisRequest));
        } catch (DeleteError e) {
            return ResponseEntity.status(455).body(new Response<>(e.getMessage(), 455, Arrays.toString(e.getStackTrace()), diagnosisRequest));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }
}
