package io.github.eggy03.yabb.exception.mapper;

import io.github.eggy03.yabb.exception.entity.ErrorResponse;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.time.ZoneId;

@Provider
@Slf4j
public class GenericExceptionMapper implements ExceptionMapper<Exception> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(Exception e) {

        log.error("An internal server error has occurred", e);

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
