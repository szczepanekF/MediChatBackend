package pl.logic.site.service.impl;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import pl.logic.site.model.dao.PatientDAO;
import pl.logic.site.model.enums.Status;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.Patient;
import pl.logic.site.model.mysql.SpringUser;
import pl.logic.site.repository.PatientRepository;
import pl.logic.site.service.ChatRoomService;

import java.util.Date;
import java.util.List;
import java.util.Optional;


import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class PatientServiceImplTest {
    @Mock
    private ChatRoomService chatRoomService;
    @Mock
    private UserServiceImpl userService;
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

        PatientDAO patientDAO = new PatientDAO(patient);
        Patient savedPatient = patientService.createPatient(patientDAO);

        verify(patientRepository, times(1)).saveAndFlush(any(Patient.class));
        Assertions.assertEquals(patient, savedPatient);
    }

    @Test
    void shouldThrowWhileCreatingPatientWithId() {


        PatientDAO patientDAO = new PatientDAO(wrongIdPatient);
        Assertions.assertThrows(SaveError.class, () -> patientService.createPatient(patientDAO));
        verify(patientRepository, never()).saveAndFlush(any(Patient.class));
    }

    @Test
    void shouldThrowWhenRepositoryErrorCreatingPatient() {
        doThrow(new NullPointerException()).when(patientRepository).saveAndFlush(any(Patient.class));
        PatientDAO patientDAO = new PatientDAO(patient);
        Assertions.assertThrows(SaveError.class, () -> patientService.createPatient(patientDAO));
        verify(patientRepository, times(1)).saveAndFlush(any(Patient.class));

    }

    @Test
    void shouldUpdatePatient() {
        Optional<Patient> patientOptional = Optional.of(patient);
        when(patientRepository.findById(patient.getId())).thenReturn(patientOptional);
        when(patientRepository.saveAndFlush(any(Patient.class))).thenReturn(patient);

        PatientDAO patientDAO = new PatientDAO(patient);
        Patient savedPatient = patientService.updatePatient(patientDAO, patient.getId());

        verify(patientRepository, times(1)).findById(patient.getId());
        verify(patientRepository, times(1)).saveAndFlush(any(Patient.class));
        Assertions.assertEquals(patient, savedPatient);
    }

    @Test
    void shouldThrowWhileUpdatingNonExistingPatient() {
        when(patientRepository.findById(patient.getId())).thenReturn(Optional.empty());


        PatientDAO patientDAO = new PatientDAO(patient);
        Assertions.assertThrows(EntityNotFound.class, () -> patientService.updatePatient(patientDAO, patient.getId()));
        verify(patientRepository, times(1)).findById(patient.getId());
        verify(patientRepository, never()).saveAndFlush(any(Patient.class));
    }

    @Test
    void shouldThrowWhenRepositoryErrorUpdatingPatient() {
        Optional<Patient> patientOptional = Optional.of(patient);
        when(patientRepository.findById(patient.getId())).thenReturn(patientOptional);
        doThrow(new NullPointerException()).when(patientRepository).saveAndFlush(any(Patient.class));
        PatientDAO patientDAO = new PatientDAO(patient);
        Assertions.assertThrows(SaveError.class, () -> patientService.updatePatient(patientDAO, patient.getId()));
        verify(patientRepository, times(1)).findById(patient.getId());
        verify(patientRepository, times(1)).saveAndFlush(any(Patient.class));
    }

    @Test
    void shouldDeletePatient() {
        Optional<Patient> patientOptional = Optional.of(patient);
        when(patientRepository.findById(patient.getId())).thenReturn(patientOptional);

        patientService.deletePatient(patient.getId());

        verify(patientRepository, times(1)).findById(patient.getId());
        verify(patientRepository, times(1)).deleteById(patient.getId());
    }

    @Test
    void shouldThrowWhileDeletingNonExistingPatient() {
        when(patientRepository.findById(patient.getId())).thenReturn(Optional.empty());


        Assertions.assertThrows(EntityNotFound.class, () -> patientService.deletePatient(patient.getId()));
        verify(patientRepository, times(1)).findById(patient.getId());
        verify(patientRepository, never()).deleteById(anyInt());
    }

    @Test
    void shouldThrowWhenRepositoryErrorDeletingPatient() {
        Optional<Patient> patientOptional = Optional.of(patient);
        when(patientRepository.findById(patient.getId())).thenReturn(patientOptional);
        doThrow(new NullPointerException()).when(patientRepository).deleteById(anyInt());
        Assertions.assertThrows(DeleteError.class, () -> patientService.deletePatient(patient.getId()));
        verify(patientRepository, times(1)).findById(patient.getId());
        verify(patientRepository, times(1)).deleteById(anyInt());
    }


    @Test
    void shouldGetSinglePatient() {
        Optional<Patient> patientOptional = Optional.of(patient);
        when(patientRepository.findById(patient.getId())).thenReturn(patientOptional);
        Patient testPatient = patientService.getPatient(patient.getId());
        Assertions.assertEquals(patient, testPatient);
        verify(patientRepository, times(1)).findById(patient.getId());
    }

    @Test
    void shouldThrowWhenGettingNonExistingPatient() {
        when(patientRepository.findById(patient.getId())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFound.class, () -> patientService.getPatient(patient.getId()));
        verify(patientRepository, times(1)).findById(patient.getId());
    }

    @Test
    void shouldGetAllPatients() {
        List<Patient> testPatientList = List.of(patient, wrongIdPatient);
        when(patientRepository.findAll()).thenReturn(testPatientList);

        List<Patient> patients = patientService.getPatients();

        Assertions.assertEquals(testPatientList, patients);
        verify(patientRepository, times(1)).findAll();
    }

    @Test
    void shouldThrowWhenNoPatientsInRepository() {
        when(patientRepository.findAll()).thenReturn(List.of());
        Assertions.assertThrows(EntityNotFound.class, () -> patientService.getPatients());

        verify(patientRepository, times(1)).findAll();
    }

    @Test
    void shouldGetOnePatientForDoctor() {
        int docSpringUserId = 2;
        int patientSpringUserId = 3;
        SpringUser docUser = SpringUser.builder().id(docSpringUserId).build();
        SpringUser patientUser = SpringUser.builder().id(patientSpringUserId).build();
        List<Patient> testPatientList = List.of(patient, wrongIdPatient);
        when(patientRepository.findAll()).thenReturn(testPatientList);

        when(userService.findSpringUser(docSpringUserId, false)).thenReturn(Optional.of(docUser));
        when(userService.findSpringUser(patient.getId(), true)).thenReturn(Optional.of(patientUser));
        when(chatRoomService.getChatRoomId(patientSpringUserId, docSpringUserId, false)).thenReturn(Optional.of("3_1"));
        List<Patient> patients = patientService.getPatients(2);

        Assertions.assertNotEquals(testPatientList, patients);
        Assertions.assertEquals(patient, patients.getFirst());
        verify(patientRepository, times(1)).findAll();
        verify(userService, times(3)).findSpringUser(anyInt(), anyBoolean());
        verify(chatRoomService, times(1)).getChatRoomId(patientSpringUserId, docSpringUserId, false);
    }

    @Test
    void shouldGetAllPatientsForDoctor() {
        int docSpringUserId = 2;
        int patientSpringUserId = 3;
        int secondPatientSpringUserId = 5;
        SpringUser docUser = SpringUser.builder().id(docSpringUserId).build();
        SpringUser patientUser = SpringUser.builder().id(patientSpringUserId).build();
        SpringUser secondPatientUser = SpringUser.builder().id(secondPatientSpringUserId).build();

        List<Patient> testPatientList = List.of(patient, wrongIdPatient);
        when(patientRepository.findAll()).thenReturn(testPatientList);

        when(userService.findSpringUser(docSpringUserId, false)).thenReturn(Optional.of(docUser));
        when(userService.findSpringUser(patient.getId(), true)).thenReturn(Optional.of(patientUser));
        when(userService.findSpringUser(wrongIdPatient.getId(), true)).thenReturn(Optional.of(secondPatientUser));
        when(chatRoomService.getChatRoomId(patientSpringUserId, docSpringUserId, false)).thenReturn(Optional.of("3_2"));
        when(chatRoomService.getChatRoomId(secondPatientSpringUserId, docSpringUserId, false)).thenReturn(Optional.of("5_2"));
        List<Patient> patients = patientService.getPatients(2);

        Assertions.assertEquals(testPatientList, patients);
        verify(patientRepository, times(1)).findAll();
        verify(userService, times(3)).findSpringUser(anyInt(), anyBoolean());
        verify(chatRoomService, times(2)).getChatRoomId(anyInt(), eq(docSpringUserId), eq(false));
    }

    @Test
    void shouldThrowWhenGettingPatientsForDoctorWhenNoChatExists() {
        int docSpringUserId = 2;
        int patientSpringUserId = 3;
        SpringUser docUser = SpringUser.builder().id(docSpringUserId).build();
        SpringUser patientUser = SpringUser.builder().id(patientSpringUserId).build();
        List<Patient> testPatientList = List.of(patient, wrongIdPatient);
        when(patientRepository.findAll()).thenReturn(testPatientList);
        when(userService.findSpringUser(docSpringUserId, false)).thenReturn(Optional.of(docUser));
        when(userService.findSpringUser(patient.getId(), true)).thenReturn(Optional.of(patientUser));
        when(chatRoomService.getChatRoomId(anyInt(), eq(docSpringUserId), anyBoolean())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFound.class, () -> patientService.getPatients(2));


        verify(patientRepository, times(1)).findAll();
        verify(userService, times(3)).findSpringUser(anyInt(), anyBoolean());
        verify(chatRoomService, times(1)).getChatRoomId(patientSpringUserId, docSpringUserId, false);

    }


    @Test
    void shouldThrowWhenGettingPatientsForDoctorWhenNoDoctorSpringUser() {
        int docSpringUserId = 2;
        List<Patient> testPatientList = List.of(patient, wrongIdPatient);
        when(patientRepository.findAll()).thenReturn(testPatientList);
        when(userService.findSpringUser(docSpringUserId, false)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFound.class, () -> patientService.getPatients(docSpringUserId));

        verify(patientRepository, times(1)).findAll();
        verify(userService, times(1)).findSpringUser(anyInt(), anyBoolean());
    }

    @Test
    void shouldThrowWhenGettingPatientsForDoctorWhenNoPatientsSpringUser() {
        int docSpringUserId = 2;
        int patientSpringUserId = 3;
        SpringUser docUser = SpringUser.builder().id(docSpringUserId).build();
        List<Patient> testPatientList = List.of(patient, wrongIdPatient);
        when(patientRepository.findAll()).thenReturn(testPatientList);
        when(userService.findSpringUser(docSpringUserId, false)).thenReturn(Optional.of(docUser));
        when(userService.findSpringUser(anyInt(), eq(true))).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFound.class, () -> patientService.getPatients(2));


        verify(patientRepository, times(1)).findAll();
        verify(userService, times(3)).findSpringUser(anyInt(), anyBoolean());
        verify(chatRoomService, never()).getChatRoomId(anyInt(), anyInt(), anyBoolean());

    }
}
