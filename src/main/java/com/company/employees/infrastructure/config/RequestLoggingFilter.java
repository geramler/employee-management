package com.company.employees.infrastructure.config;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class RequestLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOGGER = Logger.getLogger(RequestLoggingFilter.class);

    @Override
    public void filter(ContainerRequestContext request) {
        LOGGER.infof(
                "request method=%s path=%s contentType=%s accept=%s userAgent=%s traceId=%s",
                request.getMethod(),
                request.getUriInfo().getPath(),
                request.getHeaderString("Content-Type"),
                request.getHeaderString("Accept"),
                request.getHeaderString("User-Agent"),
                request.getHeaderString("X-Trace-Id")
        );
    }

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) {
        LOGGER.infof(
                "response method=%s path=%s status=%d contentType=%s traceId=%s",
                request.getMethod(),
                request.getUriInfo().getPath(),
                response.getStatus(),
                response.getMediaType(),
                request.getHeaderString("X-Trace-Id")
        );
    }
}
