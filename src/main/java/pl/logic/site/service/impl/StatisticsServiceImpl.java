package pl.logic.site.service.impl;

import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.logic.site.model.enums.ReportFiletype;
import pl.logic.site.model.enums.ReportType;
import pl.logic.site.model.mysql.Report;
import pl.logic.site.model.reportsForms.ReportCreateForm;
import pl.logic.site.service.StatisticsService;

import java.util.Date;

@Slf4j
@Service
public class StatisticsServiceImpl implements StatisticsService {
    @Override
    @Transactional
    public Report createReport(ReportCreateForm reportCreateForm) {
        Report report = new Report();

        report.setTitle(reportCreateForm.getTitle());

        String pdf = extractReportPDF(reportCreateForm);



        return report;
    }

    private String extractReportPDF(ReportCreateForm reportCreateForm){
        if(reportCreateForm.getFiletype() == ReportFiletype.pdf){
            return switch (reportCreateForm.getReportType()){
                case ReportType.user -> createUserReport(reportCreateForm.getIdDoctor(), reportCreateForm.getFrom(), reportCreateForm.getTo());
                case ReportType.diseases -> createDiseasesReport(reportCreateForm.getIdDoctor(), reportCreateForm.getFrom(), reportCreateForm.getTo());
                case symptoms_date -> null;
                case diseases_date -> null;
                case symptoms_doctor -> null;
                case diseases_doctor -> null;
                case symptoms_age_groups -> null;
                case diseases_age_groups -> null;
                case age_groups -> null;
                case new_users -> null;
            };
        } else if(reportCreateForm.getFiletype() == ReportFiletype.csv){
            return switch (reportCreateForm.getReportType()){
                case user -> null;
                case diseases -> null;
                case ReportType.symptoms_date -> createSymptomsDateReport(reportCreateForm.getIdDoctor(), reportCreateForm.getFrom(), reportCreateForm.getTo());
                case ReportType.diseases_date -> createDiseasesDateReport(reportCreateForm.getIdDoctor(), reportCreateForm.getFrom(), reportCreateForm.getTo());
                case ReportType.symptoms_doctor -> createSymptomsDoctorReport(reportCreateForm.getIdDoctor(), reportCreateForm.getFrom(), reportCreateForm.getTo());
                case ReportType.diseases_doctor -> createDiseasesDoctorReport(reportCreateForm.getIdDoctor(), reportCreateForm.getFrom(), reportCreateForm.getTo());
                case ReportType.symptoms_age_groups -> createSymptomsAgeGroupsReport(reportCreateForm.getIdDoctor(), reportCreateForm.getFrom(), reportCreateForm.getTo());
                case ReportType.diseases_age_groups -> createDiseasesAgeGroupsReport(reportCreateForm.getIdDoctor(), reportCreateForm.getFrom(), reportCreateForm.getTo());
                case ReportType.age_groups -> createAgeGroupsReport(reportCreateForm.getIdDoctor(), reportCreateForm.getFrom(), reportCreateForm.getTo());
                case ReportType.new_users -> createNewUsersReport(reportCreateForm.getIdDoctor(), reportCreateForm.getFrom(), reportCreateForm.getTo());
            };
        }
        return null;
    }

    private String createPDF(int idDoctor, int from, int to, String content) {
        return "";//return encoded file
    }

    private String createUserReport(int idDoctor, Date fromDate, Date toDate){
        //get all parts
        //create charts
        //createPDF encoded and return it
        return "";
    }

    private String createDiseasesReport(int idDoctor, Date fromDate, Date toDate){
        return "";
    }

    private String createSymptomsDateReport(int idDoctor, Date fromDate, Date toDate){
        return "";
    }

    private String createDiseasesDateReport(int idDoctor, Date fromDate, Date toDate){
        return "";
    }

    private String createSymptomsDoctorReport(int idDoctor, Date fromDate, Date toDate){
        return "";
    }

    private String createDiseasesDoctorReport(int idDoctor, Date fromDate, Date toDate){
        return "";
    }

    private String createSymptomsAgeGroupsReport(int idDoctor, Date fromDate, Date toDate){
        return "";
    }

    private String createDiseasesAgeGroupsReport(int idDoctor, Date fromDate, Date toDate){
        return "";
    }

    private String createAgeGroupsReport(int idDoctor, Date fromDate, Date toDate){
        return "";
    }

    private String createNewUsersReport(int idDoctor, Date fromDate, Date toDate){
        return "";
    }
}
