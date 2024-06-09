package pl.logic.site.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.mysql.Disease;
import pl.logic.site.model.mysql.Report;
import pl.logic.site.repository.DiseaseRepository;
import pl.logic.site.repository.ReportRepository;
import pl.logic.site.service.DiseaseService;
import pl.logic.site.service.ReportService;
import pl.logic.site.utils.Consts;

import java.util.List;

@Slf4j
@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportRepository reportRepository;


    /**
     * Retrieves all reports by doctor id
     * @param doctorID
     * @return
     */
    @Override
    public List<Report> getReportsByDoctorId(final String doctorID) {
        return reportRepository.findAllByIdDoctor(doctorID);
    }
}
