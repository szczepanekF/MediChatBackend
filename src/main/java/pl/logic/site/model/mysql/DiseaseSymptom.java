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
public class DiseaseSymptom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "id_disease")
    private int idDisease;

    @Column(name = "id_symptom")
    private int idSymtpom;
}
