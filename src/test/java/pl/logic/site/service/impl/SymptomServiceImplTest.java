
package pl.logic.site.service.impl;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.mysql.Symptom;
import pl.logic.site.repository.SymptomRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class SymptomServiceImplTest {

    @Mock
    private SymptomRepository symptomRepository;
    @InjectMocks
    private SymptomServiceImpl symptomService;
    Symptom symptom1;
    Symptom symptom2;

    @BeforeEach
    public void setUp() {
        symptom1 = new Symptom(1, "headache");
        symptom2 = new Symptom(2, "sorethroat");
    }
           

    @Test
    void shouldGetSingleSymptom() {
        Optional<Symptom> symptomOptional = Optional.of(symptom1);
        when(symptomRepository.findById(symptom1.getId())).thenReturn(symptomOptional);
        Symptom testSymptom = symptomService.getSymptom(symptom1.getId());
        Assertions.assertEquals(symptom1, testSymptom);
        verify(symptomRepository, times(1)).findById(symptom1.getId());
    }

    @Test
    void shouldThrowWhenGettingNonExistingSymptom() {
        when(symptomRepository.findById(symptom1.getId())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFound.class, () -> symptomService.getSymptom(symptom1.getId()));
        verify(symptomRepository, times(1)).findById(symptom1.getId());
    }

    @Test
    void shouldGetAllSymptoms() {
        List<Symptom> testSymptomList = List.of(symptom1, symptom2);
        when(symptomRepository.findAll()).thenReturn(testSymptomList);

        List<Symptom> symptoms = symptomService.getSymptoms();

        Assertions.assertEquals(testSymptomList, symptoms);
        verify(symptomRepository, times(1)).findAll();
    }

    @Test
    void shouldThrowWhenNoSymptomsInRepository() {
        when(symptomRepository.findAll()).thenReturn(List.of());
        Assertions.assertThrows(EntityNotFound.class, () -> symptomService.getSymptoms());

        verify(symptomRepository, times(1)).findAll();
    }

}
