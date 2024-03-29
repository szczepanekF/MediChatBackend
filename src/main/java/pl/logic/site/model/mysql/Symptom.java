package pl.logic.site.model.mysql;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.persistence.Id;
import org.springframework.data.annotation.Immutable;


import jakarta.persistence.Column;

@Slf4j
@Immutable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Symptom {
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;
}
