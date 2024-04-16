package pl.logic.site.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDoctorRequest {
    private String name;
    private String surname;
    private Date birthDate;
    private int specialisationId;
    private String email;
    private String username;
    private String password;
}
