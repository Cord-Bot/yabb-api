package io.github.eggy03.yabb.controller;

import io.github.eggy03.yabb.dto.BirthdayRequestDTO;
import io.github.eggy03.yabb.dto.BirthdayResponseDTO;
import io.github.eggy03.yabb.exception.entity.ErrorResponse;
import io.github.eggy03.yabb.service.BirthdayService;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
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
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("api/v1/birthday")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
@ApplicationScoped
@Tag(name = "Birthday")
public class BirthdayController {

    private final @NonNull BirthdayService birthdayService;

    @POST
    @Blocking
    @RunOnVirtualThread
    @Operation(summary = "Save a Birthday")
    @APIResponse(responseCode = "201", description = "Success")
    @APIResponse(responseCode = "400", description = "Validation Errors", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    @APIResponse(responseCode = "409", description = "Constraint Violation Errors", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    public Response saveBirthday(@Valid BirthdayRequestDTO dto) {
        birthdayService.saveBirthday(dto);
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Path("/{guildId}/{userId}")
    @Blocking
    @RunOnVirtualThread
    @Operation(summary = "Get Birthday for a User in a Guild")
    @APIResponse(responseCode = "200", description = "Success", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = BirthdayResponseDTO.class)))
    @APIResponse(responseCode = "400", description = "Validation Errors", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    @APIResponse(responseCode = "404", description = "Birthday not found", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    public Response getBirthday(@PathParam("guildId") @Positive long guildId, @PathParam("userId") @Positive long userId) {
        return Response
                .ok(birthdayService.getBirthday(guildId, userId))
                .build();
    }

    @GET
    @Path("/{guildId}")
    @Blocking
    @RunOnVirtualThread
    @Operation(summary = "Get Birthdays for a Guild")
    @APIResponse(responseCode = "200", description = "Success", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = BirthdayResponseDTO[].class)))
    @APIResponse(responseCode = "400", description = "Validation Errors", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    public Response getBirthdays(@PathParam("guildId") @Positive long guildId) {
        return Response
                .ok(birthdayService.getBirthdays(guildId))
                .build();
    }

    @GET
    @Path("/pending")
    @Blocking
    @RunOnVirtualThread
    @Operation(summary = "Get Pending Birthday List")
    @APIResponse(responseCode = "200", description = "Success", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = BirthdayResponseDTO[].class)))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    public Response getPendingBirthdays() {
        return Response
                .ok(birthdayService.getPendingBirthdays())
                .build();
    }

    @PATCH
    @Blocking
    @RunOnVirtualThread
    @Operation(summary = "Update a Birthday")
    @APIResponse(responseCode = "204", description = "Success")
    @APIResponse(responseCode = "400", description = "Validation Errors", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    @APIResponse(responseCode = "404", description = "Birthday not found", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    public Response updateBirthday(@Valid BirthdayRequestDTO updatedDTO) {
        birthdayService.updateBirthday(updatedDTO);
        return Response.noContent().build();
    }

    @PATCH
    @Path("/update-for-next-year")
    @Blocking
    @RunOnVirtualThread
    @Operation(summary = "Update Birthday for Next Year")
    @APIResponse(responseCode = "204", description = "Success")
    @APIResponse(responseCode = "400", description = "Validation Errors", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    @APIResponse(responseCode = "404", description = "Birthday not found", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    public Response updateBirthdayForNextYear(@Valid BirthdayRequestDTO dto) {
        birthdayService.updateBirthdayForNextYear(dto);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{guildId}")
    @Blocking
    @RunOnVirtualThread
    @Operation(summary = "Delete all Birthdays belonging to a Guild")
    @APIResponse(responseCode = "200", description = "Success", content = @Content(mediaType = MediaType.TEXT_PLAIN))
    @APIResponse(responseCode = "400", description = "Validation Errors", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    public Response deleteBirthdayByGuild(@PathParam("guildId") @Positive long guildId) {
        return Response
                .ok(String.valueOf(birthdayService.deleteBirthdayByGuild(guildId)))
                .build();
    }

    @DELETE
    @Path("/{guildId}/{userId}")
    @Blocking
    @RunOnVirtualThread
    @Operation(summary = "Delete a User's Birthday")
    @APIResponse(responseCode = "204", description = "Success")
    @APIResponse(responseCode = "400", description = "Validation Errors", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    @APIResponse(responseCode = "404", description = "Birthday not found", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    public Response deleteBirthday(@PathParam("guildId") @Positive long guildId, @PathParam("userId") @Positive long userId) {
        birthdayService.deleteBirthday(guildId, userId);
        return Response.noContent().build();
    }
}