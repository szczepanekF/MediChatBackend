package pl.logic.site.model;

import lombok.Data;
import pl.logic.site.model.enums.ReportFiletype;
import pl.logic.site.model.mysql.Report;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;

@Data
public class ReportDTO {
    private int id;
    private String idDoctor;
    private String file; // Base64 encoded string
    private ReportFiletype filetype;
    private String title;

    public ReportDTO(Report report) throws SQLException, IOException {
        this.id = report.getId();
        this.idDoctor = report.getIdDoctor();
        this.filetype = report.getFiletype();
        this.title = report.getTitle();
        if (report.getFile() != null) {
            this.file = encodeBlobToBase64(report.getFile());
        }
    }

    private String encodeBlobToBase64(Blob blob) throws SQLException, IOException {
        try (InputStream inputStream = blob.getBinaryStream()) {
            byte[] bytes = inputStream.readAllBytes();
            return Base64.getEncoder().encodeToString(bytes);
        }
    }
}