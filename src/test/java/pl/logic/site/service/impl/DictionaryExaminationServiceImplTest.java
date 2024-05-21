
package pl.logic.site.service.impl;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.mysql.DictionaryExamination;
import pl.logic.site.repository.DictionaryExaminationRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class DictionaryExaminationServiceImplTest {

    @Mock
    private DictionaryExaminationRepository dictionaryExaminationRepository;
    @InjectMocks
    private DictionaryExaminationServiceImpl dictionaryExaminationService;
    DictionaryExamination dictionaryExamination1;
    DictionaryExamination dictionaryExamination2;

    @BeforeEach
    public void setUp() {
        dictionaryExamination1 = new DictionaryExamination(1, "test1", 2, "value1");
        dictionaryExamination2 = new DictionaryExamination(2, "test2", 3, "value2");
    }
           

    @Test
    void shouldGetSingleDictionaryExamination() {
        Optional<DictionaryExamination> dictionaryExaminationOptional = Optional.of(dictionaryExamination1);
        when(dictionaryExaminationRepository.findById(dictionaryExamination1.getId())).thenReturn(dictionaryExaminationOptional);
        DictionaryExamination testDictionaryExamination = dictionaryExaminationService.getDictionaryExamination(dictionaryExamination1.getId());
        Assertions.assertEquals(dictionaryExamination1, testDictionaryExamination);
        verify(dictionaryExaminationRepository, times(1)).findById(dictionaryExamination1.getId());
    }

    @Test
    void shouldThrowWhenGettingNonExistingDictionaryExamination() {
        when(dictionaryExaminationRepository.findById(dictionaryExamination1.getId())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFound.class, () -> dictionaryExaminationService.getDictionaryExamination(dictionaryExamination1.getId()));
        verify(dictionaryExaminationRepository, times(1)).findById(dictionaryExamination1.getId());
    }

    @Test
    void shouldGetAllDictionaryExaminations() {
        List<DictionaryExamination> testDictionaryExaminationList = List.of(dictionaryExamination1, dictionaryExamination2);
        when(dictionaryExaminationRepository.findAll()).thenReturn(testDictionaryExaminationList);

        List<DictionaryExamination> dictionaryExaminations = dictionaryExaminationService.getDictionaryExaminations();

        Assertions.assertEquals(testDictionaryExaminationList, dictionaryExaminations);
        verify(dictionaryExaminationRepository, times(1)).findAll();
    }

    @Test
    void shouldThrowWhenNoDictionaryExaminationsInRepository() {
        when(dictionaryExaminationRepository.findAll()).thenReturn(List.of());
        Assertions.assertThrows(EntityNotFound.class, () -> dictionaryExaminationService.getDictionaryExaminations());

        verify(dictionaryExaminationRepository, times(1)).findAll();
    }

}
