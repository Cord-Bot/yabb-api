package io.github.eggy03.yabb.controller;

import io.github.eggy03.yabb.dto.GuildDTO;
import io.github.eggy03.yabb.exception.entity.ErrorResponse;
import io.github.eggy03.yabb.service.GuildService;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
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

@Path("api/v1/guild")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
@ApplicationScoped
@Tag(name = "Guild")
public class GuildController {

    private final @NonNull GuildService guildService;

    @POST
    @Blocking
    @RunOnVirtualThread
    @Operation(summary = "Save a Guild")
    @APIResponse(responseCode = "201", description = "Success")
    @APIResponse(responseCode = "400", description = "Validation Errors", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    public Response saveGuild(@Valid GuildDTO dto) {
        guildService.saveGuild(dto);
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Path("/{guildId}")
    @Blocking
    @RunOnVirtualThread
    @Operation(summary = "Get a Guild")
    @APIResponse(responseCode = "200", description = "Success", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GuildDTO.class)))
    @APIResponse(responseCode = "400", description = "Validation Errors", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    @APIResponse(responseCode = "404", description = "Guild not found", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    public Response getGuild(@PathParam("guildId") @Positive long guildId) {
        return Response
                .ok(guildService.getGuild(guildId))
                .build();
    }

    @GET
    @Blocking
    @RunOnVirtualThread
    @Operation(summary = "Get All Guilds")
    @APIResponse(responseCode = "200", description = "Success", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GuildDTO[].class)))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    public Response getAllGuilds() {
        return Response
                .ok(guildService.getAllGuilds())
                .build();
    }

    @PUT
    @Blocking
    @RunOnVirtualThread
    @Operation(summary = "Update a Guild")
    @APIResponse(responseCode = "204", description = "Success")
    @APIResponse(responseCode = "400", description = "Validation Errors", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    @APIResponse(responseCode = "404", description = "Guild not found", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    public Response updateGuild(@Valid GuildDTO updatedDTO) {
        guildService.updateGuild(updatedDTO);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{guildId}")
    @Blocking
    @RunOnVirtualThread
    @Operation(summary = "Delete a Guild")
    @APIResponse(responseCode = "204", description = "Success")
    @APIResponse(responseCode = "400", description = "Validation Errors", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    @APIResponse(responseCode = "404", description = "Guild not found", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    @APIResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ErrorResponse.class)))
    public Response deleteGuild(@PathParam("guildId") @Positive long guildId) {
        guildService.deleteGuild(guildId);
        return Response.noContent().build();
    }
}
