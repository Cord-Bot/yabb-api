package io.github.eggy03.yabb.exception.mapper;

import io.github.eggy03.yabb.exception.entity.ErrorResponse;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.zone.ZoneRulesException;

@Provider
public class ZoneRulesExceptionMapper implements ExceptionMapper<ZoneRulesException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(ZoneRulesException e) {

        ErrorResponse errorResponse = new ErrorResponse(
                Response.Status.NOT_FOUND.getStatusCode(),
                e.getClass().getSimpleName(),
                e.getMessage(),
                LocalDateTime.now(ZoneId.systemDefault()),
                uriInfo.getPath()
        );

        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(errorResponse)
                .build();
    }
}
