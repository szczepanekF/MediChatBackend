package pl.logic.site.model.mysql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Immutable;
import pl.logic.site.model.enums.ReportFiletype;
import pl.logic.site.model.enums.Status;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
@Immutable
@Entity
@Table(name = "report")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "id_doctor", nullable = false)
    private String idDoctor;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "filetype", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportFiletype filetype;
}
