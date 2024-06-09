package pl.logic.site.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.logic.site.aspects.AuthorizationHeaderHolder;
import pl.logic.site.aspects.ControllerUtils;
import pl.logic.site.model.enums.LogType;
import pl.logic.site.model.exception.InvalidProportion;
import pl.logic.site.model.mysql.Disease;
import pl.logic.site.model.mysql.Doctor;
import pl.logic.site.model.response.Response;
import pl.logic.site.service.LoggingService;
import pl.logic.site.service.PredictionService;
import pl.logic.site.utils.Consts;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
    @Autowired
    LoggingService loggingService;
    @Autowired
    HttpServletRequest request;

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
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
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
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
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
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(405).body(new Response<>(e.getMessage(), 405, Arrays.toString(e.getStackTrace()), null));
        } catch (Exception e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
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
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
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
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
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
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }


    /**
     * This endpoint returns the top N diseases' names that are most often predicted for people who have not yet contracted the disease.
     * The diseases are returned in descending order by the number of occurrences in the database.
     * knn is counted for the entire database, so with increasingly larger data sets you will have to wait a while.
     * If the number of different predicted diseases is smaller than N,
     * then the program will return such many results that have or empty array.
     * different diseases in the database, then the program will throw an appropriate exception.
     *
     * @param N - The number of top diseases to return
     * @return A ResponseEntity containing a list of the top N diseases' names
     */
    @GetMapping(value = "/topNDiseases/{N}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get the top N diseases.",
            description = "Get the top N diseases.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully computed"),
            @ApiResponse(responseCode = "500", description = "An internal server error occurred while processing the request."),
    })
    public ResponseEntity<List<String>> getTopNDiseases(
            @Parameter(description = "The number of top diseases to return")
            @PathVariable int N) {
        try {
            List<String> result = this.predictionService.getTopNDiseases(N);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * This endpoint returns a list of predicted symptoms within a specified date interval.
     * The returned list contained 3 another list: symptoms' names, dates and data.
     * The prediction is based on historical data and the interval is defined by the fromDate and toDate parameters.
     * The date format should be yyyy-MM-dd. The prediction algorithm takes into account the symptom occurrences
     * in the past intervals defined by the constant MAX_DEEP_OF_PREDICTIONS.
     * The response includes a list of symptoms and their predicted occurrences in the given interval.
     * If the interval between dates is up to 2 months, the time interval will be 1 day and
     * the returned dates will be in the format yyyy-MM-dd However, if the interval is greater than or equal to 3 months,
     * the interval will be 1 month in the format yyyy-MM.
     * Accurate characteristics of the returned data.
     * First list returns symptom names in ascending order by symptom id according to their occurrence in the database.
     * The second list returns the dates for which the prediction was calculated.
     * The columns of the third list correspond to the rows of this list and are counted from fromDate to toDate.
     * the third list is two-dimensional. A row represents a set of calculations for a given symptom,
     * and the order of the rows corresponds to the symptoms in the first table.
     * The table columns represent calculations for a given symptom and a specific date in accordance
     * with the order of dates in the second table.
     *
     * Example of the response:
     *
     * Symptoms' names:
     * 0	"headache"
     * 1	"sore throat"
     * 2	"abdominal pain"
     * 	...
     * Dates:
     * 0	"2024-07-01"
     * 1	"2024-07-02"
     * 2	"2024-07-03"
     *  ...
     * Data:
     * (Results for the first symptom - headache)
     * 0	0.93 // for date - 2024-07-01
     * 1	2.4 // for date - 2024-07-02
     * 2	4 // for date - 2024-07-03
     * 	...
     * (Results for the second symptom - sore throat)
     * 0	0.33 // for date - 2024-07-01
     * 1	1.33 // for date - 2024-07-02
     * 2	1.93 // for date - 2024-07-03
     *  ...
     * (Results for the third symptom - abdominal pain)
     * 0	0 // for date - 2024-07-01
     * 1	1.33 // for date - 2024-07-02
     * 2	1.53 // for date - 2024-07-03
     *  ...
     *
     * @param fromDate - The start date of the interval in yyyy-MM-dd format
     * @param toDate - The end date of the interval in yyyy-MM-dd format
     * @return A ResponseEntity containing a Response object with the predicted symptoms in the given interval
     */
    @GetMapping(value = "/symptomsPrediction/{fromDate}/{toDate}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get the symptoms prediction in a given interval.",
            description = "Get the symptoms prediction in a given interval.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully computed"),
            @ApiResponse(responseCode = "500", description = "An internal server error occurred while processing the request."),
    })
    public ResponseEntity<Response> getSymptomsPredictionInInterval(
            @Parameter(description = "Start date of the interval in yyyy-MM-dd format")
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @Parameter(description = "End date of the interval in yyyy-MM-dd format")
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate) {
        try {
            List<Object> result = this.predictionService.getSymptomsPredictionInInterval(fromDate, toDate);
            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", result));
        } catch (Exception e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    /**
     * This endpoint returns a list of predicted diseases within a specified date interval.
     * The returned list contained 3 another list: diseases' names, dates and data.
     * The prediction is based on historical data and the interval is defined by the fromDate and toDate parameters.
     * The date format should be yyyy-MM-dd. The prediction algorithm takes into account the disease occurrences
     * in the past intervals defined by the constant MAX_DEEP_OF_PREDICTIONS.
     * The response includes a list of diseases and their predicted occurrences in the given interval.
     * If the interval between dates is up to 2 months, the time interval will be 1 day and
     * the returned dates will be in the format yyyy-MM-dd However, if the interval is greater than or equal to 3 months,
     * the interval will be 1 month in the format yyyy-MM.
     * Accurate characteristics of the returned data.
     * First list returns diseases names in ascending order by disease id according to their occurrence in the database.
     * The second list returns the dates for which the prediction was calculated.
     * The columns of the third list correspond to the rows of this list and are counted from fromDate to toDate.
     * the third list is two-dimensional. A row represents a set of calculations for a given disease,
     * and the order of the rows corresponds to the diseases in the first table.
     * The table columns represent calculations for a given disease and a specific date in accordance
     * with the order of dates in the second table.
     *
     * Example of the response:
     *
     * Diseases' names:
     * 0	"flu"
     * 1	"cold"
     * 2	"stomach ache"
     * 	...
     * Dates:
     * 0	"2024-07-01"
     * 1	"2024-07-02"
     * 2	"2024-07-03"
     *  ...
     * Data:
     * (Results for the first disease - flu)
     * 0	0.93 // for date - 2024-07-01
     * 1	2.4 // for date - 2024-07-02
     * 2	4 // for date - 2024-07-03
     * 	...
     * (Results for the second disease - cold)
     * 0	0.33 // for date - 2024-07-01
     * 1	1.33 // for date - 2024-07-02
     * 2	1.93 // for date - 2024-07-03
     *  ...
     * (Results for the third disease - stomach ache)
     * 0	0 // for date - 2024-07-01
     * 1	1.33 // for date - 2024-07-02
     * 2	1.53 // for date - 2024-07-03
     *  ...
     *
     * @param fromDate - The start date of the interval in yyyy-MM-dd format
     * @param toDate - The end date of the interval in yyyy-MM-dd format
     * @return A ResponseEntity containing a Response object with the predicted diseases in the given interval
     */
    @GetMapping(value = "/diseasesPrediction/{fromDate}/{toDate}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get the diseases prediction in a given interval.",
            description = "Get the diseases prediction in a given interval.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully computed"),
            @ApiResponse(responseCode = "500", description = "An internal server error occurred while processing the request."),
    })
    public ResponseEntity<Response> getDiseasesPredictionInInterval(
            @Parameter(description = "Start date of the interval in yyyy-MM-dd format")
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @Parameter(description = "End date of the interval in yyyy-MM-dd format")
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate) {
        try {
            List<Object> result = this.predictionService.getDiseasesPredictionInInterval(fromDate, toDate);
            return ResponseEntity.ok(new Response<>(Consts.C200, 200, "", result));
        } catch (Exception e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }


    /**
     * This endpoint returns a list of predicted symptoms within a specified date interval
     * and a specific age groups given with StatisticService interface.
     *
     * Example of the response:
     *
     * Symptoms' names:
     * 0	"headache"
     * 1	"sore throat"
     * 2	"abdominal pain"
     * 	...
     * Age group:
     * 0	"0-5"
     * 1	"6-10"
     * 2	"11-18"
     *  ...
     * Data:
     * (Results for the first symptom - headache)
     * 0	0.93 // for age group "0-5"
     * 1	2.4 // for age group "6-10"
     * 2	4 // for age group "11-18"
     * 	...
     * (Results for the second symptom - sore throat)
     * 0	0.33 // for age group "0-5"
     * 1	1.33 // for age group "6-10"
     * 2	1.93 // for age group "11-18"
     *  ...
     * (Results for the third symptom - abdominal pain)
     * 0	0  // for age group "0-5"
     * 1	1.33 // for age group "6-10"
     * 2	1.53 // for age group "11-18"
     *  ...
     *
     * @param fromDate - The start date of the interval in yyyy-MM-dd format
     * @param toDate - The end date of the interval in yyyy-MM-dd format
     * @return A ResponseEntity containing a Response object with the predicted diseases in the given interval
     */
    @GetMapping(value = "/ageGroupSymptomsPrediction", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get the symptoms prediction for a specific age groups.",
            description = "Get the symptoms prediction for a specific age groups in StatisticServiceImpl.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully computed"),
            @ApiResponse(responseCode = "500", description = "An internal server error occurred while processing the request."),
    })
    public ResponseEntity<List<Object>> getAgeGroupSymptomsPredictionInInterval(
            @Parameter(description = "Start date of the interval in yyyy-MM-dd format")
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @Parameter(description = "End date of the interval in yyyy-MM-dd format")
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate
    ) {
        try {
            List<Object> result = this.predictionService.getAgeGroupSymptomsPrediction(fromDate, toDate);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * This endpoint returns a list of predicted diseases within a specified date interval
     * and a specific age groups given with StatisticService interface.
     *
     * Example of the response:
     *
     * Diseases' names:
     * 0	"flu"
     * 1	"cold"
     * 2	"stomach ache"
     * 	...
     * Age group:
     * 0	"0-5"
     * 1	"6-10"
     * 2	"11-18"
     *  ...
     * Data:
     * (Results for the first disease - flu)
     * 0	0.93 // for age group "0-5"
     * 1	2.4 // for age group "6-10"
     * 2	4 // for age group "11-18"
     * 	...
     * (Results for the second disease - cold)
     * 0	0.33 // for age group "0-5"
     * 1	1.33 // for age group "6-10"
     * 2	1.93 // for age group "11-18"
     *  ...
     * (Results for the third disease - stomach ache)
     * 0	0  // for age group "0-5"
     * 1	1.33 // for age group "6-10"
     * 2	1.53 // for age group "11-18"
     *  ...
     *
     * @param fromDate - The start date of the interval in yyyy-MM-dd format
     * @param toDate - The end date of the interval in yyyy-MM-dd format
     * @return A ResponseEntity containing a Response object with the predicted diseases in the given interval
     */
    @GetMapping(value = "/ageGroupDiseasesPrediction", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get the diseases prediction for a specific age group in a given interval.",
            description = "Get the diseases prediction for a specific age group in a given interval.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully computed"),
            @ApiResponse(responseCode = "500", description = "An internal server error occurred while processing the request."),
    })
    public ResponseEntity<List<Object>> getAgeGroupDiseasesPredictionInInterval(
            @Parameter(description = "Start date of the interval in yyyy-MM-dd format")
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @Parameter(description = "End date of the interval in yyyy-MM-dd format")
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate
    ) {
        try {
            List<Object> result = this.predictionService.getAgeGroupDiseasesPrediction(fromDate, toDate);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            loggingService.createLog(ControllerUtils.combinePaths(request) + Consts.LOG_ERROR, e.getStackTrace(),
                    LogType.error, AuthorizationHeaderHolder.getAuthorizationHeader());
            return ResponseEntity.status(500).body(null);
        }
    }
}
