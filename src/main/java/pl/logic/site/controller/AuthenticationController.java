package pl.logic.site.controller;

import pl.logic.site.model.response.AuthenticationResponse;
import pl.logic.site.model.request.LoginRequest;
import pl.logic.site.model.request.RegisterPatientRequest;
import pl.logic.site.model.request.RegisterDoctorRequest;
import pl.logic.site.service.impl.AuthenticationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationServiceImpl authenticationService;

    @PostMapping("/register/doctor")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterDoctorRequest request) {
        return ResponseEntity.ok(authenticationService.registerDoctor(request));
    }

    @PostMapping("/register/patient")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterPatientRequest request) {
        return ResponseEntity.ok(authenticationService.registerPatient(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }
}
