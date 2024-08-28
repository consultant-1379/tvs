package com.ericsson.gic.tms.tvs.presentation.resources;

import com.ericsson.gic.tms.tvs.presentation.dto.UserBean;
import com.ericsson.gic.tms.tvs.presentation.dto.UserCredentials;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path(LoginResource.BASE_PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface LoginResource {

    String BASE_PATH = "login";

    /**
     * Request authentication status.
     *
     * @return Response with Authentication status.
     */
    @GET
    UserBean status();

    /**
     * <p>Log's user in with provided credentials, or returns authentication error.</p>
     *
     * @param userCredentials User Credentials with username and password.
     * @return Response with request status.
     */
    @POST
    UserBean login(@Valid @NotNull UserCredentials userCredentials);

    /**
     * <p>Log's user out and clears user session.</p>
     *
     * @return Response with Authentication status.
     */
    @DELETE
    UserBean logout();

}
