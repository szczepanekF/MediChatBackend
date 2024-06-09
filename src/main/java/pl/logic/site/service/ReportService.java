package pl.logic.site.service;

import pl.logic.site.model.mysql.Disease;
import pl.logic.site.model.mysql.Report;

import java.util.List;

public interface ReportService {
    List<Report> getReportsByDoctorId(String doctorID);
}
