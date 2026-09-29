package io.github.eggy03.yabb.exception.mapper;

import io.github.eggy03.yabb.exception.BirthdayNotificationNotFoundException;
import io.github.eggy03.yabb.exception.entity.ErrorResponse;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Provider
public class BirthdayNotificationNotFoundExceptionMapper implements ExceptionMapper<BirthdayNotificationNotFoundException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(BirthdayNotificationNotFoundException e) {

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
