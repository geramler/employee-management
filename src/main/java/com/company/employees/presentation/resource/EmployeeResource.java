package com.company.employees.presentation.resource;

import com.company.employees.application.dto.request.CreateEmployeeRequest;
import com.company.employees.application.dto.request.CreateEmployeesPayload;
import com.company.employees.application.dto.request.UpdateEmployeeRequest;
import com.company.employees.application.dto.response.EmployeeResponse;
import com.company.employees.application.service.EmployeeService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/employees")
@Tag(name = "Employees")
public class EmployeeResource {

    private final EmployeeService service;

    @Inject
    public EmployeeResource(EmployeeService service) {
        this.service = service;
    }

    @GET
    public List<EmployeeResponse> findAll() {
        return service.findAll();
    }

    @GET
    @Path("/{id}")
    public EmployeeResponse findById(@PathParam("id") Long id) {
        return service.findById(id);
    }

    @POST
    public Response create(@NotNull @Valid CreateEmployeesPayload payload) {
        return Response.status(Response.Status.CREATED)
                .entity(service.create(payload.employees()))
                .build();
    }

    @PUT
    @Path("/{id}")
    public EmployeeResponse update(
            @PathParam("id") Long id,
            @NotNull @Valid UpdateEmployeeRequest request
    ) {
        return service.update(id, request);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        service.delete(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/search")
    public List<EmployeeResponse> search(@QueryParam("name") @NotBlank String name) {
        return service.searchByName(name);
    }
}
