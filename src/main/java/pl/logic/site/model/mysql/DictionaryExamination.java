package pl.logic.site.model.mysql;

import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "examination_name")
    private String examinationName;

    @Column(name = "id_disease")
    private int idDisease;

    @Column(name = "examination_required_value")
    private String examinationRequiredValue;
}
