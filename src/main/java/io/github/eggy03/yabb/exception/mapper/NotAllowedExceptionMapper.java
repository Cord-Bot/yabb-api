package io.github.eggy03.yabb.exception.mapper;

import io.github.eggy03.yabb.annotation.UTCClock;
import io.github.eggy03.yabb.exception.entity.ErrorResponse;
import jakarta.ws.rs.NotAllowedException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jspecify.annotations.NonNull;

import java.time.Clock;

@Provider
public class NotAllowedExceptionMapper implements ExceptionMapper<NotAllowedException> {

    @Context
    UriInfo uriInfo;

    private final @NonNull Clock clock;

    public NotAllowedExceptionMapper(@NonNull @UTCClock Clock clock) {
        this.clock = clock;
    }

    @Override
    public Response toResponse(@NonNull NotAllowedException e) {

        ErrorResponse errorResponse = new ErrorResponse(
                Response.Status.METHOD_NOT_ALLOWED.getStatusCode(),
                e.getClass().getSimpleName(),
                e.getMessage() == null ? "N/A" : e.getMessage(),
                clock.instant(),
                uriInfo.getPath() == null ? "N/A" : uriInfo.getPath()
        );

        return Response
                .status(Response.Status.METHOD_NOT_ALLOWED)
                .entity(errorResponse)
                .build();
    }
}
