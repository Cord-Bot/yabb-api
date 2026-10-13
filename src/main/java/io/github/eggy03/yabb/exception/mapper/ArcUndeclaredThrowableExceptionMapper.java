package io.github.eggy03.yabb.exception.mapper;

import io.github.eggy03.yabb.exception.entity.ErrorResponse;
import io.quarkus.arc.ArcUndeclaredThrowableException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.hibernate.exception.ConstraintViolationException;

import java.time.OffsetDateTime;
import java.time.ZoneId;

@Provider
public class ArcUndeclaredThrowableExceptionMapper implements ExceptionMapper<ArcUndeclaredThrowableException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(ArcUndeclaredThrowableException e) {

        // CVE is wrapped in RollbackException which is wrapped in ArcUndeclaredThrowableException
        if (e.getCause().getCause() instanceof ConstraintViolationException cve) {
            return cveResponse(cve);
        }

        return genericResponse(e);
    }

    private Response cveResponse(ConstraintViolationException cve) {
        ErrorResponse errorResponse = new ErrorResponse(
                Response.Status.CONFLICT.getStatusCode(),
                cve.getClass().getSimpleName(),
                cve.getMessage(),
                OffsetDateTime.now(ZoneId.systemDefault()),
                uriInfo.getPath()
        );

        return Response
                .status(Response.Status.CONFLICT)
                .entity(errorResponse)
                .build();
    }

    private Response genericResponse(ArcUndeclaredThrowableException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                e.getClass().getSimpleName(),
                e.getMessage(),
                OffsetDateTime.now(ZoneId.systemDefault()),
                uriInfo.getPath()
        );

        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorResponse)
                .build();
    }
}
