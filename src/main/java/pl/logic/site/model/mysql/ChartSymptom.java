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
public class ChartSymptom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "id_chart")
    private int idChart;

    @Column(name = "id_symptom")
    private int idSymptom;

    @Column(name = "symptom_value_level")
    private String symptomValueLevel;
}
