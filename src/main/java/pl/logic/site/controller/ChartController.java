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
import pl.logic.site.model.dao.ChartDAO;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.Chart;
import pl.logic.site.model.response.Response;
import pl.logic.site.utils.Consts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("chartController")
@Scope("request")
public class ChartController {
    @Autowired
    ObjectFacade objectFacade;


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
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>(Consts.C201, 201, "", chart));
        } catch (SaveError e) {
            return ResponseEntity.status(453).body(new Response<>(e.getMessage(), 453, Arrays.toString(e.getStackTrace()), chart));
        }  catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    /**
     * An endpoint for getting all chart entities
     *
     * @return HTTP response
     */
    @GetMapping(value = "/charts/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all charts for specific patient from the database", description = "Get all charts for specific patient from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Response> getAllCharts(@Parameter(description = "chart id") @PathVariable int id) {
        List<Chart> charts = new ArrayList<>();
        try {
            charts = (List<Chart>) objectFacade.getObjects(new ChartDAO(new Chart()), id);
            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", charts));
        } catch (EntityNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), charts));
        }  catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    /**
     * An endpoint for getting chart by ID
     *
     * @param id - id of the chart
     * @return HTTP response
     */
    @GetMapping(value = "/chart/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get chart from the database", description = "Get chart from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Response> getChart(@Parameter(description = "patient id") @PathVariable int id) {
        Chart chart = new Chart();
        try {
            chart = (Chart) objectFacade.getObject(new ChartDAO(new Chart()), id);
            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", chart));
        } catch (EntityNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), chart));
        }  catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    /**
     * An endpoint for updating specific chart entity
     *
     * @param id - id of the chart
     * @param chartDAO
     * @return HTTP response
     */
    @ResponseBody
    @PutMapping(value = "/charts/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update specific chart from the database", description = "Update specific chart from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "209", description = "Successfully updated"),
            @ApiResponse(responseCode = "404", description = "Entity not found"),
            @ApiResponse(responseCode = "454", description = "Error during update")
    })
    public ResponseEntity<Response> updateChart(@Parameter(description = "id of chart to be searched") @PathVariable int id, @RequestBody ChartDAO chartDAO) {
        Chart chart = new Chart();
        try {
            chart = (Chart) objectFacade.updateObject(chartDAO, id);
            // Update chart logic here
            return ResponseEntity.status(209).body(new Response<>(Consts.C209, 209, "", chart));
        } catch (EntityNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), chart));
        } catch (SaveError e) {
            return ResponseEntity.status(454).body(new Response<>(e.getMessage(), 454, Arrays.toString(e.getStackTrace()), chart));
        }  catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    /**
     * An endpoint for specific chart deletion
     *
     * @param id - id of the chart
     * @return HTTP response
     */
    @ResponseBody
    @DeleteMapping(value = "/charts/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete specific chart from the database", description = "Delete specific chart from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Entity not found"),
            @ApiResponse(responseCode = "455", description = "Error during deletion")
    })
    public ResponseEntity<Response> deleteChart(@Parameter(description = "id of chart to be searched") @PathVariable int id) {
        Chart chart = new Chart();
        try {
            objectFacade.deleteObject(new ChartDAO(new Chart()), id);
            // Update chart logic here
            return ResponseEntity.status(210).body(new Response<>(Consts.C210, 210, "", chart));
        } catch (EntityNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), chart));
        } catch (DeleteError e) {
            return ResponseEntity.status(455).body(new Response<>(e.getMessage(), 455, Arrays.toString(e.getStackTrace()), chart));
        }  catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }
    
}
