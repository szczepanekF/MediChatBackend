package pl.logic.site.model.views;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Immutable;

import java.util.Date;
@Entity
@Data
@Table(name = "doctorpatientsfromchats")
public class DoctorPatientsFromChats {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "doctorid")
    private Long doctorID;

    @Column(name = "springuserid")
    private Long springUserID;

    @Column(name = "roomid")
    private Long roomID;

    @Column(name = "patientid")
    private Long patientID;

    @Column(name = "patientname")
    private String patientname;

    @Column(name = "patientsurname")
    private String patientsurname;

    @Column(name = "isdiagnosisrequest")
    private String isDiagnosisRequest;

    @Column(name = "dateofcontact")
    private Date dateofcontact;

}
