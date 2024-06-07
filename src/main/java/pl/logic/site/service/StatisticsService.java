package pl.logic.site.service;

import pl.logic.site.model.mysql.Report;
import pl.logic.site.model.reportsForms.ReportCreateForm;

public interface StatisticsService {
    Report createReport(ReportCreateForm reportCreateForm);
}
