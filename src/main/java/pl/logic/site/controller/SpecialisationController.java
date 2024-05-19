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
import pl.logic.site.model.dao.SpecialisationDAO;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.Specialisation;
import pl.logic.site.model.response.Response;
import pl.logic.site.utils.Consts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("specialisationController")
@Scope("request")
public class SpecialisationController {
    @Autowired
    ObjectFacade objectFacade;

    /**
     * An endpoint for creating specialisation entity
     *
     * @param specialisationDao
     * @return HTTP Response
     */
    @PostMapping(value = "/specialisation", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create specialisation entity and push it to database", description = "Create specialisation entity and push it to database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created"),
            @ApiResponse(responseCode = "453", description = "Error during saving an entity")
    })
    public ResponseEntity<Response> createSpecialisation(@RequestBody SpecialisationDAO specialisationDao) {
        Specialisation specialisation = new Specialisation();
        try {
            specialisation = (Specialisation) objectFacade.createObject(specialisationDao);
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>(Consts.C201, 201, "", specialisation));
        } catch (SaveError e) {
            return ResponseEntity.status(453).body(new Response<>(e.getMessage(), 453, Arrays.toString(e.getStackTrace()), specialisation));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    /**
     * An endpoint for getting all specialisation entities
     *
     * @return HTTP response
     */
    @GetMapping(value = "/specialisations", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all specialisations from the database", description = "Get all specialisations from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Response> getAllSpecialisations() {
        List<Specialisation> specialisations = new ArrayList<>();
        try {
            specialisations = (List<Specialisation>) objectFacade.getObjects(new SpecialisationDAO(new Specialisation()), -1);
            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", specialisations));
        } catch (EntityNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), specialisations));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    /**
     * An endpoint for getting specialisation by ID
     *
     * @param specialisationId - id of the specialisation
     * @return HTTP response
     */
    @GetMapping(value = "/specialisations/{specialisationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get specialisation from the database", description = "Get specialisation from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Response> getSpecialisation(@Parameter(description = "id of specialisation to be searched") @PathVariable int specialisationId) {
        Specialisation specialisation = new Specialisation();
        try {
            specialisation = (Specialisation) objectFacade.getObject(new SpecialisationDAO(new Specialisation()), specialisationId);
            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", specialisation));
        } catch (EntityNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), specialisation));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    /**
     * An endpoint for updating specific specialisation entity
     *
     * @param specialisationId  - id of the specialisation
     * @param specialisationDAO
     * @return HTTP response
     */
    @ResponseBody
    @PutMapping(value = "/specialisations/{specialisationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update specific specialisation from the database", description = "Update specific specialisation from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "209", description = "Successfully updated"),
            @ApiResponse(responseCode = "404", description = "Entity not found"),
            @ApiResponse(responseCode = "454", description = "Error during update")
    })
    public ResponseEntity<Response> updateSpecialisation(@Parameter(description = "id of specialisation to be searched") @PathVariable int specialisationId, @RequestBody SpecialisationDAO specialisationDAO) {
        Specialisation specialisation = new Specialisation();
        try {
            specialisation = (Specialisation) objectFacade.updateObject(specialisationDAO, specialisationId);
            // Update specialisation logic here
            return ResponseEntity.status(209).body(new Response<>(Consts.C209, 209, "", specialisation));
        } catch (EntityNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), specialisation));
        } catch (SaveError e) {
            return ResponseEntity.status(454).body(new Response<>(e.getMessage(), 454, Arrays.toString(e.getStackTrace()), specialisation));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    /**
     * An endpoint for specific specialisation deletion
     *
     * @param specialisationId - id of the specialisation
     * @return HTTP response
     */
    @ResponseBody
    @DeleteMapping(value = "/specialisations/{specialisationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete specific specialisation from the database", description = "Delete specific specialisation from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Entity not found"),
            @ApiResponse(responseCode = "455", description = "Error during deletion")
    })
    public ResponseEntity<Response> deleteSpecialisation(@Parameter(description = "id of specialisation to be searched") @PathVariable int specialisationId) {
        Specialisation specialisation = new Specialisation();
        try {
            objectFacade.deleteObject(new SpecialisationDAO(new Specialisation()), specialisationId);
            // Update specialisation logic here
            return ResponseEntity.status(210).body(new Response<>(Consts.C210, 210, "", specialisation));
        } catch (EntityNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), specialisation));
        } catch (DeleteError e) {
            return ResponseEntity.status(455).body(new Response<>(e.getMessage(), 455, Arrays.toString(e.getStackTrace()), specialisation));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }
}
