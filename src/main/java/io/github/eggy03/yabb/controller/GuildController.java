package io.github.eggy03.yabb.controller;

import io.github.eggy03.yabb.dto.GuildDTO;
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

@Path("api/v1/guild")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
@ApplicationScoped
public class GuildController {

    private final @NonNull GuildService guildService;

    @POST
    @Blocking
    @RunOnVirtualThread
    public Response saveGuild(@Valid GuildDTO dto) {
        guildService.saveGuild(dto);
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Path("/{guildId}")
    @Blocking
    @RunOnVirtualThread
    public Response getGuild(@PathParam("guildId") @Positive long guildId) {
        return Response
                .ok(guildService.getGuild(guildId))
                .build();
    }

    @GET
    @Blocking
    @RunOnVirtualThread
    public Response getAllGuilds() {
        return Response
                .ok(guildService.getAllGuilds())
                .build();
    }

    @PUT
    @Blocking
    @RunOnVirtualThread
    public Response updateGuild(@Valid GuildDTO updatedDTO) {
        guildService.updateGuild(updatedDTO);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{guildId}")
    @Blocking
    @RunOnVirtualThread
    public Response deleteGuild(@PathParam("guildId") @Positive long guildId) {
        guildService.deleteGuild(guildId);
        return Response.noContent().build();
    }
}
