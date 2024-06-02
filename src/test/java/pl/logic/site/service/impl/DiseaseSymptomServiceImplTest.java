
package pl.logic.site.service.impl;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.mysql.DiseaseSymptom;
import pl.logic.site.repository.DiseaseSymptomRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public  class DiseaseSymptomServiceImplTest {

    @Mock
    private DiseaseSymptomRepository diseaseSymptomRepository;
    @InjectMocks
    private DiseaseSymptomServiceImpl diseaseSymptomService;
    DiseaseSymptom diseaseSymptom1;
    DiseaseSymptom diseaseSymptom2;
    DiseaseSymptom diseaseSymptom3;

    @BeforeEach
    public void setUp() {
        diseaseSymptom1 = new DiseaseSymptom(1, 1, 1);
        diseaseSymptom2 = new DiseaseSymptom(2, 1, 2);
        diseaseSymptom3 = new DiseaseSymptom(2, 2, 1);
    }


    @Test
    void shouldGetSingleDiseaseSymptom() {
        Optional<DiseaseSymptom> diseaseSymptomOptional = Optional.of(diseaseSymptom1);
        when(diseaseSymptomRepository.findById(diseaseSymptom1.getId())).thenReturn(diseaseSymptomOptional);
        DiseaseSymptom testDiseaseSymptom = diseaseSymptomService.getDiseaseSymptom(diseaseSymptom1.getId());
        Assertions.assertEquals(diseaseSymptom1, testDiseaseSymptom);
        verify(diseaseSymptomRepository, times(1)).findById(diseaseSymptom1.getId());
    }

    @Test
    void shouldThrowWhenGettingNonExistingDiseaseSymptom() {
        when(diseaseSymptomRepository.findById(diseaseSymptom1.getId())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFound.class, () -> diseaseSymptomService.getDiseaseSymptom(diseaseSymptom1.getId()));
        verify(diseaseSymptomRepository, times(1)).findById(diseaseSymptom1.getId());
    }

    @Test
    void shouldGetAllDiseaseSymptoms() {
        List<DiseaseSymptom> testDiseaseSymptomList = List.of(diseaseSymptom1, diseaseSymptom2);
        when(diseaseSymptomRepository.findAll()).thenReturn(testDiseaseSymptomList);

        List<DiseaseSymptom> diseaseSymptoms = diseaseSymptomService.getDiseaseSymptoms();

        Assertions.assertEquals(testDiseaseSymptomList, diseaseSymptoms);
        verify(diseaseSymptomRepository, times(1)).findAll();
    }

    @Test
    void shouldThrowWhenNoDiseaseSymptomsInRepository() {
        when(diseaseSymptomRepository.findAll()).thenReturn(List.of());
        Assertions.assertThrows(EntityNotFound.class, () -> diseaseSymptomService.getDiseaseSymptoms());

        verify(diseaseSymptomRepository, times(1)).findAll();
    }

    @Test
    void shouldGetAllDiseaseSymptomsForSymptom() {
        List<DiseaseSymptom> testDiseaseSymptomList = List.of(diseaseSymptom1, diseaseSymptom2, diseaseSymptom3);
        when(diseaseSymptomRepository.findByIdSymptom(anyInt())).thenReturn(List.of(diseaseSymptom1, diseaseSymptom3));

        List<DiseaseSymptom> diseaseSymptoms = diseaseSymptomService.getDiseaseSymptomsForSymptom(1);

        Assertions.assertNotEquals(testDiseaseSymptomList, diseaseSymptoms);
        Assertions.assertEquals(List.of(diseaseSymptom1, diseaseSymptom3), diseaseSymptoms);
        verify(diseaseSymptomRepository, times(1)).findByIdSymptom(anyInt());
    }

    @Test
    void shouldThrowWhenNoDiseaseSymptomsForGivenSymptomInRepository() {
        when(diseaseSymptomRepository.findByIdSymptom(anyInt())).thenReturn(List.of());
        Assertions.assertThrows(EntityNotFound.class, () -> diseaseSymptomService.getDiseaseSymptomsForSymptom(1));

        verify(diseaseSymptomRepository, times(1)).findByIdSymptom(anyInt());
    }

    @Test
    void shouldGetAllDiseaseSymptomsForDisease() {
        List<DiseaseSymptom> testDiseaseSymptomList = List.of(diseaseSymptom1, diseaseSymptom2, diseaseSymptom3);
        when(diseaseSymptomRepository.findByIdDisease(anyInt())).thenReturn(List.of(diseaseSymptom1, diseaseSymptom2));

        List<DiseaseSymptom> diseaseSymptoms = diseaseSymptomService.getDiseaseSymptomsForDisease(1);

        Assertions.assertNotEquals(testDiseaseSymptomList, diseaseSymptoms);
        Assertions.assertEquals(List.of(diseaseSymptom1, diseaseSymptom2), diseaseSymptoms);
        verify(diseaseSymptomRepository, times(1)).findByIdDisease(anyInt());
    }

    @Test
    void shouldThrowWhenNoDiseaseSymptomsForGivenDiseaseInRepository() {
        when(diseaseSymptomRepository.findByIdDisease(anyInt())).thenReturn(List.of());
        Assertions.assertThrows(EntityNotFound.class, () -> diseaseSymptomService.getDiseaseSymptomsForDisease(1));

        verify(diseaseSymptomRepository, times(1)).findByIdDisease(anyInt());
    }


}
