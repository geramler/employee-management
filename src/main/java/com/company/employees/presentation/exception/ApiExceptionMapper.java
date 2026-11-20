package com.company.employees.presentation.exception;

import com.company.employees.domain.exception.EmployeeNotFoundException;
import com.company.employees.domain.exception.EmployeeValidationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Provider
public class ApiExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOGGER = Logger.getLogger(ApiExceptionMapper.class);

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(Exception exception) {
        @Nullable EmployeeNotFoundException notFound =
                findCause(exception, EmployeeNotFoundException.class);
        if (notFound != null) {
            return response(Response.Status.NOT_FOUND, notFound.getMessage(), Map.of());
        }

        @Nullable EmployeeValidationException invalidEmployee =
                findCause(exception, EmployeeValidationException.class);
        if (invalidEmployee != null) {
            return response(
                    Response.Status.BAD_REQUEST,
                    invalidEmployee.getMessage(),
                    invalidEmployee.getFieldErrors()
            );
        }

        @Nullable ConstraintViolationException violations =
                findCause(exception, ConstraintViolationException.class);
        if (violations != null) {
            return validationResponse(uriInfo, violations);
        }

        if (findCause(exception, BadRequestException.class) != null
                || findCause(exception, JsonProcessingException.class) != null) {
            return response(Response.Status.BAD_REQUEST, "Malformed JSON request.", Map.of());
        }

        LOGGER.error("Unhandled request error", exception);
        return response(Response.Status.INTERNAL_SERVER_ERROR, "An unexpected error occurred.", Map.of());
    }

    private Response response(Response.Status status, String message, Map<String, String> fieldErrors) {
        return createResponse(uriInfo, status, message, fieldErrors);
    }

    static Response validationResponse(UriInfo uriInfo, ConstraintViolationException exception) {
        Map<String, String> fieldErrors = new TreeMap<>();
        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            fieldErrors.merge(
                    violation.getPropertyPath().toString(),
                    violation.getMessage(),
                    (first, second) -> first + "; " + second
            );
        }
        return createResponse(uriInfo, Response.Status.BAD_REQUEST, "Request validation failed.", fieldErrors);
    }

    private static Response createResponse(
            UriInfo uriInfo,
            Response.Status status,
            String message,
            Map<String, String> fieldErrors
    ) {
        ApiError body = new ApiError(
                Instant.now(),
                status.getStatusCode(),
                status.getReasonPhrase(),
                message,
                uriInfo == null ? "" : uriInfo.getPath(),
                fieldErrors
        );
        return Response.status(status).entity(body).build();
    }

    private <T extends Throwable> @Nullable T findCause(Throwable exception, Class<T> type) {
        Set<Throwable> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        for (Throwable cause = exception; cause != null && visited.add(cause); cause = cause.getCause()) {
            if (type.isInstance(cause)) {
                return type.cast(cause);
            }
        }
        return null;
    }
}
