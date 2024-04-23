package pl.logic.site.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwt;
import org.springframework.http.HttpStatus;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.Patient;
import pl.logic.site.model.response.AuthenticationResponse;
import pl.logic.site.model.request.LoginRequest;
import pl.logic.site.model.request.RegisterPatientRequest;
import pl.logic.site.model.request.RegisterDoctorRequest;
import pl.logic.site.model.response.Response;
import pl.logic.site.service.impl.AuthenticationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.logic.site.utils.Consts;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Map;

import java.util.Arrays;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/app/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationServiceImpl authenticationService;

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
            Map<String, String> payloadMap = mapper.readValue(payload, new TypeReference<Map<String, String>>(){});

            return ResponseEntity.status(HttpStatus.CREATED).body(new Response<>(Consts.C201, 201, "", payloadMap));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response<>(e.getMessage(), 500, Arrays.toString(e.getStackTrace()), null));
        }
    }

}
