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
import pl.logic.site.model.dao.ExaminationDAO;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.Examination;
import pl.logic.site.model.response.Response;
import pl.logic.site.utils.Consts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("examinationController")
@Scope("request")
public class ExaminationController {
    @Autowired
    ObjectFacade objectFacade;

    /**
     * An endpoint for creating examination entity
     *
     * @param examinationDao
     * @return HTTP Response
     */
    @PostMapping(value = "/examination", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create examination entity and push it to database", description = "Create examination entity and push it to database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created"),
            @ApiResponse(responseCode = "453", description = "Error during saving an entity")
    })
    public ResponseEntity<Response> createExamination(@RequestBody ExaminationDAO examinationDao) {
        Examination examination = new Examination();
        try {
            examination = (Examination) objectFacade.createObject(examinationDao);
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>(Consts.C201, 201, "", examination));
        } catch (SaveError e) {
            return ResponseEntity.status(453).body(new Response<>(e.getMessage(), 453, Arrays.toString(e.getStackTrace()), examination));
        }  catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    /**
     * An endpoint for getting all examination entities
     *
     * @return HTTP response
     */
    @GetMapping(value = "/examinations", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all examinations from the database", description = "Get all examinations from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Response> getAllExaminations() {
        List<Examination> examinations = new ArrayList<>();
        try {
            examinations = (List<Examination>) objectFacade.getObjects(new ExaminationDAO(new Examination()), -1);
            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", examinations));
        } catch (EntityNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), examinations));
        }  catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    /**
     * An endpoint for getting examination by ID
     *
     * @param id - id of the examination
     * @return HTTP response
     */
    @GetMapping(value = "/examinations/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get examination from the database", description = "Get examination from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Response> getExamination(@Parameter(description = "id of examination to be searched") @PathVariable int id) {
        Examination examination = new Examination();
        try {
            examination = (Examination) objectFacade.getObject(new ExaminationDAO(new Examination()), id);
            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", examination));
        } catch (EntityNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), examination));
        }  catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    /**
     * An endpoint for updating specific examination entity
     *
     * @param id         - id of the examination
     * @param examinationDAO
     * @return HTTP response
     */
    @ResponseBody
    @PutMapping(value = "/examinations/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update specific examination from the database", description = "Update specific examination from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "209", description = "Successfully updated"),
            @ApiResponse(responseCode = "404", description = "Entity not found"),
            @ApiResponse(responseCode = "454", description = "Error during update")
    })
    public ResponseEntity<Response> updateExamination(@Parameter(description = "id of examination to be searched") @PathVariable int id, @RequestBody ExaminationDAO examinationDAO) {
        Examination examination = new Examination();
        try {
            examination = (Examination) objectFacade.updateObject(examinationDAO, id);
            // Update examination logic here
            return ResponseEntity.status(209).body(new Response<>(Consts.C209, 209, "", examination));
        } catch (EntityNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), examination));
        } catch (SaveError e) {
            return ResponseEntity.status(454).body(new Response<>(e.getMessage(), 454, Arrays.toString(e.getStackTrace()), examination));
        }  catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    /**
     * An endpoint for specific examination deletion
     *
     * @param id - id of the examination
     * @return HTTP response
     */
    @ResponseBody
    @DeleteMapping(value = "/examinations/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete specific examination from the database", description = "Delete specific examination from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Entity not found"),
            @ApiResponse(responseCode = "455", description = "Error during deletion")
    })
    public ResponseEntity<Response> deleteExamination(@Parameter(description = "id of examination to be searched") @PathVariable int id) {
        Examination examination = new Examination();
        try {
            objectFacade.deleteObject(new ExaminationDAO(new Examination()), id);
            // Update examination logic here
            return ResponseEntity.status(210).body(new Response<>(Consts.C210, 210, "", examination));
        } catch (EntityNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), examination));
        } catch (DeleteError e) {
            return ResponseEntity.status(455).body(new Response<>(e.getMessage(), 455, Arrays.toString(e.getStackTrace()), examination));
        }  catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }
}
