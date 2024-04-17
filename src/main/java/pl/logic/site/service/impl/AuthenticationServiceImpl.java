package pl.logic.site.service.impl;

import pl.logic.site.model.exception.EmailOrUsernameJustExist;
import pl.logic.site.model.exception.InvalidPassword;
import pl.logic.site.model.exception.UserNotFound;
import pl.logic.site.model.mysql.*;
import pl.logic.site.model.request.LoginRequest;
import pl.logic.site.model.request.RegisterDoctorRequest;
import pl.logic.site.model.request.RegisterPatientRequest;
import pl.logic.site.model.response.AuthenticationResponse;
import pl.logic.site.repository.DoctorRepository;
import pl.logic.site.repository.PatientRepository;
import pl.logic.site.repository.SpringUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl {
    private final SpringUserRepository springUserRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtServiceImpl jwtService;

    public AuthenticationResponse registerPatient(RegisterPatientRequest request) {
        if (isEmailOrUsernameJustExist(request.getEmail(), request.getUsername())) {
            throw new EmailOrUsernameJustExist("Email or username just exist");
        }

        Patient patient = Patient.builder()
                .name(request.getName())
                .surname(request.getSurname())
                .birth_date(request.getBirthDate())
                .height(request.getHeight())
                .weight(request.getWeight())
                .gender(request.getGender())
                .status(request.getStatus())
                .build();
        patientRepository.save(patient);

        SpringUser springUser = createSpringUser(request.getEmail(), request.getUsername(), request.getPassword(), null, patient.getId(), Role.PATIENT);
        return createAuthenticationResponse(springUser);
    }

    public AuthenticationResponse registerDoctor(RegisterDoctorRequest request) {
        if (isEmailOrUsernameJustExist(request.getEmail(), request.getUsername())) {
            throw new EmailOrUsernameJustExist("Email or username just exist");
        }

        Doctor doctor = Doctor.builder()
                .name(request.getName())
                .surname(request.getSurname())
                .birth_date(request.getBirthDate())
                .specialisation_id(request.getSpecialisationId())
                .build();
        doctorRepository.save(doctor);

        SpringUser springUser = createSpringUser(request.getEmail(), request.getUsername(), request.getPassword(), doctor.getId(), null, Role.DOCTOR);
        return createAuthenticationResponse(springUser);
    }

    private boolean isEmailOrUsernameJustExist(String email, String username) {
        return springUserRepository.findByEmail(email).isPresent()
                && springUserRepository.findByUsername(username).isPresent();
    }

    private SpringUser createSpringUser(String email, String username, String password, Integer doctorId, Integer patientId, Role role) {
        SpringUser springUser = SpringUser.builder()
                .email(email)
                .username(username)
                .password(passwordEncoder.encode(password))
                .doctorId(doctorId)
                .patientId(patientId)
                .role(role)
                .build();
        return springUserRepository.save(springUser);
    }

    private AuthenticationResponse createAuthenticationResponse(SpringUser springUser) {
        Map<String, Object> extraClaims = getExtraClaims(springUser);
        extraClaims.put("emailAddress", springUser.getEmail());
        extraClaims.put("role", springUser.getRole());

        var jwtToken = jwtService.generateToken(extraClaims, springUser);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }


    public AuthenticationResponse login(LoginRequest request) {
        SpringUser springUser = springUserRepository
                .findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail())
                .orElseThrow(() -> new UserNotFound("User with this username or email address does not exist"));

        if (!passwordEncoder.matches(request.getPassword(), springUser.getPassword())) {
            throw new InvalidPassword("Invalid password");
        }

       return createAuthenticationResponse(springUser);
    }

    private Map<String, Object> getExtraClaims(SpringUser springUser) {
        if(springUser.getDoctorId() != null) {
            Doctor doctor = doctorRepository
                    .findById(springUser.getDoctorId())
                    .orElseThrow();
            return getExtraDoctorClaims(doctor);
        } else if(springUser.getPatientId() != null) {
            Patient patient = patientRepository.findById(springUser.getPatientId()).orElseThrow();
            return getExtraPatientClaims(patient);
        } else {
            throw new RuntimeException("Both doctor id and patient id equals to null");
        }
    }

    private Map<String, Object> getExtraDoctorClaims(Doctor doctor) {
        Map<String, Object> doctorClaims = new HashMap<>();
        doctorClaims.put("name", doctor.getName());
        doctorClaims.put("surname", doctor.getSurname());
        doctorClaims.put("birth_date", getHumanReadableDate(doctor.getBirth_date()));
        doctorClaims.put("specialisation_id", doctor.getSpecialisation_id());
        return doctorClaims;
    }

    private Map<String, Object> getExtraPatientClaims(Patient patient) {
        Map<String, Object> patientClaims = new HashMap<>();
        patientClaims.put("name", patient.getName());
        patientClaims.put("surname", patient.getSurname());
        patientClaims.put("birth_date", getHumanReadableDate(patient.getBirth_date()));
        patientClaims.put("height", patient.getHeight());
        patientClaims.put("weight", patient.getWeight());
        patientClaims.put("gender", patient.getGender());
        patientClaims.put("status", patient.getStatus());
        return patientClaims;
    }

    private String getHumanReadableDate(Date date) {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }

}

