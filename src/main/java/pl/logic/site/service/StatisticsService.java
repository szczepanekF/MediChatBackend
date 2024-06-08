package pl.logic.site.service;

import pl.logic.site.model.mysql.Disease;
import pl.logic.site.model.mysql.Report;
import pl.logic.site.model.mysql.Symptom;
import pl.logic.site.model.reportsForms.ReportCreateForm;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface StatisticsService {
    Report createReport(ReportCreateForm reportCreateForm);

    public String[] getAgeGroups();

    public int findSymptomIndex(List<Symptom> symptomsList, Symptom symptom);

    public int findDiseaseIndex(List<Disease> diseasesList, Disease disease);

    public int findAgeGroupIndex(int age);

    public List<String> generateDateRange(Date fromDate, Date toDate);

    public int findIndexForDate(Date searchDate, List<String> timeList);

    public LocalDate convertToLocalDate(Date date);
}
