package pl.logic.site.service.impl;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.logic.site.model.dao.SpecialisationDAO;
import pl.logic.site.model.exception.DeleteError;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.Specialisation;
import pl.logic.site.repository.SpecialisationRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class SpecialisationServiceImplTest {

    @Mock
    private SpecialisationRepository specialisationRepository;
    @InjectMocks
    private SpecialisationServiceImpl specialisationService;
    Specialisation specialisationWithId0;
    Specialisation specialisationWithId2;

    @BeforeEach
    public void setUp() {
        specialisationWithId0 = new Specialisation(0, "pediatrics");
        specialisationWithId2 = new Specialisation(2, "cardiology");
    }

    @Test
    void shouldCreateSpecialisation() {
        when(specialisationRepository.saveAndFlush(any(Specialisation.class))).thenReturn(specialisationWithId0);

        SpecialisationDAO specialisationDAO = new SpecialisationDAO(specialisationWithId0);
        Specialisation savedSpecialisation = specialisationService.createSpecialisation(specialisationDAO);

        verify(specialisationRepository, times(1)).saveAndFlush(any(Specialisation.class));
        Assertions.assertEquals(specialisationWithId0, savedSpecialisation);
    }

    @Test
    void shouldThrowWhileCreatingSpecialisationWithId() {


        SpecialisationDAO specialisationDAO = new SpecialisationDAO(specialisationWithId2);
        Assertions.assertThrows(SaveError.class, () -> specialisationService.createSpecialisation(specialisationDAO));
        verify(specialisationRepository, never()).saveAndFlush(any(Specialisation.class));
    }

    @Test
    void shouldThrowWhenRepositoryErrorCreatingSpecialisation() {
        doThrow(new NullPointerException()).when(specialisationRepository).saveAndFlush(any(Specialisation.class));
        SpecialisationDAO specialisationDAO = new SpecialisationDAO(specialisationWithId0);
        Assertions.assertThrows(SaveError.class, () -> specialisationService.createSpecialisation(specialisationDAO));
        verify(specialisationRepository, times(1)).saveAndFlush(any(Specialisation.class));

    }

    @Test
    void shouldUpdateSpecialisation() {
        Optional<Specialisation> specialisationOptional = Optional.of(specialisationWithId0);
        when(specialisationRepository.findById(specialisationWithId0.getId())).thenReturn(specialisationOptional);
        when(specialisationRepository.saveAndFlush(any(Specialisation.class))).thenReturn(specialisationWithId0);

        SpecialisationDAO specialisationDAO = new SpecialisationDAO(specialisationWithId0);
        Specialisation savedSpecialisation = specialisationService.updateSpecialisation(specialisationDAO, specialisationWithId0.getId());

        verify(specialisationRepository, times(1)).findById(specialisationWithId0.getId());
        verify(specialisationRepository, times(1)).saveAndFlush(any(Specialisation.class));
        Assertions.assertEquals(specialisationWithId0, savedSpecialisation);
    }

    @Test
    void shouldThrowWhileUpdatingNonExistingSpecialisation() {
        when(specialisationRepository.findById(specialisationWithId0.getId())).thenReturn(Optional.empty());


        SpecialisationDAO specialisationDAO = new SpecialisationDAO(specialisationWithId0);
        Assertions.assertThrows(EntityNotFound.class, () -> specialisationService.updateSpecialisation(specialisationDAO, specialisationWithId0.getId()));
        verify(specialisationRepository, times(1)).findById(specialisationWithId0.getId());
        verify(specialisationRepository, never()).saveAndFlush(any(Specialisation.class));
    }

    @Test
    void shouldThrowWhenRepositoryErrorUpdatingSpecialisation() {
        Optional<Specialisation> specialisationOptional = Optional.of(specialisationWithId0);
        when(specialisationRepository.findById(specialisationWithId0.getId())).thenReturn(specialisationOptional);
        doThrow(new NullPointerException()).when(specialisationRepository).saveAndFlush(any(Specialisation.class));
        SpecialisationDAO specialisationDAO = new SpecialisationDAO(specialisationWithId0);
        Assertions.assertThrows(SaveError.class, () -> specialisationService.updateSpecialisation(specialisationDAO, specialisationWithId0.getId()));
        verify(specialisationRepository, times(1)).findById(specialisationWithId0.getId());
        verify(specialisationRepository, times(1)).saveAndFlush(any(Specialisation.class));
    }

    @Test
    void shouldDeleteSpecialisation() {
        Optional<Specialisation> specialisationOptional = Optional.of(specialisationWithId0);
        when(specialisationRepository.findById(specialisationWithId0.getId())).thenReturn(specialisationOptional);

        specialisationService.deleteSpecialisation(specialisationWithId0.getId());

        verify(specialisationRepository, times(1)).findById(specialisationWithId0.getId());
        verify(specialisationRepository, times(1)).deleteById(specialisationWithId0.getId());
    }

    @Test
    void shouldThrowWhileDeletingNonExistingSpecialisation() {
        when(specialisationRepository.findById(specialisationWithId0.getId())).thenReturn(Optional.empty());


        Assertions.assertThrows(EntityNotFound.class, () -> specialisationService.deleteSpecialisation(specialisationWithId0.getId()));
        verify(specialisationRepository, times(1)).findById(specialisationWithId0.getId());
        verify(specialisationRepository, never()).deleteById(anyInt());
    }

    @Test
    void shouldThrowWhenRepositoryErrorDeletingSpecialisation() {
        Optional<Specialisation> specialisationOptional = Optional.of(specialisationWithId0);
        when(specialisationRepository.findById(specialisationWithId0.getId())).thenReturn(specialisationOptional);
        doThrow(new NullPointerException()).when(specialisationRepository).deleteById(anyInt());
        Assertions.assertThrows(DeleteError.class, () -> specialisationService.deleteSpecialisation(specialisationWithId0.getId()));
        verify(specialisationRepository, times(1)).findById(specialisationWithId0.getId());
        verify(specialisationRepository, times(1)).deleteById(anyInt());
    }


    @Test
    void shouldGetSingleSpecialisation() {
        Optional<Specialisation> specialisationOptional = Optional.of(specialisationWithId0);
        when(specialisationRepository.findById(specialisationWithId0.getId())).thenReturn(specialisationOptional);
        Specialisation testSpecialisation = specialisationService.getSpecialisation(specialisationWithId0.getId());
        Assertions.assertEquals(specialisationWithId0, testSpecialisation);
        verify(specialisationRepository, times(1)).findById(specialisationWithId0.getId());
    }

    @Test
    void shouldThrowWhenGettingNonExistingSpecialisation() {
        when(specialisationRepository.findById(specialisationWithId0.getId())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFound.class, () -> specialisationService.getSpecialisation(specialisationWithId0.getId()));
        verify(specialisationRepository, times(1)).findById(specialisationWithId0.getId());
    }

    @Test
    void shouldGetAllSpecialisations() {
        List<Specialisation> testSpecialisationList = List.of(specialisationWithId0, specialisationWithId2);
        when(specialisationRepository.findAll()).thenReturn(testSpecialisationList);

        List<Specialisation> specialisations = specialisationService.getSpecialisations();

        Assertions.assertEquals(testSpecialisationList, specialisations);
        verify(specialisationRepository, times(1)).findAll();
    }

    @Test
    void shouldThrowWhenNoSpecialisationsInRepository() {
        when(specialisationRepository.findAll()).thenReturn(List.of());
        Assertions.assertThrows(EntityNotFound.class, () -> specialisationService.getSpecialisations());

        verify(specialisationRepository, times(1)).findAll();
    }

}
