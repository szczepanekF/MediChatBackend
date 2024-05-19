package pl.logic.site.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import pl.logic.site.model.exception.InvalidRecoveryTokenEmailPairException;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.request.NewPasswordRequest;
import pl.logic.site.model.response.AuthenticationResponse;
import pl.logic.site.model.request.LoginRequest;
import pl.logic.site.model.request.RegisterPatientRequest;
import pl.logic.site.model.request.RegisterDoctorRequest;
import pl.logic.site.model.response.Response;
import pl.logic.site.service.impl.AuthenticationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.logic.site.service.impl.EmailServiceImpl;
import pl.logic.site.utils.Consts;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.*;

import java.util.Map;

@RestController
@RequestMapping("/app/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationServiceImpl authenticationService;
    private final EmailServiceImpl emailService;


    @PostMapping("/register/doctor")
    public ResponseEntity<Response> register(@RequestBody RegisterDoctorRequest request) {
        try {
            AuthenticationResponse authenticationResponse = authenticationService.registerDoctor(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>(Consts.C201, 201, "", authenticationResponse));
        } catch (SaveError e) {
            return ResponseEntity.status(453).body(new Response<>(e.getMessage(), 453, Arrays.toString(e.getStackTrace()), null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }

    }

    @PostMapping("/register/patient")
    public ResponseEntity<Response> register(@RequestBody RegisterPatientRequest request) {
        try {
            AuthenticationResponse authenticationResponse = authenticationService.registerPatient(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>(Consts.C201, 201, "", authenticationResponse));
        } catch (SaveError e) {
            return ResponseEntity.status(453).body(new Response<>(e.getMessage(), 453, Arrays.toString(e.getStackTrace()), null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody LoginRequest request) {
        try {
            AuthenticationResponse authenticationResponse = authenticationService.login(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>(Consts.C201, 201, "", authenticationResponse));
        } catch (SaveError e) {
            return ResponseEntity.status(453).body(new Response<>(e.getMessage(), 453, Arrays.toString(e.getStackTrace()), null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }


    @PostMapping("/decodeJWT")
    public ResponseEntity<Response> decodeJWT(@RequestBody String token) {
        try {
            String[] chunks = token.split("\\.");
            Base64.Decoder decoder = Base64.getUrlDecoder();

            String payload = new String(decoder.decode(chunks[1]));

            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> payloadMap = mapper.readValue(payload, new TypeReference<Map<String, String>>() {
            });

            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>(Consts.C201, 201, "", payloadMap));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

    @PostMapping("/passwordRecovery")
    public ResponseEntity<Response> resetPassword(@RequestParam("emailAddress") String userEmailAddress) {
        try {
            String recoveryToken = UUID.randomUUID().toString();
            authenticationService.createPasswordRecoveryToken(userEmailAddress, recoveryToken);
            emailService.sendEmail(recoveryToken, userEmailAddress);
            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(Consts.C200, 200, "", "Mail sent to " + userEmailAddress));
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response<>(e.getMessage(), 417, Arrays.toString(e.getStackTrace()), "Operation of creating recovery token or mail sending failed"));
        }
    }

    @PostMapping("/checkEmailAndToken")
    public ResponseEntity<Response> checkEmailAndTokenCorrectness(@RequestParam("userEmailAddress") String userEmailAddress, @RequestParam("token") String token) {
        try {
            if(authenticationService.isPairEmailTokenValid(userEmailAddress, token)) {
                return ResponseEntity.status(HttpStatus.OK).body(new Response<>(Consts.C200, 200, "", "Pair emailAddress and recoveryToken match"));
            } else throw new InvalidRecoveryTokenEmailPairException("Pair emailAddress and recoveryToken do not match");
        }
        catch(Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response<>(e.getMessage(), 417, "", "Pair emailAddress and recoveryToken do not match"));
        }
    }

    @PostMapping("/changePassword")
    public ResponseEntity<Response>changeUserPassword(@RequestBody NewPasswordRequest request) {
        try {
            authenticationService.resetUserPassword(request);
            return ResponseEntity.status(HttpStatus.OK).body(new Response<>(Consts.C209, 209, "", "Password reset successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response<>(Consts.C454_UPDATING_ERROR, 454, "", "Reset password operation failed"));
        }
    }
}
