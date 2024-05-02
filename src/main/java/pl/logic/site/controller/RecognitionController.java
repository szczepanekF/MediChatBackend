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
import pl.logic.site.model.dao.RecognitionDAO;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.Recognition;
import pl.logic.site.model.response.Response;
import pl.logic.site.utils.Consts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("recognitionController")
@Scope("request")
public class RecognitionController {
    @Autowired
    ObjectFacade objectFacade;

    /**
     * An endpoint for creating recognition entity
     *
     * @param recognitionDao
     * @return HTTP Response
     */
    @PostMapping(value = "/recognition", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create recognition entity and push it to database", description = "Create recognition entity and push it to database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created"),
            @ApiResponse(responseCode = "453", description = "Error during saving an entity")
    })
    public ResponseEntity<Response> createRecognition(@RequestBody RecognitionDAO recognitionDao) {
        Recognition recognition = new Recognition();
        try {
            recognition = (Recognition) objectFacade.createObject(recognitionDao);
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>(Consts.C201, 201, "", recognition));
        } catch (SaveError e) {
            return ResponseEntity.status(453).body(new Response<>(e.getMessage(), 453, Arrays.toString(e.getStackTrace()), recognition));
        }  catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    /**
     * An endpoint for getting all recognition entities
     *
     * @return HTTP response
     */
    @GetMapping(value = "/recognitions/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all recognitions from the database", description = "Get all recognitions for chart id from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Response> getAllRecognitions(@Parameter(description = "chart id") @PathVariable int id) {
        List<Recognition> recognitions = new ArrayList<>();
        try {
            recognitions = (List<Recognition>) objectFacade.getObjects(new RecognitionDAO(new Recognition()), id);
            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", recognitions));
        } catch (EntityNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), recognitions));
        }  catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }


    /**
     * An endpoint for updating specific recognition entity
     *
     * @param id         - id of the recognition
     * @param recognitionDAO
     * @return HTTP response
     */
    @ResponseBody
    @PutMapping(value = "/recognitions/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update specific recognition from the database", description = "Update specific recognition from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "209", description = "Successfully updated"),
            @ApiResponse(responseCode = "404", description = "Entity not found"),
            @ApiResponse(responseCode = "454", description = "Error during update")
    })
    public ResponseEntity<Response> updateRecognition(@Parameter(description = "id of recognition to be searched") @PathVariable int id, @RequestBody RecognitionDAO recognitionDAO) {
        Recognition recognition = new Recognition();
        try {
            recognition = (Recognition) objectFacade.updateObject(recognitionDAO, id);
            // Update recognition logic here
            return ResponseEntity.status(209).body(new Response<>(Consts.C209, 209, "", recognition));
        } catch (EntityNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), recognition));
        } catch (SaveError e) {
            return ResponseEntity.status(454).body(new Response<>(e.getMessage(), 454, Arrays.toString(e.getStackTrace()), recognition));
        }  catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    /**
     * An endpoint for specific recognition deletion
     *
     * @param id - id of the recognition
     * @return HTTP response
     */
    @ResponseBody
    @DeleteMapping(value = "/recognitions/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete specific recognition from the database", description = "Delete specific recognition from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Entity not found"),
            @ApiResponse(responseCode = "455", description = "Error during deletion")
    })
    public ResponseEntity<Response> deleteRecognition(@Parameter(description = "id of recognition to be searched") @PathVariable int id) {
        Recognition recognition = new Recognition();
        try {
            objectFacade.deleteObject(new RecognitionDAO(new Recognition()), id);
            // Update recognition logic here
            return ResponseEntity.status(210).body(new Response<>(Consts.C210, 210, "", recognition));
        } catch (EntityNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), recognition));
        } catch (DeleteError e) {
            return ResponseEntity.status(455).body(new Response<>(e.getMessage(), 455, Arrays.toString(e.getStackTrace()), recognition));
        }  catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }
}
