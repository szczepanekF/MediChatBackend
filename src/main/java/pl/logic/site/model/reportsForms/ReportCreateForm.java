package pl.logic.site.model.reportsForms;


import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Immutable;
import pl.logic.site.model.enums.ReportFiletype;
import pl.logic.site.model.enums.ReportType;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Immutable
public class ReportCreateForm {
    private String title;
    private ReportType reportType;
    private ReportFiletype filetype;
    private Date from;
    private Date to;
    private int idDoctor;
}
