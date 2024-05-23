package pl.logic.site.model.mysql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Examination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "id_patient")
    private int idPatient;

    @Column(name = "examination")
    private String examination;

    @Column(name = "examination_value")
    private String examinationValue;
}
