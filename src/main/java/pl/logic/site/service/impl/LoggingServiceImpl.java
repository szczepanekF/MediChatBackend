package pl.logic.site.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.logic.site.model.dao.DoctorDAO;
import pl.logic.site.model.enums.LogType;
import pl.logic.site.model.exception.SaveError;
import pl.logic.site.model.mysql.Doctor;
import pl.logic.site.model.mysql.Log;
import pl.logic.site.repository.DiagnosisRequestRepository;
import pl.logic.site.repository.DoctorRepository;
import pl.logic.site.repository.LogRepository;
import pl.logic.site.repository.SpringUserRepository;
import pl.logic.site.service.LoggingService;
import pl.logic.site.service.MessageService;
import pl.logic.site.utils.Consts;

import java.util.Date;

/**
 * A service used for managing logs.
 */

@Service
@RequiredArgsConstructor
public class LoggingServiceImpl implements LoggingService {
    @Autowired
    private LogRepository logRepository;
    @Autowired
    private JwtServiceImpl jwtService;

    @Transactional
    public Log addLog(final Log log) {
        if (log.getId() != 0)
            throw new SaveError(Consts.C453_SAVING_ERROR + " Explicitly stated entity ID, entity: " + log);

        Log returned;
        try {
            returned = logRepository.saveAndFlush(log);
        } catch (Exception e) {
            throw new SaveError(Consts.C453_SAVING_ERROR + " " + log);
        }
        return returned;
    }



    @Override
    public void createLog(String message, Object details, LogType logType, String header) {
        try {
            int userId =  Integer.parseInt(jwtService.decodeJWT(header).get("SpringUserId"));
            switch (logType){
                case info, error -> {
                    addLog(new Log(0, new Date(), message + "Action taken by springUserId: "+userId, logType, (String) details, userId));
                }
                case create,delete,update -> {
                    addLog(new Log(0, new Date(), message + "Action taken by springUserId: "+userId, logType, new ObjectMapper().writeValueAsString(details), userId));
                }
            }
         } catch (Exception e){
            System.out.println("Error creating log.");
            e.printStackTrace();
        }
    }

}
