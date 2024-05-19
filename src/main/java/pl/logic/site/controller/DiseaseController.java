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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.logic.site.facade.ObjectFacade;
import pl.logic.site.model.dao.DiseaseDAO;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.mysql.Disease;
import pl.logic.site.model.response.Response;
import pl.logic.site.utils.Consts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("diseaseController")
@Scope("request")

public class DiseaseController {
    @Autowired
    ObjectFacade objectFacade;

    /**
     * An endpoint for getting disease by ID
     *
     * @param diseaseId - id of the disease
     * @return HTTP response
     */
    @GetMapping(value = "/disease/{diseaseId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get disease from the database", description = "Get disease from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Response> getDisease(@Parameter(description = "id of disease to be searched") @PathVariable int diseaseId) {
        Disease disease = new Disease();
        try {
            disease = (Disease) objectFacade.getObject(new DiseaseDAO(new Disease()), diseaseId);
            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", disease));
        } catch (EntityNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), disease));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    /**
     * An endpoint for getting all disease entities
     *
     * @return HTTP response
     */
    @GetMapping(value = "/diseases", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all diseases from the database", description = "Get all diseases from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Response> getAllDiseases() {
        List<Disease> diseases = new ArrayList<>();
        try {
            diseases = (List<Disease>) objectFacade.getObjects(new DiseaseDAO(new Disease()), -1);
            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", diseases));
        } catch (EntityNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), diseases));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }
}
