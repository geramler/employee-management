package com.company.employees.presentation.resource;

import com.company.employees.application.dto.request.LoginRequest;
import com.company.employees.application.dto.response.LoginResponse;
import com.company.employees.application.service.AuthenticationService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/auth")
public class AuthenticationResource {

    private final AuthenticationService authenticationService;

    @Inject
    public AuthenticationResource(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest request) {
        LoginResponse response = authenticationService.login(request);
        if (response == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(java.util.Map.of("message", "Invalid username or password"))
                    .build();
        }
        return Response.ok(response).build();
    }
}
