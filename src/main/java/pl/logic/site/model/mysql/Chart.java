package pl.logic.site.model.mysql;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
public class Chart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "id_patient")
    private int idPatient;

    @Column(name = "date")
    private Date date;
}
