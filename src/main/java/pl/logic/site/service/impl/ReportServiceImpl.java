package pl.logic.site.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.logic.site.model.ReportDTO;
import pl.logic.site.model.exception.EntityNotFound;
import pl.logic.site.model.mysql.Disease;
import pl.logic.site.model.mysql.Report;
import pl.logic.site.repository.DiseaseRepository;
import pl.logic.site.repository.ReportRepository;
import pl.logic.site.service.DiseaseService;
import pl.logic.site.service.ReportService;
import pl.logic.site.utils.Consts;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<ReportDTO> getReportsByDoctorId(final String doctorID) {
        List<Report> reports = reportRepository.findAllByIdDoctor(doctorID);
        return reports.stream()
                .map(this::convertReportToDTO)
                .collect(Collectors.toList());
    }

    private ReportDTO convertReportToDTO(Report report) {
        ReportDTO reportDTO = null;
        try {
            reportDTO = new ReportDTO(report);
            reportDTO.setId(report.getId());
            reportDTO.setIdDoctor(report.getIdDoctor());
            reportDTO.setTitle(report.getTitle());
            reportDTO.setFiletype(report.getFiletype());
            if (report.getFile() != null) {
                try {
                    reportDTO.setFile(encodeBlobToBase64(report.getFile()));
                } catch (SQLException | IOException e) {
                    // Handle the exception as per your application's error handling strategy
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return reportDTO;
    }

    private String encodeBlobToBase64(Blob blob) throws SQLException, IOException {
        try (InputStream inputStream = blob.getBinaryStream()) {
            byte[] bytes = inputStream.readAllBytes();
            return Base64.getEncoder().encodeToString(bytes);
        }
    }
}
