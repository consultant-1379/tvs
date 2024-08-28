package com.ericsson.gic.tms.tvs.presentation.controllers;


import com.ericsson.gic.tms.presentation.controllers.AbstractJsonApiCapableController;
import com.ericsson.gic.tms.tvs.application.services.UrlCountService;
import com.ericsson.gic.tms.tvs.application.services.UserSessionService;
import com.ericsson.gic.tms.tvs.presentation.dto.StatisticsObject;
import com.ericsson.gic.tms.tvs.presentation.resources.StatisticsResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.core.Response;
import java.util.List;

@Controller
public class StatisticsController extends AbstractJsonApiCapableController implements StatisticsResource {

    @Autowired
    private UserSessionService userSessionService;

    @Autowired
    private UrlCountService urlCountService;

    public Response getUsers() {
        List<StatisticsObject> loggedOnUsers = userSessionService.getLoggedOnUsers();
        return Response.ok(loggedOnUsers)
                .status(Response.Status.OK)
                .build();
    }

    @Override
    public Response getUrlHitCount() {
        List<StatisticsObject> urlHitCount = urlCountService.getUrlHitCount();
        return Response.ok(urlHitCount)
            .status(Response.Status.OK)
            .build();
    }
}
