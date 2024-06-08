package pl.logic.site.model.mysql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Immutable;
import pl.logic.site.model.enums.Status;
import java.time.LocalDate;
import java.time.Period;

import java.time.ZoneId;
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

    public int getAge() {
        LocalDate birthLocalDate = this.birth_date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        LocalDate currentDate = LocalDate.now();

        // Check if the birthdate is not in the future
        if (birthLocalDate.isAfter(currentDate)) {
            throw new IllegalArgumentException("Birth date cannot be in the future");
        }
        Period age = Period.between(birthLocalDate, currentDate);

        return age.getYears();
    }

    public int calculateAgeOnDate(Date onDate) {
        LocalDate birthLocalDate = this.birth_date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate onLocalDate = onDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        // Check if the birthdate is not after the given date
        if (birthLocalDate.isAfter(onLocalDate)) {
            throw new IllegalArgumentException("Birth date cannot be after the date on which age is calculated");
        }
        Period age = Period.between(birthLocalDate, onLocalDate);

        return age.getYears();
    }
}
