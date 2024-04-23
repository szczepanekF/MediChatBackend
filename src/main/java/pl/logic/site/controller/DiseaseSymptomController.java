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
import pl.logic.site.model.dao.DiseaseSymptomDAO;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.mysql.DiseaseSymptom;
import pl.logic.site.model.response.Response;
import pl.logic.site.utils.Consts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("diseaseSymptomController")
@Scope("request")

public class DiseaseSymptomController {
    @Autowired
    ObjectFacade objectFacade;

    /**
     * An endpoint for getting disease-symptom record by ID
     *
     * @param id - id of the disease-symptom record
     * @return HTTP response
     */
    @GetMapping(value = "/diseaseSymptoms/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get disease-symptom record from the database", description = "Get disease-symptom recordfrom the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Response> getDiseaseSymptom(@Parameter(description = "id of disease-symptom record to be searched") @PathVariable int id) {
        DiseaseSymptom diseaseSymptom = new DiseaseSymptom();
        try {
            diseaseSymptom = (DiseaseSymptom) objectFacade.getObject(new DiseaseSymptomDAO(new DiseaseSymptom()), id);
            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", diseaseSymptom));
        } catch (EntityNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), diseaseSymptom));
        }  catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    /**
     * An endpoint for getting all disease-symptom entities
     *
     * @return HTTP response
     */
    @GetMapping(value = "/diseaseSymptoms", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all disease-symptoms records from the database", description = "Get all disease-symptoms records from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Response> getAllDiseaseSymptoms() {
        List<DiseaseSymptom> diseaseSymptoms = new ArrayList<>();
        try {
            diseaseSymptoms = (List<DiseaseSymptom>) objectFacade.getObjects(new DiseaseSymptomDAO(new DiseaseSymptom()), -1);
            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", diseaseSymptoms));
        } catch (EntityNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), diseaseSymptoms));
        }  catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }
}
