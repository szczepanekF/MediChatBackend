
package pl.logic.site.service.impl;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.mysql.SymptomValues;
import pl.logic.site.repository.SymptomValuesRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class SymptomValuesServiceImplTest {

    @Mock
    private SymptomValuesRepository symptomValuesRepository;
    @InjectMocks
    private SymptomValuesServiceImpl symptomValuesService;
    SymptomValues symptomValues1;
    SymptomValues symptomValues2;

    @BeforeEach
    public void setUp() {
        symptomValues1 = new SymptomValues(1, 2, "low", "mid", "high");
        symptomValues2 = new SymptomValues(2, 3, "low", "mid", "high");
    }
           


    @Test
    void shouldGetAllSymptomValues() {
        List<SymptomValues> testSymptomValuesList = List.of(symptomValues1, symptomValues2);
        when(symptomValuesRepository.findAll()).thenReturn(testSymptomValuesList);

        List<SymptomValues> symptomValues = symptomValuesService.getSymptomsValues();

        Assertions.assertEquals(testSymptomValuesList, symptomValues);
        verify(symptomValuesRepository, times(1)).findAll();
    }

    @Test
    void shouldThrowWhenNoSymptomValuesInRepository() {
        when(symptomValuesRepository.findAll()).thenReturn(List.of());
        Assertions.assertThrows(EntityNotFound.class, () -> symptomValuesService.getSymptomsValues());

        verify(symptomValuesRepository, times(1)).findAll();
    }

}
