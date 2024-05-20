package pl.logic.site.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import pl.logic.site.model.dao.PatientDAO;
import pl.logic.site.model.enums.Status;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.Patient;
import pl.logic.site.repository.PatientRepository;

import java.util.Date;
import java.util.Optional;


import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class PatientServiceImplTest {
    @Mock
    private PatientRepository patientRepository;
    @InjectMocks
    private PatientServiceImpl patientService;
    Patient patient;
    Patient wrongIdPatient;

    @BeforeEach
    public void setUp() {
        patient = new Patient(0, "Testname", "Surname", new Date(), 180, 98, "male", Status.ONLINE, "cm", "kg");
        wrongIdPatient = new Patient(1, "Testname", "Surname", new Date(), 180, 98, "male", Status.ONLINE, "cm", "kg");
    }

    @Test
    void shouldCreatePatient() {
        when(patientRepository.saveAndFlush(any(Patient.class))).thenReturn(patient);

        PatientDAO patientDAO = new PatientDAO(patient); // Assuming appropriate constructor
        Patient savedPatient = patientService.createPatient(patientDAO);

        verify(patientRepository, times(1)).saveAndFlush(any(Patient.class));
        Assertions.assertEquals(patient, savedPatient);
    }

    @Test
    void shouldThrowWhileCreatingPatientWithId() {


        PatientDAO patientDAO = new PatientDAO(wrongIdPatient); // Assuming appropriate constructor
        Assertions.assertThrows(SaveError.class, () -> {
            patientService.createPatient(patientDAO);
        });
        verify(patientRepository, never()).saveAndFlush(any(Patient.class));
    }

    @Test
    void shouldThrowWhenRepositoryErrorCreatingPatient() {
        doThrow(new NullPointerException()).when(patientRepository).saveAndFlush(any(Patient.class));
        PatientDAO patientDAO = new PatientDAO(patient); // Assuming appropriate constructor
        Assertions.assertThrows(SaveError.class, () -> {
            patientService.createPatient(patientDAO);
        });
        verify(patientRepository, times(1)).saveAndFlush(any(Patient.class));

    }

    @Test
    void shouldUpdatePatient() {
        Optional<Patient> patientOptional = Optional.of(patient);
        when(patientRepository.findById(patient.getId())).thenReturn(patientOptional);
        when(patientRepository.saveAndFlush(any(Patient.class))).thenReturn(patient);

        PatientDAO patientDAO = new PatientDAO(patient); // Assuming appropriate constructor
        Patient savedPatient = patientService.updatePatient(patientDAO, patient.getId());

        verify(patientRepository, times(1)).findById(patient.getId());
        verify(patientRepository, times(1)).saveAndFlush(any(Patient.class));
        Assertions.assertEquals(patient, savedPatient);
    }

    @Test
    void shouldThrowWhileUpdatingNonExistingPatient() {


        PatientDAO patientDAO = new PatientDAO(wrongIdPatient); // Assuming appropriate constructor
        Assertions.assertThrows(SaveError.class, () -> {
            patientService.createPatient(patientDAO);
        });
        verify(patientRepository, never()).saveAndFlush(any(Patient.class));
    }

    @Test
    void shouldThrowWhenRepositoryErrorUpdatingPatient() {
        doThrow(new NullPointerException()).when(patientRepository).saveAndFlush(any(Patient.class));
        PatientDAO patientDAO = new PatientDAO(patient); // Assuming appropriate constructor
        Assertions.assertThrows(SaveError.class, () -> {
            patientService.createPatient(patientDAO);
        });
        verify(patientRepository, times(1)).saveAndFlush(any(Patient.class));

    }
    @Test
    void shouldDeletePatient() {

    }

    @Test
    void shouldGetSinglePatient() {

    }

    @Test
    void shouldGetAllPatients() {

    }

    @Test
    void shouldGetAllPatientsForDoctor() {

    }
}
