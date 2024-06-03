package pl.logic.site.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.logic.site.model.enums.Status;
import pl.logic.site.model.exception.EmailOrUsernameJustExist;
import pl.logic.site.model.exception.InvalidPassword;
import pl.logic.site.model.mysql.Patient;
import pl.logic.site.model.mysql.Role;
import pl.logic.site.model.mysql.SpringUser;
import pl.logic.site.model.request.LoginRequest;
import pl.logic.site.model.request.NewPasswordRequest;
import pl.logic.site.model.request.RegisterPatientRequest;
import pl.logic.site.model.response.AuthenticationResponse;
import pl.logic.site.repository.DoctorRepository;
import pl.logic.site.repository.PasswordRecoveryTokenRepository;
import pl.logic.site.repository.PatientRepository;
import pl.logic.site.repository.SpringUserRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceImplTest {
    @Mock
    private SpringUserRepository springUserRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtServiceImpl jwtService;
    @Mock
    private PasswordRecoveryTokenRepository passwordRecoveryTokenRepository;
    @InjectMocks
    private AuthenticationServiceImpl authenticationService;
    RegisterPatientRequest patient;
    RegisterPatientRequest patientWithExistingEmail;
    SpringUser springUser;
    NewPasswordRequest newPasswordRequest;



    @BeforeEach
    public void setUp() {
        patient = new RegisterPatientRequest("Testname", "Surname", new Date(), 180, 98, "male", Status.ONLINE, "testMail", "username", "password", "cm", "kg");
        patientWithExistingEmail = new RegisterPatientRequest("Testname", "Surname", new Date(), 180, 98, "male", Status.ONLINE, "nowekonto@wp.pl", "username", "password", "cm", "kg");
        springUser = new SpringUser(1, "username", "email", "password", null, 10, new Date(), Role.DOCTOR);
        newPasswordRequest = new NewPasswordRequest(1, "newPassword");
    }
    static Stream<Arguments> provideStateAndDiagnosis() {
        return Stream.of(
                Arguments.of("test1", "", "test3", ""),
                Arguments.of("test4", "", "", "test5")
        );
    }
    @Test
    void shouldRegisterPatient() {
    }

    @Test
    void shouldThrowWhenRegisteredPatientMailOrUsernameExists() {
    }

    @Test
    void registerPatientWithExistingEmail() {
        when(springUserRepository.findByEmail(anyString())).thenReturn(Optional.of(new SpringUser()));
        EmailOrUsernameJustExist exception = assertThrows(EmailOrUsernameJustExist.class, () -> {
            authenticationService.registerPatient(patientWithExistingEmail);
        });
        assertEquals("Email or username just exist", exception.getMessage());
    }

    @Test
    void registerPatientWithNotExistingEmail() {
        when(springUserRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(springUserRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(patientRepository.save(any(Patient.class))).thenReturn(new Patient());

        SpringUser mockSpringUser = new SpringUser();
        when(authenticationService.createSpringUser(anyString(), anyString(), anyString(), anyInt(), anyInt(), any(Role.class)))
                .thenReturn(mockSpringUser);
        AuthenticationResponse expectedResponse = new AuthenticationResponse();
        when(authenticationService.createAuthenticationResponse(mockSpringUser)).thenReturn(expectedResponse);
        AuthenticationResponse response = authenticationService.registerPatient(patient);
        assertEquals(expectedResponse, response);
    }

    @Test
    void invalidPassword() {
        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail("testUser");
        request.setPassword("password");

        SpringUser springUser = new SpringUser();
        springUser.setPassword("encodedPassword");

        when(springUserRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(springUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        InvalidPassword exception = assertThrows(InvalidPassword.class, () -> {
            authenticationService.login(request);
        });

        assertEquals("Invalid password", exception.getMessage());
    }

    @Test
    void loginSuccessfully() {
        LoginRequest request = new LoginRequest("email", "testPassword");

        when(springUserRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(springUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        AuthenticationResponse expectedResponse = new AuthenticationResponse();
        when(authenticationService.createAuthenticationResponse(any(SpringUser.class))).thenReturn(expectedResponse);
        AuthenticationResponse response = authenticationService.login(request);
        assertEquals(expectedResponse, response);
    }


    @Test
    void resetPasswordSuccessfully() {
        when(springUserRepository.findById(1)).thenReturn(Optional.of(springUser));
        when(passwordEncoder.encode(newPasswordRequest.getNewPassword())).thenReturn("newPassword");
        authenticationService.resetUserPassword(newPasswordRequest);
        verify(springUserRepository, times(1)).save(springUser); // Verify that the user is saved
        assertEquals("newPassword", springUser.getPassword());

    }
}
