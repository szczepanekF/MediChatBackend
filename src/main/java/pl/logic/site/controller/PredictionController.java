package pl.logic.site.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.logic.site.model.exception.InvalidProportion;
import pl.logic.site.model.mysql.Disease;
import pl.logic.site.model.mysql.Doctor;
import pl.logic.site.model.response.Response;
import pl.logic.site.service.PredictionService;
import pl.logic.site.utils.Consts;

import java.util.Arrays;

/**
 * This class provides REST web services for predicting the disease of a patient based on their symptoms.
 * The services include getting the statistical information of the diseases, getting the prediction accuracy,
 * and getting the predicted disease of a specific patient.
 * #This api will be expanded in the future #
 *
 * @author Kacper
 */
@Slf4j
@RestController
@RequestMapping("predictionController")
@Scope("request")
public class PredictionController {
    @Autowired
    PredictionService predictionService;

    /**
     * An endpoint for getting the statistical information of the diseases.
     * This is information about which disease is most often predicted
     * for people who have not yet contracted the disease.
     *
     * @return the statistical information of the diseases
     */
    @GetMapping(value = "/statisticDisease", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Which disease is most often predicted for people who have not yet contracted the disease.",
            description = "Which disease is most often predicted for people who have not yet contracted the disease.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully computed"),
    })
    public ResponseEntity<Response> getStatisticDisease() {
        try {
            Object result = this.predictionService.getStatisticDisease();
            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", result));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    /**
     * An endpoint for getting the accuracy of the prediction model based on default set of proportion (1:1).
     * The study was conducted only on patients with previously diagnosed diseases and with a patient card
     *
     * @return the accuracy of the prediction model
     */
    @GetMapping(value = "/predictionAccuracy", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get the accuracy of the prediction model based on default set of proportion (1:1).",
            description = "Get the accuracy of the prediction model based on default set of proportion (1:1).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully computed"),
    })
    public ResponseEntity<Response> getPredictionAccuracyDefault() {
        try {
            double accuracy = this.predictionService.getPredictionAccuracy(new String[]{"1", "1"});
            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", accuracy));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    /**
     * An endpoint for getting the accuracy of the prediction model based on a given set of proportion.
     * Proportions have to be in the format "<number>:<number>" (e.g. "1:3").
     * The domain is positive natural numbers.
     * The study was conducted only on patients with previously diagnosed diseases and with a patient card
     *
     * @param proportion the proportions between learningSet and testingSet
     * @return the accuracy of the prediction model
     */
    @GetMapping(value = "/predictionAccuracy/{proportion}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get the accuracy of the prediction model based on a given set of proportion.",
            description = "Get the accuracy of the prediction model based on a given set of proportion. " +
                    "Proportions have to be in the format \"<number>:<number>\" (e.g. \"1:3\"). " +
                    "The domain is positive natural numbers.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully computed"),
            @ApiResponse(responseCode = "405", description = "Invalid parameter's format.")
    })
    public ResponseEntity<Response> getPredictionAccuracyProportion(@Parameter(
            description = "proportions between learningSet and testingSet") @PathVariable String proportion) {
        try {
            String[] proportionParsed = proportion.split(":");
            double accuracy = this.predictionService.getPredictionAccuracy(proportionParsed);
            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", accuracy));
        } catch (InvalidProportion e) {
            return ResponseEntity.status(405).body(new Response<>(e.getMessage(), 405, Arrays.toString(e.getStackTrace()), null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    /**
     * Gets the predicted disease of a specific patient by his chart id.
     * If we want to make a prediction and the patient does not have a card, enter 0 as a parameter.
     *
     * @param chartId the id of the patient's chart
     * @return the predicted disease of the patient
     */
    @GetMapping(value = "/patientDisease/{chartId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get the predicted disease of a specific patient.",
            description = "Get the predicted disease of a specific patient.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully computed"),
    })
    public ResponseEntity<Response> getPatientDisease(@Parameter(description = "patient's chart id") @PathVariable int chartId) {
        try {
            Disease result = this.predictionService.getPatientDisease(chartId);
            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", result));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    /**
     * The return value tells us how many diagnosis requests there are likely
     * to be in the future in a given time interval.
     * The amount of past intervals that is taken into account is in constant MAX_DEEP_OF_PREDICTIONS.
     *
     * @param daysInterval - number of days as interval
     * @return the predicted number of future diagnosis requests
     */
    @GetMapping(value = "/futureDiagnosis/{daysInterval}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get the number of predicted future diagnosis requests in the given interval.",
            description = "Get the number of predicted future diagnosis requests in the given interval (in days).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully computed"),
    })
    public ResponseEntity<Response> getFutureDiagnosisRequest(@Parameter(description = "number of days interval") @PathVariable int daysInterval) {
        try {
            double result = this.predictionService.getFutureDiagnosisRequest(daysInterval);
            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", result));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    /**
     * The return value tells us which doctor is most likely to be sought after in the future.
     * The amount of past intervals that is taken into account is in constant MAX_DEEP_OF_PREDICTIONS.
     * In case of a tie in the calculation, the first doctor found is returned
     *
     * @param daysInterval - number of days as interval
     * @return the most wanted doctor
     */
    @GetMapping(value = "/mostWantedDoctor/{daysInterval}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get the most wanted Doctor, predicted within given interval",
            description = "Get the most wanted Doctor, predicted within given interval (in days). " +
                    "He checks all the doctors and returns the most sought-after one")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully computed"),
    })
    public ResponseEntity<Response> getMostWantedDoctor(@Parameter(description = "number of days interval") @PathVariable int daysInterval) {
        try {
            Doctor result = this.predictionService.getMostWantedDoctor(daysInterval);
            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", result));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }
}
