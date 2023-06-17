package service;
import dto.*;
import dto.Project;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.List;


@Path("/contest")
public class Contest {
    @Inject
    bean.Project projBean;
    @Inject
    bean.User userBean;
    @Inject
    bean.Contest contestBean;
}
