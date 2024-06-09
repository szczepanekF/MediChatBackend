package pl.logic.site.model.reportsForms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Immutable;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Immutable
public class DataTableForm {
    private Date from;
    private Date to;
    private int idDoctor;
}
