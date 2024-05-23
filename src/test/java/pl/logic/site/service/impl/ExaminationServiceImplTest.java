package pl.logic.site.service.impl;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.logic.site.model.dao.ExaminationDAO;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.Examination;
import pl.logic.site.repository.ExaminationRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ExaminationServiceImplTest {
    @Mock
    private ExaminationRepository examinationRepository;
    @InjectMocks
    private ExaminationServiceImpl examinationService;
    Examination examinationWithId0;
    Examination examination2;
    Examination examination3;

    @BeforeEach
    public void setUp() {
        examinationWithId0 = new Examination(0, 2, "test1", "value1");
        examination2 = new Examination(2, 3, "test2", "value2");
        examination3 = new Examination(3, 2, "test3", "value3");

    }

    @Test
    void shouldCreateExamination() {
        when(examinationRepository.saveAndFlush(any(Examination.class))).thenReturn(examinationWithId0);

        ExaminationDAO examinationDAO = new ExaminationDAO(examinationWithId0);
        Examination savedExamination = examinationService.createExamination(examinationDAO);

        verify(examinationRepository, times(1)).saveAndFlush(any(Examination.class));
        Assertions.assertEquals(examinationWithId0, savedExamination);
    }

    @Test
    void shouldThrowWhileCreatingExaminationWithId() {

        examinationWithId0.setId(1);
        ExaminationDAO examinationDAO = new ExaminationDAO(examination2);
        Assertions.assertThrows(SaveError.class, () -> examinationService.createExamination(examinationDAO));
        verify(examinationRepository, never()).saveAndFlush(any(Examination.class));
    }

    @Test
    void shouldThrowWhenRepositoryErrorCreatingExamination() {
        doThrow(new NullPointerException()).when(examinationRepository).saveAndFlush(any(Examination.class));

        ExaminationDAO examinationDAO = new ExaminationDAO(examinationWithId0);
        Assertions.assertThrows(SaveError.class, () -> examinationService.createExamination(examinationDAO));
        verify(examinationRepository, times(1)).saveAndFlush(any(Examination.class));

    }

    @Test
    void shouldUpdateExamination() {
        Optional<Examination> examinationOptional = Optional.of(examinationWithId0);
        when(examinationRepository.findById(examinationWithId0.getId())).thenReturn(examinationOptional);
        when(examinationRepository.saveAndFlush(any(Examination.class))).thenReturn(examinationWithId0);

        ExaminationDAO examinationDAO = new ExaminationDAO(examinationWithId0);
        Examination savedExamination = examinationService.updateExamination(examinationDAO, examinationWithId0.getId());

        verify(examinationRepository, times(1)).findById(examinationWithId0.getId());
        verify(examinationRepository, times(1)).saveAndFlush(any(Examination.class));
        Assertions.assertEquals(examinationWithId0, savedExamination);
    }

    @Test
    void shouldThrowWhileUpdatingNonExistingExamination() {
        when(examinationRepository.findById(examinationWithId0.getId())).thenReturn(Optional.empty());


        ExaminationDAO examinationDAO = new ExaminationDAO(examinationWithId0);
        Assertions.assertThrows(EntityNotFound.class, () -> examinationService.updateExamination(examinationDAO, examinationWithId0.getId()));
        verify(examinationRepository, times(1)).findById(examinationWithId0.getId());
        verify(examinationRepository, never()).saveAndFlush(any(Examination.class));
    }

    @Test
    void shouldThrowWhenRepositoryErrorUpdatingExamination() {
        Optional<Examination> examinationOptional = Optional.of(examinationWithId0);
        when(examinationRepository.findById(examinationWithId0.getId())).thenReturn(examinationOptional);
        doThrow(new NullPointerException()).when(examinationRepository).saveAndFlush(any(Examination.class));
        ExaminationDAO examinationDAO = new ExaminationDAO(examinationWithId0);
        Assertions.assertThrows(SaveError.class, () -> examinationService.updateExamination(examinationDAO, examinationWithId0.getId()));
        verify(examinationRepository, times(1)).findById(examinationWithId0.getId());
        verify(examinationRepository, times(1)).saveAndFlush(any(Examination.class));
    }

    @Test
    void shouldDeleteExamination() {
        Optional<Examination> examinationOptional = Optional.of(examinationWithId0);
        when(examinationRepository.findById(examinationWithId0.getId())).thenReturn(examinationOptional);

        examinationService.deleteExamination(examinationWithId0.getId());

        verify(examinationRepository, times(1)).findById(examinationWithId0.getId());
        verify(examinationRepository, times(1)).deleteById(examinationWithId0.getId());
    }

    @Test
    void shouldThrowWhileDeletingNonExistingExamination() {
        when(examinationRepository.findById(examinationWithId0.getId())).thenReturn(Optional.empty());


        Assertions.assertThrows(EntityNotFound.class, () -> examinationService.deleteExamination(examinationWithId0.getId()));
        verify(examinationRepository, times(1)).findById(examinationWithId0.getId());
        verify(examinationRepository, never()).deleteById(anyInt());
    }

    @Test
    void shouldThrowWhenRepositoryErrorDeletingExamination() {
        Optional<Examination> examinationOptional = Optional.of(examinationWithId0);
        when(examinationRepository.findById(examinationWithId0.getId())).thenReturn(examinationOptional);
        doThrow(new NullPointerException()).when(examinationRepository).deleteById(anyInt());
        Assertions.assertThrows(DeleteError.class, () -> examinationService.deleteExamination(examinationWithId0.getId()));
        verify(examinationRepository, times(1)).findById(examinationWithId0.getId());
        verify(examinationRepository, times(1)).deleteById(anyInt());
    }


    @Test
    void shouldGetSingleExamination() {
        Optional<Examination> examinationOptional = Optional.of(examinationWithId0);
        when(examinationRepository.findById(examinationWithId0.getId())).thenReturn(examinationOptional);
        Examination testExamination = examinationService.getExamination(examinationWithId0.getId());
        Assertions.assertEquals(examinationWithId0, testExamination);
        verify(examinationRepository, times(1)).findById(examinationWithId0.getId());
    }

    @Test
    void shouldThrowWhenGettingNonExistingExamination() {
        when(examinationRepository.findById(examinationWithId0.getId())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFound.class, () -> examinationService.getExamination(examinationWithId0.getId()));
        verify(examinationRepository, times(1)).findById(examinationWithId0.getId());
    }

    @Test
    void shouldGetAllExaminations() {
        List<Examination> testExaminationList = List.of(examinationWithId0, examination2, examination3);
        when(examinationRepository.findAllByIdPatient(examinationWithId0.getIdPatient())).thenReturn(List.of(examinationWithId0, examination3));

        List<Examination> examinations = examinationService.getExaminations(examinationWithId0.getIdPatient());
        Assertions.assertNotEquals(testExaminationList, examinations);
        Assertions.assertEquals(List.of(examinationWithId0, examination3), examinations);
        verify(examinationRepository, times(1)).findAllByIdPatient(examinationWithId0.getIdPatient());
    }

//    @Test
//    void shouldThrowWhenNoExaminationsInRepository() {
//        when(examinationRepository.findAllByIdPatient(examinationWithId0.getIdPatient())).thenReturn(List.of());
//        Assertions.assertThrows(EntityNotFound.class, () -> examinationService.getExaminations(examinationWithId0.getIdPatient()));
//
//        verify(examinationRepository, times(1)).findAllByIdPatient(examinationWithId0.getIdPatient());
//    }

}
