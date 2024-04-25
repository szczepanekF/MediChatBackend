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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
//import pl.logic.site.facade.UserFacade;
import pl.logic.site.facade.ObjectFacade;
import pl.logic.site.model.dao.DoctorDAO;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.mysql.Doctor;
import pl.logic.site.model.response.Response;
import pl.logic.site.utils.Consts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("doctorController")
@Scope("request")
//@PreAuthorize("hasAnyAuthority()")
public class DoctorController {
    @Autowired
    ObjectFacade objectFacade;



    @PostMapping(value = "/doctor", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create doctor entity and push it to database", description = "Create doctor entity and push it to database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created"),
            @ApiResponse(responseCode = "453", description = "Error during saving an entity")
    })
    public ResponseEntity<Response> createDoctor(@RequestBody DoctorDAO doctorDAO){
        Doctor doctor = new Doctor();
        try{
            doctor = (Doctor) objectFacade.createObject(doctorDAO);
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>(Consts.C201, 201, "", doctor));
        } catch (SaveError e){
            return ResponseEntity.status(453).body(new Response<>(e.getMessage(), 453, Arrays.toString(e.getStackTrace()), doctor));
        }  catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    @GetMapping(value = "/getDoctors/{doctorFilter}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all doctors from the database", description = "Get all doctors from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Response> getAllDoctors(@Parameter(description = "doctor filter, 0 humans, 1 bots, 2 all") @PathVariable int doctorFilter){
        List<Doctor> doctors = new ArrayList<>();
        try{
            doctors = (List<Doctor>) objectFacade.getObjects(new DoctorDAO(new Doctor()), doctorFilter);
            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", doctors));
        } catch (EntityNotFound e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), doctors));
        }  catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    @GetMapping(value = "/doctors/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get doctor from the database", description = "Get doctor from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Response> getDoctor(@Parameter(description = "id of doctor to be searched") @PathVariable int id){
        Doctor doctor = new Doctor();
        try{
            doctor = (Doctor) objectFacade.getObject(new DoctorDAO(new Doctor()), id);
            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", doctor));
        } catch (EntityNotFound e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), doctor));
        }  catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    @ResponseBody
    @PutMapping(value = "/doctors/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update specific doctor from the database", description = "Update specific doctor from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "209", description = "Successfully updated"),
            @ApiResponse(responseCode = "404", description = "Entity not found"),
            @ApiResponse(responseCode = "454", description = "Error during update")
    })
    public ResponseEntity<Response> updateDoctor(@Parameter(description = "id of doctor to be searched") @PathVariable int id, @RequestBody DoctorDAO doctorDAO){
        Doctor doctor = new Doctor();
        try{
            doctor = (Doctor) objectFacade.updateObject(doctorDAO, id);
            // Update doctor logic here
            return ResponseEntity.status(209).body(new Response<>(Consts.C209, 209, "", doctor));
        } catch (EntityNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), doctor));
        } catch (SaveError e) {
            return ResponseEntity.status(454).body(new Response<>(e.getMessage(), 454, Arrays.toString(e.getStackTrace()), doctor));
        }  catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }
    @ResponseBody
    @DeleteMapping(value = "/doctors/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete specific doctor from the database", description = "Delete specific doctor from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Entity not found"),
            @ApiResponse(responseCode = "455", description = "Error during deletion")
    })
    public ResponseEntity<Response> deleteDoctor(@Parameter(description = "id of doctor to be searched") @PathVariable int id){
        Doctor doctor = new Doctor();
        try{
            objectFacade.deleteObject(new DoctorDAO(new Doctor()), id);
            // Update doctor logic here
            return ResponseEntity.status(210).body(new Response<>(Consts.C210, 210, "", doctor));
        } catch (EntityNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(e.getMessage(), 404, Arrays.toString(e.getStackTrace()), doctor));
        } catch (DeleteError e) {
            return ResponseEntity.status(455).body(new Response<>(e.getMessage(), 455, Arrays.toString(e.getStackTrace()), doctor));
        }  catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

}
