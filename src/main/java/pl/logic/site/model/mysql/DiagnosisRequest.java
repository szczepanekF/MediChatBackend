package pl.logic.site.model.mysql;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.persistence.Id;
import org.springframework.data.annotation.Immutable;


import jakarta.persistence.Column;

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
