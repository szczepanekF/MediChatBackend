package pl.logic.site.model.mysql;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "symptom_values")
public class SymptomValues {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "id_symptom")
    private int symptomId;

    @Column(name = "symptom_value_weak")
    private String symptomValueWeak;

    @Column(name = "symptom_value_average")
    private String symptomValueAverage;

    @Column(name = "symptom_value_hard")
    private String symptomValueHard;
}
