package pl.logic.site.model.mysql;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Immutable;

@Slf4j
@Immutable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class DictionaryExamination {
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "examination_name")
    private String examinationName;

    @Column(name = "id_disease")
    private String idDisease;

    @Column(name = "examination_required_value")
    private String examinationRequiredValue;
}
