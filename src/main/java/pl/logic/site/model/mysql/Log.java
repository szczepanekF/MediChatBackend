package pl.logic.site.model.mysql;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Immutable;
import pl.logic.site.model.enums.LogType;

import java.util.Date;

@Slf4j
@Immutable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "event_date")
    private Date eventDate;

    @Column(name = "message")
    private String message;

    @Column(name = "type")
    private LogType type;

    @Column(name = "details")
    private String details;

    @Column(name = "user_id")
    private int userId;
}
