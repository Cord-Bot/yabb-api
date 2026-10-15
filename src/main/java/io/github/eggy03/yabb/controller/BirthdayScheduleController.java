package io.github.eggy03.yabb.controller;

import io.github.eggy03.yabb.dto.BirthdayDTO;
import io.github.eggy03.yabb.dto.BirthdayScheduleDTO;
import io.github.eggy03.yabb.exception.entity.ErrorResponse;
import io.github.eggy03.yabb.service.BirthdayScheduleService;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("api/v1/birthday/schedule")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
@ApplicationScoped
@Tag(name = "Birthday Schedule")
public class BirthdayScheduleController {

    private final @NonNull BirthdayScheduleService birthdayScheduleService;

    @GET
    @Path("/upcoming")
    @Blocking
    @RunOnVirtualThread
    @Operation(operationId = "getUpcomingSchedules", summary = "Get a list of schedules due in the future")
    @APIResponse(responseCode = "200", description = "Success", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = BirthdayScheduleDTO[].class)))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    public Response getUpcoming() {
        return Response
                .ok(birthdayScheduleService.getUpcomingSchedules())
                .build();
    }

    @GET
    @Path("/active")
    @Blocking
    @RunOnVirtualThread
    @Operation(operationId = "getActiveSchedules", summary = "Get a list of schedules overdue by at most 24 hours")
    @APIResponse(responseCode = "200", description = "Success", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = BirthdayScheduleDTO[].class)))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    public Response getActiveBirthdays() {
        return Response
                .ok(birthdayScheduleService.getActiveSchedules())
                .build();
    }

    @GET
    @Path("/expired")
    @Blocking
    @RunOnVirtualThread
    @Operation(operationId = "getExpiredSchedules", summary = "Get a list of schedules overdue by more than 24 hours")
    @APIResponse(responseCode = "200", description = "Success", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = BirthdayScheduleDTO[].class)))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    public Response getExpiredBirthdays() {
        return Response
                .ok(birthdayScheduleService.getExpiredSchedules())
                .build();
    }

    @PATCH
    @Path("/advance/{scheduleGuildId}/{scheduleUserId}")
    @Blocking
    @RunOnVirtualThread
    @Operation(operationId = "advance", summary = "Update schedule for next year")
    @APIResponse(responseCode = "204", description = "Success")
    @APIResponse(responseCode = "400", description = "Validation Errors", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    @APIResponse(responseCode = "404", description = "Schedule not found", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    public Response advanceSchedule(@Parameter(required = true) @PathParam("scheduleGuildId") @Positive long scheduleGuildId, @Parameter(required = true) @PathParam("scheduleUserId") @Positive long scheduleUserId) {
        birthdayScheduleService.advanceSchedule(scheduleGuildId, scheduleUserId);
        return Response.noContent().build();
    }
}
