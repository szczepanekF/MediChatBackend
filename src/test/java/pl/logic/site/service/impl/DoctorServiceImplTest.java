package pl.logic.site.service.impl;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.logic.site.model.dao.DoctorDAO;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.Doctor;
import pl.logic.site.repository.DoctorRepository;


import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class DoctorServiceImplTest {

    @Mock
    private DoctorRepository doctorRepository;
    @InjectMocks
    private DoctorServiceImpl doctorService;
    Doctor botDoctorWithId0;
    Doctor doctor2;
    Doctor doctor3;
    Doctor secondBotDoctor;

    @BeforeEach
    public void setUp() {
        botDoctorWithId0 = new Doctor(0, "Testname", "Surname", new Date(), 1, 1);
        doctor2 = new Doctor(1, "Testname", "Surname", new Date(), 2,0);
        doctor3 = new Doctor(1, "Testname", "Surname", new Date(), 3,0);
        secondBotDoctor = new Doctor(1, "Testname", "Surname", new Date(), 4,1);
    }

    @Test
    void shouldCreateDoctor() {
        when(doctorRepository.saveAndFlush(any(Doctor.class))).thenReturn(botDoctorWithId0);

        DoctorDAO doctorDAO = new DoctorDAO(botDoctorWithId0);
        Doctor savedDoctor = doctorService.createDoctor(doctorDAO);

        verify(doctorRepository, times(1)).saveAndFlush(any(Doctor.class));
        Assertions.assertEquals(botDoctorWithId0, savedDoctor);
    }

    @Test
    void shouldThrowWhileCreatingDoctorWithId() {


        DoctorDAO doctorDAO = new DoctorDAO(doctor2);
        Assertions.assertThrows(SaveError.class, () -> doctorService.createDoctor(doctorDAO));
        verify(doctorRepository, never()).saveAndFlush(any(Doctor.class));
    }

    @Test
    void shouldThrowWhenRepositoryErrorCreatingDoctor() {
        doThrow(new NullPointerException()).when(doctorRepository).saveAndFlush(any(Doctor.class));
        DoctorDAO doctorDAO = new DoctorDAO(botDoctorWithId0);
        Assertions.assertThrows(SaveError.class, () -> doctorService.createDoctor(doctorDAO));
        verify(doctorRepository, times(1)).saveAndFlush(any(Doctor.class));

    }

    @Test
    void shouldUpdateDoctor() {
        Optional<Doctor> doctorOptional = Optional.of(botDoctorWithId0);
        when(doctorRepository.findById(botDoctorWithId0.getId())).thenReturn(doctorOptional);
        when(doctorRepository.saveAndFlush(any(Doctor.class))).thenReturn(botDoctorWithId0);

        DoctorDAO doctorDAO = new DoctorDAO(botDoctorWithId0);
        Doctor savedDoctor = doctorService.updateDoctor(doctorDAO, botDoctorWithId0.getId());

        verify(doctorRepository, times(1)).findById(botDoctorWithId0.getId());
        verify(doctorRepository, times(1)).saveAndFlush(any(Doctor.class));
        Assertions.assertEquals(botDoctorWithId0, savedDoctor);
    }

    @Test
    void shouldThrowWhileUpdatingNonExistingDoctor() {
        when(doctorRepository.findById(botDoctorWithId0.getId())).thenReturn(Optional.empty());


        DoctorDAO doctorDAO = new DoctorDAO(botDoctorWithId0);
        Assertions.assertThrows(EntityNotFound.class, () -> doctorService.updateDoctor(doctorDAO, botDoctorWithId0.getId()));
        verify(doctorRepository, times(1)).findById(botDoctorWithId0.getId());
        verify(doctorRepository, never()).saveAndFlush(any(Doctor.class));
    }

    @Test
    void shouldThrowWhenRepositoryErrorUpdatingDoctor() {
        Optional<Doctor> doctorOptional = Optional.of(botDoctorWithId0);
        when(doctorRepository.findById(botDoctorWithId0.getId())).thenReturn(doctorOptional);
        doThrow(new NullPointerException()).when(doctorRepository).saveAndFlush(any(Doctor.class));
        DoctorDAO doctorDAO = new DoctorDAO(botDoctorWithId0);
        Assertions.assertThrows(SaveError.class, () -> doctorService.updateDoctor(doctorDAO, botDoctorWithId0.getId()));
        verify(doctorRepository, times(1)).findById(botDoctorWithId0.getId());
        verify(doctorRepository, times(1)).saveAndFlush(any(Doctor.class));
    }

    @Test
    void shouldDeleteDoctor() {
        Optional<Doctor> doctorOptional = Optional.of(botDoctorWithId0);
        when(doctorRepository.findById(botDoctorWithId0.getId())).thenReturn(doctorOptional);

        doctorService.deleteDoctor(botDoctorWithId0.getId());

        verify(doctorRepository, times(1)).findById(botDoctorWithId0.getId());
        verify(doctorRepository, times(1)).deleteById(botDoctorWithId0.getId());
    }

    @Test
    void shouldThrowWhileDeletingNonExistingDoctor() {
        when(doctorRepository.findById(botDoctorWithId0.getId())).thenReturn(Optional.empty());


        Assertions.assertThrows(EntityNotFound.class, () -> doctorService.deleteDoctor(botDoctorWithId0.getId()));
        verify(doctorRepository, times(1)).findById(botDoctorWithId0.getId());
        verify(doctorRepository, never()).deleteById(anyInt());
    }

    @Test
    void shouldThrowWhenRepositoryErrorDeletingDoctor() {
        Optional<Doctor> doctorOptional = Optional.of(botDoctorWithId0);
        when(doctorRepository.findById(botDoctorWithId0.getId())).thenReturn(doctorOptional);
        doThrow(new NullPointerException()).when(doctorRepository).deleteById(anyInt());
        Assertions.assertThrows(DeleteError.class, () -> doctorService.deleteDoctor(botDoctorWithId0.getId()));
        verify(doctorRepository, times(1)).findById(botDoctorWithId0.getId());
        verify(doctorRepository, times(1)).deleteById(anyInt());
    }


    @Test
    void shouldGetSingleDoctor() {
        Optional<Doctor> doctorOptional = Optional.of(botDoctorWithId0);
        when(doctorRepository.findById(botDoctorWithId0.getId())).thenReturn(doctorOptional);
        Doctor testDoctor = doctorService.getDoctor(botDoctorWithId0.getId());
        Assertions.assertEquals(botDoctorWithId0, testDoctor);
        verify(doctorRepository, times(1)).findById(botDoctorWithId0.getId());
    }

    @Test
    void shouldThrowWhenGettingNonExistingDoctor() {
        when(doctorRepository.findById(botDoctorWithId0.getId())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFound.class, () -> doctorService.getDoctor(botDoctorWithId0.getId()));
        verify(doctorRepository, times(1)).findById(botDoctorWithId0.getId());
    }

    @Test
    void shouldGetAllDoctors() {
        List<Doctor> testDoctorList = List.of(botDoctorWithId0, doctor2,doctor3, secondBotDoctor);
        when(doctorRepository.findAll()).thenReturn(testDoctorList);

        List<Doctor> doctors = doctorService.getDoctors(2);

        Assertions.assertEquals(testDoctorList, doctors);
        verify(doctorRepository, times(1)).findAll();
    }

    @Test
    void shouldThrowWhenNoDoctorsInRepository() {
        when(doctorRepository.findAll()).thenReturn(List.of());
        Assertions.assertThrows(EntityNotFound.class, () -> doctorService.getDoctors(2));

        verify(doctorRepository, times(1)).findAll();
    }

    @Test
    void shouldGetAllRealDoctors() {
        int isBotFilter = 0;
        List<Doctor> testDoctorList = List.of(botDoctorWithId0, doctor2,doctor3, secondBotDoctor);
        when(doctorRepository.retrieveDoctorsByType(isBotFilter)).thenReturn(testDoctorList);

        List<Doctor> doctors = doctorService.getDoctors(isBotFilter);

        Assertions.assertEquals(testDoctorList, doctors);
        verify(doctorRepository, times(1)).retrieveDoctorsByType(isBotFilter);
    }

    @Test
    void shouldThrowWhenNoRealDoctorsInRepository() {
        int isBotFilter = 0;
        when(doctorRepository.retrieveDoctorsByType(isBotFilter)).thenReturn(List.of());
        Assertions.assertThrows(EntityNotFound.class, () -> doctorService.getDoctors(isBotFilter));

        verify(doctorRepository, times(1)).retrieveDoctorsByType(isBotFilter);
    }

    @Test
    void shouldGetAllBotDoctors() {

        int isBotFilter = 1;
        when(doctorRepository.retrieveDoctorsByType(isBotFilter)).thenReturn(List.of(botDoctorWithId0, secondBotDoctor));

        List<Doctor> doctors = doctorService.getDoctors(isBotFilter);

        Assertions.assertEquals(List.of(botDoctorWithId0, secondBotDoctor), doctors);
        verify(doctorRepository, times(1)).retrieveDoctorsByType(isBotFilter);
    }

    @Test
    void shouldThrowWhenNoBotDoctorsInRepository() {
        int isBotFilter = 1;
        when(doctorRepository.retrieveDoctorsByType(isBotFilter)).thenReturn(List.of());
        Assertions.assertThrows(EntityNotFound.class, () -> doctorService.getDoctors(isBotFilter));

        verify(doctorRepository, times(1)).retrieveDoctorsByType(isBotFilter);
    }


}
