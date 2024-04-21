package pl.logic.site.model.mysql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Immutable;
import pl.logic.site.model.enums.Status;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
@Immutable
@Entity
@Table(name = "patient")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "birth_date")
    private Date birth_date;

    @Column(name = "height", nullable = false)
    private int height;

    @Column(name = "weight", nullable = false)
    private int weight;

    @Column(name = "gender")
    private String gender;

    @Column(name = "status")
    private Status status;

    @Column(name = "height_unit")
    private String heightUnit;

    @Column(name = "weight_unit")
    private String weightUnit;
}
