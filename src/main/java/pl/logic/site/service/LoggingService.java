package pl.logic.site.service;

import pl.logic.site.model.enums.LogType;
import pl.logic.site.model.mysql.Log;

import java.util.List;

/**
 * A service used for managing logs.
 */
public interface LoggingService {
    public void createLog(String message, Object details, LogType logType, String header);
    List<Log> getAllLogs();
}
