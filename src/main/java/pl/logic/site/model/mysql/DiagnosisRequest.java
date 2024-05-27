package pl.logic.site.model.mysql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Immutable;


import java.util.Date;

@Slf4j
@Immutable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class DiagnosisRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "id_chart")
    private int idChart;

    @Column(name = "id_doctor")
    private int idDoctor;

    @Column(name = "diagnosis")
    private String diagnosis;

    @Column(name = "voice_diagnosis")
    private String voiceDiagnosis;

    @Column(name = "creation_date")
    private Date creationDate;

    @Column(name = "modification_date")
    private Date modificationDate;
}
