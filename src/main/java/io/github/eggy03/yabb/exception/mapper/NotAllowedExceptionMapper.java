package io.github.eggy03.yabb.exception.mapper;

import io.github.eggy03.yabb.exception.entity.ErrorResponse;
import jakarta.ws.rs.NotAllowedException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.OffsetDateTime;
import java.time.ZoneId;

@Provider
public class NotAllowedExceptionMapper implements ExceptionMapper<NotAllowedException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(NotAllowedException e) {

        ErrorResponse errorResponse = new ErrorResponse(
                Response.Status.METHOD_NOT_ALLOWED.getStatusCode(),
                e.getClass().getSimpleName(),
                e.getMessage(),
                OffsetDateTime.now(ZoneId.systemDefault()),
                uriInfo.getPath()
        );

        return Response
                .status(Response.Status.METHOD_NOT_ALLOWED)
                .entity(errorResponse)
                .build();
    }
}
