package pl.logic.site.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.logic.site.facade.ObjectFacade;
import pl.logic.site.service.LoggingService;

@Slf4j
@RestController
@RequestMapping("statisticsController")
@Scope("request")
public class StatisticsController  {
    @Autowired
    ObjectFacade objectFacade;
    @Autowired
    LoggingService loggingService;
    @Autowired
    HttpServletRequest request;

    //getDoctorReports
    //createNewReport
}
