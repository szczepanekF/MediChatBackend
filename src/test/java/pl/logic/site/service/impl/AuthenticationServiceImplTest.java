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
import pl.logic.site.model.mysql.Patient;
import pl.logic.site.model.request.RegisterPatientRequest;
import pl.logic.site.repository.DoctorRepository;
import pl.logic.site.repository.PasswordRecoveryTokenRepository;
import pl.logic.site.repository.PatientRepository;
import pl.logic.site.repository.SpringUserRepository;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

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


    @BeforeEach
    public void setUp() {
        patient = new RegisterPatientRequest("Testname", "Surname", new Date(), 180, 98, "male", Status.ONLINE, "testMail", "username", "password", "cm", "kg");
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

}
